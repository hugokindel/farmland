package com.ustudents.engine.audio.empty;

import static com.ustudents.engine.core.Resources.getSoundsDirectoryName;

public class EmptySound {
    protected int handle;

    protected boolean isDestroyed;

    protected String path;

    public EmptySound(String filePath) {
        this.path = filePath.replace(getSoundsDirectoryName() + "/", "");
        this.handle = -1;
    }

    public void destroy() {
        if (!isDestroyed) {
            isDestroyed = true;
        }
    }

    public int getHandle() {
        return this.handle;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public String getPath() {
        return path;
    }
}
