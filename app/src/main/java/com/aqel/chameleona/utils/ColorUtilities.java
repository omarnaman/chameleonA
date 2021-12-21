package com.aqel.chameleona.utils;

public class ColorUtilities {
    public static float getColorBrightness(int color) {
       int r = (color >> 16) & 0xff;
       int g = (color >> 8)  & 0xff;
       int b = (color) & 0xff;
       float luma = 0.2126f * r + 0.7152f * g + 0.0722f * b;
       return luma;
    }
}
