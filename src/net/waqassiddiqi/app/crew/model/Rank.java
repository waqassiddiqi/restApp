package net.waqassiddiqi.app.crew.model;

public class Rank {
	private int id;
	private String rank;
	private ScheduleTemplate scheduleTemplate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	
	public ScheduleTemplate getScheduleTemplate() {
		return this.scheduleTemplate;
	}
	
	public void setScheduleTemplate(ScheduleTemplate scheduleTemplate) {
		this.scheduleTemplate = scheduleTemplate;
	}
}
