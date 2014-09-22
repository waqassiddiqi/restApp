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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import net.waqassiddiqi.app.crew.db.CrewDAO;
import net.waqassiddiqi.app.crew.model.Crew;
import net.waqassiddiqi.app.crew.model.Vessel;
import net.waqassiddiqi.app.crew.ui.BaseForm;
import net.waqassiddiqi.app.crew.ui.MainFrame;
import net.waqassiddiqi.app.crew.util.FilesUtil;

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

public class RestingHourReportForm extends BaseForm {

	private Logger log = Logger.getLogger(getClass().getName());
	private WebFileChooser fileChooser = null;
	private static String sourceImage = "";
	private File saveFileTarget = null;
	private WebTextPane reportPane;
	private WebComboBox cmbCrew;
	private WebComboBox cmbYear;
	private WebComboBox cmbMonth;
	
	@SuppressWarnings("unchecked")
	public RestingHourReportForm(MainFrame owner) {
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
		}
		
		cmbMonth = new WebComboBox();
		cmbMonth.addItem("Select Month");
		String[] months = new DateFormatSymbols().getMonths();
        for (int i = 0; i < months.length && i < 12; i++) {
        	cmbMonth.addItem(months[i]);
        }
	}

	@SuppressWarnings("serial")
	@Override
	public void setupToolBar() {
		getToolbar().add(new WebLabel("Resting Hour Report") {{ setDrawShade(true); setMargin(10); }});
		
		getToolbar().addSeparator();
		
		WebButton btnSaveAsPdf = WebButton.createIconWebButton(getIconsHelper().loadIcon("common/file_extension_pdf_16x16.png"),
				StyleConstants.smallRound, true);
		btnSaveAsPdf.putClientProperty("command", "pdf");
		btnSaveAsPdf.addActionListener(this);
		btnSaveAsPdf.setToolTipText("Save as PDF");
		
		getToolbar().add(btnSaveAsPdf);
		
		getToolbar().addSeparator();
		
		getToolbar().add(new WebLabel("Filter: ") {{ setMargin(1); }});
		
		WebButton btnFilter = WebButton.createIconWebButton(getIconsHelper().loadIcon("common/search_16x16.png"),
				StyleConstants.smallRound, true);
		
		GroupPanel gp = new GroupPanel(cmbCrew, cmbMonth, cmbYear, btnFilter);
		
		getToolbar().add(gp);
	}
	
	@Override
	public Component prepareView() {
		
		reportPane = new WebTextPane();
		reportPane.setEditable(false);
		reportPane.setContentType("text/html");
		
		Vessel currentVessel = new Vessel() {{ setImo("IMOPK"); setName("MV S"); }};
		Crew currentCrew = new Crew() {{ setFirstName("Waqas"); setLastName("Siddiqui"); setRank("Major"); }};
		
		VelocityContext localVelocityContext = new VelocityContext();
		localVelocityContext.put("currentCrew", currentCrew);
		localVelocityContext.put("currentVessel", currentVessel);
		localVelocityContext.put("lstEntryTimes", new ArrayList(100));
		
		Calendar cal = Calendar.getInstance();
		
	    localVelocityContext.put("month", new SimpleDateFormat("MMM").format(cal.getTime()));
	    localVelocityContext.put("year", cal.get(Calendar.YEAR));
	    
	    
	    VelocityEngine ve = new VelocityEngine();
	    ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
	    ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	    
	    Template reportTemplate = ve.getTemplate("resource/template/restingHourReport.html");
	    
	    StringWriter writer = new StringWriter();
	    
	    reportTemplate.merge(localVelocityContext, writer);
	    
	    reportPane.setText(writer.toString());
	    WebScrollPane scrollPane = new WebScrollPane(reportPane);
	    
		return scrollPane.setMargin(5);
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
		}
	}
	
	@SuppressWarnings("serial")
	private void saveAsPdf() {
		fileChooser = new  WebFileChooser() {
			public void approveSelection() {
				super.approveSelection();
				RestingHourReportForm.this.saveFileTarget = getSelectedFile();
				RestingHourReportForm.sourceImage = RestingHourReportForm.this.saveFileTarget.toString();
				
				if (!FilesUtil.getExtension(RestingHourReportForm.this.saveFileTarget).equalsIgnoreCase(".pdf")) {
					HtmlImageGenerator localHtmlImageGenerator = new HtmlImageGenerator();
					localHtmlImageGenerator.loadHtml(RestingHourReportForm.this.reportPane.getText());
					localHtmlImageGenerator.saveAsImage(RestingHourReportForm.sourceImage + ".png");
					RestingHourReportForm.this.generatePdf(RestingHourReportForm.sourceImage + ".pdf");
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
