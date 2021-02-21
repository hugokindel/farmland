package com.ustudents.engine.graphic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class SpriteAnimation {
    public static class Frame {
        Sprite sprite;
        Float duration;

        public Frame(Sprite sprite, Float duration) {
            this.sprite = sprite;
            this.duration = duration;
        }

        public Sprite getSprite() {
            return sprite;
        }

        public Float getDuration() {
            return duration;
        }
    }

    Boolean loop = false;

    List<Frame> frames = new ArrayList<>();

    public void addFrame(Frame frame) {
        frames.add(frame);
    }

    public static SpriteAnimation deserialize(Spritesheet spritesheet, Map<String, Object> json) {
        SpriteAnimation animation = new SpriteAnimation();

        if (json.containsKey("loop")) {
            animation.loop = (Boolean)json.get("loop");
        }

        for (Object frameElement : (List<Object>)json.get("frames")) {
            Map<String, Object> frameJson = (Map<String, Object>)frameElement;

            Frame frame = new Frame(
                    spritesheet.getSprite((String)frameJson.get("sprite")),
                    frameJson.containsKey("duration") ? ((Double)frameJson.get("duration")).floatValue() : 0
            );

            animation.frames.add(frame);
        }

        return animation;
    }

    public Boolean isLoopEnabled() {
        return loop;
    }

    public List<Frame> getFrames() {
        return frames;
    }
}
