package net.waqassiddiqi.app.crew.model;

import java.util.Date;

public class ErrorReportEntry {
	private int id;
	private Crew crew;
	private Date entryDate;
	private double workIn24hours;
	private double restIn24hours;
	private double anyRest24hours;
	private double rest7days;
	private boolean restGreater10hrs;
	private boolean workLess14hrs;
	private boolean totalRest24hrsGreater10hrs;
	private boolean totalRest7daysGreater77hrs;
	private boolean oneRestPeriod6hrs;
	private int totalRestPeriods;
	private double restHour3daysGreater36hrs;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Crew getCrew() {
		return crew;
	}
	public void setCrew(Crew crew) {
		this.crew = crew;
	}
	
	public Date getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}
	
	public double getWorkIn24hours() {
		return workIn24hours;
	}
	public void setWorkIn24hours(double workIn24hours) {
		this.workIn24hours = workIn24hours;
	}
	
	public double getRestIn24hours() {
		return restIn24hours;
	}
	public void setRestIn24hours(double restIn24hours) {
		this.restIn24hours = restIn24hours;
	}
	
	public double getAnyRest24hours() {
		return anyRest24hours;
	}
	public void setAnyRest24hours(double anyRest24hours) {
		this.anyRest24hours = anyRest24hours;
	}
	
	public double getRest7days() {
		return rest7days;
	}
	public void setRest7days(double rest7days) {
		this.rest7days = rest7days;
	}
	
	public boolean isRestGreater10hrs() {
		return restGreater10hrs;
	}
	public void setRestGreater10hrs(boolean restGreater10hrs) {
		this.restGreater10hrs = restGreater10hrs;
	}
	
	public boolean isWorkLess14hrs() {
		return workLess14hrs;
	}
	public void setWorkLess14hrs(boolean workLess14hrs) {
		this.workLess14hrs = workLess14hrs;
	}
	
	public boolean isTotalRest24hrsGreater10hrs() {
		return totalRest24hrsGreater10hrs;
	}
	public void setTotalRest24hrsGreater10hrs(boolean totalRest24hrsGreater10hrs) {
		this.totalRest24hrsGreater10hrs = totalRest24hrsGreater10hrs;
	}
	
	public boolean isTotalRest7daysGreater77hrs() {
		return totalRest7daysGreater77hrs;
	}
	public void setTotalRest7daysGreater77hrs(boolean totalRest7daysGreater77hrs) {
		this.totalRest7daysGreater77hrs = totalRest7daysGreater77hrs;
	}
	
	public boolean isOneRestPeriod6hrs() {
		return oneRestPeriod6hrs;
	}
	public void setOneRestPeriod6hrs(boolean oneRestPeriod6hrs) {
		this.oneRestPeriod6hrs = oneRestPeriod6hrs;
	}
	
	public int getTotalRestPeriods() {
		return totalRestPeriods;
	}
	public void setTotalRestPeriods(int totalRestPeriods) {
		this.totalRestPeriods = totalRestPeriods;
	}
	
	public double getRestHour3daysGreater36hrs() {
		return restHour3daysGreater36hrs;
	}
	public void setRestHour3daysGreater36hrs(double restHour3daysGreater36hrs) {
		this.restHour3daysGreater36hrs = restHour3daysGreater36hrs;
	}
}