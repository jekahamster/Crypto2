package crypto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class CryptoFileController {
	public static void createNewNumberFile(long number, String fileName) {
		File primaryNumberFile = new File(".\\src\\crypto\\"+fileName);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(primaryNumberFile));
			bw.write(number+"");
		}
		catch (IOException ioe) { ioe.printStackTrace(); }
		finally {
			try {
				bw.flush();
				bw.close();
			}
			catch (IOException ioe) { ioe.printStackTrace(); }
		}
	}
	
	public static long getNumberFromFile(String fileName) {
		long num = 0;
		File file = new File(".\\src\\crypto\\"+fileName);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			num = Long.parseLong(br.readLine()); 
		}
		catch (IOException ioe) { ioe.printStackTrace(); }
		finally {
			try { br.close(); }
			catch(IOException ioe) { ioe.printStackTrace(); }
		}
		return num;
	}

	public static void createShuffledKeysFile() {
		ArrayList<Key> resList = new ArrayList<>();
		File allKeysFile = new File(".\\src\\crypto\\allKeys");
		
		BufferedReader br = null;
		ArrayList<Key> tempList;
		try {
			br = new BufferedReader(new FileReader(allKeysFile));
			
			for (int i = 0; i < Crypter.MAX_N; i++) {
				tempList = getSetOfKeys(Crypter.getLen(i));
				Collections.shuffle(tempList);
				for (Key k : tempList)
					resList.add(k);
			}
			
		} 
		catch (FileNotFoundException e) { e.printStackTrace();} 
		finally {
			try { br.close(); }
			catch (IOException e) { e.printStackTrace(); }
		}
		
		File shuffledKeysFile = new File(".\\src\\crypto\\shuffledKeys");
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(shuffledKeysFile));
			for (Key k : resList) {
				bw.write(k.getLen() + "|" + Long.toBinaryString(k.getKey()));
				bw.newLine();
			}
		}
		catch (IOException e) { e.printStackTrace(); } 
		finally {
			try { bw.flush(); bw.close(); }
			catch (IOException ioe) { ioe.printStackTrace(); }
		}
		
	}

	public static ArrayList<Key> getAllKeys() {
		ArrayList<Key> keysList = new ArrayList<>();
		File file = new File(".\\src\\crypto\\allKeys");
		BufferedReader br = null;
		try {
			 br = new BufferedReader(new FileReader(file));
			 
			 String temp = "";
			 while ( (temp = br.readLine()) != null )
				 keysList.add(parseToKey(temp));
			 
			 br.close();
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		}
		return keysList;
	}
	
	public static ArrayList<Key> getSetOfKeys(long length) {
		ArrayList<Key> keysList = new ArrayList<>();
		File file = new File(".\\src\\crypto\\allKeys");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			Key temp = null;
			while( br.ready() ) {
				temp = parseToKey(br.readLine());
				if (temp.getLen() == length )
					keysList.add(temp);
			}
			 
			br.close();
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		}
		return keysList;	
	}
	
	public static ArrayList<Key> getSetOfKeys(long length, String fileName) {
		ArrayList<Key> keysList = new ArrayList<>();
		File file = new File(".\\src\\crypto\\"+fileName);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			Key temp = null;
			while( br.ready() ) {
				temp = parseToKey(br.readLine());
				if (temp.getLen() == length )
					keysList.add(temp);
			}
			 
			br.close();
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		}
		return keysList;	
	}
	
	public static Key parseToKey(String stringKey) {
		String sLength = stringKey.substring(0, stringKey.indexOf('|'));
		String sKey = stringKey.substring(stringKey.indexOf('|')+1, stringKey.length());
		long length = Long.parseLong(sLength);
		long key = Long.parseLong(sKey, 2);
		return new Key(length, key);
	}

	
}

/*
 * public 	static 	void 			createNewNumberFile(long primaryNumber, String fileName)
 * public 	static 	long 			getNumberFromFile(String fileName)
 * public 	static 	void 			createShuffledKeysFile()
 * public 	static 	ArrayList<Key> 	getAllKeys()
 * public 	static 	ArrayList<Key> 	getSetOfKeys(long length)
 * public 	static 	ArrayList<Key> 	getSetOfKeys(long length, String fileName)
 * public 	static 	Key 			parseToKey(String stringKey)
 * 
 */
