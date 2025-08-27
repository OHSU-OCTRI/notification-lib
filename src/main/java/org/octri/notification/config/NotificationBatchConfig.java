package org.octri.notification.config;

import org.octri.common.customizer.IdentifiableEntityFinder;
import org.octri.notification.batch.NotificationItemProcessor;
import org.octri.notification.batch.NotificationItemReader;
import org.octri.notification.batch.NotificationItemWriter;
import org.octri.notification.domain.Notification;
import org.octri.notification.domain.ProcessingMode;
import org.octri.notification.registry.NotificationTypeRegistry;
import org.octri.notification.repository.NotificationRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuration for the Notification batch job
 */
@Configuration
public class NotificationBatchConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final NotificationRepository notificationRepository;
	private final NotificationConfig notificationConfig;
	private final NotificationTypeRegistry notificationTypeRegistry;
	private final IdentifiableEntityFinder<?> recipientFinder;

	/**
	 * 
	 * @param jobRepository
	 *            the job repository
	 * @param transactionManager
	 *            the transaction manager
	 * @param notificationRepository
	 *            the notification repository
	 * @param notificationConfig
	 *            the notification configuration
	 * @param notificationTypeRegistry
	 *            the registry for notification types
	 * @param recipientFinder
	 *            the finder for recipients
	 */
	public NotificationBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			NotificationRepository notificationRepository, NotificationConfig notificationConfig,
			NotificationTypeRegistry notificationTypeRegistry, IdentifiableEntityFinder<?> recipientFinder) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.notificationRepository = notificationRepository;
		this.notificationConfig = notificationConfig;
		this.notificationTypeRegistry = notificationTypeRegistry;
		this.recipientFinder = recipientFinder;
	}

	/**
	 * 
	 * @param notificationItemReader
	 *            the ItemReader for notifications
	 * @return the notification job bean
	 */
	@Bean
	public Job notificationJob(ItemReader<Notification> notificationItemReader) {
		return new JobBuilder("notificationJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(processNotificationsStep(notificationItemReader))
				.build();
	}

	/**
	 * The scope ensures a new reader for each JobExecution and allows jobParameters to be injected
	 * 
	 * @param processingMode
	 *            the processing mode provided by the job parameters
	 * @return the notification item reader bean
	 */
	@Bean
	@StepScope
	public ItemReader<Notification> notificationItemReader(
			@Value("#{jobParameters['processingMode']}") String processingMode) {
		return new NotificationItemReader(notificationTypeRegistry, notificationRepository, recipientFinder,
				ProcessingMode.valueOf(processingMode));
	}

	/**
	 * 
	 * @return the notification item processor bean
	 */
	@Bean
	public ItemProcessor<Notification, Notification> notificationItemProcessor() {
		return new NotificationItemProcessor(notificationTypeRegistry);
	}

	/**
	 * 
	 * @return the notification item writer bean
	 */
	@Bean
	@JobScope
	public ItemWriter<Notification> notificationItemWriter() {
		return new NotificationItemWriter(notificationRepository, notificationTypeRegistry);
	}

	private Step processNotificationsStep(ItemReader<Notification> reader) {
		return new StepBuilder("processNotificationsStep", jobRepository)
				.<Notification, Notification> chunk(notificationConfig.getChunkSize(), transactionManager)
				.reader(reader)
				.processor(notificationItemProcessor())
				.writer(notificationItemWriter())
				.build();
	}
}
