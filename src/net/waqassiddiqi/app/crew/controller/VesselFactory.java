package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;
import java.util.Map;

import net.waqassiddiqi.app.crew.ui.AddVesselForm;

public class VesselFactory extends BaseFactory {

	private static VesselFactory instance = null;
	
	private VesselFactory() { }
	
	public static VesselFactory getInstance() {
		if(instance == null) {
			instance = new VesselFactory();
		}
		
		return instance;
	}
	
	@Override
	public Component get() {
		return null;
	}

	@Override
	public Component getById(String id) {
		return null;
	}

	@Override
	public Component getEdit(String id) {
		Component c = new AddVesselForm(getOwner(), Integer.parseInt(id)).getView();
		c.setName("addVessel");
		
		return c;
	}

	@Override
	public Component getAdd() {
		Component c = new AddVesselForm(getOwner()).getView();
		c.setName("addVessel");
		
		return c;
	}

	@Override
	public Component getEdit(String id, Map<String, Object> params) {
		return null;
	}
}
