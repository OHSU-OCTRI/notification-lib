# Upgrading

## Unreleased

### Jackson Exceptions

The library has upgraded to Jackson 3, as part of the upgrade to Spring Boot 4. In Jackson 3, the exception types have been reworked as new types that all derive from RuntimeException. As unchecked exceptions, these have been removed from the method signatures and JavaDocs across this code base.

When upgrading to this version, be sure to check your code for places where `JsonProcessingException` or `JsonMappingException` are caught from this library and replace the caught exceptions with `JacksonException` and `DatabindException`, respectively.