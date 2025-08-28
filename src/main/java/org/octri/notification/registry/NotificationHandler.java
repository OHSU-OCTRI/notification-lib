package org.octri.notification.registry;

import org.octri.notification.dispatch.NotificationDispatcher;
import org.octri.notification.domain.ProcessingMode;
import org.octri.notification.metadata.NotificationMetadata;
import org.octri.notification.validator.NotificationValidator;
import org.octri.notification.view.NotificationViewer;

/**
 * This class describes all the information that needs to be provided to handle a notification. It is used by the
 * {@link NotificationTypeRegistry} to allow applications to define their own notification types and create a handler
 * for them.
 */
public class NotificationHandler {

	private final ProcessingMode processingMode;
	private final Class<? extends NotificationMetadata> metadataClass;
	private final NotificationValidator validator;
	private final NotificationDispatcher dispatcher;
	private final NotificationViewer viewer;

	/**
	 * 
	 * @param processingMode
	 *            the processing mode of the notification type
	 * @param metadataClass
	 *            the metadata class needed by the notification type
	 * @param validator
	 *            the validator for the notification type
	 * @param dispatcher
	 *            the dispatcher for the notification type
	 * @param viewer
	 *            the metadata viewer for the notification type
	 */
	public NotificationHandler(ProcessingMode processingMode, Class<? extends NotificationMetadata> metadataClass,
			NotificationValidator validator, NotificationDispatcher dispatcher, NotificationViewer viewer) {
		this.processingMode = processingMode;
		this.metadataClass = metadataClass;
		this.validator = validator;
		this.dispatcher = dispatcher;
		this.viewer = viewer;
	}

	/**
	 * 
	 * @return the processing mode of the notification type
	 */
	public ProcessingMode getProcessingMode() {
		return processingMode;
	}

	/**
	 * 
	 * @return the metadata class needed by the notification type
	 */
	public Class<? extends NotificationMetadata> getMetadataClass() {
		return metadataClass;
	}

	/**
	 * 
	 * @return the validator for the notification type
	 */
	public NotificationValidator getValidator() {
		return validator;
	}

	/**
	 * 
	 * @return the dispatcher for the notification type
	 */
	public NotificationDispatcher getDispatcher() {
		return dispatcher;
	}

	/**
	 * 
	 * @return the metadata viewer for the notification type
	 */
	public NotificationViewer getViewer() {
		return viewer;
	}

}
