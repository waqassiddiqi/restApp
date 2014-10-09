package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Scanner;

import javax.swing.BorderFactory;

import net.waqassiddiqi.app.crew.db.RegistrationSettingDAO;
import net.waqassiddiqi.app.crew.db.VesselDAO;
import net.waqassiddiqi.app.crew.model.RegistrationSetting;
import net.waqassiddiqi.app.crew.model.Vessel;

import org.apache.log4j.Logger;

import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.CollapsiblePaneAdapter;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.WebCollapsiblePane;
import com.alee.extended.progress.WebProgressOverlay;
import com.alee.laf.button.WebButton;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.progressbar.WebProgressBar;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;
import com.alee.laf.text.WebTextPane;

public class ProductActivationForm extends BaseForm {

	private Logger log = Logger.getLogger(getClass().getName());
	private WebFileChooser fileChooser = null;
	private File saveFileTarget = null;
	private WebTextPane reportPane;
	private String generatedHtml = "";
	private final WebProgressOverlay poBtnPdf;
	private WebScrollPane scrollPane;
	private URL urlCustomFont = null;
	private WebTextField txtUniqueId;
	private WebTextField txtLicenseKey;
	
	private WebTextField txtName;
	private WebTextField txtImo;
	
	public ProductActivationForm(MainFrame owner) {
		super(owner);
        
        poBtnPdf = new WebProgressOverlay();
        poBtnPdf.setConsumeEvents(false);
	}
	
	@Override
	public Component prepareView() {
		
		final WebProgressBar progressBar1 = new WebProgressBar ( 0, 100 );
        progressBar1.setValue ( 0 );
        progressBar1.setIndeterminate ( false );
        progressBar1.setStringPainted ( true );
        
        RegistrationSetting s = new RegistrationSettingDAO().get();
        
        Calendar calendar1 = Calendar.getInstance();
        
        
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(s.getExpiry());
        
        
        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds1 - milliseconds2;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        
        if(s.isRegistered() == false)
        	progressBar1.setMaximum(30);
        
        progressBar1.setString(diffDays + " days remaining");
        
        progressBar1.setValue((int) diffDays);
        
        //progressBar1.setString(s)
        
        progressBar1.setPreferredWidth(300);
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
		
		txtUniqueId = new WebTextField(15);
		txtUniqueId.setEnabled(false);
		
		txtLicenseKey = new WebTextField(15);
		
		WebLabel l = new WebLabel("System ID");
		l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 50));
		
		content.add(l, "0,1");
		content.add(txtUniqueId, "1,1");
		
		l = new WebLabel("Product Key");
		l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 50));
		
		content.add(l, "0,2");
		content.add(new GroupPanel(txtLicenseKey, new WebButton("Activate")), "1,2");
		
		txtLicenseKey.setText("XXXX-XXXX-XXXX-XXXX");
		
		Process process;
		try {
			process = Runtime.getRuntime().exec(new String[] { "wmic", "bios", "get", "serialnumber" });
			process.getOutputStream().close();
	        Scanner sc = new Scanner(process.getInputStream());
	        String property = sc.next();
	        String serial = sc.next();
	        
	        
	        txtUniqueId.setText(serial);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		WebButton btnSource = (WebButton) e.getSource();
		
		if(btnSource.getClientProperty("command").equals("pdf")) {
		}
	}
}
