package org.octri.notification.view;

import org.octri.notification.domain.Notification;
import org.octri.notification.metadata.EmptyMetadata;

/**
 * Notification viewer for notifications that use the {@link EmptyMetadata} class. The recipient uuid is returned as a
 * default.
 */
public class EmptyMetadataViewer implements NotificationViewer {

	@Override
	public String getRecipientView(Notification notification) {
		return notification.getRecipientUuid();
	}

	@Override
	public String getMetadataView(Notification notification) {
		return null;
	}

}
