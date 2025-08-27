package org.octri.notification.domain;

import org.octri.common.view.Labelled;

/**
 * The mode a Notification will be processed in - either scheduled or immediate.
 */
public enum ProcessingMode implements Labelled {

	/**
	 * Notification should be processed immediately after creation
	 */
	IMMEDIATE,
	/**
	 * Notification should be processed on a schedule
	 */
	SCHEDULED;

	@Override
	public String getLabel() {
		return name();
	}

}
