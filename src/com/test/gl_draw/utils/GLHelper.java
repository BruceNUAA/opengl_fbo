
package com.test.gl_draw.utils;

import android.graphics.Bitmap;
import android.opengl.ETC1;
import android.opengl.ETC1Util;
import android.opengl.ETC1Util.ETC1Texture;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

public class GLHelper {

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

    public static boolean isEGLContextOK() {
        return !((EGL10) EGLContext.getEGL()).eglGetCurrentContext().equals(
                EGL10.EGL_NO_CONTEXT);
    }

    public static void checkEGLContextOK() {
        if (!isEGLContextOK()) {
            throw new RuntimeException("Opengl context is not created !");
        }
    }

    public static void checkGLError() {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            throw new RuntimeException("GLError 0x"
                    + Integer.toHexString(error));
        }
    }

    public static boolean isTexture(int texture) {
        return GLES20.glIsTexture(texture);
    }

    public static int getTextureMaxSize() {
        int[] max = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, max, 0);
        return max[0];
    }

    public static int loadTexture(Bitmap bitmap) {
        final int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        if (textures[0] == 0) {
            throw new RuntimeException("failed to load texture");
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        // inside antialias
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);

        if (!ETC1Util.isETC1Supported() || bitmap.hasAlpha()) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        } else {
            bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
            int bpp = 2;

            ByteBuffer bb = ByteBuffer.allocateDirect(bitmap.getByteCount());
            bb.order(ByteOrder.nativeOrder());
            bitmap.copyPixelsToBuffer(bb);
            bb.position(0);

            int encodedImageSize = ETC1.getEncodedDataSize(bitmap.getWidth(),
                    bitmap.getHeight());
            ByteBuffer compressedImage = ByteBuffer.allocateDirect(encodedImageSize).order(
                    ByteOrder.nativeOrder());
            ETC1.encodeImage(bb, bitmap.getWidth(), bitmap.getHeight(), bpp,
                    bpp * bitmap.getWidth(), compressedImage);
            ETC1Texture etc1tex = new ETC1Texture(bitmap.getWidth(), bitmap.getHeight(),
                    compressedImage);

            ETC1Util.loadTexture(GLES10.GL_TEXTURE_2D, 0, 0, GLES10.GL_RGB,
                    GLES10.GL_UNSIGNED_SHORT_5_6_5, etc1tex);
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textures[0];
    }

    public static int createTargetTexture(int width, int height) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        if (textures[0] == 0) {
            throw new RuntimeException("failed to load texture");
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width,
                height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);
        return textures[0];
    }

    public static void deleteTargetTexture(int[] texture) {
        GLES20.glDeleteTextures(texture.length, texture, 0);
    }

    public static int createFrameBuffer(int width, int height,
            int targetTextureId) {
        int framebuffer;
        int[] framebuffers = new int[1];
        GLES20.glGenFramebuffers(1, framebuffers, 0);
        framebuffer = framebuffers[0];
        GLES20.glBindFramebuffer(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                framebuffer);

        int depthbuffer;
        int[] renderbuffers = new int[1];
        GLES20.glGenRenderbuffers(1, renderbuffers, 0);
        depthbuffer = renderbuffers[0];

        GLES20.glBindRenderbuffer(GL11ExtensionPack.GL_RENDERBUFFER_OES,
                depthbuffer);
        GLES20.glRenderbufferStorage(GL11ExtensionPack.GL_RENDERBUFFER_OES,
                GL11ExtensionPack.GL_DEPTH_COMPONENT16, width, height);
        GLES20.glFramebufferRenderbuffer(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                GL11ExtensionPack.GL_DEPTH_ATTACHMENT_OES,
                GL11ExtensionPack.GL_RENDERBUFFER_OES, depthbuffer);

        GLES20.glFramebufferTexture2D(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES,
                GLES20.GL_TEXTURE_2D, targetTextureId, 0);
        int status = GLES20
                .glCheckFramebufferStatus(GL11ExtensionPack.GL_FRAMEBUFFER_OES);
        if (status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES) {
            throw new RuntimeException("Framebuffer is not complete: "
                    + Integer.toHexString(status));
        }

        GLES20.glBindFramebuffer(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
        return framebuffer;
    }

    public static void deleteFrameBuffers(int[] fbo) {

        checkGLError();
        GLES20.glDeleteFramebuffers(fbo.length, fbo, 0);
    }

    public static boolean checkIfContextSupportsNPOT() {
        return checkIfContextSupportsExtension("GL_OES_texture_npot");
    }

    public static boolean checkIfContextSupportsFrameBufferObject() {
        return checkIfContextSupportsExtension("GL_OES_framebuffer_object");
    }

    /**
     * This is not the fastest way to check for an extension, but fine if we are
     * only checking for a few extensions each time a context is created.
     * 
     * @param gl
     * @param extension
     * @return true if the extension is present in the current context.
     */
    public static boolean checkIfContextSupportsExtension(String extension) {
        String extensions = " " + GLES20.glGetString(GLES20.GL_EXTENSIONS)
                + " ";
        return extensions.indexOf(" " + extension + " ") >= 0;
    }

    public static int getMaxClipPanels() {
        int v[] = new int[1];
        GLES20.glGetIntegerv(GL11.GL_MAX_CLIP_PLANES, v, 0);
        return v[0];
    }
}