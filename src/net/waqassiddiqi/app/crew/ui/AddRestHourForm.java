package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.waqassiddiqi.app.crew.db.CrewDAO;
import net.waqassiddiqi.app.crew.db.EntryTimeDAO;
import net.waqassiddiqi.app.crew.db.ScheduleTemplateDAO;
import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.model.EntryTime;
import net.waqassiddiqi.app.crew.ui.control.TimeSheet;
import net.waqassiddiqi.app.crew.ui.control.TimeSheet.ChangeListener;
import net.waqassiddiqi.app.crew.util.CalendarUtil;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.extended.date.DateSelectionListener;
import com.alee.extended.date.WebCalendar;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.GroupPanel;
import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebTextField;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyManager;

public class AddRestHourForm extends BaseForm implements ActionListener, ChangeListener, DateSelectionListener {
	
	private WebComboBox cmbCrew;
	private WebTextField txtHoursOfRest24Hrs;
	private WebTextField txtHoursOfWork24Hrs;
	private WebCheckBox chkOnPort;
	private WebTabbedPane tabPan;
	private Crew currentCrew = null;
	private List<Crew> listCrew;
	private TimeSheet timeSheet;
	private ScheduleTemplateDAO scheduleTemplateDao;
	private WebCalendar calendar;
	private WebCheckBox chkAutoSave;
	private Date currentDate;
	
	public AddRestHourForm(MainFrame owner) {
		this(owner, -1);
	}
	
	public AddRestHourForm(MainFrame owner, int id) {
		super(owner);
		
		scheduleTemplateDao = new ScheduleTemplateDAO();
		currentDate = new Date();
		calendar = new WebCalendar(currentDate);
	}
	
	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Resting Hour Report Entry") {{ setDrawShade(true); setMargin(10); }});
		
		getToolbar().addSeparator();
		
		WebButton btnSaveNext = WebButton.createIconWebButton(getIconsHelper().loadIcon("common/save_all_16x16.png"),
				StyleConstants.smallRound, true);
		btnSaveNext.putClientProperty("command", "saveAndNext");
		btnSaveNext.addActionListener(this);
		btnSaveNext.setToolTipText("Save and move to next date (CTRL + Shift + S)");
		
		getToolbar().add(btnSaveNext);
		
		HotkeyManager.registerHotkey(getOwner(), btnSaveNext, Hotkey.CTRL_SHIFT_S);
		
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
		
		calendar.addDateSelectionListener(this);
		
		cmbCrew = new WebComboBox();
		cmbCrew.setActionCommand("crewSelectedChanged");
		cmbCrew.addActionListener(this);
		chkOnPort = new WebCheckBox("on Port?");
		chkAutoSave = new WebCheckBox("Auto save on change date");
		
		refreshData();
		bindData();
		
		GroupPanel leftPanel = new GroupPanel(false, 
				new GroupPanel(10, cmbCrew, chkOnPort),
				new GroupPanel(10, false, calendar, chkAutoSave));
		
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
		
		if(e.getSource() instanceof WebComboBox) {
			
			if( ((WebComboBox) e.getSource()).getActionCommand().equals("crewSelectedChanged")) {
				
				if(cmbCrew.getSelectedItem() instanceof Crew) {
					
					currentCrew = (Crew) cmbCrew.getSelectedItem();
					
					currentCrew.setScheduleTemplate(scheduleTemplateDao.getByCrew(currentCrew));
					
					if(currentCrew.getScheduleTemplate() != null) {
						timeSheet.setSchedule(currentCrew.getScheduleTemplate().getSchedule());
					}
				}
				
			} 
			
		} else {
			WebButton btnSource = (WebButton) e.getSource();
			
			if(btnSource.getClientProperty("command").equals("saveAndNext")) {
				
			} else if(btnSource.getClientProperty("command").equals("new")) {
				
				
			} else if(btnSource.getClientProperty("command").equals("save")) {
				
				if(currentCrew == null) {
					NotificationManager.showPopup(getOwner(), cmbCrew, new String[] { "Please select crew" });
					return;
				}
				
				saveRestingHour();
			}
		}
	}
	
	private void saveRestingHour() {
		
		if(currentCrew != null) {
			EntryTime entryTime = new EntryTime();
			
			entryTime.setEntryDate(getDate(currentDate));
			entryTime.setCrewId(currentCrew.getId());
			entryTime.setComments("");
			entryTime.setOnPort(chkOnPort.isSelected());
			entryTime.setSchedule(timeSheet.getSchedule());
			entryTime.setWorkIn24Hours(timeSheet.getTotalWork());
			entryTime.setRestIn24Hours(timeSheet.getTotalRest());
			
			if(new EntryTimeDAO().addUpdateEntry(entryTime) > 0) {
				NotificationManager.showNotification("<html>Resting hours has been saved for<br/>" + 
						CalendarUtil.format("MMM dd, yyyy", getDate(this.calendar.getDate())) + "</html>");
			}
		}
	}

	@Override
	public void changed(float totalRest, float totalWork) {
		txtHoursOfRest24Hrs.setText(Float.toString(totalRest));
		txtHoursOfWork24Hrs.setText(Float.toString(totalWork));
	}

	@Override
	public void dateSelected(Date selectedDate) {
		if(currentCrew != null) {
			
			if(chkAutoSave.isSelected()) {
				saveRestingHour();
			}
			
			EntryTime entry = new EntryTimeDAO().getByDateAndCrew(getDate(selectedDate), currentCrew);
			
			if(entry != null) {
				timeSheet.setSchedule(entry.getSchedule());
			}
		}
		
		currentDate = selectedDate;
	}
	
	private Date getDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		CalendarUtil.toBeginningOfTheDay(cal);
		
		return cal.getTime();
	}
}