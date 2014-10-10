package net.waqassiddiqi.app.crew.report;

import java.util.List;

import net.waqassiddiqi.app.crew.db.ReportDAO;

public class WorkingArragementsReport {
	
	public List<String[]> generateReport() {
		ReportDAO errRptDao = new ReportDAO();		
		return errRptDao.getWorkingArragements();		
	}
}