package org.octri.notification.dispatch;

import org.octri.notification.domain.DispatchResult;
import org.octri.notification.domain.Notification;

/**
 * Interface for a component that can dispatch a Notification of the NotificationType provided.
 */
public interface NotificationDispatcher {

	/**
	 * 
	 * @param notification
	 *            the notification to dispatch
	 * @return the DispatchResult
	 */
	public DispatchResult handleDispatch(Notification notification);

}
