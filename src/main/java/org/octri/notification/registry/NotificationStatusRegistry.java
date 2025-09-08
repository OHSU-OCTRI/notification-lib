package org.octri.notification.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.octri.notification.batch.NotificationItemReader;
import org.octri.notification.batch.NotificationItemWriter;
import org.octri.notification.domain.DefaultNotificationStatus;
import org.octri.notification.domain.NotificationStatus;

/**
 * This component allows applications to register or deregister a {@link NotificationStatus}. This library expects to
 * use values in the {@link DefaultNotificationStatus} in the {@link NotificationItemReader} and
 * {@link NotificationItemWriter}, so these Beans would need to be overridden if an application needs to support
 * custom statuses.
 */
public class NotificationStatusRegistry {

	private List<NotificationStatus> statuses = new ArrayList<>();

	/**
	 * Initialize the registry with the default statuses
	 */
	public NotificationStatusRegistry() {
		statuses.addAll(Arrays.asList(DefaultNotificationStatus.values()));
	}

	/**
	 * Register a new NotificationStatus, removing any previous status with the same name.
	 * 
	 * @param notificationStatus
	 *            the status to register
	 */
	public void register(NotificationStatus notificationStatus) {
		statuses.removeIf(s -> s.name().equals(notificationStatus.name()));
		statuses.add(notificationStatus);
	}

	/**
	 * Remove the notification status if it exists
	 * 
	 * @param notificationStatus
	 *            the status to remove
	 */
	public void deregister(NotificationStatus notificationStatus) {
		statuses.remove(notificationStatus);
	}

	/**
	 * 
	 * @return a list of statuses sorted by the ordinal value
	 */
	public List<NotificationStatus> getStatuses() {
		statuses.sort(Comparator.comparing(NotificationStatus::ordinal));
		return statuses;
	}

	/**
	 * @param name
	 *            The name of the NotificationStatus
	 * @return the NotificationStatus object for the given name
	 */
	public Optional<NotificationStatus> getStatusByName(String name) {
		return statuses.stream().filter(s -> s.name().equals(name)).findFirst();
	}

}
