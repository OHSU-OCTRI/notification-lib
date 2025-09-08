package org.octri.notification.registry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.octri.notification.dispatch.NotificationDispatcher;
import org.octri.notification.domain.ProcessingMode;
import org.octri.notification.metadata.NotificationMetadata;
import org.octri.notification.validator.NotificationValidator;
import org.octri.notification.view.NotificationViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * The registry for adding a new NotificationType and NotificationHandler.
 */
public class NotificationTypeRegistry {

	private static final Logger logger = LoggerFactory.getLogger(NotificationTypeRegistry.class);

	private final Map<String, NotificationHandler> handlers = new ConcurrentHashMap<>();

	/**
	 * 
	 * @param type
	 *            a unique string representation of the Notification type
	 * @param mode
	 *            the node this type uses for processing (IMMEDIATE or SCHEDULED)
	 * @param metadataClass
	 *            the metadata class required for processing this Notification type
	 * @param validator
	 *            the validator for this Notification type
	 * @param dispatcher
	 *            the dispatcher for this Notification type
	 * @param viewer
	 *            the viewer for the metadata of this Notification type
	 */
	public void register(String type, ProcessingMode mode, Class<? extends NotificationMetadata> metadataClass,
			NotificationValidator validator, NotificationDispatcher dispatcher, NotificationViewer viewer) {
		Assert.notNull(validator, "Validator must not be null");
		Assert.notNull(dispatcher, "Dispatcher must not be null");
		logger.debug("Registering notification type: {} with mode {}/validator {}/dispatcher {}/viewer {}", type,
				mode.name(),
				validator.getClass().getSimpleName(),
				dispatcher.getClass().getSimpleName(),
				viewer.getClass().getName());
		if (handlers.containsKey(type)) {
			logger.warn("Notification type {} is already registered. Overwriting.", type);
		}
		handlers.put(type, new NotificationHandler(mode, metadataClass, validator, dispatcher, viewer));
	}

	/**
	 * 
	 * @return the set of types that are registered
	 */
	public Set<String> getRegisteredTypes() {
		return handlers.keySet();
	}

	/**
	 * 
	 * @param type
	 *            the type string
	 * @return the NotificationHandler for the given type
	 */
	public NotificationHandler getHandler(String type) {
		return handlers.get(type);
	}

}
