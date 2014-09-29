package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;

import net.waqassiddiqi.app.crew.ui.AddRestHourForm;
import net.waqassiddiqi.app.crew.ui.report.ErrorReportForm;
import net.waqassiddiqi.app.crew.ui.report.RestingHourReportForm;

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

}
