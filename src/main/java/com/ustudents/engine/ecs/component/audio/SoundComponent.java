package com.ustudents.engine.ecs.component.audio;

import com.ustudents.engine.audio.Sound;
import com.ustudents.engine.audio.SoundSource;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import imgui.ImGui;

/** Component for a sound/music to play. */
@Viewable
public class SoundComponent extends Component {
    /** The sound source (audio to play). */
    @Viewable
    public SoundSource source;

    /**
     * Class constructor.
     *
     * @param sound The sound source.
     */
    public SoundComponent(Sound sound) {
        this(sound, false, false, false);
    }

    /**
     * Class constructor.
     *
     * @param sound The sound soure.
     * @param play If we should play the sound directly.
     */
    public SoundComponent(Sound sound, Boolean play) {
        this(sound, play, false, false);
    }

    /**
     * Class constructor.
     *
     * @param sound The sound source.
     * @param play If we should play the sound directly.
     * @param loop If we should loop the sound.
     */
    public SoundComponent(Sound sound, Boolean play, Boolean loop) {
        this(sound, play, loop, false);
    }

    /**
     * Class constructor.
     *
     * @param sound The sound source.
     * @param play If we should play the sound directly.
     * @param loop If we should loop the sound.
     * @param relative If we should define this song as relative.
     */
    public SoundComponent(Sound sound, Boolean play, Boolean loop, Boolean relative) {
        source = new SoundSource(sound, loop, relative);

        if (play) {
            source.play();
        }
    }

    @Override
    public void destroy() {
        source.stop();
        source.destroy();
    }

    /** Plays the sound. */
    public void play() {
        if (source != null) {
            source.play();
        }
    }

    /** Pauses the sound. */
    public void pause() {
        if (source != null) {
            source.pause();
        }
    }

    /** Stops the sound. */
    public void stop() {
        if (source != null) {
            source.stop();
        }
    }

    /** Renders the component (ImGui exclusive). */
    @Override
    public void renderImGui() {
        if (source.isPlaying()) {
            if (ImGui.button("pause")) {
                source.pause();
            }

            if (ImGui.button("stop")) {
                source.stop();
            }
        } else if (source.isPaused()) {
            if (ImGui.button("play")) {
                source.play();
            }

            if (ImGui.button("stop")) {
                source.stop();
            }
        } else if (source.isStopped()) {
            if (ImGui.button("play")) {
                source.play();
            }
        }
    }

    /**
     * Sets the sound source.
     *
     * @param source The new sound source.
     */
    public void setSource(SoundSource source) {
        this.source = source;
    }
}
