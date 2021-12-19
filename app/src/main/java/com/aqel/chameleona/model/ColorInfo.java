package com.aqel.chameleona.model;

public class ColorInfo {
    private Integer color;
    private String name;

    public ColorInfo(Integer color, String name) {
        this.color = color;
        this.name = name;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
