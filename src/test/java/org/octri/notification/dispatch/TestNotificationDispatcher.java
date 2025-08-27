package org.octri.notification.dispatch;

import org.octri.messaging.service.MessageDeliveryService;
import org.octri.notification.config.NotificationConfig;
import org.octri.notification.domain.DispatchResult;
import org.octri.notification.domain.Notification;

import com.samskivert.mustache.Mustache.Compiler;

/**
 * Concrete test class for {@link AbstractNotificationDispatcher}.
 */
public class TestNotificationDispatcher extends AbstractNotificationDispatcher {

	public TestNotificationDispatcher(MessageDeliveryService messageDeliveryService,
			NotificationConfig notificationConfig, Compiler mustacheCompiler) {
		super(messageDeliveryService, notificationConfig, mustacheCompiler);
	}

	@Override
	public DispatchResult handleDispatch(Notification notification) {
		throw new UnsupportedOperationException("Unimplemented method 'handleDispatch'");
	}

}