package org.octri.notification.dispatch;

import java.util.List;

import org.octri.notification.domain.DispatchResult;
import org.octri.notification.domain.Notification;

/**
 * Interface for a component that can dispatch a {@link Notification}
 */
public interface NotificationDispatcher {

	/**
	 * Method for single-dispatch scenarios. If the application only needs to dispatch once per Notification, override
	 * this method only.
	 * 
	 * @param notification
	 *            the notification to dispatch
	 * @return the DispatchResult
	 */
	default public DispatchResult handleDispatch(Notification notification) {
		throw new UnsupportedOperationException(
				getClass().getSimpleName() + " must override handleDispatch() or handleDispatches()");
	}

	/**
	 * Method for multi-dispatch scenarios. Default implementation wraps
	 * the single result from handleDispatch() in a singleton list.
	 *
	 * Implementers who want to support multiple dispatches (e.g., EMAIL and TEXT)
	 * can override this method instead. A Notification will be created for each DispatchResult.
	 * 
	 * @param notification
	 *            the notification to dispatch
	 * @return a list of DispatchResults
	 */
	default List<DispatchResult> handleDispatches(Notification notification) {
		DispatchResult result = handleDispatch(notification);
		return result == null ? List.of() : List.of(result);
	}

}
