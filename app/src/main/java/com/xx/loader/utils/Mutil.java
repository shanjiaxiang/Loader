package com.xx.loader.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Mutil {
	/**
	 * 
	 *@描述: 四分位距 <br/>
	 *@方法名: fourDivsion <br/>
	 *@param param <br/>
	 *@return double<br/>
	 */
	 public static double fourDivsion(double[] param){
         if(param == null || param.length < 4) return 0.0;
         // 转成BigDecimal类型，避免失去精度
         BigDecimal[] datas = new BigDecimal[param.length];
         for(int i=0; i<param.length; i++){
             datas[i] = BigDecimal.valueOf(param[i]);
         }
         int len = datas.length;// 数组长度
         Arrays.sort(datas);    // 数组排序，从小到大
         BigDecimal q1 = null;  // 第一四分位
         BigDecimal q2 = null;  // 第二四分位
         BigDecimal q3 = null;  // 第三四分位
         int index = 0; // 记录下标
         // n代表项数，因为下标是从0开始所以这里理解为：len = n+1
         if(len%2 == 0){ // 偶数
             index = new BigDecimal(len).divide(new BigDecimal("4")).intValue();
             q1 = datas[index-1].multiply(new BigDecimal("0.25")).add(datas[index].multiply(new BigDecimal("0.75")));
             q2 = datas[len/2].add(datas[len/2-1]).divide(new BigDecimal("2"));
             index = new BigDecimal(3*(len+1)).divide(new BigDecimal("4")).intValue();
             q3 = datas[index-1].multiply(new BigDecimal("0.75")).add(datas[index].multiply(new BigDecimal("0.25")));
         }else{ // 奇数
             q1 = datas[new BigDecimal(len).multiply(new BigDecimal("0.25")).intValue()];
             q2 = datas[new BigDecimal(len).multiply(new BigDecimal("0.5")).intValue()];
             q3 = datas[new BigDecimal(len).multiply(new BigDecimal("0.75")).intValue()];
         }
         // 保留两位小数（四舍五入），输出到控制台
//         System.out.println("四分位距" + q1.setScale(2, BigDecimal.ROUND_HALF_UP)+" "+
//                 q2.setScale(2, BigDecimal.ROUND_HALF_UP)+" "+
//                 q3.setScale(2, BigDecimal.ROUND_HALF_UP));
         // 计算四分位距
         return q3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() - q1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
     }
	
	
	/**
	 * 
	 *@描述: 相关系数 <br/>
	 *@方法名: correlation <br/>
	 *@param x <br/>
	 *@param y <br/>
	 *@return  * @返回类型 double 返回值[-1,1]{[-1,0]负相关；[0,1]正相关}， 取绝对值，越大表示相关性越强,
	 *          .8~1： 非常强 ，.6~.8： 强相关 ，.4~.6： 中度相关，.2~.4： 弱相关，.0~.： 弱相关或者无关 <br/>
	 */
	public static double correlation(double[] x, double[] y) {
		if (x.length != y.length) {
			throw new NumberFormatException();
		}
		double xSum = 0;
		double ySum = 0;
		double xP2Sum = 0;
		double yP2Sum = 0;
		double xySum = 0;
		int len = x.length;
		for (int i = 0; i < y.length; i++) {
 
			xSum = Mutil.add(xSum, x[i]);
			ySum = Mutil.add(ySum, y[i]);
			xP2Sum = Mutil.add(xP2Sum, Math.pow(x[i], 2));
			yP2Sum = Mutil.add(yP2Sum, Math.pow(y[i], 2));
			xySum = Mutil.add(xySum, Mutil.multiply(x[i], y[i]));
 
		}
		double Rxy = Mutil.subtract(Mutil.multiply(len, xySum), Mutil.multiply(xSum, ySum)) / (Math.sqrt((Mutil.multiply(len, xP2Sum) - Math.pow(xSum, 2)) * (Mutil.multiply(len, yP2Sum) - Math.pow(ySum, 2))));
		return Mutil.round(Rxy, 2);
 
	}
	
	/**
	 * 
	 *  * @描述:图像:峰度 <br/>
	 *  * @方法名: kurtosis <br/>
	 *  * @param in <br/>
	 *  * @return <br/>
	 *  * @返回类型 double 用于计算一组数据是平滑还是陡峭，返回0=常峰或者正态，否则表示陡峭 <br/>
	 */
	public static double kurtosis(double[] in) {
		double mean = mean(in);
		double SD = standardDeviation(in);
		int n = in.length;
		double sum = 0;
		for (int i = 0; i < in.length; i++) {
			sum = Mutil.add(sum, Math.pow(Mutil.divide(Mutil.subtract(in[i], mean), SD, 2), 4));
		}
		return Mutil.round(Mutil.divide(sum, n, 2) - 3, 2);
	}
	
	/**
	 * 
	 *  * @描述:图像：偏度 <br/>
	 *  * @方法名: skewness <br/>
	 *  * @param in <br/>
	 *  * @return <br/>
	 *  * @返回类型 double 返回值小于零为负偏度，大于0为正偏度，等于0是正态分布 <br/>
	 *  * @创建人 micheal <br/>  
	 */
	public static double skewness(double[] in) {
		double mean = mean(in);
		double median = median(in);
		double SD = standardDeviation(in);
		return 3 * Mutil.divide(Mutil.subtract(mean, median), SD, 2);
	}
	
	
	/**
	 * 
	 *  * @描述:变异性量数：方差 <br/>
	 *  * @方法名: variance <br/>
	 *  * @param in <br/>
	 *  * @return <br/>
	 *  
	 */
	public static double variance(double[] in) {
		double t_mean = mean(in);
		double t_sumPerPow = 0;
		for (int i = 0; i < in.length; i++) {
			t_sumPerPow = Mutil.add(t_sumPerPow, Math.pow(Mutil.subtract(in[i], t_mean), 2));
		}
		return Mutil.divide(t_sumPerPow, (in.length - 1), 2);
	}
 
	/**
	 * 
	 *  * @描述:变异性量数：标准差（无偏估计） <br/>
	 *  * @方法名: standardDeviation <br/>
	 *  * @param in <br/>
	 *  * @return <br/>
	 *  * @返回类型 double <br/>
	 *  
	 */
	public static double standardDeviation(double[] in) {
		return Math.sqrt(variance(in));
	}
 
	/**
	 * 
	 *  * @描述:变异性量数：标准差（有偏估计） <br/>
	 *  * @方法名: SD <br/>
	 *  * @param in <br/>
	 *  * @return <br/>
	 *  * @返回类型 double <br/>
	 *  
	 */
	public static double standardDeviation2(double[] in) {
		double t_mean = mean(in);
		double t_sumPerPow = 0;
		for (int i = 0; i < in.length; i++) {
			t_sumPerPow = Mutil.add(t_sumPerPow, Math.pow(Mutil.subtract(in[i], t_mean), 2));
		}
		return Math.sqrt(Mutil.divide(t_sumPerPow, (in.length), 2));								
	}
	
	/**
	 * 
	 *  * @描述:集中趋势量数：极差（不包含） <br/>
	 *  * @方法名: range <br/>
	 *  * @param in <br/>
	 *  * @return <br/>
	 *  * @返回类型 double <br/>  
	 */
	public static double range(double[] in) {
		if (in == null) {
			throw new NumberFormatException();
		}
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		for (int i = 0; i < in.length; i++) {
			max = Math.max(max, in[i]);
			min = Math.min(min, in[i]);
		}
		// return max - min;
		return Mutil.subtract(max, min);
	}
 
	/**
	 * 
	 *  * @描述: 变异性量数：极差（包含） <br/>
	 *  * @方法名: range2 <br/>
	 *  * @param in <br/>
	 *  * @return <br/>
	 *  * @返回类型 double <br/>
	 */
	public static double range2(double[] in) {
		if (in == null) {
			throw new NumberFormatException();
		}
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		for (int i = 0; i < in.length; i++) {
			max = Math.max(max, in[i]);
			min = Math.min(min, in[i]);
		}
		// return max - min + 1;
		return Mutil.subtract(max, min) + 1;
	}
	
	/**
	 * 
	 *  * @描述:集中趋势量数：计算中位数 <br/>
	 *  * @方法名: median <br/>
	 *  * @param in <br/>
	 *  * @return <br/>
	 *  * @返回类型 double <br/
	 */
	public static double median(double[] in) {
		if (in == null) {
			throw new NumberFormatException();
		}
		Arrays.sort(in);
 
		// for (int i = 0; i < in.length; i++) {
		// log.debug("sort: "+i+":::"+in[i]);
		// }
		if (in.length % 2 == 1) {
			return in[(int) Math.floor(in.length / 2)];
		} else {
			double[] avg = new double[2];
			avg[0] = in[(int) Math.floor(in.length / 2) - 1];
			avg[1] = in[(int) Math.floor(in.length / 2)];
			return mean(avg);
 
		}
	}
 
	/**
	 * 
	 *  * @描述:集中趋势量数：计算众数 <br/>
	 *  * @方法名: mode <br/>
	 *  * @param in <br/>
	 *  * @return <br/>
	 *  * @返回类型 List <br/>
	 */
	public static List mode(double[] in) {
		HashMap map = new HashMap();
		double imode = 0;
		for (int i = 0; i < in.length; i++) {
			double x = in[i];
			if (map.containsKey(String.valueOf(x))) {
				// 如果出现多次，取出以前的计数，然后加1
				int len = Integer.parseInt(map.get(String.valueOf(x)).toString());
				map.put(String.valueOf(x), String.valueOf(len + 1));
				imode = Math.max(imode, len + 1);
			} else {
				// 如果是第一次出现，计数为1
				map.put(String.valueOf(x), String.valueOf(1));
				imode = Math.max(imode, 1);
			}
		}
		Iterator iter = map.keySet().iterator();
		ArrayList lst = new ArrayList();
		while (iter.hasNext()) {
			Object key = iter.next();
			Object v = map.get(key);
			if (Integer.parseInt(v.toString()) == imode) {
				lst.add(key);
			}
		}
		return lst;
	}
	

	/**
	 * 
	 *  * @描述:集中趋势量数：均值/算术平均数（arithmetic mean) <br/>
	 *  * @方法名: mean <br/>
	 *  * @param in <br/>
	 *  * @return <br/>
	 *  * @返回类型 double <br/>
	 *  
	 */
	public static double mean(double[] in) {
		if (in == null) {
			throw new NumberFormatException();
		}
		if (in.length == 1) {
			return in[0];
		}
		double sum = 0;
		for (int i = 0; i < in.length; i++) {
			sum = Mutil.add(sum, in[i]);
			// sum += in[i];
		}
		// return sum/in.length;
		return Mutil.divide(sum, in.length, 2);
	}
	
	
	
	/**
	 * 
	 *  * @描述: 加法 <br/>
	 *  * @方法名: add <br/>
	 *  * @param v1 <br/>
	 *  * @param v2 <br/>
	 *  * @return <br/>
	 *  * @返回类型 double <br/>
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}
 
	/**
	 * 
	 *  * @描述: 减法 <br/>
	 *  * @方法名: subtract <br/>
	 *  * @param v1 <br/>
	 *  * @param v2 <br/>
	 *  * @return <br/>
	 *  * @返回类型 double <br/>
	 */
	public static double subtract(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}
 
	/**
	 * 
	 *  * @描述: 乘法 <br/>
	 *  * @方法名: mul <br/>
	 *  * @param d1 <br/>
	 *  * @param d2 <br/>
	 *  * @return <br/>
	 *  * @返回类型 double <br/>
	 */
	public static double multiply(double d1, double d2) {// 进行乘法运算
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		return b1.multiply(b2).doubleValue();
	}
 
	/**
	 * 
	 *  * @描述: 除法 ，四舍五入<br/>
	 *  * @方法名: div <br/>
	 *  * @param d1 <br/>
	 *  * @param d2 <br/>
	 *  * @param len ，保留的小数位数<br/>
	 *  * @return <br/>
	 *  * @返回类型 double <br/>
	 */
	public static double divide(double d1, double d2, int len) {// 进行除法运算
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		 
		return b1.divide(b2, len, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	/**
	 *   
	 * @描述:  除法，四舍五入取整数 ,例如：5/2=3(2.5四舍五入); 5/3=2(1.6四舍五入);<br/>
	 * @方法名: div   <br/>
	 * @param d1 <br/>
	 * @param d2 <br/>
	 * @return   <br/>
	 * @返回类型 double  <br/>
	 
	 */
	public static double divide(double d1, double d2) {// 进行除法运算
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		return b1.divide(b2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
 
	/**
	 * 
	 *  * @描述: 四舍五入 <br/>
	 *  * @方法名: round  * @param d <br/>
	 *  * @param len <br/>
	 *  * @return <br/>
	 *  * @返回类型 double <br/>
	 */
	public static double round(double d, int len) {
		BigDecimal b1 = new BigDecimal(d);
		BigDecimal b2 = new BigDecimal(1);
		// 任何一个数字除以1都是原数字
		// ROUND_HALF_UP是BigDecimal的一个常量，表示进行四舍五入的操作
		return b1.divide(b2, len, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}