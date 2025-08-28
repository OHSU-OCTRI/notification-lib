package org.octri.notification.batch;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import org.octri.common.customizer.IdentifiableEntityFinder;
import org.octri.notification.domain.DefaultNotificationStatus;
import org.octri.notification.domain.Notification;
import org.octri.notification.domain.ProcessingMode;
import org.octri.notification.domain.Recipient;
import org.octri.notification.registry.NotificationTypeRegistry;
import org.octri.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;

/**
 * The reader finds the notifications that are due for processing.
 */
public class NotificationItemReader implements ItemReader<Notification> {

	private static final Logger logger = LoggerFactory.getLogger(NotificationItemReader.class);
	private final NotificationTypeRegistry notificationTypeRegistry;
	private final NotificationRepository notificationRepository;
	private final ProcessingMode jobProcessingMode;
	private final IdentifiableEntityFinder<?> recipientFinder;
	private Iterator<Notification> notificationIterator;

	/**
	 * 
	 * @param notificationTypeRegistry
	 *            the notification type registry
	 * @param notificationRepository
	 *            the notification repository
	 * @param recipientFinder
	 *            the finder for recipients
	 * @param jobProcessingMode
	 *            the processing mode for the job
	 */
	public NotificationItemReader(NotificationTypeRegistry notificationTypeRegistry,
			NotificationRepository notificationRepository, IdentifiableEntityFinder<?> recipientFinder,
			ProcessingMode jobProcessingMode) {
		this.notificationTypeRegistry = notificationTypeRegistry;
		this.notificationRepository = notificationRepository;
		this.recipientFinder = recipientFinder;
		this.jobProcessingMode = jobProcessingMode;
	}

	@Override
	public Notification read() {
		if (notificationIterator == null) {
			LocalDate today = LocalDate.now();
			List<Notification> notifications = notificationRepository
					.findByNotificationStatusAndDateScheduledLessThanEqual(DefaultNotificationStatus.SCHEDULED, today);
			if (ProcessingMode.IMMEDIATE.equals(jobProcessingMode)) {
				notifications = notifications.stream().filter(notification -> {
					var handler = notificationTypeRegistry.getHandler(notification.getNotificationType());
					// Let through types with a null handler. The error will get flagged in the processor.
					return handler != null ? ProcessingMode.IMMEDIATE.equals(handler.getProcessingMode()) : true;
				}).toList();
			}
			logger.debug(String.format("Reading %d past due scheduled notifications in %s processing mode",
					notifications.size(), jobProcessingMode));
			notificationIterator = notifications.iterator();
		}
		Notification notification = notificationIterator.hasNext() ? notificationIterator.next() : null;
		if (notification != null) {
			// Ensure the Recipient entity is set before processing
			notification.setRecipient((Recipient) recipientFinder.findByUuid(notification.getRecipientUuid()));
		}
		return notification;
	}

}
