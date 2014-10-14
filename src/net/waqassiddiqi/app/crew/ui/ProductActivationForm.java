package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.BorderFactory;

import net.waqassiddiqi.app.crew.db.RegistrationSettingDAO;
import net.waqassiddiqi.app.crew.db.VesselDAO;
import net.waqassiddiqi.app.crew.license.LicenseManager;
import net.waqassiddiqi.app.crew.model.RegistrationSetting;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.util.NotificationManager;
import net.waqassiddiqi.crewapp.license.util.SystemUtil;

import com.alee.extended.label.WebLinkLabel;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.WebCollapsiblePane;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.progressbar.WebProgressBar;
import com.alee.laf.text.WebTextField;

public class ProductActivationForm extends BaseForm {

	private WebTextField txtUniqueId;
	private WebTextField txtLicenseKey;
	
	private WebTextField txtName;
	private WebTextField txtImo;
	
	private WebButton btnActivate; 
	
	final WebProgressBar progressBar1;
	
	public ProductActivationForm(MainFrame owner) {
		super(owner);
		
		progressBar1 = new WebProgressBar(0, 100);
		btnActivate = new WebButton("Activate");
	}
	
	@Override
	public Component prepareView() {
		
		getToolbar().setVisible(false);
		
		 
        progressBar1.setValue ( 0 );
        progressBar1.setIndeterminate ( false );
        progressBar1.setStringPainted ( true );
        
        RegistrationSetting s = new RegistrationSettingDAO().get();
        
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        
        calendar1.setTime(s.getExpiry());
        calendar2.setTime(s.getUsageStartedOn());
        
        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds1 - milliseconds2;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        
        if(s.isRegistered() == false)
        	progressBar1.setMaximum(30);
        else {
        	progressBar1.setMaximum(365);
        	
        	if(diffDays > 0)
        		btnActivate.setVisible(false);
        }
        
        if(diffDays < 1)
        	progressBar1.setString("Expired");
        else
        	progressBar1.setString(diffDays + " days remaining");
        
        progressBar1.setValue((int) diffDays);
        
        progressBar1.setPreferredWidth(410);
        
		GroupPanel panel1 = new GroupPanel(false, getForm(), new GroupPanel(progressBar1).setMargin(8));
		
		final WebCollapsiblePane leftPane = new WebCollapsiblePane("Product License", panel1);
		
		GroupPanel panel2 = new GroupPanel(false, getInfoForm());
		
		final WebCollapsiblePane leftPaneRegInfo = new WebCollapsiblePane("Registration Information", panel2);
		
		return new GroupPanel(10, false, leftPane, leftPaneRegInfo);
	}
	
	private Component getForm() {
		TableLayout layout = new TableLayout(new double[][] {
				{ TableLayout.PREFERRED, TableLayout.PREFERRED },
				{ TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED } });
		layout.setHGap(5);
		layout.setVGap(5);
		
		WebPanel content = new WebPanel(layout);
		content.setOpaque(false);
		
		txtUniqueId = new WebTextField(25);
		txtUniqueId.setEditable(false);
		
		txtLicenseKey = new WebTextField(25);
		
		txtUniqueId.setText(SystemUtil.getRegistrationId());
		
		WebLabel l = new WebLabel("System ID");
		l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 50));
		
		WebLinkLabel lblCopy = new WebLinkLabel ();
		lblCopy.setText("Copy to clipboard");
		lblCopy.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				StringSelection selection = new StringSelection(txtUniqueId.getText());
			    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			    clipboard.setContents(selection, selection);
			    
			    NotificationManager.showNotification("System ID has been copied to clipboard");
			}
		});
		
		WebLinkLabel el = new WebLinkLabel ();
        el.setEmailLink("Email Serial No", "support@shipip.com");
		
		content.add(l, "0,1");
		content.add(new GroupPanel(10, txtUniqueId, lblCopy, el), "1,1");
		
		l = new WebLabel("Product Key");
		l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 50));
		
		content.add(l, "0,2");
		
		
		content.add(new GroupPanel(10, txtLicenseKey, btnActivate), "1,2");
		
		btnActivate.addActionListener(this);
		
		txtLicenseKey.setText("XXXX-XXXX-XXXX-XXXX-XXXX");
		
		return content;
	}
	
	private Component getInfoForm() {
		TableLayout layout = new TableLayout(new double[][] {
				{ TableLayout.PREFERRED, TableLayout.PREFERRED },
				{ TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED } });
		layout.setHGap(5);
		layout.setVGap(5);
		
		WebPanel content = new WebPanel(layout);
		content.setOpaque(false);
		
		
		txtName = new WebTextField(15);
		txtImo = new WebTextField(15);

		WebLabel l = new WebLabel("Vessel Name");
		l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 50));
		
		content.add(l, "0,1");
		content.add(txtName, "1,1");
		
		l = new WebLabel("IMO Number");
		l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 50));
		
		content.add(l, "0,2");
		content.add(txtImo, "1,2");

		Vessel currentVessel = new VesselDAO().getAll().get(0);
		txtName.setText(currentVessel.getName());
		txtImo.setText(currentVessel.getImo());
		
		txtName.setEnabled(false);
		txtImo.setEnabled(false);
		
		return content;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(txtLicenseKey.getText().replace("-", "").trim().length() != 20) {
			WebOptionPane.showMessageDialog(getOwner(), "You have entered an invalid key", "Error", 
					WebOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		if(LicenseManager.registerProduct(txtUniqueId.getText().trim(), txtLicenseKey.getText().trim())) {
			NotificationManager.showNotification("Congradulations, Your product has been registered successfully");
	
			btnActivate.setVisible(false);
			
			RegistrationSetting s = new RegistrationSettingDAO().get();
			
			Calendar calendar1 = Calendar.getInstance();
	        Calendar calendar2 = Calendar.getInstance();
	        
	        calendar1.setTime(s.getExpiry());
	        calendar2.setTime(s.getUsageStartedOn());
	        
	        long milliseconds1 = calendar1.getTimeInMillis();
	        long milliseconds2 = calendar2.getTimeInMillis();
	        long diff = milliseconds1 - milliseconds2;
	        long diffDays = diff / (24 * 60 * 60 * 1000);
	        
	        if(s.isRegistered() == false)
	        	progressBar1.setMaximum(30);
	        else
	        	progressBar1.setMaximum(365);
	        
	        progressBar1.setString(diffDays + " days remaining");
	        
	        progressBar1.setValue((int) diffDays);
			
		} else {
			WebOptionPane.showMessageDialog(getOwner(), "You have entered an invalid key", "Error", 
					WebOptionPane.ERROR_MESSAGE);
		}
	}
}
