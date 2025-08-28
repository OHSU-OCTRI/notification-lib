package org.octri.notification.dispatch;

import java.util.Map;

import org.octri.messaging.exception.UnsuccessfulDeliveryException;
import org.octri.messaging.service.MessageDeliveryService;
import org.octri.notification.config.NotificationConfig;
import org.octri.notification.domain.DispatchResult;

import com.samskivert.mustache.Mustache;

/**
 * Abstract base class for notification dispatchers providing common functionality
 */
public abstract class AbstractNotificationDispatcher implements NotificationDispatcher {

	private final MessageDeliveryService messageDeliveryService;
	private final NotificationConfig notificationConfig;
	private final Mustache.Compiler mustacheCompiler;

	/**
	 * Constructor
	 * 
	 * @param messageDeliveryService
	 *            service for delivering messages
	 * @param notificationConfig
	 *            configuration for notifications
	 * @param mustacheCompiler
	 *            Mustache compiler for templating
	 */
	public AbstractNotificationDispatcher(MessageDeliveryService messageDeliveryService,
			NotificationConfig notificationConfig, Mustache.Compiler mustacheCompiler) {
		this.messageDeliveryService = messageDeliveryService;
		this.notificationConfig = notificationConfig;
		this.mustacheCompiler = mustacheCompiler;
	}

	/**
	 * Generate message content by applying the given values to the provided template
	 * 
	 * @param template
	 *            the message template
	 * @param values
	 *            the values to apply to the template
	 * @return the generated message content
	 */
	public String generateMessageContent(String template, Map<String, String> values) {
		return mustacheCompiler.compile(template).execute(values);
	}

	/**
	 * Send an email
	 * 
	 * @param recipient
	 *            the recipient email address
	 * @param subject
	 *            the subject line of the email
	 * @param messageContent
	 *            the body content of the email
	 * @return the result of the dispatch attempt
	 */
	public DispatchResult sendEmail(String recipient, String subject, String messageContent) {
		try {
			var deliveryDetails = messageDeliveryService.sendEmail(notificationConfig.getEmail(), recipient, subject,
					messageContent);
			return new DispatchResult(true, messageContent, recipient,
					deliveryDetails.isPresent() ? deliveryDetails.get() : null, null);
		} catch (UnsuccessfulDeliveryException e) {
			return new DispatchResult(false, messageContent, recipient, null, e.getErrorResponse());
		}
	}

	/**
	 * Send an SMS
	 * 
	 * @param recipient
	 *            the recipient phone number
	 * @param messageContent
	 *            the SMS message content
	 * @return the result of the dispatch attempt
	 */
	public DispatchResult sendSms(String recipient, String messageContent) {
		try {
			var deliveryDetails = messageDeliveryService.sendSms(notificationConfig.getSmsNumber(), recipient,
					messageContent);
			return new DispatchResult(true, messageContent, recipient,
					deliveryDetails.isPresent() ? deliveryDetails.get() : null, null);
		} catch (UnsuccessfulDeliveryException e) {
			return new DispatchResult(false, messageContent, recipient, null, e.getErrorResponse());
		}
	}

	/**
	 * Get the message delivery service
	 * 
	 * @return the message delivery service
	 */
	public MessageDeliveryService getMessageDeliveryService() {
		return messageDeliveryService;
	}

	/**
	 * Get the notification configuration
	 * 
	 * @return the notification configuration
	 */
	public NotificationConfig getNotificationConfig() {
		return notificationConfig;
	}

	/**
	 * Get the Mustache compiler
	 * 
	 * @return the Mustache compiler
	 */
	public Mustache.Compiler getMustacheCompiler() {
		return mustacheCompiler;
	}

}