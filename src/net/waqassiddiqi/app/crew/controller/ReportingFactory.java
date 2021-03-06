package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;
import java.util.Map;

import net.waqassiddiqi.app.crew.ui.AddRestHourForm;
import net.waqassiddiqi.app.crew.ui.report.ErrorReportForm;
import net.waqassiddiqi.app.crew.ui.report.MonthlyOvertimeRestReportForm;
import net.waqassiddiqi.app.crew.ui.report.OvertimeSummaryReportForm;
import net.waqassiddiqi.app.crew.ui.report.PivotReportForm;
import net.waqassiddiqi.app.crew.ui.report.PotentialNCReportForm;
import net.waqassiddiqi.app.crew.ui.report.RestingHourReportForm;
import net.waqassiddiqi.app.crew.ui.report.WorkingArragmentReportForm;

public class ReportingFactory extends BaseFactory {

	private static ReportingFactory instance = null;
	
	private ReportingFactory() { }
	
	public static ReportingFactory getInstance() {
		if(instance == null) {
			instance = new ReportingFactory();
		}
		
		return instance;
	}
	
	@Override
	public Component get() {
		return null;
	}

	@Override
	public Component getById(String id) {
		
		Component c = null;
		
		if(id.equals("rest")) {
			c = new RestingHourReportForm(getOwner()).getView();
			c.setName("restReport");
		} else if(id.equals("error")) {
			c = new ErrorReportForm(getOwner()).getView();
			c.setName("errorReport");
		} else if(id.equals("pivot")) {
			c = new PivotReportForm(getOwner()).getView();
			c.setName("pivotReport");
		} else if(id.equals("potential")) {
			c = new PotentialNCReportForm(getOwner()).getView();
			c.setName("potentialReport");
		} else if(id.equals("arragement")) {
			c = new WorkingArragmentReportForm(getOwner()).getView();
			c.setName("workArragementReport");
		} else if(id.equals("overtimeSummary")) {
			c = new OvertimeSummaryReportForm(getOwner()).getView();
			c.setName("overtimeSummary");
		} else if(id.equals("monthlyOvertime")) {
			c = new MonthlyOvertimeRestReportForm(getOwner()).getView();
			c.setName("monthlyOvertime");
		}
		
		return c;
	}

	@Override
	public Component getEdit(String id) {
		return null;
	}

	@Override
	public Component getAdd() {
		Component c = new AddRestHourForm(getOwner()).getView();
		c.setName("addRestHour");
		
		return c;
	}

	@Override
	public Component getEdit(String id, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

}
