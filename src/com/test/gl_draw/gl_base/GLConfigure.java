package com.test.gl_draw.gl_base;

import javax.microedition.khronos.opengles.GL10;

import com.test.gl_draw.utils.GLHelper;

public class GLConfigure {

    // GL函数调试相关的信息
    // Opengl函数的调试
    private boolean mEnableGLCallLog =  false;//GLHelper.EnableGLDebug();
    private boolean mEnableGLErrorCheck = false;//GLHelper.EnableGLDebug();
    
    // GLView函数的调试
    private boolean mEnableGLThreadCheck = GLHelper.EnableGLDebug();
    private boolean mEnableGLViewErrorCheck = GLHelper.EnableGLDebug();
    private boolean mEnableGLViewTimeLog = GLHelper.EnableGLDebug();
    private boolean mEnableGLTimerLog = GLHelper.EnableGLDebug();
    
    // 调试GL资源
    private boolean mEnableGLResourceLog = GLHelper.EnableGLDebug();
    
    // GL环境相关的信息
    private boolean mGLInited = false;
    private boolean mIsSupportNPOT = false;
    
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
    
    public boolean enableGLResourceLog() {
        return mEnableGLResourceLog;
    }
    /// 
    public boolean enableGLThreadCheck() {
        return mEnableGLThreadCheck;
    }
    
    public boolean enableGLViewErrorCheck() {
        return mEnableGLViewErrorCheck;
    }
    
    public boolean enableGLViewTimeLog() {
        return mEnableGLViewTimeLog;
    }
    
    public boolean enableGLTimerLog() {
        return mEnableGLTimerLog;
    }
    
    public boolean isSupportNPOT() {
        if (!mGLInited) {
            throw new RuntimeException("GLConfigure should be Inited!");
        }
        
        return mIsSupportNPOT;
    }
    
    private  boolean checkIfContextSupportsNPOT(String extensions) {
        return checkIfContextSupportsExtension(extensions, "GL_OES_texture_npot");
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
