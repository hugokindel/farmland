package com.ustudents.farmland.component;

import com.ustudents.farmland.scene.GameMenu;

public class Goal {

    public Goal(){}

    public boolean checkIfPlayerWin(){
        return GameMenu.getCurrentPlayerTurn().getCurrentMoney()>=10000;
    }

    public boolean checkIfPlayerLoose(){
        return GameMenu.getCurrentPlayerTurn().getCurrentMoney()<0;
    }
}
