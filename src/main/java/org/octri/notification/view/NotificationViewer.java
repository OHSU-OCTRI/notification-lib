package org.octri.notification.view;

import org.octri.notification.domain.Notification;

/**
 * Interface for customizing the view of the notification and its metadata.
 */
public interface NotificationViewer {

	/**
	 * 
	 * @param notification
	 *            the notification whose recipient is to be viewed
	 * @return a user-friendly view of the notification recipient
	 */
	public String getRecipientView(Notification notification);

	/**
	 * @param notification
	 *            the notification whose metadata is to be viewed
	 * @return a user-friendly view of the notification metadata
	 */
	public String getMetadataView(Notification notification);

}
