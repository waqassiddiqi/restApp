package net.waqassiddiqi.app.crew.util;

import java.awt.Component;

import net.waqassiddiqi.app.crew.ui.MainFrame;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.label.WebLabel;
import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.WebNotificationPopup;

public class NotificationManager {
	
	public static void showPopup(MainFrame owner, Component target, String[] messages) {
		final WebPopOver popOver = new WebPopOver(owner);
		popOver.setCloseOnFocusLoss(true);
		popOver.setMargin(10);
		popOver.setLayout(new VerticalFlowLayout());
		
		for(String message : messages) {
			popOver.add(new WebLabel(message));
		}
		
		popOver.show(target);
	}
	
	public static void showNotification(String message) {
		final WebNotificationPopup notificationPopup = new WebNotificationPopup();
		notificationPopup.setIcon(NotificationIcon.information);
		notificationPopup.setDisplayTime(3000);
		notificationPopup.setContent(new WebLabel(message));

		com.alee.managers.notification.NotificationManager.showNotification(notificationPopup);
	}
	
}