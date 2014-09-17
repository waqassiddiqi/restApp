package net.waqassiddiqi.app.crew.ui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import net.waqassiddiqi.app.crew.util.PrefsUtil;

import com.alee.extended.layout.TableLayout;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.text.WebTextField;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyManager;
import com.alee.utils.SwingUtils;

public class ServerSettingsDialog extends WebDialog {
	private static final long serialVersionUID = 1L;
	private WebTextField txtServerIP;
	private WebTextField txtServerPort;
	
	public ServerSettingsDialog(Window owner) {
		super(owner, "Server Settings");
		setIconImages(WebLookAndFeel.getImages());
		setDefaultCloseOperation(WebDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setModal(true);

		TableLayout layout = new TableLayout(new double[][] {
				{ TableLayout.PREFERRED, TableLayout.FILL },
				{ TableLayout.PREFERRED, TableLayout.PREFERRED,
						TableLayout.PREFERRED } });
		layout.setHGap(5);
		layout.setVGap(5);
		WebPanel content = new WebPanel(layout);
		content.setMargin(15, 30, 15, 30);
		content.setOpaque(false);

		content.add(new WebLabel("Server IP", WebLabel.TRAILING), "0,0");
		
		String serverIP = PrefsUtil.getString(PrefsUtil.PREF_SERVER_IP, "");
		
		if(serverIP.isEmpty()) {
			try {
				final InetAddress IP = InetAddress.getLocalHost();
				serverIP = IP.getHostAddress();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
		}
		
		txtServerIP = new WebTextField(15);
		txtServerIP.setText(serverIP);
		
		content.add(txtServerIP, "1,0");

		content.add(new WebLabel("Server Port", WebLabel.TRAILING), "0,1");
		
		txtServerPort = new WebTextField(15);
		txtServerPort.setText( Integer.toString(PrefsUtil.getInt(PrefsUtil.PREF_SERVER_PORT, 9091)));
		
		content.add(txtServerPort, "1,1");

		final WebButton login = new WebButton("Save");
		WebButton cancel = new WebButton("Cancel");
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if( ((WebButton) e.getSource()) == login ) {
					PrefsUtil.setString(PrefsUtil.PREF_SERVER_IP, txtServerIP.getText().trim());
					PrefsUtil.setInt(PrefsUtil.PREF_SERVER_PORT, Integer.valueOf(txtServerPort.getText().trim()));
					
				}
				
				setVisible(false);
			}
		};
		login.addActionListener(listener);
		cancel.addActionListener(listener);
		content.add(new CenterPanel(new GroupPanel(5, login, cancel)), "0,2,1,2");
		SwingUtils.equalizeComponentsWidths(login, cancel);

		add(content);

		HotkeyManager.registerHotkey(this, login, Hotkey.ESCAPE);
		HotkeyManager.registerHotkey(this, login, Hotkey.ENTER);
	}
}