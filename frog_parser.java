
public class frog_parser
{
	private static final short c_header = 0;
	private static final short c_body = 1;
	private static final short c_done = 2;
	private static final short c_error = 3;

	private parse_callback cb_;
	private short state_;
	private int packpos_;
	private int bodylen_;
	private byte buf_[];
	private frog_codec decoder_;

	public void setParseCallback(parse_callback cb) {
		cb_ = cb;
	}

	public int parse(byte[] data, int length) {
		int ndx[] = new int[1];
		ndx[0]=0;
		while(ndx[0]<length && state_ != c_error){
			switch(state_){
				case c_header:
					if(!readHeader(data,length,ndx)){
						break;
					}
					
					if(parseHeader() != 0){
						state_ = c_error;
						break;
					}else{
						state_ = c_body;
					}

				case c_body:
					if(parseBody(data,length,ndx)){
						state_ = c_done;
					}
					break;

				default:
					return -1;
			}
			if(state_==c_error){init();}
			if(state_==c_done){
				this.cb_.onMessage(decoder_);
				init();
			}
		}
		return 0;
	}

	public void init() {
		decoder_ = new frog_codec();
		buf_ = decoder_.getNewBuffer();
		state_ = c_header;
		packpos_ = 0;
		bodylen_ = 0;
	}

	private boolean readHeader(byte[] data, int length, int[] ndx) {
		if(0==ndx[0]){packpos_=0;}
		while(packpos_<frog_codec.c_header_size && ndx[0]<length){
			buf_[packpos_++] = data[ndx[0]++];
		}
		if(packpos_ < frog_codec.c_header_size){
			return false;
		}
		return true;
	}

	private int parseHeader() {
		if(buf_[0]!='G' || buf_[1]!='P'){return -1;}
		short cmd = decoder_.command();
		if(cmd<0 || cmd>=32000){return -2;}
		bodylen_ = decoder_.bodyLength();
		if(bodylen_<(frog_codec.c_buffer_size - frog_codec.c_header_size)){return 0;}
		return -4;
	}

	private boolean parseBody(byte[] data, int length, int[] ndx) {
		int needlen = (bodylen_ + frog_codec.c_header_size) - packpos_;
		if(needlen <=0){return true;}
		int buflen = length - ndx[0];
		if(buflen <= 0){return false;}
		int cplen = buflen<needlen?buflen:needlen;
		for(int i=0; i<cplen; ++i){
			buf_[packpos_+i] = data[ndx[0]+i];
		}
		packpos_ += cplen;
		ndx[0]+=cplen;
		if(packpos_<(bodylen_+frog_codec.c_header_size)){return false;}
		return true;
	}
}


