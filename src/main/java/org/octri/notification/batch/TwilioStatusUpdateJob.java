package org.octri.notification.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * The job responsible for checking Twilio for the final disposition of Notifications
 */
public class TwilioStatusUpdateJob {

	/**
	 * Name of the job
	 */
	public static final String TWILIO_UPDATE_JOB_NAME = "twilioUpdateJob";

	private final JobExplorer jobExplorer;
	private final JobLauncher jobLauncher;
	private final Job twilioUpdateJob;

	/**
	 * 
	 * @param jobExplorer
	 *            the job explorer
	 * @param jobLauncher
	 *            the job launcher
	 * @param twilioUpdateJob
	 *            the job for checking Twilio
	 */
	public TwilioStatusUpdateJob(JobExplorer jobExplorer, JobLauncher jobLauncher, Job twilioUpdateJob) {
		this.jobExplorer = jobExplorer;
		this.jobLauncher = jobLauncher;
		this.twilioUpdateJob = twilioUpdateJob;
	}

	/**
	 * Runs the job on demand
	 * 
	 * @throws Exception
	 *             thrown by the JobLauncher
	 */
	public void runTwilioUpdateJob() throws Exception {
		if (isJobRunning()) {
			throw new DuplicateJobException("The Twilio update job is already running.");
		}
		var jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
		jobLauncher.run(twilioUpdateJob, jobParameters);
	}

	/**
	 * Runs the job on the cron schedule
	 * 
	 * @throws Exception
	 *             thrown by the JobLauncher
	 */
	@Scheduled(cron = "${ctri.notifications.twilio-update-schedule:@yearly}", scheduler = "notificationTaskScheduler")
	public void runScheduledTwilioUpdateJob() throws Exception {
		runTwilioUpdateJob();
	}

	private boolean isJobRunning() {
		return jobExplorer.findRunningJobExecutions(TWILIO_UPDATE_JOB_NAME).size() > 0;
	}

}
