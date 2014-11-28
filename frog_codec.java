
public class frog_codec
{
	public static final short c_default_version = 1;
	public static final short c_default_subversion = 1;
	public static final short c_header_size = 9;
	public static final short c_buffer_size = 1024 * 12;

	private byte buffer_[];
	private int size_;
	private int readptr_;

	public byte[] getNewBuffer() {
		reset();
		return buffer_;
	}
/*
	public void addSize(int a) {
		size_ += a;
	}
*/
	public short command() {
		return bytesToShort(buffer_, 2);
	}

	public short bodyLength() {
		return bytesToShort(buffer_, 6);
	}

	public void reset() {
		buffer_ = new byte[c_buffer_size];
		readptr_ = c_header_size;
		size_ = c_header_size;
	}

	public byte[] buffer() {
		return buffer_;
	}

	public int size() {
		return size_;
	}

	public short readShort() {
		short val = bytesToShort(buffer_, readptr_);
		readptr_ += 2;
		return val;
	}

	public int readInt() {
		int val = bytesToInt(buffer_, readptr_);
		readptr_ += 4;
		return val;
	}

	public long readLong() {
		long val = bytesToLong(buffer_, readptr_);
		readptr_ += 8;
		return val;
	}

	public String readString() {
		int strlen = readInt();
	    byte strbytes[] = new byte[strlen-1];
		for(int i=0; i<strlen-1; ++i) {
			strbytes[i] = buffer_[readptr_+i];
		}
		readptr_ += strlen;
		return new String(strbytes);	
	}

	public boolean writeShort(short val) {
		shortToBytes(val, buffer_, size_);
		size_ += 2;
		return true;
	}

	public boolean writeInt(int val) {
		intToBytes(val, buffer_, size_);
		size_ += 4;
		return true;
	}

	public boolean writeLong(long val) {
		longToBytes(val, buffer_, size_);
		size_ += 8;
		return true;
	}

	public boolean writeString(String val) {
		int strlen = val.length();
		writeInt(strlen+1);
		for(int i=0; i<strlen; ++i) {
			buffer_[size_+i] = (byte)(val.charAt(i));
		}
		buffer_[size_+strlen] = 0;
		size_ += (strlen + 1);	
		return true;
	}

	public void begin(short cmd) {
		reset();
		buffer_[0] = 'G';
		buffer_[1] = 'P';
		shortToBytes(cmd, buffer_, 2);
		buffer_[4] = c_default_version;
		buffer_[5] = c_default_subversion;
		buffer_[8] = 0;
	}

	public void end() {
		shortToBytes((short)(size_-c_header_size), buffer_, 6);
	}

	public static byte[] longToBytes(long n) {
		byte[] b = new byte[8];
		b[7] = (byte)(n & 0xff);
		b[6] = (byte)(n >> 8  & 0xff);
		b[5] = (byte)(n >> 16 & 0xff);
		b[4] = (byte)(n >> 24 & 0xff);
		b[3] = (byte)(n >> 32 & 0xff);
		b[2] = (byte)(n >> 40 & 0xff);
		b[1] = (byte)(n >> 48 & 0xff);
		b[0] = (byte)(n >> 56 & 0xff);
		return b;
	}

	public static void longToBytes(long n, byte[] array, int offset) {
		array[7+offset] = (byte)(n & 0xff);
		array[6+offset] = (byte)(n >> 8 & 0xff);
		array[5+offset] = (byte)(n >> 16 & 0xff);
		array[4+offset] = (byte)(n >> 24 & 0xff);
		array[3+offset] = (byte)(n >> 32 & 0xff);
		array[2+offset] = (byte)(n >> 40 & 0xff);
		array[1+offset] = (byte)(n >> 48 & 0xff);
		array[0+offset] = (byte)(n >> 56 & 0xff);
	}

	public static long bytesToLong(byte[] array) {
		return ((((long)array[ 0] & 0xff) << 56)
				| (((long)array[ 1] & 0xff) << 48)
				| (((long)array[ 2] & 0xff) << 40)
				| (((long)array[ 3] & 0xff) << 32)
				| (((long)array[ 4] & 0xff) << 24)
				| (((long)array[ 5] & 0xff) << 16)
				| (((long)array[ 6] & 0xff) << 8) 
				| (((long)array[ 7] & 0xff) << 0));        
	}

