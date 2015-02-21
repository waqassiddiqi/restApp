package net.waqassiddiqi.app.crew.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.waqassiddiqi.app.crew.db.CrewDAO;
import net.waqassiddiqi.app.crew.db.EntryTimeDAO;
import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.model.EntryTime;
import net.waqassiddiqi.app.crew.model.OvertimeSummary;
import net.waqassiddiqi.app.crew.model.WageDetails;
import net.waqassiddiqi.app.crew.util.CalendarUtil;

public class OvertimeSummaryReport {
	private int month;
	private int year;
	private EntryTimeDAO entryTimeDao = null;
	private CrewDAO crewDao = null;
	private List<OvertimeSummary> listSummary;
	
	public OvertimeSummaryReport(int year, int month) {
		this.month = month;
		this.year = year;
		
		crewDao = new CrewDAO();
	}
	
	public void generateReport() {
		listSummary = new ArrayList<OvertimeSummary>();
		entryTimeDao = new EntryTimeDAO();
		OvertimeSummary summary = null;
		List<Crew> list = crewDao.getAllActive();
		List<EntryTime> lstEntryTimes = null;
		
		final Calendar calStart = Calendar.getInstance();
		final Calendar calEnd = Calendar.getInstance();
		
		calStart.set(Calendar.YEAR, year);
		calStart.set(Calendar.MONTH, month);
		
		calEnd.set(Calendar.YEAR, year);
		calEnd.set(Calendar.MONTH, month);
		
		calStart.setTime(CalendarUtil.getFirstDayOfMonth(calStart.getTime()));
		calEnd.setTime(CalendarUtil.getLastDayOfMonth(calEnd.getTime()));
		
		double totalHours = 0.0d;
		
		for(Crew c :  list) {
			summary = new OvertimeSummary();
			summary.setCrew(c);
			
			totalHours = 0.0d;
			
			WageDetails w = crewDao.getWageDetailsByCrewId(c.getId());
			summary.setFixedOvertime(w == null ? 0.0d : w.getMonthlyFixedOverrtimeHours());
			lstEntryTimes = entryTimeDao.getByYearMonthAndCrew(calStart.getTime(), calEnd.getTime(), c);
			
			if(lstEntryTimes != null) {
				for(int i=0; i<lstEntryTimes.size(); i++) {
					totalHours += (24 - lstEntryTimes.get(i).getTotalRestHours());
				}
			}
			
			summary.setTotalHours(totalHours);
			if(summary.getTotalHours() > summary.getFixedOvertime())
				summary.setExcessOvertime(summary.getTotalHours() - summary.getFixedOvertime());
			
			summary.setTotalOvertime(summary.getExcessOvertime() + summary.getHoliday());
			
			listSummary.add(summary);
		}
	}
	
	public List<OvertimeSummary> getOvertimeSummaryList() {
		return this.listSummary;
	}
	
	public String getFormattedMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		
		String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
		
		return monthNames[month];
	}
	
	public int getYear() {
		return this.year;
	}
}