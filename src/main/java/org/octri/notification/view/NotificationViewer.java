package org.octri.notification.view;

import org.octri.notification.domain.Notification;

/**
 * Interface for customizing the view of the notification and its metadata.
 */
public interface NotificationViewer {

	/**
	 * Applications that need to fetch data for each notification can prefetch and cache in a ThreadLocal to optimize
	 * performance.
	 * 
	 * @param notifications
	 *            the list of notifications that will be viewed
	 * @return an AutoCloseable that will clear the cache when closed; default has no cache and returns a no-op
	 *         AutoCloseable
	 */
	default AutoCloseable prepareCache(Iterable<Notification> notifications) {
		return () -> {
		};
	}

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
