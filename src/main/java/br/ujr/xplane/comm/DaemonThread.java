package br.ujr.xplane.comm;

public abstract class DaemonThread extends Thread {
	
	protected boolean runState = true;
	
	public DaemonThread() {
		this.setDaemon(true);
	}

	public boolean isRunState() {
		return runState;
	}

	public void setRunState(boolean runState) {
		this.runState = runState;
	}
	

}