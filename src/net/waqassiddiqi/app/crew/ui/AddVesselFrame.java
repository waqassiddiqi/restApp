package net.waqassiddiqi.app.crew.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.waqassiddiqi.app.crew.db.VesselDAO;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.desktoppane.WebInternalFrame;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.text.WebTextField;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyManager;
import com.alee.utils.SwingUtils;

public class AddVesselFrame extends WebInternalFrame {
	private static final long serialVersionUID = 1L;
	private WebTextField txtVesselName;
	private WebTextField txtIMO;
	private WebTextField txtFlag;
	
	public AddVesselFrame(final MainFrame owner, String title, boolean resizeable, boolean closeable,
			boolean maximizeable, boolean iconfiable) {
		
		super(title, resizeable, closeable, maximizeable, iconfiable);
		
		setDefaultCloseOperation(WebDialog.DISPOSE_ON_CLOSE);

		TableLayout layout = new TableLayout(new double[][] {
				{ TableLayout.PREFERRED, TableLayout.PREFERRED },
				{ TableLayout.PREFERRED, TableLayout.PREFERRED,
						TableLayout.PREFERRED, TableLayout.PREFERRED } });
		layout.setHGap(5);
		layout.setVGap(5);
		WebPanel content = new WebPanel(layout);
		content.setMargin(15, 30, 15, 30);
		content.setOpaque(false);

		txtVesselName = new WebTextField(15);
		txtIMO = new WebTextField(15);
		txtFlag = new WebTextField(15);
		
		content.add(new WebLabel("Vessel Name", WebLabel.TRAILING), "0,0");		
		content.add(txtVesselName, "1,0");

		content.add(new WebLabel("IMO", WebLabel.TRAILING), "0,1");
		content.add(txtIMO, "1,1");
		
		content.add(new WebLabel("Flag", WebLabel.TRAILING), "0,2");
		content.add(txtFlag, "1,2");

		final WebButton login = new WebButton("Save");
		WebButton cancel = new WebButton("Cancel");
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if( ((WebButton) e.getSource()) == login ) {
					
					if(!validate(txtVesselName.getText())) {
						
						NotificationManager.showPopup(owner, 
								txtVesselName, new String[] { "Vessel name cannot be empty" });
						
						txtVesselName.requestFocusInWindow();
						
						return;
					} 
					
					if(!validate(txtIMO.getText())) {
						
						NotificationManager.showPopup(owner, 
								txtIMO, new String[] { "IMO cannot be empty" });
						
						txtIMO.requestFocusInWindow();
						
						return;
					}
					
					boolean bAdded = new VesselDAO().addVessel(
							new Vessel() { { 
								setName(txtVesselName.getText());
								setImo(txtIMO.getText());
								setFlag(txtFlag.getText());
							} });
					
					if(bAdded) {
						NotificationManager.showNotification("New vessel has been added");
						
						setVisible(false);
					}
				}
				
			}
		};
		
		login.addActionListener(listener);
		cancel.addActionListener(listener);
		content.add(new CenterPanel(new GroupPanel(5, login, cancel)), "0,3,1,3");
		SwingUtils.equalizeComponentsWidths(login, cancel);

		add(content);

		HotkeyManager.registerHotkey(this, cancel, Hotkey.ESCAPE);
		HotkeyManager.registerHotkey(this, login, Hotkey.ENTER);
	}

	private boolean validate(String val) {
		if(val == null)
			return false;
		
		if(val.trim().isEmpty())
			return false;
		
		return true;
	}
}