package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;
import java.security.InvalidParameterException;
import java.util.Map;

import net.waqassiddiqi.app.crew.ui.MainFrame;

public abstract class BaseFactory {
	
	public enum ControllerAction { Get, GetById, Edit, Add };
	private MainFrame owner = null;
	
	public Component getView(ControllerAction action, Object... params) {
		
		switch(action) {
		case Add:
			return getAdd();
			
		case Edit:
			return getEdit(String.valueOf(params[0]));
			
		case GetById:
			return getEdit(String.valueOf(params[0]));
			
			default:
				return get();
		}	
	}
	
	public void setOwner(MainFrame owner) {
		if(owner != null)
			this.owner = owner;
		else
			throw new InvalidParameterException("Owner cannot be set more than once");
	}
	
	public MainFrame getOwner() {
		return this.owner;
	}
	
	public abstract Component get();
	public abstract Component getById(String id);
	public abstract Component getEdit(String id);
	public abstract Component getEdit(String id, Map<String, Object> params);
	public abstract Component getAdd();
}
