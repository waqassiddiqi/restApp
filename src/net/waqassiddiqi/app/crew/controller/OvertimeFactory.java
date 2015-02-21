package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;
import java.util.Map;

import net.waqassiddiqi.app.crew.ui.AddHolidaysForm;
import net.waqassiddiqi.app.crew.ui.ListHolidaysForm;

public class OvertimeFactory extends BaseFactory {

	private static OvertimeFactory instance = null;
	
	private OvertimeFactory() { }
	
	public static OvertimeFactory getInstance() {
		if(instance == null) {
			instance = new OvertimeFactory();
		}
		
		return instance;
	}
	
	@Override
	public Component get() {
		Component c = new ListHolidaysForm(getOwner()).getView();
		c.setName("listHolidaysList");
		
		return c;
	}

	@Override
	public Component getById(String id) {
		return null;
	}

	@Override
	public Component getEdit(String id) {
		Component c = new AddHolidaysForm(getOwner(), Integer.parseInt(id)).getView();
		c.setName("addHolidays");
		
		return c;
	}

	@Override
	public Component getAdd() {
		Component c = new AddHolidaysForm(getOwner()).getView();
		c.setName("addHolidays");
		
		return c;
		
	}

	@Override
	public Component getEdit(String id, Map<String, Object> params) {
		AddHolidaysForm f = new AddHolidaysForm(getOwner(), Integer.parseInt(id));
		
		Component c = f.getView();
		c.setName("addHolidays");
		
		return c;
	}
}
