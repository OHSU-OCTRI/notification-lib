package org.octri.notification.batch;

import org.octri.notification.domain.Notification;
import org.octri.notification.domain.Notification.NotificationStatusMetadata;
import org.octri.notification.domain.ValidationResult;
import org.octri.notification.registry.NotificationTypeRegistry;
import org.octri.notification.validator.NotificationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * The processor checks whether the Notification is still valid and persists the ValidationStatus back
 */
public class NotificationItemProcessor implements ItemProcessor<Notification, Notification> {

	private static final Logger logger = LoggerFactory.getLogger(NotificationItemProcessor.class);

	private final NotificationTypeRegistry notificationTypeRegistry;

	/**
	 * 
	 * @param notificationTypeRegistry
	 *            the notification type registry
	 */
	public NotificationItemProcessor(NotificationTypeRegistry notificationTypeRegistry) {
		this.notificationTypeRegistry = notificationTypeRegistry;
	}

	@Override
	public Notification process(Notification notification) {
		logger.debug("Processing notification " + notification.getId());
		ValidationResult validationResult = validate(notification);
		if (!validationResult.successful()) {
			logger.debug("Notification is no longer valid");
		}
		notification.setNotificationStatusMetadata(
				new NotificationStatusMetadata(validationResult, null));
		return notification;
	}

	private ValidationResult validate(Notification notification) {
		var handler = notificationTypeRegistry.getHandler(notification.getNotificationType());
		if (handler == null) {
			return new ValidationResult(false, "The application does not support this notification type.");
		}
		NotificationValidator validator = handler.getValidator();
		return validator.validate(notification);
	}

}
