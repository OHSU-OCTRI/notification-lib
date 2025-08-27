package org.octri.notification.repository;

import java.time.LocalDate;
import java.util.List;

import org.octri.notification.domain.DefaultNotificationStatus;
import org.octri.notification.domain.Notification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * The repository for working with notifications in the database
 */
public interface NotificationRepository extends CrudRepository<Notification, Long> {

	/**
	 * The query to get Twilio notifications that need to be checked for final disposition
	 */
	static final String queuedTwilioNotificationQuery = """
			SELECT *
			FROM notification
			WHERE notification_status = 'SENT'
			  AND JSON_TYPE(notification_status_metadata->'$.dispatchResult') <> 'NULL'
			  AND JSON_CONTAINS_PATH(notification_status_metadata->>'$.dispatchResult.deliveryDetails', 'one', '$.accountSid')
			  AND JSON_UNQUOTE(JSON_EXTRACT(notification_status_metadata->>'$.dispatchResult.deliveryDetails', '$.status')) = 'QUEUED'
			""";

	/**
	 * 
	 * @param notificationStatus
	 *            the notification status of notifications to find
	 * @param currentDate
	 *            the date to check for notifications against
	 * @return the notifications with the given status scheduled before or on the date passed in
	 */
	List<Notification> findByNotificationStatusAndDateScheduledLessThanEqual(
			DefaultNotificationStatus notificationStatus,
			LocalDate currentDate);

	/**
	 * 
	 * @return all the Twilio notifications that have not been checked for final disposition
	 */
	@Query(value = queuedTwilioNotificationQuery, nativeQuery = true)
	List<Notification> findAllQueuedTwilioNotifications();
}