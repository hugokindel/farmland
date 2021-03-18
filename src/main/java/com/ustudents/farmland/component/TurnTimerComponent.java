package com.ustudents.farmland.component;

import com.ustudents.engine.core.event.Event;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.scene.component.core.BehaviourComponent;
import com.ustudents.farmland.Farmland;

public class TurnTimerComponent extends BehaviourComponent {
    public static class SecondElapsed extends Event {
        public int numberOfSecondElapsed;

        public SecondElapsed(int numberOfSecondElapsed) {
            this.numberOfSecondElapsed = numberOfSecondElapsed;
        }
    }

    public float time;

    public float timePerTurn;

    public float skipturn;

    public EventDispatcher secondElapsed = new EventDispatcher(SecondElapsed.class);

    public TurnTimerComponent(float timePerTurn) {
        this.timePerTurn = timePerTurn;
    }

    @Override
    public void initialize() {
        time = Farmland.get().getCurrentSave().turnTimePassed;
        skipturn = 0;

        Farmland.get().getCurrentSave().turnEnded.add((dataType, data) -> {
            time = 0;
            skipturn = 0;
            setTimeElapsed(0);
        });
    }

    @Override
    public void update(float dt) {
        time += dt;
        skipturn += dt;

        if (skipturn >= 1 && Farmland.get().getCurrentSave() != null && Farmland.get().getCurrentSave().getCurrentPlayer().name.contains("Robot")) {
            skipturn = 0;
            Farmland.get().getCurrentSave().endTurn();
        }

        if (time >= timePerTurn) {
            time = 0;
            skipturn = 0;

            if (Farmland.get().getCurrentSave() != null) {
                Farmland.get().getCurrentSave().endTurn();
            }

            setTimeElapsed(0);
        } else if (Farmland.get().getCurrentSave() != null && time >= Farmland.get().getCurrentSave().turnTimePassed + 1) {
            setTimeElapsed(Farmland.get().getCurrentSave().turnTimePassed + 1);
        }
    }

    public void setTime(float time) {
        this.time = time;
    }

    public void setTimePerTurn(float timePerTurn) {
        this.timePerTurn = timePerTurn;
    }

    private void setTimeElapsed(int seconds) {
        Farmland.get().getCurrentSave().turnTimePassed = seconds;
        secondElapsed.dispatch(new SecondElapsed(Farmland.get().getCurrentSave().turnTimePassed));
    }
}
