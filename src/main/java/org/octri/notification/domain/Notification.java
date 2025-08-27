package org.octri.notification.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.octri.common.domain.AbstractEntity;
import org.octri.notification.metadata.NotificationMetadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing a notification to a {@link Recipient}
 */
@Entity
public class Notification extends AbstractEntity {

	/**
	 * Record representing the status metadata of a notification
	 * 
	 * @param validationResult
	 *            the result of the validation step
	 * @param dispatchResult
	 *            the result of the dispatch step
	 */
	public record NotificationStatusMetadata(ValidationResult validationResult,
			DispatchResult dispatchResult) {

	}

	/**
	 * Date the notification is scheduled for
	 */
	@NotNull
	private LocalDate dateScheduled;

	/**
	 * UUID of recipient to be notified
	 */
	@NotNull
	private String recipientUuid;

	/**
	 * Status of the notification
	 */
	@NotNull
	private NotificationStatus notificationStatus;

	/**
	 * Type of notification
	 */
	@NotNull
	private String notificationType;

	/**
	 * Metadata about the notification to be used for validation and dispatch
	 */
	@Column(columnDefinition = "JSON")
	private String notificationMetadata;

	/**
	 * Date and time the notification was processed
	 */
	private LocalDateTime dateTimeProcessed;

	/**
	 * Metadata about the disposition of the notification
	 */
	@Column(columnDefinition = "JSON")
	private String notificationStatusMetadata;

	/**
	 * Custom ObjectMapper for serializing/deserializing metadata
	 */
	@Transient
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * The recipient entity. The caller must ensure this is set along with the recipientUuid
	 */
	@Transient
	private Recipient recipient;

	/**
	 * User-friendly view of the {@link Recipient}. Populated by the NotificationViewer for display purposes only
	 */
	@Transient
	private String notificationRecipientView;
	/**
	 * User-friendly view of the {@link NotificationMetadata}. Populated by the NotificationViewer for display purposes
	 * only
	 */
	@Transient
	private String notificationMetadataView;

	/**
	 * Create an empty Notification
	 */
	public Notification() {
		this.configureMapper();
	}

	/**
	 * Create a Notification for a uniquely identified entity (ex. Participant)
	 *
	 * @param relatedEntity
	 *            the recipient entity
	 */
	public Notification(Recipient relatedEntity) {
		this.configureMapper();
		this.setRecipient(relatedEntity);
	}

	private void configureMapper() {
		// Adds support for LocalDate
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		// This ensures that an empty NotificationMetadata can be serialized
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// This ensures unnecessary fields aren't serialized/deserialized if the ReminderDayProgressionTracker is used
		mapper.addMixIn(ReminderDayProgressionTracker.class, ProgressionTrackerMixin.class);
	}

	/**
	 * @return the scheduled date
	 */
	public LocalDate getDateScheduled() {
		return dateScheduled;
	}

	/**
	 * 
	 * @param dateScheduled
	 *            the scheduled date
	 */
	public void setDateScheduled(LocalDate dateScheduled) {
		this.dateScheduled = dateScheduled;
	}

	/**
	 * 
	 * @return the UUID of the recipient to be notified
	 */
	public String getRecipientUuid() {
		return recipientUuid;
	}

	/**
	 * 
	 * @param uuid
	 *            the UUID of the recipient to be notified
	 */
	public void setRecipientUuid(String uuid) {
		// Ensure that convenience of transient entity does not get out of sync with uuid.
		// We can't update the entity since there is no guarantee of a setter.
		if (this.recipient != null) {
			this.recipient = null;
		}
		this.recipientUuid = uuid;
	}

	/**
	 * 
	 * @return the status of the notification
	 */
	public NotificationStatus getNotificationStatus() {
		return notificationStatus;
	}

	/**
	 * 
	 * @param notificationStatus
	 *            the status of the notification
	 */
	public void setNotificationStatus(NotificationStatus notificationStatus) {
		this.notificationStatus = notificationStatus;
	}

	/**
	 * 
	 * @return the notification type
	 */
	public String getNotificationType() {
		return notificationType;
	}

	/**
	 * 
	 * @param notificationType
	 *            the notification type
	 */
	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	/**
	 * 
	 * @return the notification metadata as a JSON string
	 */
	public String getNotificationMetadata() {
		return notificationMetadata;
	}

	/**
	 * 
	 * @param notificationMetadata
	 *            the notification metadata as a JSON string
	 */
	public void setNotificationMetadata(String notificationMetadata) {
		this.notificationMetadata = notificationMetadata;
	}

	/**
	 * 
	 * @param notificationMetadata
	 *            the notification metadata object
	 */
	public void setNotificationMetadata(NotificationMetadata notificationMetadata) {
		try {
			this.notificationMetadata = mapper.writeValueAsString(notificationMetadata);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to serialize metadata", e);
		}
	}

