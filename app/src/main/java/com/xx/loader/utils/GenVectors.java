package com.xx.loader.utils;

import java.io.IOException;

public class GenVectors {
	
	public static void getTrainVectors(String[] files, int width, int num, String outFile) {
		for(int i=0; i< files.length;i++) {
			float[][] array = FileUtils.toFloatArrayByFile(files[i]);
			double[][] filted = CalUtils.getLowPass(array);			
			for(int j=0; j<num; j++) {
				double[][] record = FileUtils.toRecordByWindowAfterFilter(filted, width);
				float[] vector = CalUtils.getVector(record);
				String str = CalUtils.getVectorString(vector, i);
				System.out.println(str);
				try {
					FileUtils.writeFile(outFile, str);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
