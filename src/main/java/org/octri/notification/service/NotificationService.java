package org.octri.notification.service;

import java.util.stream.StreamSupport;

import org.octri.notification.batch.NotificationBatchJob;
import org.octri.notification.domain.Notification;
import org.octri.notification.domain.ProcessingMode;
import org.octri.notification.registry.NotificationTypeRegistry;
import org.octri.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

/**
 * Service for working with Notifications
 */
@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final NotificationTypeRegistry notificationTypeRegistry;
	private final NotificationBatchJob notificationBatchJob;

	/**
	 * 
	 * @param notificationRepository
	 *            the notification repository
	 * @param notificationTypeRegistry
	 *            the notification type registry
	 * @param notificationBatchJob
	 *            the notification batch job
	 */
	public NotificationService(NotificationRepository notificationRepository,
			NotificationTypeRegistry notificationTypeRegistry, NotificationBatchJob notificationBatchJob) {
		this.notificationRepository = notificationRepository;
		this.notificationTypeRegistry = notificationTypeRegistry;
		this.notificationBatchJob = notificationBatchJob;
	}

	/**
	 * Persist a new notification. If the registered Notification type needs immediate processing, kick off the batch
	 * job.
	 * 
	 * @param notification
	 *            the notification to save
	 * @return the persisted notification
	 * @throws Exception
	 *             thrown by the JobLauncher if the batch job is triggered
	 */
	public Notification createNew(Notification notification) throws Exception {
		assert notification.getId() == null;
		Notification saved = notificationRepository.save(notification);
		var handler = notificationTypeRegistry.getHandler(notification.getNotificationType());
		if (handler != null && ProcessingMode.IMMEDIATE.equals(handler.getProcessingMode())) {
			notificationBatchJob.runImmediateNotificationJob();
		}

		return saved;
	}

	/**
	 * Persist multiple notifications. If any of the registered Notification types need immediate processing, kick off
	 * the batch job.
	 * 
	 * @param notifications
	 * @return the persisted notifications
	 * @throws Exception
	 *             thrown by the JobLauncher if the batch job is triggered
	 */
	public Iterable<Notification> createAll(Iterable<Notification> notifications) throws Exception {
		Iterable<Notification> saved = notificationRepository.saveAll(notifications);
		var needsImmediateHandling = StreamSupport.stream(saved.spliterator(), false)
				.map(n -> notificationTypeRegistry.getHandler(n.getNotificationType()))
				.anyMatch(h -> h != null && ProcessingMode.IMMEDIATE.equals(h.getProcessingMode()));

		if (needsImmediateHandling) {
			notificationBatchJob.runImmediateNotificationJob();
		}

		return saved;
	}

}
