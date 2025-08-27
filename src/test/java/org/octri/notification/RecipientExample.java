package org.octri.notification;

import org.octri.notification.domain.Recipient;

public class RecipientExample implements Recipient {

	String uuid;

	public RecipientExample(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public String getLabel() {
		return uuid;
	}

}
