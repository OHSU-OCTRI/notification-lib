package org.octri.notification.domain;

/**
 * Record encompassing the result of the validation
 */
public record ValidationResult(boolean successful, String invalidReason) {

}