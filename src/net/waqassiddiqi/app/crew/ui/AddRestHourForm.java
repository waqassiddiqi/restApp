package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.text.AbstractDocument;

import net.waqassiddiqi.app.crew.db.CrewDAO;
import net.waqassiddiqi.app.crew.db.EntryTimeDAO;
import net.waqassiddiqi.app.crew.db.QuickCommentDAO;
import net.waqassiddiqi.app.crew.db.ReportDAO;
import net.waqassiddiqi.app.crew.db.ScheduleTemplateDAO;
import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.model.EntryTime;
import net.waqassiddiqi.app.crew.model.ErrorReportEntry;
import net.waqassiddiqi.app.crew.model.QuickComment;
import net.waqassiddiqi.app.crew.model.ScheduleTemplate;
import net.waqassiddiqi.app.crew.report.ErrorReport;
import net.waqassiddiqi.app.crew.ui.control.BoundsPopupMenuListener;
import net.waqassiddiqi.app.crew.ui.control.DocumentSizeFilter;
import net.waqassiddiqi.app.crew.ui.control.ExWebCalendar;
import net.waqassiddiqi.app.crew.ui.control.TimeSheet;
import net.waqassiddiqi.app.crew.ui.control.TimeSheet.ChangeListener;
import net.waqassiddiqi.app.crew.util.CalendarUtil;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.extended.date.DateSelectionListener;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.GroupingType;
import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebTextArea;
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
	private ExWebCalendar calendar;
	private WebCheckBox chkAutoSave;
	private Date currentDate;
	private WebLabel lblNonConformities;
	private ErrorReport errorReport = null;
	private EntryTimeDAO entryTimeDao;
	private WebTextArea txtComments;
	private ErrorReportEntry errorReportEntry;	
	private WebComboBox cmbCommentList;
	
	public AddRestHourForm(MainFrame owner) {
		this(owner, -1);
	}
	
	public AddRestHourForm(MainFrame owner, int id) {
		super(owner);
				
		currentDate = new Date();
		calendar = new ExWebCalendar(currentDate, null);
		entryTimeDao = new EntryTimeDAO();
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
	
	@SuppressWarnings({ "serial", "unchecked" })
	private Component getForm() {	
		
		calendar.addDateSelectionListener(this);
		
		cmbCrew = new WebComboBox();
		cmbCrew.setActionCommand("crewSelectedChanged");
		cmbCrew.addActionListener(this);
		chkOnPort = new WebCheckBox("on Port?");
		chkOnPort.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getSchedule(currentDate);
			}
		});
		
		chkAutoSave = new WebCheckBox("Auto save on change date");
		
		chkAutoSave.setSelected(true);
		chkAutoSave.setEnabled(false);
		
		refreshData();
		bindData();
		
		GroupPanel leftPanel = new GroupPanel(false, 
				new GroupPanel(10, cmbCrew, chkOnPort),
				new GroupPanel(10, false, calendar, chkAutoSave));
		
		timeSheet = new TimeSheet(20);
		timeSheet.setChangeListener(this);
		
		txtComments = new WebTextArea();
		txtComments.setLineWrap (true);
		txtComments.setWrapStyleWord(true);
		
		AbstractDocument pDoc=(AbstractDocument) txtComments.getDocument();
		pDoc.setDocumentFilter(new DocumentSizeFilter(45));

        WebScrollPane areaScroll = new WebScrollPane(txtComments);
        areaScroll.setPreferredSize(new Dimension(300, 50));
        
        WebTextArea txtNonConformities = new WebTextArea ();
        txtNonConformities.setLineWrap (true);
        txtNonConformities.setWrapStyleWord(true);

        WebScrollPane ncScroll = new WebScrollPane(txtNonConformities);
        ncScroll.setPreferredSize(new Dimension(200, 150));
		
        cmbCommentList = new WebComboBox();
        cmbCommentList.addItem("Select quick comment ...");
        
        final QuickCommentDAO qCommentDao = new QuickCommentDAO();
        final List<QuickComment> qCommentsList = qCommentDao.getAll();
        
        for(QuickComment qComment : qCommentsList) {
        	cmbCommentList.addItem(qComment);
        }
        
        cmbCommentList.addItem("...or add new quick comment");
        
        
        
        cmbCommentList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(cmbCommentList.getSelectedIndex() == cmbCommentList.getItemCount() - 1) {
					Object value = WebOptionPane.showInputDialog (getOwner(), "Add new quick comment:", "Quick Comment", 
							JOptionPane.QUESTION_MESSAGE, null, null, "" );
					
					if(value != null && value.toString().trim().length() > 0) {
						txtComments.setText(value.toString());
						
						if(qCommentsList.size() > 0) {
							boolean bFound = false;
							
							for(int i=0; i<qCommentsList.size(); i++) {
								QuickComment q = qCommentsList.get(i);
								
								if(q.getQuickComment().equalsIgnoreCase(value.toString()) == true) {
									bFound = true;
									break;
								}
							}	
							
							if(!bFound) {
								qCommentDao.addQuickComment(value.toString());
								
								QuickComment newQuickComment = new QuickComment();
								newQuickComment.setQuickComment(value.toString());
								
								qCommentsList.add(newQuickComment);
								
								cmbCommentList.insertItemAt(newQuickComment, cmbCommentList.getItemCount() - 1);
							}
							
						} else {
							QuickComment newQuickComment = new QuickComment();
							newQuickComment.setQuickComment(value.toString());
							
							qCommentsList.add(newQuickComment);
							
							cmbCommentList.insertItemAt(newQuickComment, cmbCommentList.getItemCount() - 2);
						}
						
					}
				} else if(cmbCommentList.getSelectedIndex() > 0) {
					txtComments.setText(cmbCommentList.getSelectedItem().toString());
				}
			}
		});
        
        lblNonConformities = new WebLabel();
        
        GroupPanel panelNonConformity = new GroupPanel(false, new WebLabel("Non-Conformities:"), lblNonConformities);
        
		GroupPanel rightPanel = new GroupPanel(false, 
				new WebLabel("Resting Hours") {{ setDrawShade(true); }}, 
				timeSheet.getView(), new GroupPanel(30, getGrid(), 
						new GroupPanel(false, new WebLabel("Comments: "), areaScroll, cmbCommentList)), panelNonConformity);
		
		return new GroupPanel(GroupingType.fillLast, 20, leftPanel, rightPanel).setMargin(10);
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
		listCrew = new CrewDAO().getAllActive();
	}
	
	@SuppressWarnings("unchecked")
	private void bindData() {
		
		cmbCrew.addItem("Select Crew");
		
		for(Crew c : listCrew) {
				cmbCrew.addItem(c);
		}
		
		BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true, false);
		cmbCrew.addPopupMenuListener( listener );
		cmbCrew.setPrototypeDisplayValue("Last Name, First Name, Rank");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() instanceof WebComboBox) {
			
			if( ((WebComboBox) e.getSource()).getActionCommand().equals("crewSelectedChanged")) {
				if(cmbCrew.getSelectedItem() instanceof Crew) {

					autoSave();
					
					currentCrew = (Crew) cmbCrew.getSelectedItem();

					if(currentCrew != null) {
						
						//calendar = null;
						//calendar = new ExWebCalendar(currentDate, currentCrew.getSignOnDate());
						
						calendar.refresh(currentCrew.getSignOnDate());
						
						errorReportEntry = new ErrorReportEntry();
						errorReportEntry.setEntryDate(getDate(currentDate));
						errorReportEntry.setCrew(currentCrew);
					}
					
					getSchedule(currentDate);
				} 
				
			} 
			
		} else {
			WebButton btnSource = (WebButton) e.getSource();
			
			if(btnSource.getClientProperty("command").equals("saveAndNext")) {
				
				if(currentCrew == null) {
					NotificationManager.showPopup(getOwner(), cmbCrew, new String[] { "Please select crew" });
					return;
				}
				
				//saveRestingHour();
				//calendar.moveToNext();
				//Calendar calNext = Calendar.getInstance();
				//calNext.setTime(currentDate);
				//calNext.add(Calendar.DAY_OF_MONTH, 1);
				
				//calendar.setDate(calNext.getTime());
				
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
			entryTime.setComments(txtComments.getText());
			entryTime.setOnPort(chkOnPort.isSelected());
			entryTime.setSchedule(timeSheet.getSchedule());
			entryTime.setWorkIn24Hours(timeSheet.getTotalWork());
			entryTime.setRestIn24Hours(timeSheet.getTotalRest());
			
			if(entryTimeDao.addUpdateEntry(entryTime) > 0) {
				
				if(errorReportEntry != null)
					new ReportDAO().addErrorReportEntry(errorReportEntry);
				
				NotificationManager.showNotification("<html>Resting hours has been saved for<br/>" + 
						CalendarUtil.format("MMM dd, yyyy", getDate(currentDate)) + "</html>");
			}
		}
	}

	@Override
	public void changed(float totalRest, float totalWork) {
		txtHoursOfRest24Hrs.setText(Float.toString(totalRest));
		txtHoursOfWork24Hrs.setText(Float.toString(totalWork));
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		
		int month = cal.get(Calendar.MONTH);	
		
		StringBuilder sb = new StringBuilder();
		sb.append("<html><ul>");
		
		if(currentCrew != null) {
			
			try {
				
				EntryTime time = new EntryTime();
				time.setSchedule(timeSheet.getSchedule());
				
				double totalWorkHours = 24 - time.getTotalRestHours();
				
				errorReportEntry = new ErrorReportEntry();
				errorReportEntry.setEntryDate(getDate(currentDate));
				errorReportEntry.setCrew(currentCrew);
				
				errorReportEntry.setRestIn24hours(time.getTotalRestHours());
				errorReportEntry.setWorkIn24hours(totalWorkHours);
				
				if(time.getTotalRestHours() < 10) {
					sb.append("<li>Total period of REST > 10 Hours</li>");
					
					errorReportEntry.setRestGreater10hrs(true);
				}
				
				if(totalWorkHours > 14) {
					sb.append("<li>Total period of WORK &lt; 14 Hours</li>");
					
					errorReportEntry.setWorkLess14hrs(true);
				}
				
				errorReport = new ErrorReport(currentCrew, null, month, cal.get(Calendar.YEAR));
				errorReport.generateReport();
				
				if(errorReport.getEntryTimeList().size() >= cal.get(Calendar.DAY_OF_MONTH) - 1) {
					errorReport.getEntryTimeList().get(cal.get(Calendar.DAY_OF_MONTH) - 1).setSchedule(timeSheet.getSchedule());
					errorReport.refresh();
				}
				
				if(errorReport.getContainMoreThan2RestPeriods(cal.get(Calendar.DAY_OF_MONTH)) == true) {
					sb.append("<li>Total number of REST periods are more than 2</li>");
					
					errorReportEntry.setTotalRestPeriods(errorReport.getRestPeriodCounter(cal.get(Calendar.DAY_OF_MONTH)));
				}
				
				if(!errorReport.contain6HourContinuousRest(cal.get(Calendar.DAY_OF_MONTH))) {
					sb.append("<li>At least one period of rest must be of 6 hours in length</li>");
					
					errorReportEntry.setOneRestPeriod6hrs(true);
				}
				
				
				double restHoursIn24Hours = errorReport.get24HourRestHours(cal.get(Calendar.DAY_OF_MONTH));
				if(restHoursIn24Hours < 10) {
					sb.append("<li>Any 24-hour Total Period of REST &gt; 10 Hours</li>");
					
					errorReportEntry.setAnyRest24hours(restHoursIn24Hours);
					
					errorReportEntry.setTotalRest24hrsGreater10hrs(true);
				}
				
				double restHoursIn7Days = errorReport.get7DayRestHours(cal.get(Calendar.DAY_OF_MONTH));
				if(restHoursIn7Days < 77) {
					sb.append("<li>Any 7-days Total Period of REST &gt; 77 Hours</li>");
					
					errorReportEntry.setRest7days(restHoursIn7Days);
					
					errorReportEntry.setTotalRest7daysGreater77hrs(true);
				}
				
				double last3DayTotalRestHours = errorReport.getLast3DayTotalRestHours(cal.get(Calendar.DAY_OF_MONTH));
				if(last3DayTotalRestHours < 36) {
					sb.append("<li>Rest hours in last 3 days are &lt; 36 Hours</li>");
				}
				errorReportEntry.setRestHour3daysGreater36hrs(last3DayTotalRestHours);
				
			} catch(Exception e) { }
			
		}
		
		sb.append("</ul></html>");
		
		lblNonConformities.setText(sb.toString());
		lblNonConformities.repaint();
	}

	@Override
	public void dateSelected(Date selectedDate) {
		
		autoSave();
		currentDate = selectedDate;
		
		if(currentCrew != null) {
			errorReportEntry = new ErrorReportEntry();
			errorReportEntry.setEntryDate(getDate(currentDate));
			errorReportEntry.setCrew(currentCrew);
		}
		
		getSchedule(selectedDate);
		
	}
	
	private void autoSave() {
		if(currentCrew != null && chkAutoSave.isSelected()) {
			saveRestingHour();
			
			txtComments.setText("");
		}
	}
	
	private void getSchedule(Date selectedDate) {
		if(currentCrew != null) {
			EntryTime entry = entryTimeDao.getByDateAndCrew(getDate(selectedDate), currentCrew);
			if(entry != null) {
				timeSheet.setSchedule(entry.getSchedule());
				txtComments.setText(entry.getComments());
			} else {
				ScheduleTemplate template = new ScheduleTemplateDAO().getByCrew(currentCrew, chkOnPort.isSelected(), currentCrew.isWatchKeeper());
				if(template != null)
					timeSheet.setSchedule(template.getSchedule());
			}
		}
	}
	
	public static Date getDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		CalendarUtil.toBeginningOfTheDay(cal);
		
		return cal.getTime();
	}
}