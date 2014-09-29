package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import net.waqassiddiqi.app.crew.db.VesselDAO;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.util.InputValidator;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.extended.layout.TableLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebTextField;

public class AddVesselForm extends BaseForm implements ActionListener {
	private int id = -1;
	
	private WebTextField txtName;
	private WebTextField txtImo;
	private WebTextField txtFlag;
	private WebTextField txtVesselId;
	private Vessel currentVessel;
	private WebTabbedPane tabPan;
	
	public AddVesselForm(MainFrame owner) {
		this(owner, -1);
	}
	
	public AddVesselForm(MainFrame owner, int id) {
		super(owner);
		this.id = id;
		
		addChangeListener(getOwner());
	}
	
	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Manage Vessel") {{ setDrawShade(true); setMargin(10); }});
		super.setupToolBar();
	}
	
	@Override
	public Component prepareView() {
		tabPan = new WebTabbedPane ();
        tabPan.setOpaque(false);
        tabPan.setTabPlacement(WebTabbedPane.TOP);
        
        tabPan.addTab("  Vessel Details   ", getForm());
        
        return tabPan;
	}
	
	private Component getForm() {
		TableLayout layout = new TableLayout(new double[][] {
				{ TableLayout.PREFERRED, TableLayout.PREFERRED },
				{ TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED,
						TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED } });
		layout.setHGap(5);
		layout.setVGap(5);
		
		WebPanel content = new WebPanel(layout);
		content.setMargin(15, 30, 15, 30);
		content.setOpaque(false);
		
		txtVesselId = new WebTextField(15);
		txtVesselId.setEnabled(false);
		
		txtVesselId.setVisible(false);
		
		txtName = new WebTextField(15);
		txtFlag = new WebTextField(15);
		txtImo = new WebTextField(15);
		
		//content.add(new WebLabel("Vessel ID", WebLabel.TRAILING), "0,0");		
		//content.add(txtVesselId, "1,0");

		content.add(new WebLabel("Name", WebLabel.TRAILING), "0,1");
		content.add(txtName, "1,1");
		
		content.add(new WebLabel("IMO", WebLabel.TRAILING), "0,2");
		content.add(txtImo, "1,2");
		
		content.add(new WebLabel("Flag", WebLabel.TRAILING), "0,3");
		content.add(txtFlag, "1,3");
		
		
		
		return content;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WebButton btnSource = (WebButton) e.getSource();
		
		if(btnSource.getClientProperty("command").equals("close")) {
			
		} else if(btnSource.getClientProperty("command").equals("save")) {
			
			if(InputValidator.validateInput(getOwner(), new JTextField[] { 
				txtName, txtImo, txtFlag }, 
					"This field cannot be empty")) {
				
				Vessel vessel = new Vessel() {{ 
					setName(txtName.getText().trim());
					setImo(txtImo.getText().trim());
					setFlag(txtFlag.getText().trim());
				}};
				
				if(currentVessel == null) {
					
					this.id = new VesselDAO().addVessel(vessel);
					
					if(this.id > -1) {
						
						txtVesselId.setText(Integer.toString(this.id));
						
						vessel.setId(id);
						this.currentVessel = vessel;
						
						NotificationManager.showNotification("Vessel details has been added");
						
						if(this.changeListener != null)
							this.changeListener.added(vessel);
						
					} else {
						NotificationManager.showNotification("An error has occured while adding new rank");
					}
					
				} else {
					currentVessel.setName(txtName.getText().trim());
					currentVessel.setImo(txtImo.getText().trim());
					currentVessel.setFlag(txtFlag.getText().trim());
					
					new VesselDAO().updateVessel(currentVessel);
					
					NotificationManager.showNotification("Vessel details has been updated.");
				}
			}
		}
	}
	
	public interface ChangeListener {
		void added(Vessel vessel);
	}
	
	private ChangeListener changeListener = null;
	
	public void addChangeListener(ChangeListener l) {
		changeListener = l;
	}
}