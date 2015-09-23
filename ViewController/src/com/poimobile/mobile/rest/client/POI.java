package com.poimobile.mobile.rest.client;

public final class POI {
    private int x;
    private int y;
    private String name;

    public POI(final String _name, final int _x, final int _y) {
        name = _name;
        x = _x;
        y = _y;
    }

    public void setX(final int _x) {
        this.x = _x;
    }

    public int getX() {
        return x;
    }

    public void setY(final int _y) {
        this.y = _y;
    }

    public int getY() {
        return y;
    }

    public void setName(final String _name) {
        this.name = _name;
    }

    public String getName() {
        return name;
    }
}
