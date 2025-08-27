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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
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
	private final JobExplorer jobExplorer;
	private final JobLauncher jobLauncher;

	private final PlatformTransactionManager transactionManager;

	private final NotificationConfig notificationConfig;
	private final NotificationRepository notificationRepository;

	private final TwilioHelper twilioHelper;

	/**
	 * 
	 * @param jobRepository
	 *            the JobRepository
	 * @param jobExplorer
	 *            the JobExplorer
	 * @param jobLauncher
	 *            the JobLauncher
	 * @param transactionManager
	 *            the PlatformTransactionManager
	 * @param notificationConfig
	 *            the NotificationConfig
	 * @param notificationRepository
	 *            the NotificationRepository
	 * @param twilioHelper
	 *            the TwilioHelper
	 */
	public TwilioStatusBatchConfig(JobRepository jobRepository, JobExplorer jobExplorer, JobLauncher jobLauncher,
			PlatformTransactionManager transactionManager,
			NotificationConfig notificationConfig, NotificationRepository notificationRepository,
			TwilioHelper twilioHelper) {
		log.debug("Creating Twilio update job beans");
		this.jobRepository = jobRepository;
		this.jobExplorer = jobExplorer;
		this.jobLauncher = jobLauncher;
		this.transactionManager = transactionManager;
		this.notificationConfig = notificationConfig;
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
	 * @param jobLauncher
	 *            the JobLauncher
	 * @param twilioStatusItemReader
	 *            the item reader
	 * @return the bean for the Job
	 */
	@Bean
	public Job twilioUpdateJob(JobLauncher jobLauncher, ItemReader<Notification> twilioStatusItemReader) {
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
		return new TwilioStatusUpdateJob(jobExplorer, jobLauncher, twilioUpdateJob);
	}

	private Step twilioUpdateStep(ItemReader<Notification> reader) {
		return new StepBuilder("processTwilioNotificationsStep", jobRepository)
				.<Notification, Notification> chunk(notificationConfig.getChunkSize(), transactionManager)
				.reader(reader)
				.writer(twilioStatusItemWriter())
				.build();
	}

}
