package com.ustudents.farmland.component;

import com.ustudents.farmland.scene.InGameScene;

public class GoalComponent {

    public GoalComponent(){}

    public boolean checkIfPlayerWin(){
        return InGameScene.getCurrentPlayerTurn().getCurrentMoney()>=10000;
    }

    public boolean checkIfPlayerLoose(){
        return InGameScene.getCurrentPlayerTurn().getCurrentMoney()<0;
    }
}
