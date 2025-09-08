package org.octri.notification.dispatch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.octri.messaging.exception.UnsuccessfulDeliveryException;
import org.octri.messaging.service.MessageDeliveryService;
import org.octri.notification.config.NotificationProperties;

@ExtendWith(MockitoExtension.class)
public class AbstractNotificationDispatcherTest {

	@Mock
	MessageDeliveryService messageDeliveryService;

	@Mock
	NotificationProperties notificationProperties;

	TestNotificationDispatcher notificationDispatcher;

	private static final String NOTIFICATION_SENDER_EMAIL = "admin@email.com";
	private static final String NOTIFICATION_SENDER_SMS_NUMBER = "888-888-9999";
	private static final String PARTICIPANT_EMAIL = "participant@email.com";
	private static final String PARTICIPANT_MOBILE = "555-555-5555";
	private static final String EMAIL_SUBJECT = "Test Subject";
	private static final String CONTENT = "Test Content";

	@BeforeEach
	public void setup() {
		notificationDispatcher = new TestNotificationDispatcher(messageDeliveryService,
				notificationProperties, null);
	}

	@Test
	void testSendEmail() {
		mocknotificationProperties();
		mockSuccessfulSendEmail();
		var dispatchResult = notificationDispatcher.sendEmail(PARTICIPANT_EMAIL, EMAIL_SUBJECT, CONTENT);
		assertTrue(dispatchResult.successful(), "Dispatch should be successful.");
		assertNull(dispatchResult.errorMessage(), "Error message should not be present.");
		assertTrue(dispatchResult.recipient().equals(PARTICIPANT_EMAIL), "Message recipient should match.");
		assertTrue(dispatchResult.messageContent().equals(CONTENT), "Message content should match.");

		mockFailedSendEmail();
		dispatchResult = notificationDispatcher.sendEmail(PARTICIPANT_EMAIL, EMAIL_SUBJECT, CONTENT);
		assertFalse(dispatchResult.successful(), "Dispatch should not be successful.");
		assertNotNull(dispatchResult.errorMessage(), "Error message should be present.");
		assertTrue(dispatchResult.recipient().equals(PARTICIPANT_EMAIL),
				"Intended message recipient should still be present and match.");
		assertTrue(dispatchResult.messageContent().equals(CONTENT),
				"Intended message content should still be present and should match.");
	}

	@Test
	void testSendSms() {
		mocknotificationProperties();
		mockSuccessfulSendSms();
		var dispatchResult = notificationDispatcher.sendSms(PARTICIPANT_MOBILE, CONTENT);
		assertTrue(dispatchResult.successful(), "Dispatch should be successful.");
		assertNull(dispatchResult.errorMessage(), "Error message should not be present.");
		assertTrue(dispatchResult.recipient().equals(PARTICIPANT_MOBILE), "Message recipient should match.");
		assertTrue(dispatchResult.messageContent().equals(CONTENT), "Message content should match.");

		mockFailedSendSms();
		dispatchResult = notificationDispatcher.sendSms(PARTICIPANT_MOBILE, CONTENT);
		assertFalse(dispatchResult.successful(), "Dispatch should not be successful.");
		assertNotNull(dispatchResult.errorMessage(), "Error message should be present.");
		assertTrue(dispatchResult.recipient().equals(PARTICIPANT_MOBILE),
				"Intended message recipient should still be present and match.");
		assertTrue(dispatchResult.messageContent().equals(CONTENT),
				"Intended message content should still be present and should match.");
	}

	private void mocknotificationProperties() {
		lenient().when(notificationProperties.getEmail()).thenReturn(NOTIFICATION_SENDER_EMAIL);
		lenient().when(notificationProperties.getSmsNumber()).thenReturn(NOTIFICATION_SENDER_SMS_NUMBER);
	}

	private void mockSuccessfulSendEmail() {
		when(messageDeliveryService.sendEmail(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(Optional.empty());
	}

	private void mockFailedSendEmail() {
		when(messageDeliveryService.sendEmail(anyString(), anyString(), anyString(), anyString()))
				.thenThrow(new UnsuccessfulDeliveryException("Failed to send email."));
	}

	private void mockSuccessfulSendSms() {
		when(messageDeliveryService.sendSms(anyString(), anyString(), anyString()))
				.thenReturn(Optional.empty());
	}

	private void mockFailedSendSms() {
		when(messageDeliveryService.sendSms(anyString(), anyString(), anyString()))
				.thenThrow(new UnsuccessfulDeliveryException("Failed to send email."));
	}

}
