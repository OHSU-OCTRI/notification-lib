package org.octri.notification.domain;

/**
 * The default implementation of NotificationStatus
 */
public enum DefaultNotificationStatus implements NotificationStatus {

	/**
	 * Notification is scheduled to be sent
	 */
	SCHEDULED("Scheduled", false),
	/**
	 * Notification was no longer valid
	 */
	INVALID("Invalid", true),
	/**
	 * Notification failed delivery
	 */
	FAILED("Failed", true),
	/**
	 * Notification was sent (but not necessarily delivered)
	 */
	SENT("Sent", true);

	private final String label;
	private final boolean descending;

	DefaultNotificationStatus(String label, boolean descending) {
		this.label = label;
		this.descending = descending;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean descending() {
		return descending;
	}

}
