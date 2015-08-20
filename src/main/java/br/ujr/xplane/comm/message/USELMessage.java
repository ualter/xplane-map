package br.ujr.xplane.comm.message;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class USELMessage extends UDPMessage {
	
	private int[] data;
	
	public USELMessage() {
		setProlouge("USEL0");
	}
	
	public USELMessage(int[] data) {
		this();
		this.data = data;
	}

	public USELMessage(String data) {
		this();
		String aux[] = data.split(",");
		this.data = new int[aux.length];
		for (int i = 0; i < aux.length; i++) {
			this.data[i] = Integer.parseInt(aux[i]);
		}
	}
	
	@Override
	public ByteBuffer toByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(5 + (data.length*4));
    	String prolougue = getProlouge();
    	byteBuffer.put(prolougue.getBytes());
    	byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	for (int i = 0; i < data.length; i++) {
    		byteBuffer.putInt(data[i]);
    	}
    	return byteBuffer;
	}

	@Override
	public ByteBuffer toByteBufferWithoutPrologue() {
		return null;
	}
}