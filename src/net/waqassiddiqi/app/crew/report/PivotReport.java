package net.waqassiddiqi.app.crew.report;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.waqassiddiqi.app.crew.db.ErrorReportDAO;
import net.waqassiddiqi.app.crew.util.CalendarUtil;

public class PivotReport {
	private int month;
	private int year;
	
	public PivotReport(int month, int year) {
		this.month = month;
		this.year = year;
	}
	
	public String generateReport() {
		
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
		
		ErrorReportDAO errRptDao = new ErrorReportDAO();
		
		return errRptDao.getByYearAndMonth(calStart.getTime(), calEnd.getTime());
		
	}
	
	public String getFormattedMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		
		return new SimpleDateFormat("MMM").format(cal.getTime());
	}
	
	public int getYear() {
		return this.year;
	}
	
	public int getMonth() {
		return this.month;
	}
}