package br.ujr.xplane.comm.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DATAMessage extends UDPMessage {
	
	private int index;
	private float[] txData;
	private float[] rxData;
	
	public DATAMessage() {
		setProlouge("DATA0");
		txData = new float[8];
		rxData = new float[8];
		clear();
	}
	
	public void clear() {
		for (int i = 0; i < txData.length; i++) {
			txData[i] = -999;
		}
	}
	
	@Override
	public ByteBuffer toByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(getProlouge().length() + 4 + (txData.length * 4));
    	String prolougue = getProlouge();
    	
    	byteBuffer.put(prolougue.getBytes());
    	byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	byteBuffer.putInt(index);
    	
    	for (int i = 0; i < txData.length; i++) {
    		byteBuffer.putFloat(txData[i]);
    	}
    	return byteBuffer;
	}
	
	@Override
	public ByteBuffer toByteBufferWithoutPrologue() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + txData.length * 4);

    	byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    	byteBuffer.putInt(index);
    	
    	for (int i = 0; i < txData.length; i++) {
    		byteBuffer.putFloat(txData[i]);
    	}
    	return byteBuffer;
	}

	
	/*public String toString() {
		String s = index + " - ";
		return s;
	}*/
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public float[] getTxData() {
		return txData;
	}

	public void setTxData(float[] tXData) {
		txData = tXData;
	}

	public float[] getRxData() {
		return rxData;
	}

	public void setRxData(float[] rXData) {
		rxData = rXData;
	}
}