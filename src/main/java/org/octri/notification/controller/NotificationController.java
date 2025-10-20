package org.octri.notification.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.octri.common.controller.AbstractEntityController;
import org.octri.common.customizer.IdentifiableEntityFinder;
import org.octri.common.view.IdentifiableOptionList;
import org.octri.common.view.OptionList;
import org.octri.common.view.ViewUtils;
import org.octri.notification.domain.Notification;
import org.octri.notification.registry.NotificationStatusRegistry;
import org.octri.notification.registry.NotificationTypeRegistry;
import org.octri.notification.repository.NotificationRepository;
import org.octri.notification.view.NotificationStatusSelectOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for {@link Notification} objects.
 */
@Controller
@RequestMapping("/admin/notification")
public class NotificationController extends AbstractEntityController<Notification, NotificationRepository> {

	private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

	private final NotificationRepository repository;
	private final IdentifiableEntityFinder<?> recipientFinder;
	private final NotificationTypeRegistry notificationTypeRegistry;
	private final NotificationStatusRegistry notificationStatusRegistry;

	/**
	 * 
	 * @param repository
	 *            the notification repository
	 * @param recipientFinder
	 *            the finder for recipients
	 * @param notificationTypeRegistry
	 *            the registration for notification types
	 * @param notificationStatusRegistry
	 *            the registry for notification statuses
	 */
	public NotificationController(NotificationRepository repository, IdentifiableEntityFinder<?> recipientFinder,
			NotificationTypeRegistry notificationTypeRegistry, NotificationStatusRegistry notificationStatusRegistry) {
		this.repository = repository;
		this.recipientFinder = recipientFinder;
		this.notificationTypeRegistry = notificationTypeRegistry;
		this.notificationStatusRegistry = notificationStatusRegistry;
	}

	@GetMapping("/")
	@Override
	public String list(Map<String, Object> model) {
		var template = super.list(model);
		@SuppressWarnings("unchecked")
		Iterable<Notification> notifications = (Iterable<Notification>) model.get("entity_list");
		var viewerMap = notificationTypeRegistry.getRegisteredTypes().stream()
				.collect(Collectors.toMap(
						t -> t,
						t -> notificationTypeRegistry.getHandler(t).getViewer()));
		// Prepare viewers
		for (var type : viewerMap.keySet()) {
			var viewer = viewerMap.get(type);
			var typeNotifications = StreamSupport.stream(notifications.spliterator(), false)
					.filter(n -> n.getNotificationType().equals(type))
					.collect(Collectors.toList());
			viewer.prepare(typeNotifications);
		}
		List<Notification> notificationViews = StreamSupport
				.stream(notifications.spliterator(), false)
				.map(n -> {
					var viewer = viewerMap.get(n.getNotificationType());
					if (viewer != null) {
						n.setNotificationRecipientView(viewer.getRecipientView(n));
						n.setNotificationMetadataView(viewer.getMetadataView(n));
					}
					return n;
				}).collect(Collectors.toList());
		model.put("entity_list", notificationViews);
		model.put("notificationStatuses", notificationStatusRegistry.getStatuses());
		ViewUtils.addPageScript(model, "table-filtering.js");

		return template;
	}

	@GetMapping("/{id}")
	@Override
	public String show(Map<String, Object> model, @PathVariable Long id) {
		var template = super.show(model, id);
		Notification notification = (Notification) model.get("entity");
		var handler = notificationTypeRegistry.getHandler(notification.getNotificationType());
		if (handler != null) {
			notification.setNotificationRecipientView(handler.getViewer().getRecipientView(notification));
			notification.setNotificationMetadataView(handler.getViewer().getMetadataView(notification));
		}
		model.put("editingEnabled", model.get("isSuper"));
		ViewUtils.addPageScript(model, "notificationlib-vendor.js");
		ViewUtils.addPageScript(model, "delivery-details.js");
		return template;
	}

	@Override
	@PreAuthorize("hasRole('ROLE_SUPER')")
	public String newEntity(Map<String, Object> model) {
		String template = super.newEntity(model);

		// Add options for select.
		model.put("recipientOptions",
				IdentifiableOptionList.fromAll(recipientFinder, null));
		model.put("notificationStatusOptions",
				NotificationStatusSelectOption.fromStatuses(notificationStatusRegistry.getStatuses(),
						null));
		model.put("notificationTypeOptions",
				OptionList.forStrings(new ArrayList<String>(notificationTypeRegistry.getRegisteredTypes()), null));
		return template;
	}

	@Override
	@PreAuthorize("hasRole('ROLE_SUPER')")
	public String edit(Map<String, Object> model, @PathVariable Long id) {
		String template = super.edit(model, id);

		Notification entity = (Notification) model.get("entity");

		// Add options for select.
		model.put("recipientOptions",
				IdentifiableOptionList.fromAll(recipientFinder, entity.getRecipientUuid()));
		model.put("notificationStatusOptions",
				NotificationStatusSelectOption.fromStatuses(notificationStatusRegistry.getStatuses(),
						entity.getNotificationStatus()));
		model.put("notificationTypeOptions",
				OptionList.forStrings(new ArrayList<String>(notificationTypeRegistry.getRegisteredTypes()),
						entity.getNotificationType()));

		return template;
	}

	@Override
	@PreAuthorize("hasRole('ROLE_SUPER')")
	public String create(Map<String, Object> model, @ModelAttribute("entity") Notification entity,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		return super.create(model, entity, bindingResult, redirectAttributes);
	}

	@Override
	@PreAuthorize("hasRole('ROLE_SUPER')")
	public String update(Map<String, Object> model, @PathVariable Long id,
			@ModelAttribute("entity") Notification entity,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		return super.update(model, id, entity, bindingResult, redirectAttributes);
	}

	@Override
	protected Class<Notification> domainClass() {
		return Notification.class;
	}

	@Override
	protected NotificationRepository getRepository() {
		return this.repository;
	}
}