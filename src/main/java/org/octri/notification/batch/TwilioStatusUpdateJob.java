package org.octri.notification.batch;

import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * The job responsible for checking Twilio for the final disposition of Notifications
 */
public class TwilioStatusUpdateJob {

	/**
	 * Name of the job
	 */
	public static final String TWILIO_UPDATE_JOB_NAME = "twilioUpdateJob";

	private final JobRepository jobRepository;
	private final JobOperator jobOperator;
	private final Job twilioUpdateJob;

	/**
	 * 
	 * @param jobRepository
	 *            the job explorer
	 * @param jobOperator
	 *            the job operator
	 * @param twilioUpdateJob
	 *            the job for checking Twilio
	 */
	public TwilioStatusUpdateJob(JobRepository jobRepository, JobOperator jobOperator, Job twilioUpdateJob) {
		this.jobRepository = jobRepository;
		this.jobOperator = jobOperator;
		this.twilioUpdateJob = twilioUpdateJob;
	}

	/**
	 * Runs the job on demand
	 * 
	 * @throws Exception
	 *             thrown by the jobOperator
	 */
	public void runTwilioUpdateJob() throws Exception {
		if (isJobRunning()) {
			throw new DuplicateJobException("The Twilio update job is already running.");
		}
		var jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
		jobOperator.run(twilioUpdateJob, jobParameters);
	}

	/**
	 * Runs the job on the cron schedule
	 * 
	 * @throws Exception
	 *             thrown by the jobOperator
	 */
	@Scheduled(cron = "${octri.notifications.twilio-update-schedule:@yearly}", scheduler = "notificationTaskScheduler")
	public void runScheduledTwilioUpdateJob() throws Exception {
		runTwilioUpdateJob();
	}

	private boolean isJobRunning() {
		return jobRepository.findRunningJobExecutions(TWILIO_UPDATE_JOB_NAME).size() > 0;
	}

}
