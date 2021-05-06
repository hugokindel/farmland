package com.ustudents.engine.audio.empty;

import java.util.HashSet;
import java.util.Set;

public class EmptySoundManager {
    protected long device;

    protected long context;

    protected Set<EmptySoundSource> sources;

    public EmptySoundManager() {
        sources = new HashSet<>();
    }

    public boolean initialize() {
        return true;
    }

    public void destroy() {
        removeAll();
    }

    public void play(EmptySoundSource source) {
        sources.add(source);
    }

    public void stopAll() {
        for (EmptySoundSource source : sources) {
            source.stop();
        }
    }

    public void removeAll() {
        for (EmptySoundSource source : sources) {
            source.destroy();
        }

        sources.clear();
    }
}