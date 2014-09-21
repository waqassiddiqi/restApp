package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JTextField;

import net.waqassiddiqi.app.crew.db.CrewDAO;
import net.waqassiddiqi.app.crew.db.ScheduleTemplateDAO;
import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.model.ScheduleTemplate;
import net.waqassiddiqi.app.crew.ui.control.TimeSheet;
import net.waqassiddiqi.app.crew.ui.control.TimeSheet.ChangeListener;
import net.waqassiddiqi.app.crew.util.InputValidator;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.extended.date.WebCalendar;
import com.alee.extended.date.WebDateField;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebTextField;

public class AddRestHourForm extends BaseForm implements ActionListener, ChangeListener {
	private int id = -1;
	
	private WebComboBox cmbCrew;
	private WebTextField txtHoursOfRest24Hrs;
	private WebTextField txtHoursOfWork24Hrs;
	private WebTextField txtNationality;
	private WebTextField txtPassport;
	private WebDateField txtSignonDate;
	private WebCheckBox chkWatchkeeper;
	private WebTabbedPane tabPan;
	private Crew currentCrew = null;
	private List<Crew> listCrew;
	private TimeSheet timeSheet;;
	
	public AddRestHourForm(MainFrame owner) {
		super(owner);
	}
	
	public AddRestHourForm(MainFrame owner, int id) {
		super(owner);
		this.id = id;
	}
	
	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Repotring") {{ setDrawShade(true); setMargin(10); }});
		super.setupToolBar();
	}
	
	@Override
	public Component prepareView() {
		tabPan = new WebTabbedPane ();
        tabPan.setOpaque(false);
        tabPan.setTabPlacement(WebTabbedPane.TOP);
        
        tabPan.addTab("  Enter Hour Details   ", getForm());
        
        return tabPan;
	}
	
	@SuppressWarnings("serial")
	private Component getForm() {	
		
		cmbCrew = new WebComboBox();
		refreshData();
		bindData();
		
		GroupPanel leftPanel = new GroupPanel(false, 
				new WebLabel("Select Crew:"),
				cmbCrew,
				new WebCalendar(new Date()));
		
		timeSheet = new TimeSheet(22);
		timeSheet.setChangeListener(this);
		
		GroupPanel rightPanel = new GroupPanel(false, 
				new WebLabel("Resting Hours") {{ setDrawShade(true); }}, 
				timeSheet.getView(), getGrid());
		
		
		return new GroupPanel(20, leftPanel, rightPanel).setMargin(10);
	}
	
	private Component getGrid() {
		TableLayout layout = new TableLayout(new double[][] {
				{ TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED },
				{ TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED } });
		layout.setHGap(5);
		layout.setVGap(5);
		
		WebPanel content = new WebPanel(layout);
		content.setOpaque(false);
		
		txtHoursOfRest24Hrs = new WebTextField(5);
		txtHoursOfWork24Hrs = new WebTextField(5);
		
		txtHoursOfRest24Hrs.setText(Float.toString(timeSheet.getTotalRest()));
		txtHoursOfWork24Hrs.setText(Float.toString(timeSheet.getTotalWork()));
		
		txtHoursOfRest24Hrs.setEnabled(false);
		txtHoursOfWork24Hrs.setEnabled(false);
		
		content.add(new WebLabel("Hours of rest in 24 hours period"), "0,1");
		content.add(txtHoursOfRest24Hrs, "1,1");
		
		content.add(new WebLabel("Hours of work in 24 hours period"), "0,2");
		content.add(txtHoursOfWork24Hrs, "1,2");
		
		return content;
	}

	private void refreshData() {
		listCrew = new CrewDAO().getAll();
	}
	
	@SuppressWarnings("unchecked")
	private void bindData() {
		
		cmbCrew.addItem("Select Crew");
		
		for(Crew c : listCrew) {
			cmbCrew.addItem(c);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WebButton btnSource = (WebButton) e.getSource();
		
		if(btnSource.getClientProperty("command").equals("close")) {
			
		} else if(btnSource.getClientProperty("command").equals("new")) {
			this.id = -1;
			this.currentCrew = null;
			
			txtHoursOfRest24Hrs.setText("0");
			txtHoursOfWork24Hrs.setText("0");
			
			txtNationality.setText("");
			txtPassport.setText("");
			txtSignonDate.setText("");
			
		} else if(btnSource.getClientProperty("command").equals("save")) {
			
			if(cmbCrew.getSelectedItem() instanceof Crew) {
				
			} else {
				NotificationManager.showPopup(getOwner(), cmbCrew, new String[] { "Please select crew" });
				return;
			}
		}
	}

	@Override
	public void changed(float totalRest, float totalWork) {
		txtHoursOfRest24Hrs.setText(Float.toString(totalRest));
		txtHoursOfWork24Hrs.setText(Float.toString(totalWork));
	}
}