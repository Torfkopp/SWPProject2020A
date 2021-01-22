package de.uol.swp.common.game;

import javafx.scene.canvas.GraphicsContext;

public interface Renderable {
    public void render(int x, int y, int size, GraphicsContext map);
}
