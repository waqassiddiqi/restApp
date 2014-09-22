package net.waqassiddiqi.app.crew.model;

import java.util.Arrays;
import java.util.Date;

public class EntryTime {
	private int id;
	private int crewId;
	private Date entryDate;
	private Boolean[] schedule;
	private boolean isOnPort;
	private float workIn24Hours;
	private float restIn24Hours;
	private String comments;
	
	public EntryTime() {
		schedule = new Boolean[48];
		Arrays.fill(schedule, Boolean.TRUE);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCrewId() {
		return crewId;
	}

	public void setCrewId(int crewId) {
		this.crewId = crewId;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public Boolean[] getSchedule() {
		return schedule;
	}

	public void setSchedule(Boolean[] schedule) {
		this.schedule = schedule;
	}

	public boolean isOnPort() {
		return isOnPort;
	}

	public void setOnPort(boolean isOnPort) {
		this.isOnPort = isOnPort;
	}

	public float getWorkIn24Hours() {
		return workIn24Hours;
	}

	public void setWorkIn24Hours(float workIn24Hours) {
		this.workIn24Hours = workIn24Hours;
	}

	public float getRestIn24Hours() {
		return restIn24Hours;
	}

	public void setRestIn24Hours(float restIn24Hours) {
		this.restIn24Hours = restIn24Hours;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public void parseSchedule(String scheduleString) {
		for(int i=0; (i<48 && i<scheduleString.length()); i++) {
			
			if(scheduleString.charAt(i) == '1') {
				this.schedule[i] = true;
				
				this.workIn24Hours += 0.5;
				
			} else {
				this.schedule[i] = false;
				
				this.restIn24Hours += 0.5;
			}
		}
	}

	@Override
	public String toString() {
		return "EntryTime [id=" + id + ", crewId=" + crewId + ", entryDate="
				+ entryDate + ", schedule=" + Arrays.toString(schedule)
				+ ", isOnPort=" + isOnPort + ", workIn24Hours=" + workIn24Hours
				+ ", restIn24Hours=" + restIn24Hours + ", comments=" + comments
				+ "]";
	}
}
