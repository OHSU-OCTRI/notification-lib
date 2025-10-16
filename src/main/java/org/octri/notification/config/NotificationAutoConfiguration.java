package org.octri.notification.config;

import org.octri.notification.batch.NotificationBatchJob;
import org.octri.notification.controller.NotificationController;
import org.octri.notification.converter.NotificationStatusMvcConverter;
import org.octri.notification.registry.NotificationStatusRegistry;
import org.octri.notification.registry.NotificationTypeRegistry;
import org.octri.notification.repository.NotificationRepository;
import org.octri.notification.service.NotificationService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration for the notification library.
 */
@AutoConfiguration
@EnableConfigurationProperties(NotificationProperties.class)
@ConditionalOnProperty(value = "octri.notifications.enabled", havingValue = "true", matchIfMissing = true)
@EntityScan(basePackages = { "org.octri.notification.domain", "org.octri.notification.converter" })
@EnableJpaRepositories(basePackages = "org.octri.notification.repository")
@Import({ NotificationBatchConfig.class, TwilioStatusBatchConfig.class, NotificationController.class })
public class NotificationAutoConfiguration {

	/**
	 * 
	 * @return a bean for the notification type registry
	 */
	@Bean
	public NotificationTypeRegistry notificationTypeRegistry() {
		return new NotificationTypeRegistry();
	}

	/**
	 * 
	 * @return a bean for the notification status registry
	 */
	@Bean
	public NotificationStatusRegistry notificationStatusRegistry() {
		return new NotificationStatusRegistry();
	}

	/**
	 * 
	 * @param notificationStatusRegistry
	 *            the notification status registry
	 * @return a bean for the notification status MVC converter
	 */
	@Bean
	public NotificationStatusMvcConverter notificationStatusMvcConverter(
			NotificationStatusRegistry notificationStatusRegistry) {
		return new NotificationStatusMvcConverter(notificationStatusRegistry);
	}

	/**
	 * 
	 * @param notificationRepository
	 *            the notification repository
	 * @param notificationTypeRegistry
	 *            the notification type registry
	 * @param notificationBatchJob
	 *            the notification batch job
	 * @return a bean for the notification service
	 */
	@Bean
	public NotificationService notificationService(NotificationRepository notificationRepository,
			NotificationTypeRegistry notificationTypeRegistry,
			NotificationBatchJob notificationBatchJob) {
		return new NotificationService(notificationRepository, notificationTypeRegistry, notificationBatchJob);
	}

}
