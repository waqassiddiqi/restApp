package net.waqassiddiqi.app.crew.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import net.waqassiddiqi.app.crew.ui.MainFrame;
import net.waqassiddiqi.app.crew.ui.icons.IconsHelper;

import com.alee.extended.image.WebImage;
import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.GroupingType;
import com.alee.extended.panel.SingleAlignPanel;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.WebNotificationPopup;

public class NotificationManager {

	public static void showPopup(MainFrame owner, Component target,
			String[] messages) {
		final WebPopOver popOver = new WebPopOver(owner);
		popOver.setCloseOnFocusLoss(true);
		popOver.setMargin(10);
		popOver.setLayout(new VerticalFlowLayout());

		for (String message : messages) {
			popOver.add(new WebLabel(message));
		}

		popOver.show(target);
	}

	public static void showPopup(MainFrame owner, Component target,
			String title, String[] messages, String actionButtonText,
			ActionListener action, boolean isCloseable) {

		final WebPopOver popOver = new WebPopOver(owner);
		popOver.setMargin(10);
		popOver.setMovable(true);
		//popOver.setModal(true);
		popOver.setLayout(new VerticalFlowLayout());
		final WebImage icon = new WebImage(WebLookAndFeel.getIcon(16));
		final WebLabel titleLabel = new WebLabel(title, WebLabel.CENTER);
		final WebButton closeButton = new WebButton(new IconsHelper().loadIcon(
				IconsHelper.class, "common/cross2_16x16.png"),
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						popOver.dispose();
					}
				}).setUndecorated(true);
		
		if(isCloseable) {
			popOver.add(new GroupPanel(GroupingType.fillMiddle, 4, icon,
					titleLabel, closeButton).setMargin(0, 0, 10, 0));
		} else {
			popOver.add(new GroupPanel(GroupingType.fillMiddle, 4, icon,
					titleLabel).setMargin(0, 0, 10, 0));
		}

		for (String message : messages) {
			popOver.add(new WebLabel(message));
		}
		WebButton btnAction = new WebButton(actionButtonText, action);
		btnAction.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				popOver.dispose();
			}
		});

		popOver.add(new SingleAlignPanel(btnAction, SingleAlignPanel.RIGHT)
				.setMargin(10, 0, 0, 0));

		popOver.show(target);
	}

	public static WebPopOver showPopup(MainFrame owner, Component target, boolean isModal, boolean isCloseable, ImageIcon imgIcon, String title, Component c) {
		final WebPopOver popOver = new WebPopOver(owner);
		popOver.setMargin(10);
		popOver.setModal(isModal);
		popOver.setMovable(true);
		popOver.setLayout(new VerticalFlowLayout());
		final WebImage icon = new WebImage(imgIcon);
		final WebLabel titleLabel = new WebLabel(title, WebLabel.CENTER);
		
		if(isCloseable) {
			final WebButton closeButton = new WebButton(new IconsHelper().loadIcon(
					IconsHelper.class, "common/cross2_16x16.png"),
					new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							popOver.dispose();
						}
					}).setUndecorated(true);
			
			popOver.add(new GroupPanel(GroupingType.fillMiddle, 4, icon,
					titleLabel, closeButton).setMargin(0, 0, 10, 0));
		} else {
			popOver.add(new GroupPanel(GroupingType.fillMiddle, 4, icon,
					titleLabel).setMargin(0, 0, 10, 0));
		}
		
		popOver.add(c);

		return popOver.show(target);
	}
	
	public static void showPopup(MainFrame owner, Component target,
			String[] messages, String actionButtonText, ActionListener action) {

		final WebPopOver popOver = new WebPopOver(owner);

		popOver.setMargin(10);
		popOver.setCloseOnFocusLoss(true);
		popOver.setMovable(false);
		popOver.setLayout(new VerticalFlowLayout());

		for (String message : messages) {
			popOver.add(new WebLabel(message));
		}

		WebButton btnAction = new WebButton(actionButtonText, action);

		btnAction.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				popOver.dispose();
			}
		});

		popOver.add(new SingleAlignPanel(btnAction, SingleAlignPanel.RIGHT)
				.setMargin(10, 0, 0, 0));

		popOver.show(target);
	}

	public static void showNotification(String message) {
		final WebNotificationPopup notificationPopup = new WebNotificationPopup();
		notificationPopup.setIcon(NotificationIcon.information);
		notificationPopup.setDisplayTime(3000);
		notificationPopup.setContent(new WebLabel(message));

		com.alee.managers.notification.NotificationManager
				.showNotification(notificationPopup);
	}

}