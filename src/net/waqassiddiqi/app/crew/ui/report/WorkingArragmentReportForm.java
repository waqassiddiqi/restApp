package net.waqassiddiqi.app.crew.ui.report;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import net.waqassiddiqi.app.crew.db.ApplicationSettingDAO;
import net.waqassiddiqi.app.crew.db.VesselDAO;
import net.waqassiddiqi.app.crew.model.ApplicationSetting;
import net.waqassiddiqi.app.crew.report.WorkingArragementsReport;
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

import com.alee.extended.progress.WebProgressOverlay;
import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.label.WebLabel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextPane;
import com.lowagie.text.pdf.BaseFont;

public class WorkingArragmentReportForm extends BaseForm {

	private Logger log = Logger.getLogger(getClass().getName());
	private WebFileChooser fileChooser = null;
	private File saveFileTarget = null;
	private WebTextPane reportPane;	
	private WorkingArragementsReport waReport;
	private String generatedHtml = "";
	private final WebProgressOverlay poBtnPdf;
	private WebScrollPane scrollPane;
	private URL urlCustomFont = null;
	private String logoPath;
	private final WebProgressOverlay progressOverlay;
	
	public WorkingArragmentReportForm(MainFrame owner) {
		super(owner);
		
		progressOverlay = new WebProgressOverlay();
        progressOverlay.setConsumeEvents(false);
        
        poBtnPdf = new WebProgressOverlay();
        poBtnPdf.setConsumeEvents(false);
	}

	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Shipboard working arrangements report") {{ setDrawShade(true); setMargin(10); }});
		
		getToolbar().addSeparator();
		
		WebButton btnSaveAsPdf = WebButton.createIconWebButton(getIconsHelper().loadIcon("common/file_extension_pdf_16x16.png"),
				StyleConstants.smallRound, true);
		btnSaveAsPdf.putClientProperty("command", "pdf");
		btnSaveAsPdf.addActionListener(this);
		btnSaveAsPdf.setToolTipText("Save as PDF");
		
		poBtnPdf.setComponent(btnSaveAsPdf);
		
		getToolbar().add(poBtnPdf);
		
		getToolbar().addSeparator();
		
		WebButton btnFilter = new WebButton("Generate Report", getIconsHelper().loadIcon("common/settings_16x16.png"));
		btnFilter.putClientProperty("command", "filter");
		btnFilter.addActionListener(this);
		progressOverlay.setComponent(btnFilter);
		
		getToolbar().add(progressOverlay);
	}
	
	@Override
	public Component prepareView() {
		
		reportPane = new WebTextPane();
		reportPane.setEditable(false);
		reportPane.setContentType("text/html");
		
		
	    scrollPane = new WebScrollPane(reportPane);
	    
		return scrollPane.setMargin(5);
	}
	
	private String generateReport() {
		
		VelocityContext localVelocityContext = new VelocityContext();
		localVelocityContext.put("currentVessel", new VesselDAO().getAll().get(0));
		
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
		localVelocityContext.put("customText", settings.getCustomWorkingReportText());
		
		waReport = new WorkingArragementsReport();
		List<String[]> data = waReport.generateReport();		
		
		localVelocityContext.put("data", data);
	    
		localVelocityContext.put("host", waReport);
		
	    VelocityEngine ve = new VelocityEngine();

	    Properties props = new Properties();
	    props.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
	    props.put("runtime.log.logsystem.log4j.category", "velocity");
	    props.put("runtime.log.logsystem.log4j.logger", "velocity");
	    
	    props.put(RuntimeConstants.RESOURCE_LOADER, "classpath");
	    props.put("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

	    ve.init(props);
	    
	    Template reportTemplate = ve.getTemplate("resource/template/working_arragment_report.html");
	    
	    StringWriter writer = new StringWriter();
	    
	    reportTemplate.merge(localVelocityContext, writer);
	    
	    generatedHtml = writer.toString();
	    
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
			poBtnPdf.setShowLoad(!poBtnPdf.isShowLoad());			
			saveAsPdf();		
		} else if(btnSource.getClientProperty("command").equals("filter")) {			
			
			progressOverlay.setShowLoad(!progressOverlay.isShowLoad());
			
			new GenerateReportTask().execute();
		}
	}
	
	public final class GenerateReportTask extends SwingWorker<String, Void> {

		@Override
		protected String doInBackground() throws Exception {
			
			return generateReport();
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
				WorkingArragmentReportForm.this.saveFileTarget = getSelectedFile();
				
				if (!FilesUtil.getExtension(WorkingArragmentReportForm.this.saveFileTarget).equalsIgnoreCase(".pdf")) {					
					SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {

						@Override
						protected String doInBackground() throws Exception {
							return generatePdf(WorkingArragmentReportForm.this.saveFileTarget.toString());
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
