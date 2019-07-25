package crypto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;


public class Crypter {
	public static final long[] LEN = {
			0L, 1L, 3L, 7L, 15L, 31L, 63L, 127L, 255L, 511L, 1023L, 2047L, 
			4095L, 8191L, 16383L, 32767L, 65535L, 131071L, 262143L, 
			524287L, 1048575L, 2097151L, 4194303L, 8388607L, 16777215L, 
			33554431L, 67108863L, 134217727L, 268435455L, 536870911L, 
			1073741823L, 2147483647L, 4294967295L, 8589934591L, 17179869183L };
	public static final int MAX_N = 34; 
	public static final int TEMP_MAX_N = 11; 
	/* 
	 * Because in allKeys file N = 11 - the MAXIMUM value.
	 * When the file will be filled to N = 34 (Len = getLen(34)),
	 * plz delete TEMP_MAX_N variable and replace it to MAX_N in code
	 * And delete plz 
	 * // TEMP // 
	 * ... 
	 * ////////// 
	 * blocks in code when file will be filled
	*/
	public static long createNewPrimaryNumber(String pass) {
		long primaryNumber = 0;
		long multCode = 1;
		for (int i = 0; i < pass.length(); i++) {
			char c = pass.charAt(i);
			multCode *= (long)c;
		}
		primaryNumber = multCode % ((long)(Math.pow(2, MAX_N))); 
		CryptoFileController.createNewNumberFile(primaryNumber, "primaryNumber");
		return primaryNumber;
	}
	
	public static long createNewSecondaryNumber(String pass) {
		long primaryNumber = CryptoFileController.getNumberFromFile("primaryNumber");
		long secondaryNumber = 0;
		long multCode = 1;
		for (int i = 0; i < pass.length(); i++) {
			char c = pass.charAt(i);
			multCode *= (long)c;
		}
		secondaryNumber = (multCode*primaryNumber) % ((long)(Math.pow(2, MAX_N))); 
		CryptoFileController.createNewNumberFile(secondaryNumber, "secondaryNumber");
		return secondaryNumber;
	}
	
	public static long getLen(int n) {
		long len = 0;
		int temp_n = 0;
		while ( temp_n <= n ) {
			len = (long)Math.pow(2, temp_n) - 1;
			temp_n++;
		}
		return len;
	}
	
	public static long getLen(String str) {
		long bits = str.length() * 8;
		long len = 0;
		int n = 0;
		while ( len <= bits ) {
			len = (long)Math.pow(2, n) - 1;
			n++;
		}
		return len;
	}
	
	public static int getN(long len) {
		long temp_len = 0;
		int n = 0;
		while ( temp_len < len) {
			temp_len = (long)Math.pow(2, n) - 1;
			n++;
		}
		return --n;
	}
	
	public static int getBlockIndex(int startIndex) {
		LocalDate date = LocalDate.now();
		int day = date.getDayOfYear();
		int month = date.getMonthValue();
		
		int i = TEMP_MAX_N;
		int blockIndex = (day+month) % i--;
		while ( (blockIndex < startIndex || blockIndex > TEMP_MAX_N-1) && i > 0 ) {
			blockIndex = (day+month) % i--;
		}
		
		if (i <= 0) 
			return startIndex;
		
		return blockIndex;
	}
	
	public static int getKeyIndex(long len) {
		long secondaryNumber = CryptoFileController.getNumberFromFile("secondaryNumber");
		LocalDate date = LocalDate.now();
		int day = date.getDayOfYear();
		int year = date.getYear();
		long mult = secondaryNumber * Long.parseLong(day+""+year);
		int keyNumber = (int)(mult % CryptoFileController.getSetOfKeys(len).size());
		return keyNumber;
	}
	
	public static long getPhase(long blockLen) {
		long secondaryNumber = CryptoFileController.getNumberFromFile("secondaryNumber");
		LocalDate date = LocalDate.now();
		int day = date.getDayOfYear();
		int month = date.getMonthValue();
		int year = date.getYear();
		long mult = secondaryNumber * Long.parseLong(""+day+month+year);
		long phase = mult % blockLen;
		return phase;
	}
	
