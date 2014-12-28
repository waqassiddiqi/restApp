package net.waqassiddiqi.app.crew.ui.report;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import net.waqassiddiqi.app.crew.report.PivotReport;
import net.waqassiddiqi.app.crew.ui.BaseForm;
import net.waqassiddiqi.app.crew.ui.MainFrame;
import net.waqassiddiqi.app.crew.util.FilesUtil;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.alee.extended.panel.GroupPanel;
import com.alee.extended.progress.WebProgressOverlay;
import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.label.WebLabel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;

public class PivotReportForm extends BaseForm {

	private Logger log = Logger.getLogger(getClass().getName());
	private WebFileChooser fileChooser = null;
	private File saveFileTarget = null;

	private Object[][] data = { };
	private String[] columnNames = { "MONTH", "VESSELNAME","IMONUMBER","CREWNAME","BOOK_NUMBER_OR_PASSPORT","RANK","WATCHKEEPER","DATE","WORK_24HR",
			"REST_24HR","ANY_REST_24HR","REST_7DAYS","Total period of REST > 10 Hrs","Total Period of WORK < 14 Hrs","24-hour Total Period of REST > 10 Hrs",
			"7-days Total Period of REST > 77 Hrs","At least one period of rest 6 hours in length","TOTAL_REST_PERIODS","REST_HRS_GREATER_36_3_DAYS" };
	
	private WebComboBox cmbYear;
	private WebComboBox cmbMonth;
	private WebTable table;
	private PivotTableModel tableModel;
	private List<String[]> csvEntries;
	private final WebProgressOverlay progressOverlay;
	private final WebProgressOverlay poBtnCsv;
	
	@SuppressWarnings("unchecked")
	public PivotReportForm(MainFrame owner) {
		super(owner);
		
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		
		cmbYear = new WebComboBox();
		cmbYear.addItem("Select Year");
		for(int i=2004; i<currentYear + 10; i++) {
			cmbYear.addItem(i);
			
			if(i == currentYear) {
				cmbYear.setSelectedIndex(i - 2003);
			}
		}
		
		cmbMonth = new WebComboBox();
		cmbMonth.addItem("Select Month");
		String[] months = new DateFormatSymbols().getMonths();
		String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
		
        for (int i = 0; i < months.length && i < 12; i++) {
        	cmbMonth.addItem(monthNames[i]);
        	
        	if(i == Calendar.getInstance().get(Calendar.MONTH) + 1) {
        		cmbMonth.setSelectedIndex(i);
        	}
        }
        
        progressOverlay = new WebProgressOverlay();
        progressOverlay.setConsumeEvents(false);
        
        poBtnCsv = new WebProgressOverlay();
        poBtnCsv.setConsumeEvents(false);
	}

	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Pivot Report") {{ setDrawShade(true); setMargin(10); }});
		
		getToolbar().addSeparator();
		
		WebButton btnSaveCsv = WebButton.createIconWebButton(getIconsHelper().loadIcon("common/file_extension_csv_16x16.png"),
				StyleConstants.smallRound, true);
		btnSaveCsv.putClientProperty("command", "csv");
		btnSaveCsv.addActionListener(this);
		btnSaveCsv.setToolTipText("Export as CSV");
		
		poBtnCsv.setComponent(btnSaveCsv);
		
		getToolbar().add(poBtnCsv);
		
		getToolbar().addSeparator();
		
		getToolbar().add(new WebLabel("Filter: ") {{ setMargin(1); }});
		
		WebButton btnFilter = new WebButton("Generate Report", getIconsHelper().loadIcon("common/settings_16x16.png"));
		btnFilter.putClientProperty("command", "filter");
		btnFilter.addActionListener(this);
		progressOverlay.setComponent(btnFilter);
		
		GroupPanel gp = new GroupPanel(cmbMonth, cmbYear, progressOverlay);
		
