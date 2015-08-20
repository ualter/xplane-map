package br.ujr.xplane.comm.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DATAREFMessage extends UDPMessage {
	
	private float value;
	private String dataRef;
	
	public DATAREFMessage(String dataRef, float value) {
		setProlouge("DREF0");
		this.dataRef = dataRef;
		this.value = value;
	}
	
	@Override
	public ByteBuffer toByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(getProlouge().length() + 4 + 500);

    	String prolougue = getProlouge();
    	
    	byteBuffer.put(prolougue.getBytes());
    	byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	byteBuffer.putFloat(value);
    	byteBuffer.put(dataRef.getBytes());
    	
    	return byteBuffer;
	}
	
	@Override
	public ByteBuffer toByteBufferWithoutPrologue() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + 1);
    	return byteBuffer;
	}
}