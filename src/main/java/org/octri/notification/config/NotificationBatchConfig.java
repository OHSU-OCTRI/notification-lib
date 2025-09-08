package org.octri.notification.config;

import org.octri.common.customizer.IdentifiableEntityFinder;
import org.octri.notification.batch.NotificationBatchJob;
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
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuration for the Notification batch job
 */
@Configuration
public class NotificationBatchConfig {

	private final JobExplorer jobExplorer;
	private final JobLauncher jobLauncher;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final NotificationRepository notificationRepository;
	private final NotificationProperties notificationProperties;
	private final NotificationTypeRegistry notificationTypeRegistry;
	private final IdentifiableEntityFinder<?> recipientFinder;

	/**
	 * 
	 * @param jobExplorer
	 *            the job explorer
	 * @param jobLauncher
	 *            the job launcher
	 * @param jobRepository
	 *            the job repository
	 * @param transactionManager
	 *            the transaction manager
	 * @param notificationRepository
	 *            the notification repository
	 * @param notificationProperties
	 *            the notification configuration
	 * @param notificationTypeRegistry
	 *            the registry for notification types
	 * @param recipientFinder
	 *            the finder for recipients
	 */
	public NotificationBatchConfig(JobExplorer jobExplorer, JobLauncher jobLauncher, JobRepository jobRepository,
			PlatformTransactionManager transactionManager,
			NotificationRepository notificationRepository, NotificationProperties notificationProperties,
			NotificationTypeRegistry notificationTypeRegistry, IdentifiableEntityFinder<?> recipientFinder) {
		this.jobExplorer = jobExplorer;
		this.jobLauncher = jobLauncher;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.notificationRepository = notificationRepository;
		this.notificationProperties = notificationProperties;
		this.notificationTypeRegistry = notificationTypeRegistry;
		this.recipientFinder = recipientFinder;
	}

	/**
	 * 
	 * @param notificationJob
	 *            the spring batch job
	 * @return the Bean for the NotificationBatchJob
	 */
	@Bean
	public NotificationBatchJob notificationBatchJob(Job notificationJob) {
		return new NotificationBatchJob(jobExplorer, jobLauncher, notificationJob);
	}

	/**
	 * 
	 * @param notificationItemReader
	 *            the ItemReader for notifications
	 * @return the job bean
	 */
	@Bean
	@ConditionalOnMissingBean(name = "notificationJob")
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
	@ConditionalOnMissingBean(name = "notificationItemReader")
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
	@ConditionalOnMissingBean(name = "notificationItemProcessor")
	public ItemProcessor<Notification, Notification> notificationItemProcessor() {
		return new NotificationItemProcessor(notificationTypeRegistry);
	}

	/**
	 * 
	 * @return the notification item writer bean
	 */
	@Bean
	@ConditionalOnMissingBean(name = "notificationItemWriter")
	@JobScope
	public ItemWriter<Notification> notificationItemWriter() {
		return new NotificationItemWriter(notificationRepository, notificationTypeRegistry);
	}

	private Step processNotificationsStep(ItemReader<Notification> reader) {
		return new StepBuilder("processNotificationsStep", jobRepository)
				.<Notification, Notification> chunk(notificationProperties.getChunkSize(), transactionManager)
				.reader(reader)
				.processor(notificationItemProcessor())
				.writer(notificationItemWriter())
				.build();
	}
}
