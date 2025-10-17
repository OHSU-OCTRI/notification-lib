package org.octri.notification.batch;

import org.octri.messaging.sms.TwilioHelper;
import org.octri.notification.domain.DefaultNotificationStatus;
import org.octri.notification.domain.DispatchResult;
import org.octri.notification.domain.Notification;
import org.octri.notification.domain.Notification.NotificationStatusMetadata;
import org.octri.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

/**
 * Checks the final disposition of the Notifications in Twilio and updates the Notification status
 */
public class TwilioStatusItemWriter implements ItemWriter<Notification> {

	private static final Logger log = LoggerFactory.getLogger(TwilioStatusItemWriter.class);

	private final NotificationRepository notificationRepository;
	private final TwilioHelper twilioHelper;

	/**
	 * 
	 * @param notificationRepository
	 *            the Notification repository
	 * @param twilioHelper
	 *            the helper for Twilio
	 */
	public TwilioStatusItemWriter(NotificationRepository notificationRepository, TwilioHelper twilioHelper) {
		this.notificationRepository = notificationRepository;
		this.twilioHelper = twilioHelper;
	}

	@Override
	public void write(Chunk<? extends Notification> chunk) throws Exception {
		for (Notification notification : chunk) {
			var statusMetadata = notification.getStatusMetadata();
			var dispatchResult = statusMetadata.dispatchResult();
			var messageJson = dispatchResult.deliveryDetails();

			var oldState = twilioHelper.loadMessageFromString(messageJson);
			var messageSid = oldState.getSid();

			log.debug("Getting updated status for message {}", messageSid);
			var updatedState = twilioHelper.fetchMessage(messageSid);

			log.debug("Old status: {} current status: {}", oldState.getStatus(), updatedState.getStatus());
			if (!oldState.getStatus().equals(updatedState.getStatus())) {
				if (!twilioHelper.isSuccessResponse(updatedState)) {
					notification.setNotificationStatus(DefaultNotificationStatus.FAILED);
				}

				var updatedDeliveryDetails = twilioHelper.serializeMessageToJson(updatedState);
				var updatedDispatchResult = new DispatchResult(twilioHelper.isSuccessResponse(updatedState),
						dispatchResult.messageContent(), dispatchResult.recipient(), updatedDeliveryDetails,
						updatedState.getErrorMessage() != null ? updatedState.getErrorMessage() : null);
				var updatedStatusMetadata = new NotificationStatusMetadata(
						statusMetadata.validationResult(), updatedDispatchResult);
				notification.setNotificationStatusMetadata(updatedStatusMetadata);
			}

			notificationRepository.save(notification);
		}
	}

}
