package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.laf.desktoppane.WebInternalFrame;

public abstract class BaseChildFrame extends WebInternalFrame {
	private static final long serialVersionUID = 1L;
	private List<Component> components = new ArrayList<Component>();
	private MainFrame owner;
	
	public BaseChildFrame(final MainFrame owner, String title, boolean resizeable, boolean closeable,
			boolean maximizeable, boolean iconfiable) {
		
		super(title, resizeable, closeable, maximizeable, iconfiable);
		this.owner = owner;
	}
	
	protected void addInputField(Component... component) {
		components.addAll(Arrays.asList(component));
	}
	
	@Override
	public void close() {
		
		for(Component c : components) {
			if(c instanceof JTextField)
				((JTextField) c).setText("");
			else if(c instanceof JTextField)
				((JCheckBox) c).setSelected(false);
		}
		
		super.close();
	}
	
	protected boolean validateInput(JTextField[] fields, String errorMessage) {
		if(fields == null)
			return false;
		
		for(JTextField field : fields) {
			if(field.getText().trim().isEmpty()) {
				
				NotificationManager.showPopup(owner, 
						field, new String[] { errorMessage });
				
				return false;
			}
		}
		
		return true;
	}
}
