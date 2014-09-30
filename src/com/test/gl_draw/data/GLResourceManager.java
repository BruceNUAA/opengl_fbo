
package com.test.gl_draw.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.util.Log;

import com.test.gl_draw.gl_base.GLConfigure;
import com.test.gl_draw.gl_base.NonThreadSafe;

public class GLResourceManager extends NonThreadSafe {

    public interface IGLResource {

        String getLogMessage();

        String getDetailLogMessage();

        void didLoad(long create_time);

        void unload();
        
        void didUnload();
    }

    static public GLResourceManager sGlResourceManager = null;

    private List<IGLResource> mResources = new CopyOnWriteArrayList<IGLResource>();
    
    private List<Long> mResourceAliveTimes = new ArrayList<Long>();
    private List<Long> mResourceCreateTimes = new ArrayList<Long>();

    static public GLResourceManager getInstance() {
        if (sGlResourceManager == null) {
            sGlResourceManager = new GLResourceManager();
        }
        return sGlResourceManager;
    }

    private GLResourceManager() {
    }

    @Override
    public boolean enableThreadCheck() {
        return GLConfigure.getInstance().enableGLThreadCheck();
    }

    public boolean enableUnloadWhenInvaislbe() {
        return true;
    }
    
    public void ResoureLoad(IGLResource rs, long create_time) {
        if (mResources.contains(rs)) {
            throw new RuntimeException("the resource has loaded! [" + rs.getLogMessage() + "]");
        }

        mResources.add(rs);
        
        if (GLConfigure.getInstance().enableGLResourceLog()) {
            mResourceCreateTimes.add(create_time);
            
            mResourceAliveTimes.add(System.currentTimeMillis());
            
            StringBuilder b = new StringBuilder();
            b.append("Create Time: ");
            b.append(create_time/1000.0f);
            b.append("*0.001ms Loaded: ");
            b.append(rs.getLogMessage());
            b.append(" | ");
            b.append(rs.getDetailLogMessage());
            b.append("| All Size:");
            b.append(mResources.size());
            
            Log.v(getClass().getSimpleName(), b.toString());
        }
    }

    public void ResoureUnLoad(IGLResource rs) {
        if (!mResources.contains(rs)) {
            throw new RuntimeException("the resource has loaded! [" + rs.getLogMessage() + "]");
        }
        
        int index = mResources.indexOf(rs);
        
        mResources.remove(index);
        
        if (GLConfigure.getInstance().enableGLResourceLog()) {
            long alive_time = System.currentTimeMillis() - mResourceAliveTimes.get(index);
            
            if(enableUnloadWhenInvaislbe()) {
                mResourceCreateTimes.remove(index);
                mResourceAliveTimes.remove(index);
            }
            
            StringBuilder b = new StringBuilder();
            b.append("Alive Time: ");
            b.append(alive_time);
            b.append("(ms) UnLoad: ");
            b.append(rs.getLogMessage());
            b.append(" | ");
            b.append(rs.getDetailLogMessage());
            b.append("| Left:");
            b.append(index);
            b.append("/");
            b.append(mResources.size());
            
            Log.v(getClass().getSimpleName(), b.toString());
        }
    }
    
    public void CleanUp() {
        ClearRemains();
        CheckResource();
    }
    
    private void ClearRemains() {
        
        for(IGLResource rs : mResources) {
            rs.unload();
        }
    }
    
    private void CheckResource() {
        if (!GLConfigure.getInstance().enableGLResourceLog())
            return;
        
        if (mResources.isEmpty()) {
            Log.v(getClass().getSimpleName(), "All Unloaded!");
        } else {
            StringBuilder b = new StringBuilder();
            b.append("GLResource Leak!: (");
            b.append(mResources.size());
            b.append(")\n");
            for(IGLResource rs : mResources) {
                b.append(rs.getLogMessage());
                b.append("\n");
            }
            
            throw new RuntimeException(b.toString());
        }
    }
}
