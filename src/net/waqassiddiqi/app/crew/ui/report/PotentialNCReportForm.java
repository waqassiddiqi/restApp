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
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import net.waqassiddiqi.app.crew.db.ApplicationSettingDAO;
import net.waqassiddiqi.app.crew.db.VesselDAO;
import net.waqassiddiqi.app.crew.model.ApplicationSetting;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.report.PotentialNCReport;
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

public class PotentialNCReportForm extends BaseForm {

	private Logger log = Logger.getLogger(getClass().getName());
	private WebFileChooser fileChooser = null;
	private File saveFileTarget = null;
	private WebTextPane reportPane;
	private WebComboBox cmbYear;
	private WebComboBox cmbMonth;
	private PotentialNCReport pNcReport;
	private String generatedHtml = "";
	private final WebProgressOverlay progressOverlay;
	private final WebProgressOverlay poBtnPdf;
	private WebButton btnFilter;
	private WebScrollPane scrollPane;
	private URL urlCustomFont = null;
	private String logoPath;
	
	@SuppressWarnings("unchecked")
	public PotentialNCReportForm(MainFrame owner) {
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
        for (int i = 0; i < months.length && i < 12; i++) {
        	cmbMonth.addItem(months[i]);
        	
        	if(i == Calendar.getInstance().get(Calendar.MONTH) + 1) {
        		cmbMonth.setSelectedIndex(i);
        	}
        }
        
        progressOverlay = new WebProgressOverlay();
        progressOverlay.setConsumeEvents(false);
        
        poBtnPdf = new WebProgressOverlay();
        poBtnPdf.setConsumeEvents(false);
	}

	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Summary of Non-Conformities") {{ setDrawShade(true); setMargin(10); }});
		
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
		
		
		btnFilter = new WebButton("Generate Report", getIconsHelper().loadIcon("common/settings_16x16.png"));
		btnFilter.putClientProperty("command", "filter");
		btnFilter.addActionListener(this);		
		progressOverlay.setComponent(btnFilter);
		
		GroupPanel gp = new GroupPanel(cmbMonth, cmbYear, progressOverlay);
		
		getToolbar().add(gp);
	}
	
	@Override
	public Component prepareView() {
		
		reportPane = new WebTextPane();
		reportPane.setEditable(false);
		reportPane.setContentType("text/html");
		
		
	    scrollPane = new WebScrollPane(reportPane);
	    
		return scrollPane.setMargin(5);
	}
	
	private String generateReport(Vessel vessel, int month, int year) {
		
		VelocityContext localVelocityContext = new VelocityContext();
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
			URL u = ClassLoader.class.getResource("/resource/template/logo.png");
			if(u != null) logoPath = u.toString(); 
		}
		
		localVelocityContext.put("logo", logoPath);
		
		pNcReport = new PotentialNCReport(month, year);
		List<String[]> data = pNcReport.generateReport();		
		
		localVelocityContext.put("data", data);
	    
		localVelocityContext.put("host", pNcReport);
		
	    VelocityEngine ve = new VelocityEngine();
	    ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
	    ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	    
	    Template reportTemplate = ve.getTemplate("resource/template/potential_nc_detailed_report.html");
	    
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
		
			if(urlCustomFont == null) {
				urlCustomFont = ClassLoader.class.getResource("/resource/template/CarroisGothic-Regular.ttf");
				renderer.getFontResolver().addFont(urlCustomFont.toString(), true);
			}
			
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
			
			new GenerateReportTask(currentVessel, cmbMonth.getSelectedIndex() - 1, 
					(int) cmbYear.getSelectedItem()).execute();
		}
	}
	
	public final class GenerateReportTask extends SwingWorker<String, Void> {

		private Vessel vessel;
		private int month;
		private int year;
		
		public GenerateReportTask(Vessel v, int month, int year) {
			this.vessel = v;
			this.month = month;
			this.year = year;
		}
		
		@Override
		protected String doInBackground() throws Exception {
			
			return generateReport(vessel, month, year);
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
				PotentialNCReportForm.this.saveFileTarget = getSelectedFile();
				
				if (!FilesUtil.getExtension(PotentialNCReportForm.this.saveFileTarget).equalsIgnoreCase(".pdf")) {					
					SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {

						@Override
						protected String doInBackground() throws Exception {
							return generatePdf(PotentialNCReportForm.this.saveFileTarget.toString());
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
							
							NotificationManager.showNotification(notificationMsg);
							poBtnPdf.setShowLoad(!poBtnPdf.isShowLoad());
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
