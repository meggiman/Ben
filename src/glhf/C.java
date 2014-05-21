package glhf;

import glhf.Hash;

public class C {
	
	public static void initialize(int hashMapSize)
	{
		fields = new long[80];
		for(int i = 0; i < 64; i++)
		{
			fields[i] = 1L << i;
		}
		for(int i = 64; i < 80; i++)
		{
			fields[i] = 0L;
		}
		
		hashMap = new HashMap(hashMapSize);
		
		
		moveOrderFields = new long[64];
		fields = new long[64];
		possibleFields = new long[60];
		int index = 0;
		for(int i = 0; i < 64; i++)
		{
			fields[i] = 1L << i;
			moveOrderFields[i] = 1L << moveOrder[i];
			if(i != 27 & i != 28 & i != 35 & i != 36)
			{
				possibleFields[index] = 1L << i;
			}
			else
			{
				index--;
			}
			index++;
			
		}
	}
	
	//color
	public static final int RED = 0;
	public static final int GREEN = 1;
	
	//masks
	public static final long RIGHT_MASK = 0xfefefefefefefefeL;
	public static final long LEFT_MASK 	= 0x7f7f7f7f7f7f7f7fL;
	

	//hash
	public static Hash gen = new Hash();
	
	//hashMap
	public static HashMap hashMap;
	
	//precomputed fields
	public static long[] fields;
	
	public static int[] directions = {1,7,8,9,-1,-7,-8,-9};
	
	public static int[] corners = {0, 7, 56, 63};
	public static int[][] cornerDirections = {{1, 7, 8},{-1, 7, 6},{1, -7, -8},{-1,-7, -6}};
	
	
	//String
	public static String[] letters = {"a", "b", "c" , "d", "e", "f", "g", "h"};
	
	//moveorder
	//copy from Xiaolong
	public final static int[] moveOrder = { 0, 7, 56, 63, 18, 21, 42, 45, 2, 5, 16, 23, 40, 47, 58, 61, 3, 4, 24, 31, 32, 39, 59, 60, 19, 20, 26, 29, 34, 37, 43, 44, 11, 12, 25, 30, 33, 38, 51, 52, 10, 13, 17, 22, 41, 46, 50, 53, 1, 6, 8, 15, 48, 55, 57, 62, 9, 14, 49, 54, 27, 28, 35, 36 };
	
	
	public static long[] moveOrderFields;
	public static long[] possibleFields;
}
