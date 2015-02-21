package net.waqassiddiqi.app.crew.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HolidayList {
	private int id;
	private String name;
	private Date from;
	private Date to;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		return name + " (" + sdf.format(from) + " - " + sdf.format(to) + ")";
	}
}
