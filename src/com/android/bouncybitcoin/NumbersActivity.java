package com.android.bouncybitcoin;


import java.util.HashMap;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

public class NumbersActivity {
	public HashMap<Character, int[]> numbers;
	
	public NumbersActivity() {
		 numbers = new HashMap<Character, int[]>();
		 numbers.put('0', new int[]{0,1,2,4,5,6});
		 numbers.put('1', new int[]{2,5});
		 numbers.put('2', new int[]{0,2,3,4,6});
		 numbers.put('3', new int[]{0,2,3,5,6});
		 numbers.put('4', new int[]{1,2,3,5});
		 numbers.put('5', new int[]{0,1,3,5,6});
		 numbers.put('6', new int[]{0,1,3,4,5,6});
		 numbers.put('7', new int[]{0,2,5});
		 numbers.put('8', new int[]{0,1,2,3,4,5,6});
		 numbers.put('9', new int[]{0,1,2,3,5,6});
		 numbers.put('.', new int[]{7}); 
	}
	
	public int[][] ballCoords(int segmentId) { // refactor measurements to constants
		switch(segmentId) {
		case 0: return new int[][]{{0,0},{35,0},{70,0},{105,0}};
		case 1: return new int[][]{{0,0},{0,35},{0,70}};
		case 2: return new int[][]{{105,0},{105,35},{105,70}};
		case 3: return new int[][]{{0,70},{35,70},{70,70},{105,70}};
		case 4: return new int[][]{{0,70},{0,105},{0,140}};
		case 5: return new int[][]{{105,70},{105,105},{105,140}};
		case 6: return new int[][]{{0,140},{35,140},{70,140},{105,140}};
		case 7: return new int[][]{{53,140}};
		}
		return null;
	}
	
	public final int displayHeight(Context context) {
		DisplayMetrics d = context.getResources().getDisplayMetrics();
		int screenHeight = d.heightPixels;
		return screenHeight / 34;
	}
	
}
