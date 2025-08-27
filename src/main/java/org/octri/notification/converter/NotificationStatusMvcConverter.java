package org.octri.notification.converter;

import org.octri.notification.domain.NotificationStatus;
import org.octri.notification.registry.NotificationStatusRegistry;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * This allows Spring MVC to convert the String in the form to a NotificationStatus for persistence
 */
@Component
public class NotificationStatusMvcConverter implements Converter<String, NotificationStatus> {

	private final NotificationStatusRegistry notificationStatusRegistry;

	/**
	 * 
	 * @param notificationStatusRegistry
	 *            the registry for notification statuses
	 */
	public NotificationStatusMvcConverter(NotificationStatusRegistry notificationStatusRegistry) {
		this.notificationStatusRegistry = notificationStatusRegistry;
	}

	@Override
	public NotificationStatus convert(String status) {
		return notificationStatusRegistry.getStatusByName(status).orElseThrow();
	}
}
