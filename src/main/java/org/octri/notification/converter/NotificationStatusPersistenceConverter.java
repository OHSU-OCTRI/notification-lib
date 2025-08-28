package org.octri.notification.converter;

import org.octri.notification.domain.NotificationStatus;
import org.octri.notification.registry.NotificationStatusRegistry;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * This Converter tells Hibernate how to serialize/deserialize NotificationStatus from the persistence layer
 */
@Converter(autoApply = true)
public class NotificationStatusPersistenceConverter implements AttributeConverter<NotificationStatus, String> {

	private final NotificationStatusRegistry notificationStatusRegistry;

	/**
	 * 
	 * @param notificationStatusRegistry
	 *            the registry for notification statuses
	 */
	public NotificationStatusPersistenceConverter(NotificationStatusRegistry notificationStatusRegistry) {
		this.notificationStatusRegistry = notificationStatusRegistry;
	}

	@Override
	public String convertToDatabaseColumn(NotificationStatus status) {
		return (status == null) ? null : status.name();
	}

	@Override
	public NotificationStatus convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		return notificationStatusRegistry.getStatusByName(dbData).orElseThrow();
	}
}