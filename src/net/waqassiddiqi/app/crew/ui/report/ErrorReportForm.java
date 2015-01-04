package net.waqassiddiqi.app.crew.ui.report;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import net.waqassiddiqi.app.crew.db.ApplicationSettingDAO;
import net.waqassiddiqi.app.crew.db.CrewDAO;
import net.waqassiddiqi.app.crew.db.VesselDAO;
import net.waqassiddiqi.app.crew.model.ApplicationSetting;
import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.report.ErrorReport;
import net.waqassiddiqi.app.crew.ui.BaseForm;
import net.waqassiddiqi.app.crew.ui.MainFrame;
import net.waqassiddiqi.app.crew.util.FilesUtil;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.alee.extended.panel.GroupPanel;
import com.alee.extended.progress.WebProgressOverlay;
import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.label.WebLabel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextPane;
import com.lowagie.text.pdf.BaseFont;

public class ErrorReportForm extends BaseForm {

	private Logger log = Logger.getLogger(getClass().getName());
	private WebFileChooser fileChooser = null;
	private File saveFileTarget = null;
	private WebTextPane reportPane;
	private WebComboBox cmbCrew;
	private WebComboBox cmbYear;
	private WebComboBox cmbMonth;
	private ErrorReport errorReport;
	private String logoPath;
	private final WebProgressOverlay poBtnPdf;
	private URL urlCustomFont = null;
	private String generatedHtml = "";
	private final WebProgressOverlay progressOverlay;
	private WebScrollPane scrollPane;
	
	@SuppressWarnings("unchecked")
	public ErrorReportForm(MainFrame owner) {
		super(owner);
		
		cmbCrew = new WebComboBox();
		cmbCrew.addItem("Select Crew");
		List<Crew> listCrew = new CrewDAO().getAllActive();
		for(Crew c : listCrew) {
			cmbCrew.addItem(c);
		}
		
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
        
        poBtnPdf = new WebProgressOverlay();
        poBtnPdf.setConsumeEvents(false);
        
        progressOverlay = new WebProgressOverlay();
        progressOverlay.setConsumeEvents(false);
	}

	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Error Report") {{ setDrawShade(true); setMargin(10); }});
		
		getToolbar().addSeparator();
		
		WebButton btnSaveAsPdf = WebButton.createIconWebButton(getIconsHelper().loadIcon("common/file_extension_pdf_16x16.png"),
				StyleConstants.smallRound, true);
		btnSaveAsPdf.putClientProperty("command", "pdf");
		btnSaveAsPdf.addActionListener(this);
		btnSaveAsPdf.setToolTipText("Save as PDF");
		poBtnPdf.setComponent(btnSaveAsPdf);		
		getToolbar().add(poBtnPdf);
		
		getToolbar().addSeparator();
		
		getToolbar().add(new WebLabel("Filter: ") {{ setMargin(1); }});
		
		WebButton btnFilter = new WebButton("Generate Report", getIconsHelper().loadIcon("common/settings_16x16.png"));
		btnFilter.putClientProperty("command", "filter");
		btnFilter.addActionListener(this);
		progressOverlay.setComponent(btnFilter);
		
		GroupPanel gp = new GroupPanel(cmbCrew, cmbMonth, cmbYear, progressOverlay);
		
