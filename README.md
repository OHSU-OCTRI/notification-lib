# OCTRI Notification Library

This package supports creation, validation, and dispatch of notifications through email or SMS.

This package contains the infrastructure for creating, validating, and dispatching notifications within OCTRI Spring-based web applications, including:

* Definition of a Notification domain object with a Controller, Repository, and Service
* A Spring Batch implementation of a Notification processor that can be customized by the application
* A Spring Batch implementation to check Twilio for the final disposition of a queed message and update the Notification
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


Your application should configure the following properties for your use case:

| Property | Type | Default Value | Description |
------------------------------------------------
| octri.notifications.enabled | boolean | TRUE | Set to false to disable the notification functionality entirely. No notifications will be processed, and the Notifications menu will not appear. This may be useful in a testing environment. |
| octri.notifications.chunk-size | number | 50 | The number of notifications to process in a single "batch". This should only be edited if you experience performance issues. |
| octri.notifications.email | string | None | The email address messages will send from |
| octri.notifications.sms-number | string | None | The SMS number messages will send from |
| octri.notifications.schedule | string | @yearly | The CRON string indicating the schedule for sending notifications.|
| octri.notifications.twilio-update-schedule | string | @yearly | The CRON string indicating the schedule for checking Twilio for the final disposition of queued messages |