	public static String encryptMessage(String msg) {
		class CryptoContainer {
			public long key;
			public long phase;
			public CryptoContainer(long key, long phase) 
			{ this.key = key; this.phase = phase; }
			public String toString() 
			{ return super.hashCode() + " K:" +
					 Long.toBinaryString(this.key) + " P:" +
					 Long.toBinaryString(this.phase);}
		}
		ArrayList<CryptoContainer> cList = new ArrayList<>();
		long minLen = getLen(msg);
		//////    TEMP    ///////
		if ( getN(minLen) < 7 ) { minLen = 7; };		
		/////////////////////////
		
		int startIndex = Arrays.binarySearch(LEN, minLen);
		int currentBlockIndex = getBlockIndex(startIndex);
		long currentBlockLen = getLen(currentBlockIndex+1);
		int currentKeyIndex = getKeyIndex(currentBlockLen);
		ArrayList<Key> currentKeysBlock = 
				CryptoFileController.getSetOfKeys(currentBlockLen, "shuffledKeys");
		long currentKey = currentKeysBlock.get(currentKeyIndex).getKey();
		long currentPhase = getPhase(currentBlockLen);
		cList.add(new CryptoContainer(currentKey, currentPhase));
	
		for (int i = 0; i < 3; i++) {
			if (currentBlockIndex < TEMP_MAX_N-1) 
				currentBlockIndex++;			
			else
				currentKeyIndex = ++currentKeyIndex % currentKeysBlock.size();
			currentBlockLen = getLen(currentBlockIndex+1);
			currentKeysBlock = 
				CryptoFileController.getSetOfKeys(currentBlockLen, "shuffledKeys");
			currentKey = currentKeysBlock.get(currentKeyIndex).getKey();
			currentPhase *= 2;
			cList.add(new CryptoContainer(currentKey, currentPhase));
		}
		
		for (int i = 0; i < cList.size(); i++) {
			CryptoContainer container = cList.get(i);
			msg = crypt(msg, container.key, container.phase);
		}
		return msg;
	}
	
	public static String decryptMessage(String msg) {
		class CryptoContainer {
			public long key;
			public long phase;
			public CryptoContainer(long key, long phase) 
			{ this.key = key; this.phase = phase; }
			public String toString() 
			{ return super.hashCode() + " K:" +
					 Long.toBinaryString(this.key) + " P:" +
					 Long.toBinaryString(this.phase);}
		}
		Stack<CryptoContainer> cStack = new Stack<>();
		long minLen = getLen(msg);
		//////    TEMP    ///////
		if ( getN(minLen) < 7 ) { minLen = 7; };		
		/////////////////////////
		
		int startIndex = Arrays.binarySearch(LEN, minLen);
		int currentBlockIndex = getBlockIndex(startIndex);
		long currentBlockLen = getLen(currentBlockIndex+1);
		int currentKeyIndex = getKeyIndex(currentBlockLen);
		ArrayList<Key> currentKeysBlock = 
				CryptoFileController.getSetOfKeys(currentBlockLen, "shuffledKeys");
		long currentKey = currentKeysBlock.get(currentKeyIndex).getKey();
		long currentPhase = getPhase(currentBlockLen);
		cStack.push(new CryptoContainer(currentKey, currentPhase));
		
		for (int i = 0; i < 3; i++) {
			if (currentBlockIndex < TEMP_MAX_N-1)
				currentBlockIndex++;			
			else
				currentKeyIndex = ++currentKeyIndex % currentKeysBlock.size();
			currentBlockLen = getLen(currentBlockIndex+1);
			currentKeysBlock = 
				CryptoFileController.getSetOfKeys(currentBlockLen, "shuffledKeys");
			currentKey = currentKeysBlock.get(currentKeyIndex).getKey();
			currentPhase *= 2;
			cStack.push(new CryptoContainer(currentKey, currentPhase));
		}
		for (int i = 0, temp = cStack.size(); i < temp; i++) {
			CryptoContainer container = cStack.pop();
			msg = crypt(msg, container.key, container.phase);
		}
		return msg;
	}
	
	public static String crypt(String msg, long key, long code) {
        long temp = 0;
        int count = 0;
        String result = "";
        for(int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);
            for(int j = 0; j < 8; j++) {
                temp = code & key;
                count = Long.bitCount(temp);
                code *= 2;
                if(count %2 == 1) {
                    code++;
                }
            }
            char r = (char)(code^c);
            result = result.concat(String.valueOf(r));
        }
        return result;
	}
}

/*
	public	static 	long 	createNewPrimaryNumber(String pass)
	public 	static 	long 	createNewSecondaryNumber(String pass)
	public 	static 	long 	getLen(int n)
	public 	static 	long 	getLen(String str)
	public 	static 	int 	getN(long len)
	public 	static 	int 	getBlockIndex(int startIndex)
	public 	static 	int 	getKeyIndex(long len)
	public 	static 	long 	getPhase(long blockLen)
	public 	static 	String 	cryptMessage(String msg)
	public 	static 	String 	decryptMessage(String msg)
	public 	static 	String 	crypt(String msg, long key, long code)

*/
