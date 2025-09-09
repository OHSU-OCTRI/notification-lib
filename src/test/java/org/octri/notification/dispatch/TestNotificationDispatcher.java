package org.octri.notification.dispatch;

import org.octri.messaging.service.MessageDeliveryService;
import org.octri.notification.config.NotificationProperties;
import org.octri.notification.domain.DispatchResult;
import org.octri.notification.domain.Notification;

import com.samskivert.mustache.Mustache.Compiler;

/**
 * Concrete test class for {@link AbstractNotificationDispatcher}.
 */
public class TestNotificationDispatcher extends AbstractNotificationDispatcher {

	public TestNotificationDispatcher(MessageDeliveryService messageDeliveryService,
			NotificationProperties notificationProperties, Compiler mustacheCompiler) {
		super(messageDeliveryService, notificationProperties, mustacheCompiler);
	}

	@Override
	public DispatchResult handleDispatch(Notification notification) {
		throw new UnsupportedOperationException("Unimplemented method 'handleDispatch'");
	}

}