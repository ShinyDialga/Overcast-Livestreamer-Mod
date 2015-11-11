package com.shinycraft.streamermod.renderer;

import net.minecraft.client.gui.Gui;

/**
 * Created by ShinyDialga45 on 11/7/2015.
 */
public class ScreenDisplay {

    private int leftX = 0;
    private int leftY = 0;
    private int rightX = 0;
    private int rightY = 0;

    public ScreenDisplay() {

    }

    public int getLeftX() {
        return leftX;
    }

    public void setLeftX(int leftX) {
        this.leftX = leftX;
    }

    public int getLeftY() {
        return leftY;
    }

    public void setLeftY(int leftY) {
        this.leftY = leftY;
    }

    public int getRightX() {
        return rightX;
    }

    public void setRightX(int rightX) {
        this.rightX = rightX;
    }

    public int getRightY() {
        return rightY;
    }

    public void setRightY(int rightY) {
        this.rightY = rightY;
    }

    public void drawText(String text, Side side, int x, int y) {
        if (side.equals(Side.RIGHT)) {
            setRightX(x);
        } else {
            setLeftX(x);

        }
    }

    public void drawScaledCustomSizeModalRect(Side side, int a, int b, float c, float d, int e, int f, int g, int h, float i, float j) {
        Gui.drawScaledCustomSizeModalRect(a, b, c, d, e, f, g, h, i, j);
    }

    public void clear() {
        setLeftX(0);
        setLeftY(0);
        setRightX(0);
        setRightY(0);
    }

    public enum Side {

        LEFT(),
        RIGHT();

        Side() {

        }

    }

}
