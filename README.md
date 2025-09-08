# OCTRI Notification Library

This package contains the infrastructure for creating, validating, and dispatching notifications within OCTRI Spring-based web applications, including:

* Definition of a Notification domain object with a Controller, Repository, and Service
* A Spring Batch implementation of a Notification processor that can be customized by the application
* A Spring Batch implementation to check Twilio for the final disposition of a queued message and update the Notification
* Extensions allowing the application to register custom NotificationTypes, NotificationStatuses, and handlers to control validation logic and views.

## Using this package

To use this package, add it to your `pom.xml` file.

```xml
	<dependency>
		<groupId>org.octri.notifications</groupId>
		<artifactId>notification_lib</artifactId>
		<version>${notification_lib.version}</version>
	</dependency>
```

### Flyway Migrations / Data Fixtures

To create the database tables used by the library, copy the SQL migrations from `setup/migrations/` into your project's Flyway migration directory (`src/main/resources/db/migration/`). You may need to rename the migrations so that they are applied after existing migrations.

The `setup/migration` directory also includes a fixtures file with translations for the mustache view templates. These can be appended to the application translation fixtures.

### Configurable Properties

Your application should configure the following properties for your use case:

| Property | Type | Default Value | Description |
|---|---|---|---|
| octri.notifications.enabled | boolean | TRUE | Set to false to disable the notification functionality. No scheduled job will process notifications and no routes for managing them will be created. This may be useful in a testing environment. Note that octri.messaging.enabled should also be set to TRUE if this flag is.|
| octri.notifications.chunk-size | number | 50 | The number of notifications to process in a single "batch". This should only be edited if you experience performance issues. |
| octri.notifications.email | string | None | The email address messages will send from |
| octri.notifications.sms-number | string | None | The SMS number messages will send from |
| octri.notifications.schedule | string | @yearly | The CRON string indicating the schedule for sending notifications.|
| octri.notifications.twilio-update-schedule | string | @yearly | The CRON string indicating the schedule for checking Twilio for the final disposition of queued messages |

This package depends on the messaging library, which has its own set of configurable properties documented [here](https://github.com/OHSU-OCTRI/messaging-lib).

### Domain

Notifications can be assigned to an entity that implements the `Recipient` interface provided by the Notification Library. For most applications this will be a Participant. For example:

```java
@Entity
public class Participant extends AbstractEntity implements Recipient {

	@Column(unique = true, length = 36)
	@NotNull
	private String uuid;

	/**
	 * Default constructor
	 */
	public Participant() {
		setUuid(UUID.randomUUID().toString());
	}

	@Override
	public String getLabel() {
		return getUuid();
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
```

```sql
DROP TABLE IF EXISTS `participant`;
CREATE TABLE `participant` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`version` int NOT NULL,
	`created_at` datetime NOT NULL,
	`updated_at` datetime NOT NULL,
	`updated_by` varchar(320) DEFAULT NULL,
	`uuid` varchar(36) NOT NULL UNIQUE,
	primary key(`id`),
	UNIQUE KEY `participant_uuid_uk` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

It is also advisable to add a database constraint to enforce a foreign key relationship in the notification table.

```sql
ALTER TABLE `notification`
ADD CONSTRAINT notification_recipient_fk FOREIGN KEY (`recipient_uuid`) REFERENCES `participant` (`uuid`);
```

Applications will also need to provide a service to use for specifying how to search for entities that can receive notifications. This service will need to implement the `IdentifiableEntityFinder` interface. For example,

```java
@Service
public class ParticipantService implements IdentifiableEntityFinder<Participant> {

	@Autowired
	ParticipantRepository participantRepository;

	@Override
	public Iterable<Participant> findAll() {
		return participantRepository.findAll();
	}

	@Override
	public Participant findByUuid(String uuid) {
		return participantRepository.findByUuid(uuid);
	}
}
```

Note that the logic for creating a Notification is application-dependent. A default controller and views are provided to manually create or edit a Notification for users with the SUPER role, but for a production application, notifications will likely be created through event listeners. For example:

```java
@EventListener
public void onRegistrationComplete(ClientRegistrationCompleteEvent event) throws Exception {
	Client client = event.getClient();
	createClientWelcomeNotification(client);
}
```

### Navigation

Navigation to notification pages can be added by including the `{{>components/navigation_menu_items}}` mustache component in a menu. Routes are configured at /admin so that the application can add the desired security. Create and edit routes are only available to the SUPER role. The application is responsible for hiding this component when notifications are not enabled in configuration. Add a reference to the flag in TemplateAdvice:

```
model.addAttribute("notificationsEnabled", NotificationProperties.getNotificationsEnabled());
```

Then add the component in admin_items:

```
{{#notificationsEnabled}}
{{>components/notification_menu_items}}
{{/notificationsEnabled}}
```

### Notification Batch Job

A Spring Batch job is configured to read, validate, and send Notifications that have a status of "SCHEDULED". Applications will use the `NotificationTypeRegistry` to indicate how different notification types will be validated and dispatched based on their own needs. See the [registration README](NOTIFICATION_REGISTRATION.md) for details on configuring Notifications for processing.

### Twilio Status Batch Job

If Twilio is configured, there is a Spring Batch job that will periodically check the status of messages and record the final disposition in the `NotificationStatusMetadata` field of the Notification. If a failure occurs, a Notification may transition from SENT to INACTIVE.

## Extension Points

### Changes to Notification Status

This library has 3 default statuses for Notifications: SCHEDULED, INACTIVE, and SENT, but it provides a NotificationStatusRegistry bean that can be used to override these statuses. Applications should take care to also override any functionality in this library that relies on the default statuses for behavior. If you simply want to override the "label" for the status without changing the names or any functionality, you can follow this example that overrides the SCHEDULED status with the label "Queued":

```
notificationStatusRegistry.register(new NotificationStatus() {

	@Override
	public String name() {
		return "SCHEDULED";
	}

	@Override
	public int ordinal() {
		return 0;
	}

	@Override
	public boolean descending() {
		return false;
	}

	@Override
	public String getLabel() {
		return "Queued";
	}
});
```

If you want to override deeper behavior in the batch processes, there are additional touchpoints you will need to consider. This type of customization has not been tested:

1. The NotificationItemReader uses DefaultNotificationStatus to get all "Scheduled" notifications, so this Bean would need to be overridden.
2. The NotificationItemWriter uses DefaultNotificationStatus to reset the status after processing, so this would need to be overridden.
3. The query to get Twilio notifications that haven't received a final disposition also relies on knowing the status.


