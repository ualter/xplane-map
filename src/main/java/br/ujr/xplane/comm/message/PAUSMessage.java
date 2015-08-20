package br.ujr.xplane.comm.message;

import java.nio.ByteBuffer;

public class PAUSMessage extends UDPMessage {
	
	private char data;
	
	public PAUSMessage() {
		setProlouge("PAUS0");
	}
	
	@Override
	public ByteBuffer toByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(5);
    	String prolougue = getProlouge();
    	byteBuffer.put(prolougue.getBytes());
    	return byteBuffer;
	}

	@Override
	public ByteBuffer toByteBufferWithoutPrologue() {
		return null;
	}

	public char getData() {
		return data;
	}

	public void setData(char data) {
		this.data = data;
	}

}