		getToolbar().add(gp);
	}
	
	@Override
	public Component prepareView() {
		
		tableModel = new PivotTableModel(null, columnNames);
		table = new WebTable(tableModel);
		
		WebScrollPane scrollPane = new WebScrollPane(table);
		
		table.setFocusable(false);
		
		return scrollPane;
	}
	
	private void generateReport(int month, int year) {
		CSVReader reader = null;
		
		try {
			
			reader = new CSVReader(new StringReader(new PivotReport(month, year).generateReport()));
			csvEntries = reader.readAll();
		
			data = new Object[csvEntries.size() - 1][];
			
			if(csvEntries.size() > 1) {
				for(int i=0, j=1; j<csvEntries.size(); j++, i++) {
					data[i] = csvEntries.get(j);
				}
			} else {
				Arrays.fill(data, null);
			}
			
			
		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader != null) reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String generateCsv(String filePath) {
		
		if(filePath.contains(".csv") == false)
			filePath = filePath.trim() + ".csv";
		
		CSVWriter writer = null;
		
		try {
			
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
			writer = new CSVWriter(out);
			writer.writeAll(csvEntries);
			out.close();
			
		} catch (IOException e) {
			log.debug("Error closing resource: " + e.getMessage());
			
			return null;
			
		} finally {
			try {
				if(writer != null) writer.close();
			} catch (Exception ex) {
				log.debug("Error closing resource: " + ex.getMessage());
				
				return null;
			}
		}
		
		return filePath;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WebButton btnSource = (WebButton) e.getSource();
		
		if(btnSource.getClientProperty("command").equals("csv")) {
			
			poBtnCsv.setShowLoad(!poBtnCsv.isShowLoad());
			saveAsCsv();
			
		} else if(btnSource.getClientProperty("command").equals("filter")) {
			
			if(cmbMonth.getSelectedIndex() == 0) {
				NotificationManager.showPopup(getOwner(), cmbMonth, new String[] { "Please select valid month" });
				return;
			}
			
			if(cmbYear.getSelectedIndex() == 0) {
				NotificationManager.showPopup(getOwner(), cmbYear, new String[] { "Please select valid year" });
				return;
			}
			
			progressOverlay.setShowLoad(!progressOverlay.isShowLoad());
			
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {
					generateReport(cmbMonth.getSelectedIndex() - 1, (int) cmbYear.getSelectedItem());
					return null;
				}
				
				@Override
				protected void done() {
					tableModel.fireTableDataChanged();
					progressOverlay.setShowLoad(!progressOverlay.isShowLoad());
				}
				
			};
			
			worker.execute();
		}
	}
	
	@SuppressWarnings("serial")
	private void saveAsCsv() {
		fileChooser = new  WebFileChooser() {
			public void approveSelection() {
				super.approveSelection();
				PivotReportForm.this.saveFileTarget = getSelectedFile();
				
				if (!FilesUtil.getExtension(PivotReportForm.this.saveFileTarget).equalsIgnoreCase(".csv")) {
					
					SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {

						@Override
						protected String doInBackground() throws Exception {
							return generateCsv(PivotReportForm.this.saveFileTarget.toString());
						}
						
						@Override
						protected void done() {
							
							String notificationMsg = "";
							
							try {
								String path = get();
								
								if(path != null) {
									notificationMsg = "Report has been generated at: " + path;
								} else {
									notificationMsg = "Report generation failed";
								}
								
							} catch (InterruptedException | ExecutionException e) {
								notificationMsg = "Report generation failed";
							}
							
							NotificationManager.showNotification(notificationMsg);
							poBtnCsv.setShowLoad(!poBtnCsv.isShowLoad());
						}
					};
					
					worker.execute();					
				}
			}
		};
		
		fileChooser.setDialogTitle("Save to");
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setMaximumSize(new Dimension(425, 245));
        fileChooser.addChoosableFileFilter(new PDFFileFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.showSaveDialog(getOwner());
	}
	
	class PDFFileFilter extends FileFilter {
		public PDFFileFilter() {
		}

		public boolean accept(File paramFile) {
			return (paramFile.isDirectory())
					|| (paramFile.getPath().toLowerCase().endsWith(".pdf"));
		}

		public String getDescription() {
			return "PDF";
		}
	}
	
	public class PivotTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		public final Object[] longValues;
		
		public PivotTableModel(Object[][] data, Object[] columnNames) {
			super(data, columnNames);
			
			longValues = new Object[columnNames.length];
			
			for(int i=0; i<columnNames.length; i++) {
				longValues[i] = "";
			}
		}
		
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Class getColumnClass(int c) {
			return longValues[c].getClass();
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}
	}
}