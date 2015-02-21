package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.SwingConstants;

import net.waqassiddiqi.app.crew.db.HolidayDAO;
import net.waqassiddiqi.app.crew.model.Holiday;
import net.waqassiddiqi.app.crew.model.HolidayList;
import net.waqassiddiqi.app.crew.util.CalendarUtil;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.extended.date.DateSelectionListener;
import com.alee.extended.date.WebDateField;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.list.CheckBoxListModel;
import com.alee.extended.list.WebCheckBoxList;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebTextField;

public class AddHolidaysForm extends BaseForm implements ActionListener {
	private int id = -1;
	
	private WebTextField txtHolidayName;
	private WebDateField txtValidFrom;
	private WebDateField txtValidTo;
	private WebTabbedPane tabPan;
	private WebCheckBoxList webCheckBoxList;
	private HolidayList currentHolidayList = null;
	private HolidayDAO holidayDao = new HolidayDAO();
	
	public AddHolidaysForm(MainFrame owner) {
		this(owner, -1);
	}
	
	public AddHolidaysForm(MainFrame owner, int id) {
		super(owner);
		this.id = id;
	}
	
	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Overtime / Holidays") {{ setDrawShade(true); setMargin(10); }});
		super.setupToolBar();
	}
	
	@SuppressWarnings("serial")
	@Override
	public Component prepareView() {
		
		tabPan = new WebTabbedPane () {
		
			@Override
			public void setSelectedIndex(int newIndex) {
				super.setSelectedIndex(newIndex);
			}
			
		};
		
        tabPan.setOpaque(false);
        tabPan.setTabPlacement(WebTabbedPane.TOP);
        
        
        //WebScrollPane scrollPane = new WebScrollPane(new GroupPanel(false, new WebLabel("List of days")).setMargin(5));
        
        tabPan.addTab("  Holiday Details   ", getForm());
        //tabPan.addTab("  Holiday Dates ", getHolidyasForm());
        //tabPan.setSelectedIndex(defaultTabIndex);
        
        bindData();
        
        tabPan.setContentInsets(new Insets(10, 10, 10, 10));
        
        return tabPan;
	}
	
	/*
	private Component getHolidyasForm() {
		calendar = new ExWebCalendar(new Date(), new Date());
		
		GroupPanel leftPanel = new GroupPanel(false, 
				new GroupPanel(10, new WebLabel("Holiday dates")),
				new GroupPanel(10, false, calendar));
		
		GroupPanel rightPanel = new GroupPanel(false, 
				new WebLabel("Resting Hours") {{ setDrawShade(true); }}, 
				new WebLabel("Comments: "));
		
		return new GroupPanel(GroupingType.fillLast, 20, leftPanel, rightPanel).setMargin(10);
	}
	*/
	
	@SuppressWarnings("unchecked")
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
		
		webCheckBoxList = new WebCheckBoxList();
		webCheckBoxList.setMultiplySelectionAllowed(true);
		webCheckBoxList.setVisibleRowCount(5);
		
		txtValidFrom = new WebDateField ();
		txtValidFrom.setInputPrompt ("Select date...");
		txtValidFrom.setDate(new Date());
		txtValidFrom.setInputPromptPosition (SwingConstants.CENTER);
		
		Calendar calNext = Calendar.getInstance();
		calNext.setTime(new Date());
		calNext.add(Calendar.DAY_OF_MONTH, 3);
		
		txtValidTo = new WebDateField ();
		txtValidTo.setInputPrompt ("Select date...");
		txtValidTo.setDate(calNext.getTime());
		txtValidTo.setInputPromptPosition (SwingConstants.CENTER);
		
		txtValidFrom.addDateSelectionListener(new DateSelectionListener() {
			
			@Override
			public void dateSelected(Date date) {
				if(date.compareTo(txtValidTo.getDate()) > 0) {
					txtValidTo.setDate(txtValidFrom.getDate());
				}
				
				webCheckBoxList.setModel(getHolidayList(txtValidFrom.getDate(), txtValidTo.getDate()));
			}
		});
		
		txtValidTo.addDateSelectionListener(new DateSelectionListener() {
			
			@Override
			public void dateSelected(Date date) {
				if(date.compareTo(txtValidFrom.getDate()) < 0) {
					txtValidTo.setDate(txtValidFrom.getDate());
				}
				
				webCheckBoxList.setModel(getHolidayList(txtValidFrom.getDate(), txtValidTo.getDate()));
			}
		});
		
		txtHolidayName = new WebTextField(15);

		content.add(new WebLabel("Holiday Name", WebLabel.TRAILING), "0,1");
		content.add(txtHolidayName, "1,1");
		
		content.add(new WebLabel("Valid From", WebLabel.TRAILING), "0,2");
		content.add(txtValidFrom, "1,2");
		
		content.add(new WebLabel("Valid Till", WebLabel.TRAILING), "0,3");
		content.add(txtValidTo, "1,3");
		
		webCheckBoxList.setModel(getHolidayList(txtValidFrom.getDate(), txtValidTo.getDate()));
		
		content.add(new WebLabel("List of holidays", WebLabel.TRAILING), "0,5");
		content.add(new WebScrollPane(webCheckBoxList), "1,5");
		
		return content;
	}
	
	private CheckBoxListModel getHolidayList(Date start, Date end) {
		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		
		calStart.setTime(start);
		calEnd.setTime(end);
		
		CalendarUtil.toBeginningOfTheDay(calStart);
		CalendarUtil.toBeginningOfTheDay(calEnd);
		
		long timeDifference = calEnd.getTime().getTime() - calStart.getTime().getTime();
		long daysInBetween = timeDifference / (24*60*60*1000);
		
		CheckBoxListModel model = new CheckBoxListModel();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		for(int i=0; i<=daysInBetween; i++) {
			model.addCheckBoxElement(sdf.format(calStart.getTime()), false);
			calStart.add(Calendar.DATE, 1);
		}
		
		return model;
		
	}
	
	private void bindData() {
		
		HolidayDAO holidayDao = new HolidayDAO();
		
		if(this.id > 0) {
			currentHolidayList = holidayDao.getHolidayList(id);
			
			if(currentHolidayList != null) {
				
				txtHolidayName.setText(currentHolidayList.getName());
				txtValidFrom.setDate(currentHolidayList.getFrom());
				txtValidTo.setDate(currentHolidayList.getTo());
				
				List<Holiday> list = holidayDao.getHolidayByListId(id);
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
				
				for(int i=0; i<webCheckBoxList.getModelSize(); i++) {
					for(Holiday h : list) {
						
						if(sdf.format(h.getHolidayDate()).equals((
								(com.alee.extended.list.CheckBoxCellData) webCheckBoxList
									.getValueAt(i)).getUserObject().toString())) {
							webCheckBoxList.setCheckBoxSelected(i, true);
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WebButton btnSource = (WebButton) e.getSource();
		
		if(btnSource.getClientProperty("command").equals("close")) {
			
		} else if(btnSource.getClientProperty("command").equals("new")) {
			
			save();
			
			this.id = -1;
			
			txtHolidayName.setText("");
			txtValidFrom.setText("");
			txtValidTo.setText("");
									
		} else if(btnSource.getClientProperty("command").equals("save")) {
			save();
		}
	}
	
	private boolean save() {
		
		final List<HolidayList> listHolidays = holidayDao.getAllHolidayList();
		if(listHolidays.size() > 0) {
			
			boolean bExists = false;
			
			for(HolidayList holiday : listHolidays) {
				
				if(currentHolidayList == null) {
					if(holiday.getName().equalsIgnoreCase(txtHolidayName.getText().trim())) {
						bExists = true;
						break;
					}
				} else {
					if(holiday.getName().equalsIgnoreCase(txtHolidayName.getText().trim()) 
							&& holiday.getId() != currentHolidayList.getId()) {
						
						bExists = true;
						break;
					}
				}
				
			}
			
			if(bExists) {
				NotificationManager.showPopup(getOwner(), txtHolidayName, 
						new String[] { "Holiday list with name '" + txtHolidayName.getText().trim() + "' already exists" });
				return false;
			}
		}
		
		if(currentHolidayList != null) {
			currentHolidayList.setFrom(txtValidFrom.getDate());
			currentHolidayList.setTo(txtValidTo.getDate());
			currentHolidayList.setName(txtHolidayName.getText().trim());
			
			int rowsUpdated = holidayDao.updateHolidayList(currentHolidayList);
			addHolidaysToList(currentHolidayList.getId());
			
			if(rowsUpdated > 0) {
				NotificationManager.showNotification("Holiday list has been updated");
				return true;
			} else {
				NotificationManager.showNotification("An error has occured while updating holiday list");
				return false;
			}
			
		} else {
			
			currentHolidayList = new HolidayList();
			currentHolidayList.setFrom(txtValidFrom.getDate());
			currentHolidayList.setTo(txtValidTo.getDate());
			currentHolidayList.setName(txtHolidayName.getText().trim());
			
			this.id = holidayDao.addHolidayList(currentHolidayList);
			
			if(this.id > -1) {
				currentHolidayList.setId(id);
				addHolidaysToList(id);
				
				NotificationManager.showNotification("New holiday list has been added");
				return true;
			} else {
				NotificationManager.showNotification("An error has occured while adding new holiday list");
				return false;
			}
		}
	}
	
	private void addHolidaysToList(int holidayListId) {
		holidayDao.removeHolidaysFromHolidayList(holidayListId);
		
		List<Object> l = webCheckBoxList.getCheckedValues();
		Holiday holiday = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		for(Object o : l) {
			
			try {
				
				Date date = sdf.parse(o.toString());
				
				holiday = new Holiday();
				holiday.setDescription("");
				holiday.setHolidayDate(date);
				holiday.setHolidayListId(id);
				
				holidayDao.addHoliday(holiday);
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
}