package com.xx.loader.utils;

public class DataConvertsUtils {
	
    // 将字符串分割成float数组
	public static float[] parseFloatArray(String[] str_array) {
        float[] flo_array = null;
        if (str_array != null) {
            flo_array = new float[str_array.length];
            for (int i = 0; i < str_array.length; i++) {
                try {
                    flo_array[i] = Float.parseFloat(str_array[i]);
                } catch(NumberFormatException e) {
                    System.out.println(e.getMessage());
                    continue;
                }
            }
        }
        return flo_array;
    }
}
