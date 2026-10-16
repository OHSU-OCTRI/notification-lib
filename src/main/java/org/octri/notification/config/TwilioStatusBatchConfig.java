package org.octri.notification.config;

import org.octri.messaging.autoconfig.TwilioConfiguredCondition;
import org.octri.messaging.sms.TwilioHelper;
import org.octri.notification.batch.TwilioStatusItemReader;
import org.octri.notification.batch.TwilioStatusItemWriter;
import org.octri.notification.batch.TwilioStatusUpdateJob;
import org.octri.notification.domain.Notification;
import org.octri.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuration for the batch job for checking Twilio status
 */
@Configuration
@Conditional(TwilioConfiguredCondition.class)
public class TwilioStatusBatchConfig {

	private static final Logger log = LoggerFactory.getLogger(TwilioStatusBatchConfig.class);

	private final JobRepository jobRepository;
	private final JobOperator jobOperator;

	private final PlatformTransactionManager transactionManager;

	private final NotificationProperties notificationProperties;
	private final NotificationRepository notificationRepository;

	private final TwilioHelper twilioHelper;

	/**
	 * 
	 * @param jobRepository
	 *            the jobRepository
	 * @param jobOperator
	 *            the jobOperator
	 * @param transactionManager
	 *            the PlatformTransactionManager
	 * @param notificationProperties
	 *            the notificationProperties
	 * @param notificationRepository
	 *            the NotificationRepository
	 * @param twilioHelper
	 *            the TwilioHelper
	 */
	public TwilioStatusBatchConfig(JobRepository jobRepository, JobOperator jobOperator,
			PlatformTransactionManager transactionManager, NotificationProperties notificationProperties,
			NotificationRepository notificationRepository, TwilioHelper twilioHelper) {
		log.debug("Creating Twilio update job beans");
		this.jobRepository = jobRepository;
		this.jobOperator = jobOperator;
		this.transactionManager = transactionManager;
		this.notificationProperties = notificationProperties;
		this.notificationRepository = notificationRepository;
		this.twilioHelper = twilioHelper;
	}

	/**
	 * 
	 * @return the ItemReader bean for the TwilioStatusUpdateJob
	 */
	@Bean
	@StepScope
	public ItemReader<Notification> twilioStatusItemReader() {
		return new TwilioStatusItemReader(notificationRepository);
	}

	/**
	 * 
	 * @return the ItemWriter bean for the TwilioStatusUpdateJob
	 */
	@Bean
	@JobScope
	public ItemWriter<Notification> twilioStatusItemWriter() {
		return new TwilioStatusItemWriter(notificationRepository, twilioHelper);
	}

	/**
	 * 
	 * @param jobOperator
	 *            the jobOperator
	 * @param twilioStatusItemReader
	 *            the item reader
	 * @return the bean for the Job
	 */
	@Bean
	public Job twilioUpdateJob(JobOperator jobOperator, ItemReader<Notification> twilioStatusItemReader) {
		return new JobBuilder(TwilioStatusUpdateJob.TWILIO_UPDATE_JOB_NAME, jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(twilioUpdateStep(twilioStatusItemReader))
				.build();
	}

	/**
	 * 
	 * @param twilioUpdateJob
	 *            the Job
	 * @return the Bean for the TwilioStatusUpdateJob
	 */
	@Bean
	public TwilioStatusUpdateJob twilioStatusUpdateJob(Job twilioUpdateJob) {
		return new TwilioStatusUpdateJob(jobRepository, jobOperator, twilioUpdateJob);
	}

	private Step twilioUpdateStep(ItemReader<Notification> reader) {
		return new StepBuilder("processTwilioNotificationsStep", jobRepository)
				.<Notification, Notification> chunk(notificationProperties.getChunkSize())
				.transactionManager(transactionManager)
				.reader(reader)
				.writer(twilioStatusItemWriter())
				.build();
	}

}
