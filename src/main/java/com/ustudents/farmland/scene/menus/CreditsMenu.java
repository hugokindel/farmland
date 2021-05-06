package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.graphic.Anchor;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Origin;
import com.ustudents.engine.gui.GuiBuilder;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import org.joml.Vector2f;

public class CreditsMenu extends MenuScene {
    @Override
    public void initialize() {
        String[] buttonNames = {};
        String[] buttonIds = {};
        EventListener[] eventListeners = new EventListener[buttonNames.length];
        GuiBuilder guiBuilder = new GuiBuilder();

        printListOfDev(guiBuilder);
        displayDevMessage(guiBuilder);
        canGoBackButton(guiBuilder);

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, false);

        super.initialize();
    }

    private String makeSomeLineBreak(int n){
        StringBuilder res = new StringBuilder();
        for(int i = 0; i<n; i++){
            res.append("\n");
        }
        return res.toString();
    }
    private String makeSomeSpace(int n){
        StringBuilder res = new StringBuilder();
        for(int i = 0; i<n; i++){
            res.append(" ");
        }
        return res.toString();
    }

    private void printListOfDev(GuiBuilder guiBuilder){
        GuiBuilder.WindowData windowData = new GuiBuilder.WindowData();
        windowData.origin = new Origin(Origin.Vertical.Middle, Origin.Horizontal.Center);
        windowData.anchor = new Anchor(Anchor.Vertical.Middle, Anchor.Horizontal.Center);
        windowData.position =  new Vector2f(0, -10);
        guiBuilder.beginWindow(windowData);

        // Happens within the window.
        {
            GuiBuilder.TextData textData = new GuiBuilder.TextData(makeSomeSpace(11) + Resources.getLocalizedText("developers") + makeSomeSpace(10)+ makeSomeLineBreak(2 ) + makeSomeSpace(3) +"- Kindel Hugo" + makeSomeLineBreak(2 ) + makeSomeSpace(3) +"- Le Corre Léo" +  makeSomeLineBreak(2 ) + makeSomeSpace(3) + "- Jauroyon Maxime" + makeSomeLineBreak(2 ) + makeSomeSpace(3) +"- Paulas Victor Francis" + makeSomeLineBreak(100) + " "/*"Développeurs:\n\nKINDEL Hugo\nLE CORRE Léo\nJAUROYON Maxime\nPAULAS VICTOR Francis\n\n"*/);
            textData.id = "stateLabel";
            textData.origin = new Origin(Origin.Vertical.Middle, Origin.Horizontal.Center);
            textData.anchor = new Anchor(Anchor.Vertical.Middle, Anchor.Horizontal.Center);
            textData.color = Color.BLACK;
            guiBuilder.addText(textData);
        }

        guiBuilder.endWindow();
    }

    private void displayDevMessage(GuiBuilder guiBuilder){
        GuiBuilder.TextData textData = new GuiBuilder.TextData(Resources.getLocalizedText("thankForPlaying"));
        textData.id = "stateLabel";
        textData.origin = new Origin(Origin.Vertical.Middle, Origin.Horizontal.Center);
        textData.anchor = new Anchor(Anchor.Vertical.Middle, Anchor.Horizontal.Center);
        textData.color = Color.BLACK;
        textData.position = new Vector2f(0, 150);
        guiBuilder.addText(textData);
    }

    private void canGoBackButton(GuiBuilder guiBuilder){
        GuiBuilder.ButtonData goBack = new GuiBuilder.ButtonData(Resources.getLocalizedText("return"), (dataType, data) -> {
            Farmland.get().reloadCustomizableCommands();
            SceneManager.get().goBack();
        });
        goBack.id = "goBack";
        goBack.origin = new Origin(Origin.Vertical.Bottom, Origin.Horizontal.Center);
        goBack.anchor = new Anchor(Anchor.Vertical.Bottom, Anchor.Horizontal.Center);
        goBack.position = new Vector2f(0, -75); // x = 0
        guiBuilder.addButton(goBack);
    }


}
