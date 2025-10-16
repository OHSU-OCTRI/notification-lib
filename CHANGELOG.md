# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Added a method to NotificationDispatcher to support multiple dispatches (COMPASS-160)

### Changed

- Publish -SNAPSHOT releases to Maven Central. (CIS-3368)
- Provide recipient info in Notification status details on list page (COMPASS-160)

## [0.2.0] - 2025-09-30

### Added

- Added a createAll method to the NotificationService

### Fixed

- Add missed translation to fixtures
- Remove reference to non-existent Mustache component

### Upgrading

- Add the translation message key `notificationList.filter.all` from [fixture\_\_notification_translations.sql](./setup/migrations/fixture__notification_translations.sql)

## [0.1.1] - 2025-09-19

### Fixed

- Support default date formatting in Notification form
- Add defaults to NotificationProperties as documented in the README
- Update the README to clarify the desired Spring Batch property settings

## [0.1.0] - 2025-09-12

### Added

- Initial implementation of the library (CIS-3203)
- Add a dedicated Task Scheduler for the batch jobs (CIS-3347)
- README documentation (CIS-3349)
- Add Vue infrastructure and delivery-details.js (CIS-3348)

### Changed

- Use Spring Boot Autoconfiguration (CIS-3347)

[unreleased]: https://github.com/OHSU-OCTRI/notification-lib/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/OHSU-OCTRI/notification-lib/compare/v0.1.1...v0.2.0
[0.1.1]: https://source.ohsu.edu/OCTRI-Apps/compass/compare/v0.1.0...v0.1.1
[0.1.0]: https://source.ohsu.edu/OCTRI-Apps/compass/tree/v0.1.0
