package com.xx.loader.utils;

import java.text.DecimalFormat;

import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignExstrom;

public class CalUtils {
	private static DecimalFormat FORMAT=new DecimalFormat(".00");

	// 向量字符串
	public static String getVectorString(float[] vector, int label) {
		StringBuilder sb = new StringBuilder();
		sb.append(label);
		sb.append(' ');
		for(int i=0; i<vector.length; i++) {
			sb.append(i+1);
			sb.append(':');
			sb.append(vector[i]);
			sb.append(' ');
		}
		sb.subSequence(0, sb.length()-1);
		sb.append('\n');
		return sb.toString();
	}
	
	
	// 计算特征向量
	public static float[] getVector(double[][] record) {
		float[] vector = new float[8];
		//合加速度标准差
		vector[0] = convertFloat2(Mutil.standardDeviation(record[3]));
		//合加速度偏度
		vector[1] = convertFloat2(Mutil.skewness(record[3])); 
		//合加速度四分位距
		vector[2] = convertFloat2(Mutil.fourDivsion(record[3])); 
		//合加速度均值
		vector[3] = convertFloat2(Mutil.mean(record[3])); 
		//合加速度峰度
		vector[4] = convertFloat2(Mutil.kurtosis(record[3])); 
		//XY相关系数
		vector[5] = convertFloat2(Mutil.correlation(record[0], record[1])); 
		//YZ相关系数
		vector[6] = convertFloat2(Mutil.correlation(record[1], record[2])); 
		//XZ相关系数
		vector[7] = convertFloat2(Mutil.correlation(record[0], record[2])); 
		return vector;
	}
	
	public static double[] IIRFilter(double[] signal, double[] a, double[] b) {
		double[] in = new double[b.length];
		double[] out = new double[a.length-1];
		double[] outData = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            System.arraycopy(in, 0, in, 1, in.length - 1);
            in[0] = signal[i];
            //calculate y based on a and b coefficients
            //and in and out.
            float y = 0;
            for(int j = 0 ; j < b.length ; j++){
                y += b[j] * in[j];
            }
            for(int j = 0;j < a.length-1;j++){
                y -= a[j+1] * out[j];
            }

            //shift the out array
            System.arraycopy(out, 0, out, 1, out.length - 1);
            out[0] = y;
            outData[i] = y;
        }
        return outData;
    }
	
	// 低通滤波
	public static double[][] getLowPass(float[][] list){	
		 IirFilterCoefficients iirFilterCoefficients;
//		 iirFilterCoefficients = IirFilterDesignExstrom.design(FilterPassType.lowpass, 8, 10.0/50, 10.0/50);
		 iirFilterCoefficients = IirFilterDesignExstrom.design(FilterPassType.lowpass, 8, 0.1, 13.0/50);
		 double[][] result = new double[4][list.length];
		 for(int j=0; j<4; j++) {
			 double[] data = new double[list.length];
			 for(int i=0; i<list.length; i++) {
				 data[i] = list[i][j];
			 }
			 double[] filted = IIRFilter(data, iirFilterCoefficients.a, iirFilterCoefficients.b);
			 result[j] = filted;
		 }
		return result; 
		 
//		 String filePath = "E:\\eclipse_projects\\svmlib\\trainfiles\\filted.txt";
//		 File file = new File(filePath);
//		 FileWriter writer = null;
//		 if (!file.exists()) {	
//			 try {
//				 file.createNewFile();// ����Ŀ���ļ�
//				 writer = new FileWriter(file, true);
//				 for(int i=0; i<list.length; i++) {
//					 System.out.println(list[i][0]+":"+filted[i]);
//					 writer.append(list[i][0]+"\t"+filted[i]+"\n");
//				     writer.flush();
//				 }
//			} catch (Exception e) {
//				
//			}finally {
//				if (null != writer)
//		            writer.close();
//			} 
//	     }
	}

	//
	public static float getSTD(float[] array) {
		float sum = 0;
		for(int i=0;i<array.length;i++){
		    sum += array[i];      //
		}
		float average = sum/array.length;  //
		float total=0;
		for(int i=0;i<array.length;i++){
		    total += (array[i]-average)*(array[i]-average);   //
		}
		float standardDeviation = (float)Math.sqrt(total/array.length);  
		return convertFloat2(standardDeviation);
	}
	
	public static float convertFloat2(double value) {
		String p = FORMAT.format(value);
		return Float.parseFloat(p);
	}
}
