package net.waqassiddiqi.app.crew.ui;

import java.awt.Component;
import java.awt.event.ActionListener;

import net.waqassiddiqi.app.crew.ui.icons.IconsHelper;

import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.GroupingType;
import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyManager;

public abstract class BaseForm implements ActionListener {
	
	private WebButton btnNew;
	private WebButton btnSave;
	private WebButton btnClose;
	private IconsHelper iconsHelper = new IconsHelper();
	private WebButton btnCancel;
	private MainFrame owner;
	private WebToolBar toolbar;
	private WebPanel contentPane;
	
	public BaseForm(MainFrame owner) {
		this.owner = owner;
		
		toolbar = new WebToolBar(WebToolBar.HORIZONTAL);
		toolbar.setFloatable(false);
		
		this.contentPane = new WebPanel();
	}
	
	public MainFrame getOwner() {
		return this.owner;
	}
	
	public IconsHelper getIconsHelper() {
		return this.iconsHelper;
	}
	
	public WebToolBar getToolbar() {
		return this.toolbar;
	}
	
	public void setupToolBar() {		
		toolbar.addSeparator();
		
		btnNew = WebButton.createIconWebButton(iconsHelper.loadIcon("common/new_16x16.png"),
				StyleConstants.smallRound, true);
		btnNew.putClientProperty("command", "new");
		btnNew.addActionListener(this);
		btnNew.setToolTipText("New (Ctrl+N)");
		
		toolbar.add(btnNew);
		
		btnSave = WebButton.createIconWebButton(iconsHelper.loadIcon("common/save.png"),
				StyleConstants.smallRound, true);
		btnSave.putClientProperty("command", "save");
		btnSave.addActionListener(this);
		btnSave.setToolTipText("Save (Ctrl+S)");
		btnSave.addHotkey(getOwner(), Hotkey.CTRL_SHIFT_S);
		
		toolbar.add(btnSave);
		
		btnCancel = WebButton.createIconWebButton(iconsHelper.loadIcon("common/cancel.png"),
				StyleConstants.smallRound, true);
		btnCancel.putClientProperty("command", "cancel");
		btnCancel.addActionListener(this);
		btnCancel.setToolTipText("Cancel Changes");
		
		toolbar.add(btnCancel);
		
		toolbar.addSeparator();
		
		btnClose = WebButton.createIconWebButton(iconsHelper.loadIcon("common/cross2_16x16.png"),
				StyleConstants.smallRound, true);
		btnClose.putClientProperty("command", "close");
		btnClose.addActionListener(this);
		btnClose.setToolTipText("Close (Ctrl+F4)");
		
		toolbar.addToEnd(btnClose);
		
		HotkeyManager.registerHotkey(owner, btnSave, Hotkey.CTRL_S);
		HotkeyManager.registerHotkey(owner, btnNew, Hotkey.CTRL_N);
	}
	
	public WebPanel getContentPane() {
		return contentPane;
	}
	
	public Component getView() {
		setupToolBar();
		
		final GroupPanel titlePanel = new GroupPanel(GroupingType.fillFirst, 5, toolbar);
		return new GroupPanel(GroupingType.fillLast, 10, false, titlePanel, prepareView()).setMargin(10);
	}
	
	public abstract Component prepareView();
}