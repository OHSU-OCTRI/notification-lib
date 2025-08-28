package org.octri.notification;

import java.time.LocalDate;

import org.octri.notification.domain.Notification;
import org.octri.notification.domain.Recipient;
import org.octri.notification.domain.ValidationResult;
import org.octri.notification.metadata.NotificationMetadata;

public class NotificationTestUtil {

	public static Notification createNotification(String notificationType,
			Recipient recipient,
			NotificationMetadata notificationMetadata,
			LocalDate dateScheduled) {
		var notification = new Notification(recipient);
		notification.setNotificationType(notificationType);
		notification.setNotificationMetadata(notificationMetadata);
		notification.setDateScheduled(dateScheduled);
		return notification;
	}

	public static boolean invalidReasonMatches(ValidationResult status, String reason) {
		return status.invalidReason() != null && status.invalidReason().contains(reason);
	}

}