		getToolbar().add(gp);
	}
	
	@Override
	public Component prepareView() {
		
		reportPane = new WebTextPane();
		reportPane.setEditable(false);
		reportPane.setContentType("text/html");
		
		
	    WebScrollPane scrollPane = new WebScrollPane(reportPane);
	    
		return scrollPane.setMargin(5);
	}
	
	private String generateReport(Crew crew, Vessel vessel, int month, int year) {
		
		VelocityContext localVelocityContext = new VelocityContext();
		localVelocityContext.put("currentCrew", crew);
		localVelocityContext.put("currentVessel", vessel);
	
		ApplicationSetting settings = new ApplicationSettingDAO().get();
		
		try {
			if(settings != null && settings.getLogo() != null) {
				File outputfile = File.createTempFile("logo", ".png");

				ImageIO.write(settings.getLogo(), "png", outputfile);
				
				logoPath = "file:/" + outputfile.getAbsolutePath();
			}
			
		} catch(Exception e) { }
		
		
		
		if(this.logoPath == null) {
			URL u = ClassLoader.class.getResource("/resource/template/logo.jpg");
			if(u != null) logoPath = u.toString(); 
		}
		
		localVelocityContext.put("logo", logoPath);
		localVelocityContext.put("customText", settings.getCustomRestReportText());
		
		errorReport = new ErrorReport(crew, vessel, month, year);
		errorReport.generateReport();		
		
		localVelocityContext.put("lstEntryTimes", errorReport.getEntryTimeList());	    
	    localVelocityContext.put("host", errorReport);
	    
	    VelocityEngine ve = new VelocityEngine();
	    Properties props = new Properties();
	    props.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
	    props.put("runtime.log.logsystem.log4j.category", "velocity");
	    props.put("runtime.log.logsystem.log4j.logger", "velocity");
	    
	    props.put(RuntimeConstants.RESOURCE_LOADER, "classpath");
	    props.put("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

	    ve.init(props);
	    
	    Template reportTemplate = ve.getTemplate("resource/template/error_report.html");
	    
	    StringWriter writer = new StringWriter();
	    
	    reportTemplate.merge(localVelocityContext, writer);
	    
	    generatedHtml = writer.toString();
	    
	    if(log.isDebugEnabled()) {
	    	log.debug(generatedHtml);
	    }
	    
	    return generatedHtml;
	}
	
	private String generatePdf(String filePath) {
		
		if(filePath.contains(".pdf") == false)
			filePath = filePath.trim() + ".pdf";
		
		ITextRenderer renderer = new ITextRenderer();
		
		try {
		
			urlCustomFont = ClassLoader.class.getResource("/resource/template/arialuni.ttf");
			renderer.getFontResolver().addFont(urlCustomFont.toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		
			if(generatedHtml == null)
				return "";
			
			if(!generatedHtml.startsWith("<?xml version="))
				generatedHtml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + generatedHtml;
			
			renderer.setDocumentFromString(generatedHtml);
			renderer.layout();
			
			String fileNameWithPath = filePath;
			FileOutputStream fos = new FileOutputStream(fileNameWithPath);
			renderer.createPDF(fos);
			fos.close();
				
		} catch (Exception e) {
			log.error("Failed to generate PDF", e);
			
			return null;
		} finally {
			renderer = null;
		}
		
		return filePath;
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WebButton btnSource = (WebButton) e.getSource();
		
		if(btnSource.getClientProperty("command").equals("pdf")) {
			poBtnPdf.setShowLoad(!progressOverlay.isShowLoad());
			saveAsPdf();
		} else if(btnSource.getClientProperty("command").equals("filter")) {
			
			if(cmbCrew.getSelectedItem() instanceof Crew) {
				
				if(cmbMonth.getSelectedIndex() == 0) {
					NotificationManager.showPopup(getOwner(), cmbMonth, new String[] { "Please select valid month" });
					return;
				}
				
				if(cmbYear.getSelectedIndex() == 0) {
					NotificationManager.showPopup(getOwner(), cmbYear, new String[] { "Please select valid year" });
					return;
				}
				
				Vessel currentVessel = new VesselDAO().getAll().get(0);
				
				progressOverlay.setShowLoad(!progressOverlay.isShowLoad());
				
				new GenerateReportTask((Crew) cmbCrew.getSelectedItem(), currentVessel, cmbMonth.getSelectedIndex() - 1, 
						(int) cmbYear.getSelectedItem()).execute();
				
				
			} else {
				NotificationManager.showPopup(getOwner(), cmbCrew, new String[] { "Please select crew" });
				return;
			}			
		}
	}
	
	public final class GenerateReportTask extends SwingWorker<String, Void> {

		private Crew crew;
		private Vessel vessel;
		private int month;
		private int year;
		
		public GenerateReportTask(Crew c, Vessel v, int month, int year) {
			this.crew = c;
			this.vessel = v;
			this.month = month;
			this.year = year;
		}
		
		@Override
		protected String doInBackground() throws Exception {
			
			return generateReport(crew, vessel, month, year);
		}
		
		@Override
		protected void done() {
			reportPane.setText(generatedHtml);
			
			progressOverlay.setShowLoad(!progressOverlay.isShowLoad());
			
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					scrollPane.getVerticalScrollBar().setValue(0);
				}
			});
		}
	}
	
	@SuppressWarnings("serial")
	private void saveAsPdf() {
		fileChooser = new  WebFileChooser() {
			public void approveSelection() {
				super.approveSelection();
				ErrorReportForm.this.saveFileTarget = getSelectedFile();
				
				if (!FilesUtil.getExtension(ErrorReportForm.this.saveFileTarget).equalsIgnoreCase(".pdf")) {					
					SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {

						@Override
						protected String doInBackground() throws Exception {
							return generatePdf(ErrorReportForm.this.saveFileTarget.toString());
						}
						
						@Override
						protected void done() {
							
							String notificationMsg = "";
							
							try {
								String path = get();
								
								if(path != null) {
									notificationMsg = "PDF has been generated at: " + path;
								} else {
									notificationMsg = "PDF generation failed";
								}
								
							} catch (InterruptedException | ExecutionException e) {
								notificationMsg = "PDF generation failed";
							}
							
							poBtnPdf.setShowLoad(!poBtnPdf.isShowLoad());
							NotificationManager.showNotification(notificationMsg);
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
}