package com.ustudents.farmland.component;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.event.Event;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.ecs.component.core.BehaviourComponent;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.player.Bot;
import com.ustudents.farmland.core.player.Player;
import com.ustudents.farmland.scene.InGameScene;

@SuppressWarnings("unchecked")
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

    public boolean skipmadepart1;

    public EventDispatcher secondElapsed = new EventDispatcher(SecondElapsed.class);

    public TurnTimerComponent(float timePerTurn) {
        this.timePerTurn = timePerTurn;
    }

    @Override
    public void initialize() {
        time = Farmland.get().getLoadedSave().turnTimePassed;
        skipturn = 0;
        skipmadepart1 = false;

        Farmland.get().getLoadedSave().turnEnded.add((dataType, data) -> {
            onTurnEnded();
        });
    }

    @Override
    public void update(float dt) {
        if (((InGameScene)Game.get().getSceneManager().getCurrentScene()).inPause) {
            return;
        }

        time += dt;
        skipturn += dt;

        if (Game.get().hasAuthority()) {
            if (skipturn >= 1f && !skipmadepart1 && Farmland.get().getLoadedSave() != null && Farmland.get().getLoadedSave().getCurrentPlayer().type == Player.Type.Robot) {
                Bot.playTurn();
                skipmadepart1 = true;
            }

            if (skipturn >= 2f && skipmadepart1 && Farmland.get().getLoadedSave() != null && Farmland.get().getLoadedSave().getCurrentPlayer().type == Player.Type.Robot) {
                Farmland.get().getLoadedSave().endTurn();
            }
        }

        if (time >= timePerTurn) {
            if (Game.get().hasAuthority() && Farmland.get().getLoadedSave() != null) {
                Farmland.get().getLoadedSave().endTurn();
            }

            setTimeElapsed(0);
        } else if (Farmland.get().getLoadedSave() != null && time >= Farmland.get().getLoadedSave().turnTimePassed + 1) {
            setTimeElapsed(Farmland.get().getLoadedSave().turnTimePassed + 1);
        }
    }

    public void setTime(float time) {
        this.time = time;
    }

    public void setTimePerTurn(float timePerTurn) {
        this.timePerTurn = timePerTurn;
    }

    public void onTurnEnded() {
        time = 0;
        skipturn = 0;
        skipmadepart1 = false;
        setTimeElapsed(0);
    }

    private void setTimeElapsed(int seconds) {
        if(Farmland.get().getLoadedSave() == null)
            return;
        Farmland.get().getLoadedSave().turnTimePassed = seconds;
        secondElapsed.dispatch(new SecondElapsed(Farmland.get().getLoadedSave().turnTimePassed));
    }
}
