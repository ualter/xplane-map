package br.ujr.xplane.comm.message;

import java.nio.ByteBuffer;

public class CHARMessage extends UDPMessage {
	
	private char key;
	
	public CHARMessage(char key) {
		setProlouge("CHAR0");
		this.key = key;
	}
	
	@Override
	public ByteBuffer toByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(5 + 2);
    	String prol = getProlouge();
    	byteBuffer.put(prol.getBytes());
    	byteBuffer.put((byte)key);
    	return byteBuffer;
	}

	@Override
	public ByteBuffer toByteBufferWithoutPrologue() {
		return null;
	}
}