package org.octri.notification.domain;

import org.octri.common.view.Labelled;
import org.octri.notification.batch.NotificationItemReader;
import org.octri.notification.batch.NotificationItemWriter;
import org.octri.notification.registry.NotificationStatusRegistry;

/**
 * The extensible interface defining the NotificationStatus of a Notification. Applications can use the
 * {@link NotificationStatusRegistry} to add or remove statuses or override properties. This library expects a status
 * with the name 'SCHEDULED' in the {@link NotificationItemReader} and statuses with the name 'SENT' and 'INACTIVE' in
 * the {@link NotificationItemWriter}. These beans would need to be overridden if an application removes these status
 * names. Additionally, the batch job that checks Twilio for the final disposition of a text uses the 'SENT' status in
 * the NotificationRepository.
 */
public interface NotificationStatus extends Labelled {

	/**
	 * @return the unique, stable name of the status (used in DB, business logic)
	 */
	String name();

	/**
	 * 
	 * @return the order of the status, used for tabbed filter display on the Notification list page
	 */
	int ordinal();

	/**
	 * 
	 * @return whether the status should be displayed in descending order on the list page
	 */
	boolean descending();

}
