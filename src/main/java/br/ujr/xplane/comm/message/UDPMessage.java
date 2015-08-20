package br.ujr.xplane.comm.message;

import java.nio.ByteBuffer;

public abstract class UDPMessage {
	
	private String prolouge;

	public String getProlouge() {
		return prolouge;
	}

	public void setProlouge(String prolouge) {
		this.prolouge = prolouge;
	}

	public abstract ByteBuffer toByteBuffer();
	
	public abstract ByteBuffer toByteBufferWithoutPrologue();
	
}