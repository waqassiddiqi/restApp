package net.waqassiddiqi.app.crew.model;

public class WageDetails {
	private int id;
	private int crewId;
	private int holidayListId;
	private double basicWageWeekday;
	private double basicWageSaturday;
	private double basicWageSunday;
	private double basicWageHoliday;
	private double monthlyFixedOverrtimeHours;
	private double hourlyRates;
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
	public int getHolidayListId() {
		return holidayListId;
	}
	public void setHolidayListId(int holidayListId) {
		this.holidayListId = holidayListId;
	}
	public double getBasicWageWeekday() {
		return basicWageWeekday;
	}
	public void setBasicWageWeekday(double basicWageWeekday) {
		this.basicWageWeekday = basicWageWeekday;
	}
	public double getBasicWageSaturday() {
		return basicWageSaturday;
	}
	public void setBasicWageSaturday(double basicWageSaturday) {
		this.basicWageSaturday = basicWageSaturday;
	}
	public double getBasicWageSunday() {
		return basicWageSunday;
	}
	public void setBasicWageSunday(double basicWageSunday) {
		this.basicWageSunday = basicWageSunday;
	}
	public double getBasicWageHoliday() {
		return basicWageHoliday;
	}
	public void setBasicWageHoliday(double basicWageHoliday) {
		this.basicWageHoliday = basicWageHoliday;
	}
	public double getMonthlyFixedOverrtimeHours() {
		return monthlyFixedOverrtimeHours;
	}
	public void setMonthlyFixedOverrtimeHours(double monthlyFixedOverrtimeHours) {
		this.monthlyFixedOverrtimeHours = monthlyFixedOverrtimeHours;
	}
	public double getHourlyRates() {
		return hourlyRates;
	}
	public void setHourlyRates(double hourlyRates) {
		this.hourlyRates = hourlyRates;
	}
}
