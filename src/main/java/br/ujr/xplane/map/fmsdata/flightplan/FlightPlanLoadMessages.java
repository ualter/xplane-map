package br.ujr.xplane.map.fmsdata.flightplan;

import java.util.ArrayList;
import java.util.List;

public class FlightPlanLoadMessages {
	
	private List<String> messages = new ArrayList<String>();
	
	

	public FlightPlanLoadMessages() {
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
	public void addMessage(String msg) {
		this.messages.add(msg);
	}

	public boolean isEmpty() {
		return this.messages.size() == 0;
	}
	
	

}
