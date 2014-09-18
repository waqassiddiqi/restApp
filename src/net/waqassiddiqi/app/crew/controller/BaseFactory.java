package net.waqassiddiqi.app.crew.controller;

import java.awt.Component;

public abstract class BaseFactory {
	
	public enum ControllerAction { Get, GetById, Edit, Add };
	
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
	
	public abstract Component get();
	public abstract Component getById(String id);
	public abstract Component getEdit(String id);
	public abstract Component getAdd();
}
