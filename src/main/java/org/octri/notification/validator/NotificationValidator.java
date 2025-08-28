package org.octri.notification.validator;

import org.octri.notification.domain.Notification;
import org.octri.notification.domain.ValidationResult;

/**
 * Interface for a component that can validate whether a Notification should be sent
 */
public interface NotificationValidator {

	/**
	 * 
	 * @param notification
	 *            the Notification to validate
	 * @return the result of the validation
	 */
	public ValidationResult validate(Notification notification);

}
