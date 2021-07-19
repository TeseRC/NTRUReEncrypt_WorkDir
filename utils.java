package nics.crypto.ntrureencrypt;

import java.io.*;

public class utils {
	
	private static String getRepresantationOfLowIntValue(int toConvert) {
	    if(toConvert >= 0 && toConvert < 10) {
	        return "" + toConvert;
	    }

	    switch(toConvert) {
	        case 10 : return "A";
	        case 11 : return "B";
	        case 12 : return "C";
	        case 13 : return "D";
	        case 14 : return "E";
	        case 15 : return "F";
	    }

	    return "Error, cannot transform number < 0 or > 15";
	    //throw new IllegalArgumentException("cannot transform number < 0 or >15");
	}
	
	
	public static String convertFromDecimal(int number, int base) {
	    String result = "";

	    int lastQuotient = 0;

	    for(int operatingNumber = number;operatingNumber > base; operatingNumber = operatingNumber/base) {
	        result = getRepresantationOfLowIntValue(operatingNumber%base) + result;
	        lastQuotient = operatingNumber/base;
	    }

	    result = getRepresantationOfLowIntValue(lastQuotient) + result;

	    return result;
	}
	
	
	public static int[] convertTernary(String s, boolean mode) {
		
		// Creating array of string length
        int[] intArr = new int[s.length()];
        // Copy character by character into array
        for (int i = 0; i < s.length(); i++) {
            intArr[i] = Character.getNumericValue(s.charAt(i));
        }
        
        if (mode) {
        	for (int i = 0; i < s.length(); i++) {
                if(intArr[i] == 2)
                	intArr[i] = -1;
            }
        }
		
		return intArr;
	}
	
}