	public static long bytesToLong(byte[] array, int offset) {
		return ((((long)array[offset + 0] & 0xff) << 56)
				| (((long)array[offset + 1] & 0xff) << 48)
				| (((long)array[offset + 2] & 0xff) << 40)
				| (((long)array[offset + 3] & 0xff) << 32)
				| (((long)array[offset + 4] & 0xff) << 24)
				| (((long)array[offset + 5] & 0xff) << 16)
				| (((long)array[offset + 6] & 0xff) << 8) 
				| (((long)array[offset + 7] & 0xff) << 0));            
	}

	public static byte[] intToBytes(int n) {
		byte[] b = new byte[4];
		b[3] = (byte)(n & 0xff);
		b[2] = (byte)(n >> 8 & 0xff);
		b[1] = (byte)(n >> 16 & 0xff);
		b[0] = (byte)(n >> 24 & 0xff);
		return b;
	}

	public static void intToBytes(int n, byte[] array, int offset) {
		array[3+offset] = (byte)(n & 0xff);
		array[2+offset] = (byte)(n >> 8 & 0xff);
		array[1+offset] = (byte)(n >> 16 & 0xff);
		array[offset] = (byte)(n >> 24 & 0xff);
	}    

	public static int bytesToInt(byte b[]) {
		return b[3] & 0xff 
			| (b[2] & 0xff) << 8 
			| (b[1] & 0xff) << 16
			| (b[0] & 0xff) << 24;
	}

	public static int bytesToInt(byte b[], int offset) {
		return b[offset+3] & 0xff 
			| (b[offset+2] & 0xff) << 8 
			| (b[offset+1] & 0xff) << 16
			| (b[offset] & 0xff) << 24;
	}

	public static byte[] uintToBytes(long n) {
		byte[] b = new byte[4];
		b[3] = (byte)(n & 0xff);
		b[2] = (byte)(n >> 8 & 0xff);
		b[1] = (byte)(n >> 16 & 0xff);
		b[0] = (byte)(n >> 24 & 0xff);

		return b;
	}

	public static void uintToBytes(long n, byte[] array, int offset) {
		array[3+offset] = (byte)(n);
		array[2+offset] = (byte)(n >> 8 & 0xff);
		array[1+offset] = (byte)(n >> 16 & 0xff);
		array[offset] = (byte)(n >> 24 & 0xff);
	}

	public static long bytesToUint(byte[] array) {  
		return ((long)(array[3] & 0xff))  
			| ((long)(array[2] & 0xff)) << 8  
			| ((long)(array[1] & 0xff)) << 16  
			| ((long)(array[0] & 0xff)) << 24;  
	}

	public static long bytesToUint(byte[] array, int offset) {   
		return ((long)(array[offset+3] & 0xff))  
			| ((long)(array[offset+2] & 0xff)) << 8  
			| ((long)(array[offset+1] & 0xff)) << 16  
			| ((long)(array[offset] & 0xff)) << 24;  
	}

	public static byte[] shortToBytes(short n) {
		byte[] b = new byte[2];
		b[1] = (byte)(n & 0xff);
		b[0] = (byte)((n >> 8) & 0xff);
		return b;
	}

	public static void shortToBytes(short n, byte[] array, int offset) {        
		array[offset+1] = (byte)(n & 0xff);
		array[offset] = (byte)((n >> 8) & 0xff);
	}

	public static short bytesToShort(byte[] b) {
		return (short)(b[1] & 0xff | (b[0] & 0xff) << 8); 
	}    

	public static short bytesToShort(byte[] b, int offset) {
		return (short)(b[offset+1] & 0xff | (b[offset] & 0xff) << 8); 
	}

	public static byte[] ushortToBytes(int n) {
		byte[] b = new byte[2];
		b[1] = (byte)(n & 0xff);
		b[0] = (byte)((n >> 8) & 0xff);
		return b;
	}    

	public static void ushortToBytes(int n, byte[] array, int offset) {
		array[offset+1] = (byte)(n & 0xff);
		array[offset] = (byte)((n >> 8) & 0xff);
	}

	public static int bytesToUshort(byte b[]) {
		return b[1] & 0xff | (b[0] & 0xff) << 8;
	}    

	public static int bytesToUshort(byte b[], int offset) {
		return b[offset+1] & 0xff | (b[offset] & 0xff) << 8;
	}    

	public static byte[] ubyteToBytes(int n) {
		byte[] b = new byte[1];
		b[0] = (byte) (n & 0xff);
		return b;
	}

	public static void ubyteToBytes(int n, byte[] array, int offset) {
		array[0] = (byte) (n & 0xff);
	}

	public static int bytesToUbyte(byte[] array) {            
		return array[0] & 0xff;
	}        

	public static int bytesToUbyte(byte[] array, int offset) {            
		return array[offset] & 0xff;
	} 
}

