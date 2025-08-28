INSERT INTO translation (`version`, `created_at`, `updated_at`, `locale`, `message_key`, `content`, `description`, `markup_allowed`)
VALUES

(0, NOW(), NOW(), 'en-US', 'notification.dateScheduled.label', 'Date scheduled', 'Label for Notification dateScheduled field', 0),
(0, NOW(), NOW(), 'en-US', 'notification.recipient.label', 'Recipient', 'Label for Notification recipient field', 0),
(0, NOW(), NOW(), 'en-US', 'notification.notificationStatus.label', 'Status', 'Label for Notification notificationStatus field', 0),
(0, NOW(), NOW(), 'en-US', 'notification.notificationType.label', 'Type', 'Label for Notification notificationType field', 0),
(0, NOW(), NOW(), 'en-US', 'notification.notificationMetadata.label', 'Info', 'Label for Notification notificationMetadata field', 0),
(0, NOW(), NOW(), 'en-US', 'notification.dateTimeProcessed.label', 'Date/Time processed', 'Label for Notification dateTimeProcessed field', 0),
(0, NOW(), NOW(), 'en-US', 'notification.notificationStatusMetadata.label', 'Status Details', 'Label for Notification notificationStatusMetadata field', 0),
(0, NOW(), NOW(), 'en-US', 'notification.dateScheduled.validationMessage', 'Value must be present and formatted yyyy-mm-dd', 'Validation message for Notification dateScheduled field', 0),
(0, NOW(), NOW(), 'en-US', 'notification.recipient.validationMessage', 'Value must be present', 'Validation message for Notification recipient field', 0),
(0, NOW(), NOW(), 'en-US', 'notification.notificationStatus.validationMessage', 'Value must be present', 'Validation message for Notification status field', 0),
(0, NOW(), NOW(), 'en-US', 'notification.notificationType.validationMessage', 'Value must be present', 'Validation message for Notification type field', 0),
(0, NOW(), NOW(), 'en-US', 'notificationStatusMetadata.validationFailed.label', 'Validation Failed', 'Label for heading indicating validation failed', 0),
(0, NOW(), NOW(), 'en-US', 'notificationStatusMetadata.validationFailed.reason.label', 'Invalid Reason', 'Label for Invalid reason', 0),
(0, NOW(), NOW(), 'en-US', 'notificationStatusMetadata.sendSuccessful.label', 'Sent', 'Label indicating send was successful', 0),
(0, NOW(), NOW(), 'en-US', 'notificationStatusMetadata.sendFailed.label', 'Not Sent', 'Label indicating send failed', 0),
(0, NOW(), NOW(), 'en-US', 'notificationStatusMetadata.message.label', 'Message', 'Label for message to be dispatched', 0),
(0, NOW(), NOW(), 'en-US', 'notificationStatusMetadata.errorMessage.label', 'Error Message', 'Label for dispatch error message', 0),
(0, NOW(), NOW(), 'en-US', 'notificationStatusMetadata.deliveryDetails.label', 'Delivery Details', 'Label for heading of Delivery Details', 0);

