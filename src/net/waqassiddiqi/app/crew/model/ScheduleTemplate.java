package net.waqassiddiqi.app.crew.model;

public class ScheduleTemplate {
	private int id;
	private Boolean[] schedule;
	private boolean isOnPort;
	private boolean isWatchKeeping;
	
	public ScheduleTemplate() {
		this.schedule = new Boolean[48];
		isWatchKeeping = true;
		isOnPort = false;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Boolean[] getSchedule() {
		return schedule;
	}
	public void setSchedule(Boolean[] scheduleArray) {
		this.schedule = scheduleArray;
	}
	
	public void parseSchedule(String scheduleString) {
		for(int i=0; (i<48 && i<scheduleString.length()); i++) {
			this.schedule[i] = (scheduleString.charAt(i) == '1') ? true : false; 
		}
	}
	
	public String getScheduleString() {
		StringBuilder sb = new StringBuilder();
		
		for(Boolean b : this.schedule) {
			sb.append( (b == true) ? 1 : 0);
		}
		
		return sb.toString();
	}
	
	public static String getScheduleString(Boolean[] scheduleArray) {
		StringBuilder sb = new StringBuilder();
		
		for(Boolean b : scheduleArray) {
			sb.append( (b == true) ? 1 : 0);
		}
		
		return sb.toString();	
	}

	public boolean isOnPort() {
		return isOnPort;
	}
	public void setOnPort(boolean isOnPort) {
		this.isOnPort = isOnPort;
	}

	public boolean isWatchKeeping() {
		return isWatchKeeping;
	}

	public void setWatchKeeping(boolean isWatchKeeping) {
		this.isWatchKeeping = isWatchKeeping;
	}
}