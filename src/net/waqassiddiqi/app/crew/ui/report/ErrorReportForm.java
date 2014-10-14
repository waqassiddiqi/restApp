package net.waqassiddiqi.app.crew.ui.report;

import gui.ava.html.image.generator.HtmlImageGenerator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import net.waqassiddiqi.app.crew.db.CrewDAO;
import net.waqassiddiqi.app.crew.db.VesselDAO;
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

import com.alee.extended.panel.GroupPanel;
import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.label.WebLabel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextPane;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

public class ErrorReportForm extends BaseForm {

	private Logger log = Logger.getLogger(getClass().getName());
	private WebFileChooser fileChooser = null;
	private static String sourceImage = "";
	private File saveFileTarget = null;
	private WebTextPane reportPane;
	private WebComboBox cmbCrew;
	private WebComboBox cmbYear;
	private WebComboBox cmbMonth;
	private ErrorReport errorReport;
	
	@SuppressWarnings("unchecked")
	public ErrorReportForm(MainFrame owner) {
		super(owner);
		
		cmbCrew = new WebComboBox();
		cmbCrew.addItem("Select Crew");
		List<Crew> listCrew = new CrewDAO().getAll();
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
        for (int i = 0; i < months.length && i < 12; i++) {
        	cmbMonth.addItem(months[i]);
        	
        	if(i == Calendar.getInstance().get(Calendar.MONTH) + 1) {
        		cmbMonth.setSelectedIndex(i);
        	}
        }
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
		getToolbar().add(btnSaveAsPdf);
		
		getToolbar().addSeparator();
		
		getToolbar().add(new WebLabel("Filter: ") {{ setMargin(1); }});
		
		WebButton btnFilter = new WebButton("Generate Report", getIconsHelper().loadIcon("common/settings_16x16.png"));
		btnFilter.putClientProperty("command", "filter");
		btnFilter.addActionListener(this);
		GroupPanel gp = new GroupPanel(cmbCrew, cmbMonth, cmbYear, btnFilter);
		
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
	
	private void generateReport(Crew crew, Vessel vessel, int month, int year) {
		
		VelocityContext localVelocityContext = new VelocityContext();
		localVelocityContext.put("currentCrew", crew);
		localVelocityContext.put("currentVessel", vessel);
				
		errorReport = new ErrorReport(crew, vessel, month, year);
		errorReport.generateReport();		
		
		localVelocityContext.put("lstEntryTimes", errorReport.getEntryTimeList());	    
	    localVelocityContext.put("host", errorReport);
	    
	    VelocityEngine ve = new VelocityEngine();
	    ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
	    ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	    
	    Template reportTemplate = ve.getTemplate("resource/template/errorReport.vm");
	    
	    StringWriter writer = new StringWriter();
	    
	    reportTemplate.merge(localVelocityContext, writer);
	    
	    reportPane.setText(writer.toString());
	}
	
	private void generatePdf(String path) {
		ByteArrayOutputStream localByteArrayOutputStream = null;
		Document localDocument = null;
		PdfWriter localPdfWriter = null;
		
		try {

			localByteArrayOutputStream = new ByteArrayOutputStream();
			localDocument = new Document(PageSize.A4_LANDSCAPE.rotate());

			localPdfWriter = PdfWriter.getInstance(localDocument, new FileOutputStream(path));
			localPdfWriter.setStrictImageSequence(true);
			localDocument.open();
			Image localImage = Image.getInstance(sourceImage + ".png");
			localImage.scaleToFit(650.0F, 600.0F);
			localDocument.add(localImage);
			localDocument.close();
			localByteArrayOutputStream.flush();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		try {
			File localFile = new File(sourceImage + ".png");
			if (localFile.delete()) {
				log.info(localFile.getName() + " is deleted!");
			} else {
				log.info("Delete operation is failed.");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WebButton btnSource = (WebButton) e.getSource();
		
		if(btnSource.getClientProperty("command").equals("pdf")) {
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
				
				generateReport( (Crew) cmbCrew.getSelectedItem(), currentVessel, cmbMonth.getSelectedIndex() - 1, 
						(int) cmbYear.getSelectedItem());
				
			} else {
				NotificationManager.showPopup(getOwner(), cmbCrew, new String[] { "Please select crew" });
				return;
			}			
		}
	}
	
	@SuppressWarnings("serial")
	private void saveAsPdf() {
		fileChooser = new  WebFileChooser() {
			public void approveSelection() {
				super.approveSelection();
				ErrorReportForm.this.saveFileTarget = getSelectedFile();
				ErrorReportForm.sourceImage = ErrorReportForm.this.saveFileTarget.toString();
				
				if (!FilesUtil.getExtension(ErrorReportForm.this.saveFileTarget).equalsIgnoreCase(".pdf")) {
					HtmlImageGenerator localHtmlImageGenerator = new HtmlImageGenerator();
					localHtmlImageGenerator.loadHtml(ErrorReportForm.this.reportPane.getText());
					localHtmlImageGenerator.saveAsImage(ErrorReportForm.sourceImage + ".png");
					ErrorReportForm.this.generatePdf(ErrorReportForm.sourceImage + ".pdf");
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