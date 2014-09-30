
package com.test.gl_draw.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.ETC1;
import android.opengl.ETC1Util;
import android.opengl.ETC1Util.ETC1Texture;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLUtils;

import com.example.gl_fbo.BuildConfig;

public class GLHelper {

    public static int ColorBetween(int color_a, int color_b, float alpha) {
        alpha = Math.max(0, Math.min(1, alpha));

        int a = (int) (Color.alpha(color_a) * alpha + Color.alpha(color_b) * (1 - alpha));
        int r = (int) (Color.red(color_a) * alpha + Color.red(color_b) * (1 - alpha));
        int g = (int) (Color.green(color_a) * alpha + Color.green(color_b) * (1 - alpha));
        int b = (int) (Color.blue(color_a) * alpha + Color.blue(color_b) * (1 - alpha));
        return Color.argb(a, r, g, b);
    }

    public static long cellPowerOf2(long n) {
        n--;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        n++;
        return n;
    }
    
    public static String getFuncCallMsg(int offset) {
        Throwable throwable = new Throwable();
        
        int inspact_pos = offset + 1;
        
        StackTraceElement[] call_stacks = throwable.getStackTrace();
        
        if (call_stacks.length < inspact_pos) {
            return "";
        }
        
        StringBuilder b = new StringBuilder();
        
        b.append(call_stacks[inspact_pos].getFileName());
        b.append(":");
        b.append(call_stacks[inspact_pos].getMethodName());
        b.append("(");
        b.append(call_stacks[inspact_pos].getLineNumber());
        b.append(")");
        return b.toString();
    }
    
    public static boolean EnableGLDebug() {
        return BuildConfig.DEBUG;
    }

    public static boolean isEGLContextOK() {
        return !((EGL10) EGLContext.getEGL()).eglGetCurrentContext().equals(
                EGL10.EGL_NO_CONTEXT);
    }

    public static void checkEGLContextOK() {
        if (EnableGLDebug() && !isEGLContextOK()) {
            throw new RuntimeException("Opengl context is not created !");
        }
    }

    public static void checkGLError(GL10 gl) {
        if (!EnableGLDebug())
            return;
        
        int error = gl.glGetError();

        if (error != GL10.GL_NO_ERROR) {
            throw new GLException(error);
        }
    }

    public static boolean isTexture(GL10 gl, int texture) {
        GL11 gl11 = (GL11) gl;
        boolean hr = texture != 0 && gl11.glIsTexture(texture);
        checkGLError(gl);
        return hr;
    }

    public static boolean isFrameBuffer(GL10 gl, int framebuffer) {
        GL11ExtensionPack glexp = (GL11ExtensionPack) gl;
        return framebuffer != 0 && glexp.glIsFramebufferOES(framebuffer);
    }

    public static int loadTexture(GL10 gl, Bitmap bitmap) {

        if (bitmap == null || bitmap.isRecycled())
            return 0;

        int[] textures = new int[1];

        gl.glGenTextures(1, textures, 0);

        if (textures[0] == 0 && EnableGLDebug()) {
            throw new RuntimeException("failed to load texture");
        }

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        // inside antialias
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_REPEAT);

        int pixel_byte = 0;

        switch (bitmap.getConfig()) {
            case ALPHA_8:
                pixel_byte = 1;
                break;
            case RGB_565:
            case ARGB_4444:
                pixel_byte = 2;
                break;
            case ARGB_8888:
                pixel_byte = 4;
                break;
        }

        gl.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT, pixel_byte);

        if (!ETC1Util.isETC1Supported() || bitmap.hasAlpha()) {
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        } else {
            bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
            int bpp = 2;

            ByteBuffer bb = ByteBuffer.allocateDirect(bitmap.getByteCount());
            bb.order(ByteOrder.nativeOrder());
            bitmap.copyPixelsToBuffer(bb);
            bb.position(0);

            int encodedImageSize = ETC1.getEncodedDataSize(bitmap.getWidth(),
                    bitmap.getHeight());
            ByteBuffer compressedImage = ByteBuffer.allocateDirect(
                    encodedImageSize).order(ByteOrder.nativeOrder());
            ETC1.encodeImage(bb, bitmap.getWidth(), bitmap.getHeight(), bpp,
                    bpp * bitmap.getWidth(), compressedImage);
            ETC1Texture etc1tex = new ETC1Texture(bitmap.getWidth(),
                    bitmap.getHeight(), compressedImage);

            ETC1Util.loadTexture(GLES10.GL_TEXTURE_2D, 0, 0, GLES10.GL_RGB,
                    GLES10.GL_UNSIGNED_SHORT_5_6_5, etc1tex);
        }

        gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

        checkGLError(gl);
        return textures[0];
    }

    public static void deleteTargetTexture(GL10 gl, int... texture) {
        gl.glDeleteTextures(texture.length, texture, 0);
    }

    public static int createFrameBuffer(GL10 gl, int[] size,
            int targetTextureId) {
        int framebuffer;
        int[] framebuffers = new int[1];
        GL11ExtensionPack gl11 = (GL11ExtensionPack) gl;

        gl11.glGenFramebuffersOES(1, framebuffers, 0);
        framebuffer = framebuffers[0];
        gl11.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                framebuffer);

        int depthbuffer;
        int[] renderbuffers = new int[1];
        gl11.glGenRenderbuffersOES(1, renderbuffers, 0);
        depthbuffer = renderbuffers[0];

        gl11.glBindRenderbufferOES(GL11ExtensionPack.GL_RENDERBUFFER_OES,
                depthbuffer);
        gl11.glRenderbufferStorageOES(GL11ExtensionPack.GL_RENDERBUFFER_OES,
                GL11ExtensionPack.GL_DEPTH_COMPONENT16, size[0], size[1]);
        gl11.glFramebufferRenderbufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                GL11ExtensionPack.GL_DEPTH_ATTACHMENT_OES,
                GL11ExtensionPack.GL_RENDERBUFFER_OES, depthbuffer);

        gl11.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES,
                GL10.GL_TEXTURE_2D, targetTextureId, 0);
        int status = gl11
                .glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);

        if (status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES) {
            throw new RuntimeException("Framebuffer is not complete: "
                    + Integer.toHexString(status));
        }

        gl11.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
        GLHelper.checkGLError(gl);
        return framebuffer;
    }
    
    public static int createTargetTexture(GL10 gl, int width, int height) {

        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);

        if (textures[0] == 0) {
            return 0;
        }

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, width,
                height, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_REPEAT);
        checkGLError(gl);
        return textures[0];
    }

    public static boolean deleteFrameBuffers(GL10 gl, int fbo) {
        GL11ExtensionPack gl11 = (GL11ExtensionPack) gl;

        if (!isFrameBuffer(gl, fbo))
            return false;

        gl11.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, fbo);
        gl11.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D, 0, 0);

        gl11.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);

        gl11.glDeleteFramebuffersOES(1, new int[] {
                fbo
        }, 0);

        checkGLError(gl);
        return true;
    }

}
