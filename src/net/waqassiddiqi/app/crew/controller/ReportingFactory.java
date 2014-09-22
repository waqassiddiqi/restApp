package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;

import net.waqassiddiqi.app.crew.ui.AddRestHourForm;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Component getById(String id) {
		if(id.equals("rest")) {
			return new RestingHourReportForm(getOwner()).getView();
		}
		
		return null;
	}

	@Override
	public Component getEdit(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Component getAdd() {
		return new AddRestHourForm(getOwner()).getView();
	}

}
