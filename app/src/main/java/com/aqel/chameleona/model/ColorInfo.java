package com.aqel.chameleona.model;

import android.graphics.Color;

import java.util.ArrayList;

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


    public static ArrayList<ColorInfo> getListOfColors(){
        ArrayList<ColorInfo> colorsList = new ArrayList<>();
        colorsList.add(new ColorInfo(Color.parseColor("#751D17"), "Dark Red"));
        colorsList.add(new ColorInfo(Color.parseColor("#FC1303"), "Red"));
        colorsList.add(new ColorInfo(Color.parseColor("#E63A2E"), "Meh Red"));
        colorsList.add(new ColorInfo(Color.parseColor("#D65249"), "Light Red"));
        colorsList.add(new ColorInfo(Color.parseColor("#804C14"), "Brown"));
        colorsList.add(new ColorInfo(Color.parseColor("#F28502"), "Bright Orange"));
        colorsList.add(new ColorInfo(Color.parseColor("#D1872E"), "Orange"));
        colorsList.add(new ColorInfo(Color.parseColor("#D6A365"), "Light Orange"));
        colorsList.add(new ColorInfo(Color.parseColor("#A39A8B"), "Paige"));
        colorsList.add(new ColorInfo(Color.parseColor("#36DFEB"), "Cyan"));
        colorsList.add(new ColorInfo(Color.parseColor("#238A91"), "Dark Cyan"));
        colorsList.add(new ColorInfo(Color.parseColor("#54BFC7"), "Light Cyan"));
        colorsList.add(new ColorInfo(Color.parseColor("#1473E0"), "Blue"));
        colorsList.add(new ColorInfo(Color.parseColor("#083A73"), "Navy"));
        colorsList.add(new ColorInfo(Color.parseColor("#505D63"), "Grey"));
        colorsList.add(new ColorInfo(Color.parseColor("#687175"), "Light Grey"));
        colorsList.add(new ColorInfo(Color.parseColor("#63828F"), "Cyan Grey"));
        colorsList.add(new ColorInfo(Color.parseColor("#D4D6D6"), "White"));
        colorsList.add(new ColorInfo(Color.parseColor("#000000"), "Black"));

        return colorsList;
    }
}
