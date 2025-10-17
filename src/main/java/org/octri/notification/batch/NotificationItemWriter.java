package org.octri.notification.batch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.octri.notification.domain.DefaultNotificationStatus;
import org.octri.notification.domain.DispatchResult;
import org.octri.notification.domain.Notification;
import org.octri.notification.domain.Notification.NotificationStatusMetadata;
import org.octri.notification.domain.ReminderDayProgressionTracker;
import org.octri.notification.domain.ValidationResult;
import org.octri.notification.registry.MissingHandlerException;
import org.octri.notification.registry.NotificationTypeRegistry;
import org.octri.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * The writer tries to send the notification if it is still valid and sets the status.
 */
public class NotificationItemWriter implements ItemWriter<Notification> {

	private static final Logger logger = LoggerFactory.getLogger(NotificationItemWriter.class);
	private final NotificationRepository notificationRepository;
	private NotificationTypeRegistry notificationTypeRegistry;
	private Set<String> dispatchedKeys = new HashSet<>();

	/**
	 * 
	 * @param notificationRepository
	 *            the notification repository
	 * @param notificationTypeRegistry
	 *            the notification type registry
	 */
	public NotificationItemWriter(NotificationRepository notificationRepository,
			NotificationTypeRegistry notificationTypeRegistry) {
		this.notificationRepository = notificationRepository;
		this.notificationTypeRegistry = notificationTypeRegistry;
	}

	@Override
	public void write(Chunk<? extends Notification> notifications) throws Exception {
		for (Notification notification : notifications) {
			var notificationStatusMetadata = notification.getStatusMetadata();
			notification.setDateTimeProcessed(LocalDateTime.now());
			if (isNotificationValid(notificationStatusMetadata)) {
				var dispatchKey = notification.getDispatchKey();
				if (dispatchedKeys.contains(dispatchKey)) {
					var validationResult = new ValidationResult(false, "Duplicate notification - will not be sent.");
					notification.setNotificationStatusMetadata(new NotificationStatusMetadata(validationResult, null));
					notification = updateInvalidNotification(notification);
				} else {
					var handler = notificationTypeRegistry.getHandler(notification.getNotificationType());
					if (handler == null) {
						throw new MissingHandlerException(
								String.format("No handler found for notification type: %s",
										notification.getNotificationType()));
					}
					var dispatcher = handler.getDispatcher();
					var dispatchResults = dispatcher.handleDispatches(notification);
					createAdditionalNotificationsIfNeeded(notification, dispatchResults);
					if (notification
							.getNotificationMetadata(
									handler.getMetadataClass()) instanceof ReminderDayProgressionTracker) {
						ReminderDayProgressionTracker progressionTracker = (ReminderDayProgressionTracker) notification
								.getNotificationMetadata(handler.getMetadataClass());
						Optional<Notification> nextNotification = nextNotification(notification, progressionTracker);
						if (nextNotification.isPresent()) {
							notificationRepository.save(nextNotification.get());
						}
					}
					notification = updateDispatchedNotification(notification,
							notificationStatusMetadata.validationResult(),
							dispatchResults.get(0));
					if (dispatchResults.get(0).successful()) {
						dispatchedKeys.add(dispatchKey);
					}
				}
			} else {
				notification = updateInvalidNotification(notification);
			}

			notificationRepository.save(notification);
		}
	}

	/**
	 * If there is more than one dispatch result, create additional Notifications to match
	 * 
	 * @param notification
	 * @param dispatchResults
	 */
	private void createAdditionalNotificationsIfNeeded(Notification notification,
			List<DispatchResult> dispatchResults) {

		if (dispatchResults.size() > 1) {
			List<Notification> newNotifications = new ArrayList<>();
			for (int i = 1; i < dispatchResults.size(); i++) {
				DispatchResult dispatchResult = dispatchResults.get(i);
				Notification copy = new Notification();
				copy.setDateScheduled(notification.getDateScheduled());
				copy.setDateTimeProcessed(notification.getDateTimeProcessed());
				copy.setNotificationMetadata(notification.getNotificationMetadata());
				copy.setNotificationStatus(notification.getNotificationStatus());
				copy.setNotificationStatusMetadata(notification.getNotificationStatusMetadata());
				copy.setNotificationType(notification.getNotificationType());
				copy.setRecipientUuid(notification.getRecipientUuid());
				copy = updateDispatchedNotification(copy, copy.getStatusMetadata().validationResult(), dispatchResult);
				newNotifications.add(copy);
			}
			notificationRepository.saveAll(newNotifications);
		}
	}

	/**
	 * Generate the next future Notification in the progression of reminders
	 * 
	 * @param notification
	 * @param progressionTracker
	 * @return
	 * @throws JsonProcessingException
	 */
	static Optional<Notification> nextNotification(Notification notification,
			ReminderDayProgressionTracker progressionTracker)
			throws JsonProcessingException {

		// Skip any notifications that fall in the past, advancing to the next future date in the progression tracker
		while (!progressionTracker.isFinal()) {
			progressionTracker.advance();
			if (progressionTracker.getCurrentDate().isAfter(LocalDate.now())) {
				logger.debug("Adding the next notification in the series.");
				Notification copy = new Notification();
				copy.setNotificationType(notification.getNotificationType());
				copy.setRecipientUuid(notification.getRecipientUuid());
				copy.setNotificationStatus(DefaultNotificationStatus.SCHEDULED);
				copy.setDateScheduled(progressionTracker.getCurrentDate());
				copy.setNotificationMetadata(notification.getObjectMapper().writeValueAsString(progressionTracker));
				return Optional.of(copy);
			}
		}

		return Optional.empty();
	}

	static boolean isNotificationValid(NotificationStatusMetadata notificationStatusMetadata) {
		return notificationStatusMetadata.validationResult().successful();
	}

	static Notification updateInvalidNotification(Notification notification) {
		logger.debug(String.format("Notification %s marked invalid", notification.getId()));
		notification.setNotificationStatus(DefaultNotificationStatus.INVALID);
		return notification;
	}

	static Notification updateDispatchedNotification(Notification notification,
			ValidationResult validationResult,
			DispatchResult dispatchResult) {
		notification.setNotificationStatusMetadata(
				new NotificationStatusMetadata(validationResult,
						dispatchResult));
		if (dispatchResult.successful()) {
			notification.setNotificationStatus(DefaultNotificationStatus.SENT);
			logger.debug(String.format("Notification %s sent successfully", notification.getId()));
		} else {
			notification.setNotificationStatus(DefaultNotificationStatus.FAILED);
			logger.error(String.format("Notification %s failed to send: %s", notification.getId(),
					dispatchResult.errorMessage()));
		}
		return notification;
	}

}