package org.octri.notification.batch;

import org.octri.notification.domain.ProcessingMode;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * The component that handles running the job if it's not already in progress
 */
public class NotificationBatchJob {

	private final JobExplorer jobExplorer;
	private final JobLauncher jobLauncher;
	private final Job notificationJob;

	/**
	 * 
	 * @param jobExplorer
	 *            the JobExplorer
	 * @param jobLauncher
	 *            the JobLauncher
	 * @param notificationJob
	 *            the notificationJob bean
	 */
	public NotificationBatchJob(JobExplorer jobExplorer, JobLauncher jobLauncher, Job notificationJob) {
		this.jobExplorer = jobExplorer;
		this.jobLauncher = jobLauncher;
		this.notificationJob = notificationJob;
	}

	/**
	 * Run the batch process for sending notifications
	 * 
	 * @param mode
	 *            the job ProcessingMode
	 * @throws Exception
	 *             exception thrown by the jobLauncher
	 */
	public void runNotificationJob(ProcessingMode mode) throws Exception {
		if (isJobRunning("notificationJob")) {
			throw new DuplicateJobException("The notification job is already running.");
		}
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.addString("processingMode", mode.name())
				.toJobParameters();
		jobLauncher.run(notificationJob, jobParameters);
	}

	/**
	 * Run the job to process IMMEDIATE notifications
	 * 
	 * @throws Exception
	 *             exception thrown by the jobLauncher
	 */
	public void runImmediateNotificationJob() throws Exception {
		runNotificationJob(ProcessingMode.IMMEDIATE);
	}

	/**
	 * Run the job to process SCHEDULED notifications.
	 * 
	 * @throws Exception
	 *             exception thrown by the jobLauncher
	 */
	@Scheduled(cron = "${octri.notifications.schedule:@yearly}")
	public void runScheduledNotificationJob() throws Exception {
		runNotificationJob(ProcessingMode.SCHEDULED);
	}

	private boolean isJobRunning(String jobName) {
		return jobExplorer.findRunningJobExecutions(jobName).size() > 0;
	}
}
