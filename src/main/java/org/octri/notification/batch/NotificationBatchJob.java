package org.octri.notification.batch;

import org.octri.notification.domain.ProcessingMode;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * The component that handles running the job if it's not already in progress
 */
public class NotificationBatchJob {

	private final JobRepository jobRepository;
	private final JobOperator jobOperator;
	private final Job notificationJob;

	/**
	 * 
	 * @param jobRepository
	 *            the jobRepository
	 * @param jobOperator
	 *            the jobOperator
	 * @param notificationJob
	 *            the notificationJob bean
	 */
	public NotificationBatchJob(JobRepository jobRepository, JobOperator jobOperator, Job notificationJob) {
		this.jobRepository = jobRepository;
		this.jobOperator = jobOperator;
		this.notificationJob = notificationJob;
	}

	/**
	 * Run the batch process for sending notifications
	 * 
	 * @param mode
	 *            the job ProcessingMode
	 * @throws Exception
	 *             exception thrown by the jobOperator
	 */
	public void runNotificationJob(ProcessingMode mode) throws Exception {
		if (isJobRunning("notificationJob")) {
			throw new DuplicateJobException("The notification job is already running.");
		}
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.addString("processingMode", mode.name())
				.toJobParameters();
		jobOperator.run(notificationJob, jobParameters);
	}

	/**
	 * Run the job to process IMMEDIATE notifications
	 * 
	 * @throws Exception
	 *             exception thrown by the jobOperator
	 */
	public void runImmediateNotificationJob() throws Exception {
		runNotificationJob(ProcessingMode.IMMEDIATE);
	}

	/**
	 * Run the job to process SCHEDULED notifications.
	 * 
	 * @throws Exception
	 *             exception thrown by the jobOperator
	 */
	@Scheduled(cron = "${octri.notifications.schedule:@yearly}", scheduler = "notificationTaskScheduler")
	public void runScheduledNotificationJob() throws Exception {
		runNotificationJob(ProcessingMode.SCHEDULED);
	}

	private boolean isJobRunning(String jobName) {
		return jobRepository.findRunningJobExecutions(jobName).size() > 0;
	}
}
