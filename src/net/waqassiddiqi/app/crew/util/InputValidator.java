package net.waqassiddiqi.app.crew.util;

import javax.swing.JTextField;

import net.waqassiddiqi.app.crew.ui.MainFrame;

public class InputValidator {
	
	public static boolean validateInput(MainFrame owner, JTextField[] fields, String errorMessage) {
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
