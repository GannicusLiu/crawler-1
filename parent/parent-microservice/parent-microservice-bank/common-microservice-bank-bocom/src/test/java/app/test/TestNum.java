package app.test;

import java.util.ArrayList;
import java.util.List;

public class TestNum {
	
	public static void main(String[] args) {
		double[] arr = {4.08,8.39,14.38,14.66,15.73,16.23,16.57,17.89,18.34,18.62,20.46,24.55,
				25.4,32.57,37.02,38.06,43.15,43.47,46.6,52.74,54.94,56.6,61.42,68,95.9,117.74,
				122.05,135.62,141.85,143.85,150,150.82,152.56,169.81,180.49,182.04,224.49,256.02,262.14,
				262.14,262.14,290.6,305.13,316.07,317.69,319.51,340.11,435.46,438.62,481.82,522.64,538.56,
				543.48,566.52,570.87,627.7,656.36,660.68,668.38,672.81,691.83,699.03,763.58,
				826.9,828.06,849.06,873.79,885.7,902.31,971.35,1002.57,1086.12,1126.58,1154.4,
				1165.05,1165.13,1175.89,1190,1196.73,1226.8,1232.91,1236.95,1250.51,1281.53,
				1285.92,1294.72,1296.12,1307.69,1339.81,1452.99,1452.99,1452.99,1473.01,1567.49,
				1589.54,1601.72,1606.47,1665.15,1676.28,1678.21,1699.78,1699.78,1699.78,1699.78,1699.78,1699.78,1699.78,1699.78,
				1699.78,1699.78,1699.78,1747.57,1747.57,1937.12,1994.93,2053.39,2092.31,2126.53,
				2193.41,2194.78,2241.22,2264.92,2324.79,2330.1,2487.06,2537.21,2621.36,2716.98,2740.2,2752.51,2758.43,2763.03};
		List list = parser(arr,2670.41);
		for(int i = 0;i<list.size();i++){
			System.out.println(list.get(i));
		}
	}
	
	public static List parser(double[] arr, double d) {
		boolean isTrue = false;
		for(int i = 0;i<arr.length-1;i++){
			arr[i] +=arr[i+1];
			if(arr[i] == d){
				System.out.println(arr[i]+":"+arr[i+1]);
				isTrue = true;
			}
		}
		
		
		return null;
		
	}

}