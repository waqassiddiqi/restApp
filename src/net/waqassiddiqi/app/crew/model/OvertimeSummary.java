package net.waqassiddiqi.app.crew.model;

public class OvertimeSummary {
	private Crew crew;
	private double totalHours;
	private double fixedOvertime;
	private double excessOvertime;
	private double holiday;
	private double totalOvertime;
	
	public Crew getCrew() {
		return crew;
	}
	public void setCrew(Crew crew) {
		this.crew = crew;
	}
	public double getTotalHours() {
		return totalHours;
	}
	public void setTotalHours(double totalHours) {
		this.totalHours = totalHours;
	}
	public double getFixedOvertime() {
		return fixedOvertime;
	}
	public void setFixedOvertime(double fixedOvertime) {
		this.fixedOvertime = fixedOvertime;
	}
	public double getExcessOvertime() {
		return excessOvertime;
	}
	public void setExcessOvertime(double excessOvertime) {
		this.excessOvertime = excessOvertime;
	}
	public double getHoliday() {
		return holiday;
	}
	public void setHoliday(double holiday) {
		this.holiday = holiday;
	}
	public double getTotalOvertime() {
		return totalOvertime;
	}
	public void setTotalOvertime(double totalOvertime) {
		this.totalOvertime = totalOvertime;
	}	
}
