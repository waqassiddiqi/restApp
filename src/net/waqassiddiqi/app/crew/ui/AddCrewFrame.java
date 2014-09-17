package net.waqassiddiqi.app.crew.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingConstants;

import net.waqassiddiqi.app.crew.db.VesselDAO;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.extended.date.WebDateField;
import com.alee.extended.image.WebImageDrop;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.AlignPanel;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.GroupingType;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.desktoppane.WebInternalFrame;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.text.WebTextField;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyManager;
import com.alee.utils.SwingUtils;

public class AddCrewFrame extends WebInternalFrame {
	private static final long serialVersionUID = 1L;
	private WebComboBox cmbVessel;
	private WebTextField txtFirstName;
	private WebTextField txtLastName;
	private WebTextField txtRank;
	private WebTextField txtNationality;
	private WebTextField txtPassport;
	private WebDateField txtSignonDate;
	private WebCheckBox chkWatchkeeper;
	private List<Vessel> listVessel;
	
	public AddCrewFrame(final MainFrame owner, String title, boolean resizeable, boolean closeable,
			boolean maximizeable, boolean iconfiable) {
		
		super(title, resizeable, closeable, maximizeable, iconfiable);
		
		setDefaultCloseOperation(WebDialog.DISPOSE_ON_CLOSE);

		TableLayout layout = new TableLayout(new double[][] {
				{ TableLayout.PREFERRED, TableLayout.PREFERRED },
				{ TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED,
						TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED } });
		layout.setHGap(5);
		layout.setVGap(5);
		
		WebPanel content = new WebPanel(layout);
		content.setMargin(15, 30, 15, 30);
		content.setOpaque(false);

		listVessel = new VesselDAO().getAll();
		
		cmbVessel = new WebComboBox();
		
		for(Vessel v : listVessel) {
			cmbVessel.addItem(v.getName());
		}
		
		txtSignonDate = new WebDateField ();
		txtSignonDate.setInputPrompt ("Enter date...");
		txtSignonDate.setInputPromptPosition (SwingConstants.CENTER);
		
		chkWatchkeeper = new WebCheckBox("Is watchkeeper?");
		
		
		txtFirstName = new WebTextField(15);
		txtLastName = new WebTextField(15);
		txtRank = new WebTextField(15);
		txtNationality = new WebTextField(15);
		txtPassport = new WebTextField(15);

		content.add(new WebImageDrop(64, 64), "1,0,LEFT,CENTER");

		content.add(new WebLabel("Vessel", WebLabel.TRAILING), "0,1");
		content.add(cmbVessel, "1,1");
		
		content.add(new WebLabel("First Name", WebLabel.TRAILING), "0,2");
		content.add(txtFirstName, "1,2");
		
		content.add(new WebLabel("Last Name", WebLabel.TRAILING), "0,3");
		content.add(txtLastName, "1,3");
		
		content.add(new WebLabel("Rank", WebLabel.TRAILING), "0,4");
		content.add(txtRank, "1,4");
		
		content.add(new WebLabel("Nationality", WebLabel.TRAILING), "0,5");
		content.add(txtNationality, "1,5");
		
		content.add(new WebLabel("Passport / Book Number", WebLabel.TRAILING), "0,6");
		content.add(txtPassport, "1,6");
		
		content.add(new WebLabel("Sign On Date", WebLabel.TRAILING), "0,7");
		content.add(txtSignonDate, "1,7");
		
		content.add(new WebLabel("", WebLabel.TRAILING), "0,8");
		content.add(chkWatchkeeper, "1,8");

		final WebButton login = new WebButton("Save");
		WebButton cancel = new WebButton("Cancel");
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if( ((WebButton) e.getSource()) == login ) {
					
					
					
					
				}
				
			}
		};
		
		login.addActionListener(listener);
		cancel.addActionListener(listener);
		content.add(new CenterPanel(new GroupPanel(5, login, cancel)), "0,9,1,9");
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