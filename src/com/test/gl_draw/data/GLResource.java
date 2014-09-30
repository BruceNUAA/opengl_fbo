package com.test.gl_draw.data;

import com.test.gl_draw.gl_base.GLObject;
import com.test.gl_draw.utils.GLHelper;

public abstract class GLResource extends GLObject implements GLResourceManager.IGLResource {
    
    private String mLogMsg;
    
    public GLResource() {
        mLogMsg = GLHelper.getFuncCallMsg(2);
    }
    
    @Override
    public String getLogMessage() {
        return mLogMsg;
    }

    @Override
    public String getDetailLogMessage() {
        return "";
    }
    
    @Override
    public void didLoad(long create_time) {
        GLResourceManager.getInstance().ResoureLoad(this, create_time);
    }

    @Override
    public  void didUnload() {
        GLResourceManager.getInstance().ResoureUnLoad(this);
    }
}
