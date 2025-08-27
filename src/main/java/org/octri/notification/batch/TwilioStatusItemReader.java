package org.octri.notification.batch;

import java.util.Iterator;

import org.octri.notification.domain.Notification;
import org.octri.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;

/**
 * Determines the set of Notifications that need to be checked for final disposition in Twilio
 */
public class TwilioStatusItemReader implements ItemReader<Notification> {

	private static final Logger log = LoggerFactory.getLogger(TwilioStatusItemReader.class);

	private final NotificationRepository notificationRepository;
	private Iterator<Notification> iterator;

	/**
	 * 
	 * @param notificationRepository
	 *            the Notification repository
	 */
	public TwilioStatusItemReader(NotificationRepository notificationRepository) {
		this.notificationRepository = notificationRepository;
	}

	@Override
	public Notification read() {
		var queuedNotifications = notificationRepository.findAllQueuedTwilioNotifications();
		log.debug("Updating status for {} queued Twilio notifications.", queuedNotifications.size());
		if (iterator == null) {
			iterator = queuedNotifications.iterator();
		}
		return iterator.hasNext() ? iterator.next() : null;
	}

}
