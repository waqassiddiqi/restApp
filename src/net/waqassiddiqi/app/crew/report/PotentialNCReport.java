package net.waqassiddiqi.app.crew.report;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.waqassiddiqi.app.crew.db.ReportDAO;
import net.waqassiddiqi.app.crew.util.CalendarUtil;

public class PotentialNCReport {
	private int month;
	private int year;
	
	public PotentialNCReport(int month, int year) {
		this.month = month;
		this.year = year;
	}
	
	public List<String[]> generateReport() {
		
		final Calendar calStart = Calendar.getInstance();
		final Calendar calEnd = Calendar.getInstance();
		
		calStart.set(Calendar.YEAR, year);
		calStart.set(Calendar.MONTH, month);
		
		calEnd.set(Calendar.YEAR, year);
		calEnd.set(Calendar.MONTH, month);
		
		calStart.setTime(CalendarUtil.getFirstDayOfMonth(calStart.getTime()));
		calEnd.setTime(CalendarUtil.getLastDayOfMonth(calEnd.getTime()));
		
		CalendarUtil.toBeginningOfTheDay(calStart);
		CalendarUtil.toBeginningOfTheDay(calEnd);
		
		final Calendar previousCal = Calendar.getInstance();
		previousCal.setTime(calStart.getTime());
		previousCal.add(Calendar.DAY_OF_MONTH, -1);
		
		ReportDAO errRptDao = new ReportDAO();
		
		return errRptDao.getPotentialNonConformities(calStart.getTime(), calEnd.getTime());
		
	}
	
	public String getFormattedMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		
		String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
		return monthNames[month];
	}
	
	public boolean isInFuture(String date) {
		Calendar cal = Calendar.getInstance();
		CalendarUtil.toBeginningOfTheDay(cal);
		
		Date toCompare = new Date(Long.parseLong(date));
		
		if(toCompare.getTime() > cal.getTime().getTime()) {
			return true;
		}
		
		return false;
	}
	
	public int getYear() {
		return this.year;
	}
	
	public int getMonth() {
		return this.month;
	}
}