package net.waqassiddiqi.app.crew.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

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

public class EulaForm extends BaseForm {

	private Logger log = Logger.getLogger(getClass().getName());
	private WebFileChooser fileChooser = null;
	private File saveFileTarget = null;
	private WebTextPane reportPane;
	private String generatedHtml = "";
	private final WebProgressOverlay poBtnPdf;
	private WebScrollPane scrollPane;
	private URL urlCustomFont = null;
	
	public EulaForm(MainFrame owner) {
		super(owner);
        
        poBtnPdf = new WebProgressOverlay();
        poBtnPdf.setConsumeEvents(false);
	}

	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("End-User License Agreement") {{ setDrawShade(true); setMargin(10); }});
		
		getToolbar().addSeparator();
		
		WebButton btnSaveAsPdf = WebButton.createIconWebButton(getIconsHelper().loadIcon("common/file_extension_pdf_16x16.png"),
				StyleConstants.smallRound, true);
		btnSaveAsPdf.putClientProperty("command", "pdf");
		btnSaveAsPdf.addActionListener(this);
		btnSaveAsPdf.setToolTipText("Save as PDF");
		
		poBtnPdf.setComponent(btnSaveAsPdf);
		
		getToolbar().add(poBtnPdf);
	}
	
	@Override
	public Component prepareView() {
		
		reportPane = new WebTextPane();
		reportPane.setEditable(false);
		reportPane.setContentType("text/html");
		
		reportPane.setText(getEula());
		
	    scrollPane = new WebScrollPane(reportPane);
	    scrollPane.setBackground(Color.white);
		return scrollPane.setMargin(5);
	}
	
	private String getEula() {			    
	    VelocityEngine ve = new VelocityEngine();
	    ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
	    ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	    
	    Template reportTemplate = ve.getTemplate("resource/template/EULA.htm");
	    
	    StringWriter writer = new StringWriter();
	    
	    reportTemplate.merge(new VelocityContext(), writer);
	    
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
			poBtnPdf.setShowLoad(!poBtnPdf.isShowLoad());
			saveAsPdf();
		}
	}
	
	@SuppressWarnings("serial")
	private void saveAsPdf() {
		fileChooser = new  WebFileChooser() {
			public void approveSelection() {
				super.approveSelection();
				EulaForm.this.saveFileTarget = getSelectedFile();
				
				if (!FilesUtil.getExtension(EulaForm.this.saveFileTarget).equalsIgnoreCase(".pdf")) {					
					SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {

						@Override
						protected String doInBackground() throws Exception {
							return generatePdf(EulaForm.this.saveFileTarget.toString());
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
