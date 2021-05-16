package com.ustudents.engine.tools;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.graphic.Font;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.scene.Scene;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DebugTools {
    private boolean statisticsEnabled = true;

    private boolean gridEnabled = true;

    private boolean textBoxEnabled = true;

    private Font debugFont;

    public void initialize() {
        debugFont = Resources.getFont("ui/debug.ttf", 16);
    }

    public void render() {
        if (statisticsEnabled || gridEnabled) {
            Scene scene = Game.get().getSceneManager().getCurrentScene();
            Spritebatch spritebatch = scene.getSpritebatch();

            spritebatch.begin(scene.getUiCamera());

            if (statisticsEnabled) {
                int fps = Game.get().getTimer().getFPS();
                double ms = BigDecimal.valueOf(Game.get().getTimer().getFrameDuration())
                        .setScale(3, RoundingMode.HALF_UP).doubleValue();
                int numEntities = scene.getRegistry().getTotalNumberOfEntities();

                spritebatch.drawText(new Spritebatch.TextData(
                        "FPS: " + fps + "\nFramerate: " + ms + "\nNumber of entities: " + numEntities,
                        debugFont,
                        new Vector2f(10.0f, 10.0f)
                ) {{
                    scale = new Vector2f(2.0f, 2.0f);
                }});
            }

            if (gridEnabled) {
                Vector2i windowSize = Game.get().getWindow().getSize();

                spritebatch.drawLine(new Spritebatch.LineData(
                        new Vector2f(0, (float)windowSize.y / 2),
                        new Vector2f(windowSize.x, (float)windowSize.y / 2)
                ));

                spritebatch.drawLine(new Spritebatch.LineData(
                        new Vector2f((float)windowSize.x / 2, 0),
                        new Vector2f((float)windowSize.x / 2, windowSize.y)
                ));
            }

            spritebatch.end();
        }

    }

    public boolean isStatisticsEnabled() {
        return statisticsEnabled;
    }

    public void setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
    }

    public boolean isGridEnabled() {
        return gridEnabled;
    }

    public void setGridEnabled(boolean gridEnabled) {
        this.gridEnabled = gridEnabled;
    }

    public boolean isTextBoxEnabled() {
        return textBoxEnabled;
    }

    public void setTextBoxEnabled(boolean textBoxEnabled) {
        this.textBoxEnabled = textBoxEnabled;
    }

    public Font getDebugFont() {
        return debugFont;
    }

    public void setDebugFont(Font debugFont) {
        this.debugFont = debugFont;
    }
}