	/**
	 * Deserialize the notification metadata into the specified class
	 * 
	 * @param <T>
	 *            the type of the metadata
	 * @param metadataClass
	 *            the class to deserialize into
	 * @return the deserialized metadata
	 */
	public <T extends NotificationMetadata> T getNotificationMetadata(Class<T> metadataClass) {
		if (notificationMetadata == null) {
			throw new IllegalStateException("Notification metadata is null");
		}
		try {
			return mapper.readValue(notificationMetadata, metadataClass);
		} catch (Exception e) {
			throw new RuntimeException("Failed to deserialize metadata", e);
		}
	}

	/**
	 * 
	 * @return the date and time the notification was processed
	 */
	public LocalDateTime getDateTimeProcessed() {
		return dateTimeProcessed;
	}

	/**
	 * 
	 * @param dateTimeProcessed
	 *            the date and time the notification was processed
	 */
	public void setDateTimeProcessed(LocalDateTime dateTimeProcessed) {
		this.dateTimeProcessed = dateTimeProcessed;
	}

	/**
	 * 
	 * @return the notification status metadata as a JSON string
	 */
	public String getNotificationStatusMetadata() {
		return notificationStatusMetadata;
	}

	/**
	 * 
	 * @param notificationStatusMetadata
	 *            the notification status metadata as an object
	 */
	public void setNotificationStatusMetadata(String notificationStatusMetadata) {
		this.notificationStatusMetadata = notificationStatusMetadata;
	}

	/**
	 * 
	 * @param notificationStatusMetadataRecord
	 *            the notification status metadata record
	 */
	public void setNotificationStatusMetadata(NotificationStatusMetadata notificationStatusMetadataRecord) {
		try {
			this.notificationStatusMetadata = mapper.writeValueAsString(notificationStatusMetadataRecord);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(
					"Failed to serialize status metadata", e);
		}
	}

	/**
	 * 
	 * @return the deserialized notification status metadata
	 */
	public NotificationStatusMetadata getStatusMetadata() {
		if (notificationStatusMetadata == null) {
			return null;
		}
		try {
			return mapper.readValue(notificationStatusMetadata, NotificationStatusMetadata.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to deserialize status metadata", e);
		}
	}

	/**
	 * 
	 * @return the scheduled date in ISO format
	 */
	public String dateScheduledIso() {
		return getDateScheduled().format(DateTimeFormatter.ISO_DATE);
	}

	/**
	 * 
	 * @return a user-friendly view of the recipient
	 */
	public String getNotificationRecipientView() {
		return notificationRecipientView;
	}

	/**
	 * 
	 * @param notificationRecipientView
	 *            a user-friendly view of the recipient
	 */
	public void setNotificationRecipientView(String notificationRecipientView) {
		this.notificationRecipientView = notificationRecipientView;
	}

	/**
	 * @return a user-friendly view of the notification metadata
	 */
	public String getNotificationMetadataView() {
		return notificationMetadataView;
	}

	/**
	 * @param notificationMetadataView
	 *            a user-friendly view of the notification metadata
	 */
	public void setNotificationMetadataView(String notificationMetadataView) {
		this.notificationMetadataView = notificationMetadataView;
	}

	/**
	 * The caller must ensure this is set along with the recipientUuid
	 * 
	 * @return the recipient entity
	 */
	public Recipient getRecipient() {
		return recipient;
	}

	/**
	 * 
	 * @param entity
	 *            the recipient of the notification
	 */
	public void setRecipient(Recipient entity) {
		if (entity == null) {
			this.setRecipientUuid(null);
		} else {
			// Prevent mismatch when loading entity from the database.
			if (recipient == null && recipientUuid != null && !entity.getUuid().equals(recipientUuid)) {
				throw new IllegalArgumentException("UUID of provided entity must match the current uuid value.");
			}
			this.setRecipientUuid(entity.getUuid());
		}
		this.recipient = entity;
	}

	/**
	 * 
	 * @return A unique key used by the dispatch process to prevent sending duplicates
	 */
	public String getDispatchKey() {
		return String.join(this.getNotificationType(), String.valueOf(this.getRecipientUuid()),
				this.getDateScheduled().toString(), this.getNotificationMetadata(), "-");
	}

	/**
	 * 
	 * @return The JSON ObjectMapper to use for parsing NotificationMetadata and NotificationStatusMetadata
	 */
	public ObjectMapper getObjectMapper() {
		return mapper;
	}

	@Override
	public String toString() {
		return "Notification [dateScheduled=" + dateScheduled + ", recipientUuid=" + recipientUuid
				+ ", notificationStatus=" + notificationStatus + ", notificationType=" + notificationType + "]";
	}

}
