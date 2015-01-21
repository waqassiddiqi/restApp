package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;
import java.util.Map;

import net.waqassiddiqi.app.crew.ui.AddCrewForm;
import net.waqassiddiqi.app.crew.ui.AddHolidaysForm;
import net.waqassiddiqi.app.crew.ui.ListCrewForm;

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
		Component c = new ListCrewForm(getOwner()).getView();
		c.setName("listCrew");
		
		return c;
	}

	@Override
	public Component getById(String id) {
		return null;
	}

	@Override
	public Component getEdit(String id) {
		Component c = new AddCrewForm(getOwner(), Integer.parseInt(id)).getView();
		c.setName("addCrew");
		
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
		AddCrewForm f = new AddCrewForm(getOwner(), Integer.parseInt(id));
		
		if(params.containsKey("defaultView"))
			f.setDefaultTabIndex((int) params.get("defaultView"));
		
		Component c = f.getView();
		c.setName("addCrew");
		
		return c;
	}
}
