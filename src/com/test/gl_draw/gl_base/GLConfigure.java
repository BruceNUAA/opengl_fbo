package com.test.gl_draw.gl_base;

import javax.microedition.khronos.opengles.GL10;

public class GLConfigure {

    // GL函数调试相关的信息
    private boolean mEnableGLCallLog = false;//BuildConfig.DEBUG;
    private boolean mEnableGLErrorCheck = false;//BuildConfig.DEBUG;
    
    // GL环境相关的信息
    private boolean mGLInited = false;
    private boolean mIsSupportNPOT = false;
    private boolean mIsSupportFBO = false;
    
    private static GLConfigure sGlConfigure = null;
    
    public static GLConfigure getInstance() {
        if (sGlConfigure == null) {
            sGlConfigure = new GLConfigure();
        }
        
        return sGlConfigure;
    }
    
    public void Init(GL10 gl) {
        String extensions = " " + gl.glGetString(GL10.GL_EXTENSIONS)
                + " ";
        
        mIsSupportNPOT = checkIfContextSupportsNPOT(extensions);
        mIsSupportFBO = checkIfContextSupportsFrameBufferObject(extensions);
        
        extensions = null;
        
        mGLInited = true;
    }
    
    public void enbleGLLog(boolean call_log, boolean error_check) {
        mEnableGLCallLog = call_log;
        mEnableGLErrorCheck = error_check;
    }
    
    public boolean enableGLCallLog() {
        return mEnableGLCallLog;
    }
    
    public boolean enableGLErrorCheck() {
        return mEnableGLErrorCheck;
    }
    
    public boolean isSupportFBO(GL10 gl) {
        if (!mGLInited) {
            Init(gl);
        }
        
        return mIsSupportFBO;
    }
    
    public boolean isSupportNPOT(GL10 gl) {
        if (!mGLInited) {
            Init(gl);
        }
        
        return mIsSupportNPOT;
    }
    
    private  boolean checkIfContextSupportsNPOT(String extensions) {
        return checkIfContextSupportsExtension(extensions, "GL_OES_texture_npot");
    }

    private  boolean checkIfContextSupportsFrameBufferObject(String extensions) {
        return checkIfContextSupportsExtension(extensions, "GL_OES_framebuffer_object");
    }

    /**
     * This is not the fastest way to check for an extension, but fine if we are
     * only checking for a few extensions each time a context is created.
     * 
     * @param gl
     * @param extension
     * @return true if the extension is present in the current context.
     */
    private boolean checkIfContextSupportsExtension(String extensions, String extension) {
  
        return extensions.indexOf(" " + extension + " ") >= 0;
    }
}
