package com.ustudents.farmland.component;

import com.ustudents.engine.core.event.EventData;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.scene.component.core.BehaviourComponent;
import com.ustudents.farmland.Farmland;

public class TurnTimerComponent extends BehaviourComponent {
    public static class SecondElapsed extends EventData {
        public int numberOfSecondElapsed;

        public SecondElapsed(int numberOfSecondElapsed) {
            this.numberOfSecondElapsed = numberOfSecondElapsed;
        }
    }

    public float time;

    public float timePerTurn;

    public EventDispatcher secondElapsed = new EventDispatcher(SecondElapsed.class);

    public TurnTimerComponent(float timePerTurn) {
        this.timePerTurn = timePerTurn;
    }

    @Override
    public void initialize() {
        time = Farmland.get().currentSave.turnTimePassed;

        Farmland.get().currentSave.turnEnded.add((dataType, data) -> {
            time = 0;
            setTimeElapsed(0);
        });
    }

    @Override
    public void update(float dt) {
        time += dt;

        if (time >= timePerTurn) {
            time = 0;

            if (Farmland.get().currentSave != null) {
                Farmland.get().currentSave.endTurn();
            }

            setTimeElapsed(0);
        } else if (Farmland.get().currentSave != null && time >= Farmland.get().currentSave.turnTimePassed + 1) {
            setTimeElapsed(Farmland.get().currentSave.turnTimePassed + 1);
        }
    }

    public void setTime(float time) {
        this.time = time;
    }

    public void setTimePerTurn(float timePerTurn) {
        this.timePerTurn = timePerTurn;
    }

    private void setTimeElapsed(int seconds) {
        Farmland.get().currentSave.turnTimePassed = seconds;
        secondElapsed.dispatch(new SecondElapsed(Farmland.get().currentSave.turnTimePassed));
    }
}
