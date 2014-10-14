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
	private WebTextArea txtCustomText;
	
	public CustomizationForm(MainFrame owner) {
		super(owner);
		
		txtCustomText = new WebTextArea();
		txtCustomText.setLineWrap (true);
		txtCustomText.setWrapStyleWord(true);
		
		AbstractDocument pDoc=(AbstractDocument) txtCustomText.getDocument();
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
			drop.setImage(ImageUtils.getBufferedImage(ClassLoader.class.getResource("/resource/template/logo.png")));
		}
				
		final WebCollapsiblePane leftPane = new WebCollapsiblePane("Company Logo", new GroupPanel(10, false, drop, 
				new WebLabel("<html>This logo will be displayed on reports <br/><i>(Recommended size is 80x80 pixels)</i></html>", JLabel.CENTER)).setMargin(20));
		
		if(settings != null) {
			txtCustomText.setText(settings.getCustomText());
		}
		
		WebScrollPane areaScroll = new WebScrollPane(txtCustomText);
        areaScroll.setPreferredSize(new Dimension(300, 100));
		
		final WebCollapsiblePane leftPaneRegInfo = new WebCollapsiblePane("Custom Text", new GroupPanel(10, false, areaScroll,
				new WebLabel("<html>This text will be displayed under reports <br/><i>You can use html tags for formatting</i></html>", JLabel.CENTER)
				).setMargin(10));
		
		return new GroupPanel(10, false, leftPane, leftPaneRegInfo);
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
				settings.setCustomText(txtCustomText.getText());
				dao.updateApplicationSetting(settings);
				
				NotificationManager.showNotification("Application settings have been saved");
			}
		}
	}
}
