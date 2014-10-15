package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.text.AbstractDocument;

import net.waqassiddiqi.app.crew.db.ApplicationSettingDAO;
import net.waqassiddiqi.app.crew.model.ApplicationSetting;
import net.waqassiddiqi.app.crew.ui.control.DocumentSizeFilter;
import net.waqassiddiqi.app.crew.util.NotificationManager;

import com.alee.extended.image.WebImageDrop;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.WebCollapsiblePane;
import com.alee.global.GlobalConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.label.WebLabel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextArea;
import com.alee.utils.ImageUtils;

public class CustomizationForm extends BaseForm {
	private WebFileChooser fileChooser = null;
	private File imageFile = null;
	private ApplicationSetting settings;
	private WebTextArea txtRestHourCustomText;
	private WebTextArea txtErrorReportCustomText;
	private WebTextArea txtNCReportCustomText;
	private WebTextArea txtWorkingArragementReportCustomText;
	
	public CustomizationForm(MainFrame owner) {
		super(owner);
		
		txtRestHourCustomText = new WebTextArea();
		txtRestHourCustomText.setLineWrap (true);
		txtRestHourCustomText.setWrapStyleWord(true);
		
		AbstractDocument pDoc = (AbstractDocument) txtRestHourCustomText.getDocument();
		pDoc.setDocumentFilter(new DocumentSizeFilter(1000));
		
		txtErrorReportCustomText = new WebTextArea();
		txtErrorReportCustomText.setLineWrap (true);
		txtErrorReportCustomText.setWrapStyleWord(true);
		
		pDoc = (AbstractDocument) txtErrorReportCustomText.getDocument();
		pDoc.setDocumentFilter(new DocumentSizeFilter(1000));
		
		txtNCReportCustomText = new WebTextArea();
		txtNCReportCustomText.setLineWrap (true);
		txtNCReportCustomText.setWrapStyleWord(true);
		
		pDoc = (AbstractDocument) txtNCReportCustomText.getDocument();
		pDoc.setDocumentFilter(new DocumentSizeFilter(1000));
		
		txtWorkingArragementReportCustomText = new WebTextArea();
		txtWorkingArragementReportCustomText.setLineWrap (true);
		txtWorkingArragementReportCustomText.setWrapStyleWord(true);
		
		pDoc = (AbstractDocument) txtWorkingArragementReportCustomText.getDocument();
		pDoc.setDocumentFilter(new DocumentSizeFilter(1000));
		
		settings = new ApplicationSettingDAO().get();
	}
	
	@SuppressWarnings("serial")
	@Override
	public Component prepareView() {
		
		final MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 0) {
					
					if (fileChooser == null) {
						fileChooser = new WebFileChooser();
						fileChooser.setDialogTitle("Choose an image to add");
						fileChooser.setMultiSelectionEnabled(false);
						fileChooser.setAcceptAllFileFilterUsed(false);
						fileChooser
								.addChoosableFileFilter(GlobalConstants.IMAGES_FILTER);
					}
					if (fileChooser.showOpenDialog(getOwner()) == WebFileChooser.APPROVE_OPTION) {
						for (File file : fileChooser.getSelectedFiles()) {
							((WebImageDrop) e.getSource()).setImage(ImageUtils.getBufferedImage(new ImageIcon(file.getAbsolutePath())));
							
							imageFile = file;
							
							break;
						}
					}
				}
			}
		};
		
		WebImageDrop drop = new WebImageDrop(80, 80) { { setToolTipText("Click here to add/update company logo"); addMouseListener(ma);  } };
		if(settings != null && settings.getLogo() != null) {
			drop.setImage(settings.getLogo());
		} else {
			drop.setImage(ImageUtils.getBufferedImage(ClassLoader.class.getResource("/resource/template/logo.jpg")));
		}
				
		final WebCollapsiblePane leftPane = new WebCollapsiblePane("Company Logo", new GroupPanel(10, false, drop, 
				new WebLabel("<html>This logo will be displayed on reports <br/><i>(Recommended size is 80x80 pixels)</i></html>", JLabel.CENTER)).setMargin(20));
		
		if(settings != null) {
			txtRestHourCustomText.setText(settings.getCustomRestReportText());
			txtErrorReportCustomText.setText(settings.getCustomErrorReportText());
			txtNCReportCustomText.setText(settings.getCustomNCReportText());
			txtWorkingArragementReportCustomText.setText(settings.getCustomWorkingReportText());
		}
		
		WebScrollPane areaScroll = new WebScrollPane(txtRestHourCustomText);
        areaScroll.setPreferredSize(new Dimension(300, 100));
		
		final WebCollapsiblePane leftPaneRegInfo = new WebCollapsiblePane("Custom Text", new GroupPanel(10, false,
				getLabel("Resting hour report:"),
				areaScroll,
				
				getLabel("Error report:"),
				getTextAreaScroll(txtErrorReportCustomText),
				
				getLabel("Non-Conformity summary report:"),
				getTextAreaScroll(txtNCReportCustomText),
				
				getLabel("Working arragements report:"),
				getTextAreaScroll(txtWorkingArragementReportCustomText),
				new WebLabel("<html>This text will be displayed in report's footer</html>", JLabel.CENTER)
				).setMargin(10));
		
		
		WebScrollPane sp = new WebScrollPane(new GroupPanel(10, false, leftPane, leftPaneRegInfo));
		sp.setPreferredHeight(300);
		
		return sp;
	}
	
	private WebLabel getLabel(String title) {
		WebLabel label = new WebLabel(title);
		label.setDrawShade(true);
		
		return label;
	}
	
	private WebScrollPane getTextAreaScroll(WebTextArea textArea) {
		WebScrollPane areaScroll = new WebScrollPane(textArea);
        areaScroll.setPreferredSize(new Dimension(300, 100));
        
        return areaScroll;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		WebButton btnSource = (WebButton) e.getSource();
		
		if(btnSource.getClientProperty("command").equals("save")) {
			ApplicationSettingDAO dao = new ApplicationSettingDAO();
			
			if(imageFile != null) {			
				dao.addLogo(imageFile);				
			}
			
			if(settings != null) {
				settings.setCustomRestReportText(txtRestHourCustomText.getText());
				settings.setCustomErrorReportText(txtErrorReportCustomText.getText());
				settings.setCustomNCReportText(txtNCReportCustomText.getText());
				settings.setCustomWorkingReportText(txtWorkingArragementReportCustomText.getText());
				
				dao.updateApplicationSetting(settings);
				
				NotificationManager.showNotification("Application settings have been saved");
			}
		}
	}
}
