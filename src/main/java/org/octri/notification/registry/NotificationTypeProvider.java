package org.octri.notification.registry;

import org.octri.notification.dispatch.NotificationDispatcher;
import org.octri.notification.domain.ProcessingMode;
import org.octri.notification.metadata.EmptyMetadata;
import org.octri.notification.metadata.NotificationMetadata;
import org.octri.notification.validator.NotificationValidator;
import org.octri.notification.view.EmptyMetadataViewer;
import org.octri.notification.view.NotificationViewer;

/**
 * Interface for objects that provide the dependencies needed to process a type of notification. Notification types
 * provided by instances of this interface are discovered at runtime and automatically registered with the
 * {@link NotificationTypeRegistry}.
 */
public interface NotificationTypeProvider {

	/**
	 * Unique type name for this type of notification
	 *
	 * @return
	 */
	String getNotificationType();

	/**
	 * Processing mode for this type of notification. Defaults to SCHEDULED.
	 *
	 * @return
	 */
	default ProcessingMode getProcessingMode() {
		return ProcessingMode.SCHEDULED;
	}

	/**
	 * Class of NotificationMetadata used by this type of notification. Defaults to {@link EmptyMetadata}.
	 *
	 * @return
	 */
	default Class<? extends NotificationMetadata> getNotificationMetadata() {
		return EmptyMetadata.class;
	}

	/**
	 * Validator used to determine if a notification of this type should be delivered.
	 *
	 * @return
	 */
	default NotificationValidator getNotificationValidator() {
		return NotificationValidator.NOOP;
	};

	/**
	 * Dispatcher used to deliver notifications of this type.
	 *
	 * @return
	 */
	NotificationDispatcher getNotificationDispatcher();

	/**
	 * Viewer used to display metadata for this type of notification. Defaults to {@link EmptyMetadataViewer}.
	 *
	 * @return
	 */
	default NotificationViewer getNotificationViewer() {
		return new EmptyMetadataViewer();
	}

}
