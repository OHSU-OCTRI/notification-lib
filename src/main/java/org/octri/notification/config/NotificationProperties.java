package org.octri.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the notification system
 */
@ConfigurationProperties(prefix = "octri.notifications")
public class NotificationProperties {

	private Boolean enabled;

	private Integer chunkSize;

	private String email;

	private String smsNumber;

	private String schedule;

	private String twilioUpdateSchedule;

	/**
	 * A static getter for any custom logic relying on this flag in the application. This is available even if this bean
	 * is never created.
	 *
	 * @return whether notifications are enabled
	 */
	public static boolean getNotificationsEnabled() {
		String enabled = System.getProperty("octri.notifications.enabled",
				System.getenv("OCTRI_NOTIFICATIONS_ENABLED"));
		return "true".equalsIgnoreCase(enabled);
	}

	/**
	 * 
	 * @return whether notifications are enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * 
	 * @param enabled
	 *            whether notifications are enabled
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * 
	 * @return the chunk size for processing
	 */
	public Integer getChunkSize() {
		return chunkSize;
	}

	/**
	 * 
	 * @param chunkSize
	 *            the chunk size for processing
	 */
	public void setChunkSize(Integer chunkSize) {
		this.chunkSize = chunkSize;
	}

	/**
	 * 
	 * @return The email notifications will come from
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * @param email
	 *            The email notifications will come from
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 
	 * @return The number text notifications will come from
	 */
	public String getSmsNumber() {
		return smsNumber;
	}

	/**
	 * 
	 * @param smsNumber
	 *            The number text notifications will come from
	 */
	public void setSmsNumber(String smsNumber) {
		this.smsNumber = smsNumber;
	}

	/**
	 * 
	 * @return The CRON schedule for the batch job that processes notifications
	 */
	public String getSchedule() {
		return schedule;
	}

	/**
	 * 
	 * @param schedule
	 *            The CRON schedule for the batch job that processes notifications
	 */
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	/**
	 * 
	 * @return the CRON scheduled for the batch job that checks Twilio status
	 */
	public String getTwilioUpdateSchedule() {
		return twilioUpdateSchedule;
	}

	/**
	 * 
	 * @param twilioUpdateSchedule
	 *            the CRON scheduled for the batch job that checks Twilio status
	 */
	public void setTwilioUpdateSchedule(String twilioUpdateSchedule) {
		this.twilioUpdateSchedule = twilioUpdateSchedule;
	}

}
