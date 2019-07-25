package crypto;

public class Key {
	private final long length, key;
	public Key(long length, long key) {
		this.length = length;
		this.key = key;
	}
	
	public long getLen() 
	{ return length; }
	
	public long getKey() 
	{ return key; }
	
	public String toString() 
	{ return "{Key: "+super.hashCode()+
			 " L:"+this.length+
			 " K:"+Long.toBinaryString(this.key)+"}"; }
}
