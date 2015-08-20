package br.ujr.xplane.comm;

public abstract class DaemonThreads extends Thread {
	
	protected boolean runState = true;
	
	public DaemonThreads() {
		this.setDaemon(true);
	}

	public boolean isRunState() {
		return runState;
	}

	public void setRunState(boolean runState) {
		this.runState = runState;
	}
	

}