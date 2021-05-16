package com.ustudents.farmland.scene.menus;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.farmland.Farmland;
import com.ustudents.farmland.core.Save;
import com.ustudents.farmland.network.general.GameInformationsRequest;
import com.ustudents.farmland.network.general.GameInformationsResponse;
import com.ustudents.farmland.scene.InGameScene;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDataType;
import imgui.type.ImLong;
import imgui.type.ImString;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class ServerCustomMenu extends MenuScene {
    ImString address = new ImString();
    List<String> errors = new ArrayList<>();

    @Override
    public void initialize() {
        forceImGui = true;

        String[] buttonNames = {};
        String[] buttonIds = {};
        EventListener[] eventListeners = new EventListener[buttonNames.length];

        for (int i = 0; i < buttonNames.length; i++) {
            int j = i;
            eventListeners[i] = (dataType, data) -> {
                switch (buttonIds[j]) {

                }
            };
        }

        initializeMenu(buttonNames, buttonIds, eventListeners, true, false, false, true);

        super.initialize();
    }

    @Override
    public void renderImGui() {
        ImGuiUtils.setNextWindowWithSizeCentered(550, 300, ImGuiCond.Appearing);
        ImGui.begin(Resources.getLocalizedText("researchServer"));

        ImGui.text(Resources.getLocalizedText("enterIp"));
        ImGui.pushItemWidth(-1);
        ImGui.inputText("##label", address);
        ImGui.popItemWidth();
        ImGui.text(Resources.getLocalizedText("exampleServer"));

        if (ImGui.button(Resources.getLocalizedText("return"))) {
            SceneManager.get().goBack();
        }

        ImGui.sameLine();

        if (ImGui.button(Resources.getLocalizedText("joinServer"))) {
            errors.clear();

            if (address.isEmpty()) {
                errors.add(Resources.getLocalizedText("correctInfo"));
            }

            if (!address.isEmpty() && address.get().split(":").length != 2) {
                errors.add(Resources.getLocalizedText("correctFormat"));
            }

            if (errors.isEmpty()) {
                if (!Farmland.get().getClient().isAlive()) {
                    String[] rAddress = address.get().split(":");
                    Farmland.get().clientServerIp = rAddress[0];
                    Farmland.get().clientServerPort = Integer.parseInt(rAddress[1]);
                    Farmland.get().getClient().start(Farmland.get().clientServerIp, Farmland.get().clientServerPort);
                }

                boolean serverExists = Farmland.get().getClient().isAlive();

                if (serverExists) {
                    GameInformationsResponse informations = Farmland.get().getClient().request(new GameInformationsRequest(), GameInformationsResponse.class);

                    if (informations.getNumberOfConnectedPlayers() < informations.getCapacity()) {
                        SceneManager.get().popTypeOfLastScene();

                        changeScene(new ServerWaitingRoomMenu(), false);
                    } else {
                        Farmland.get().clientServerIp = null;
                        Farmland.get().clientServerPort = 0;
                        errors.add(Resources.getLocalizedText("serverFull"));
                    }
                } else {
                    Farmland.get().clientServerIp = null;
                    Farmland.get().clientServerPort = 0;
                    errors.add(Resources.getLocalizedText("serverNotOk"));
                }
            }
        }

        for (String error : errors) {
            ImGui.text(error);
        }

        ImGui.end();
    }
}
