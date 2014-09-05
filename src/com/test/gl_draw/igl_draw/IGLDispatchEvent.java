
package com.test.gl_draw.igl_draw;

public interface IGLDispatchEvent {
    void doGLTask(Runnable task);

    void doUITask(Runnable task);
}
