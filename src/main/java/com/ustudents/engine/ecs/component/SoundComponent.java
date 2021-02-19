package com.ustudents.engine.ecs.component;

import com.ustudents.engine.audio.Sound;
import com.ustudents.engine.audio.SoundSource;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import imgui.ImGui;

@Editable
@JsonSerializable
public class SoundComponent extends Component {
    @Editable
    @JsonSerializable
    public SoundSource source;

    public SoundComponent() {
        this(null, false, false, false);
    }

    public SoundComponent(Sound sound) {
        this(sound, false, false, false);
    }

    public SoundComponent(Sound sound, Boolean play) {
        this(sound, play, false, false);
    }

    public SoundComponent(Sound sound, Boolean play, Boolean loop) {
        this(sound, play, loop, false);
    }

    public SoundComponent(Sound sound, Boolean play, Boolean loop, Boolean relative) {
        source = new SoundSource(sound, loop, relative);

        if (play) {
            source.play();
        }
    }

    public void play() {
        if (source != null) {
            source.play();
        }
    }

    public void pause() {
        if (source != null) {
            source.pause();
        }
    }

    public void stop() {
        if (source != null) {
            source.stop();
        }
    }

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
}
