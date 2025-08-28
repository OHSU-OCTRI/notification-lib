package org.octri.notification.domain;

/**
 * Record representing the result of a dispatch attempt
 * 
 * @param successful
 *            whether the dispatch was successful
 * @param messageContent
 *            the content of the message that was attempted to be sent
 * @param recipient
 *            the recipient of the message
 * @param deliveryDetails
 *            details about the delivery
 * @param errorMessage
 *            error message if the dispatch failed
 */
public record DispatchResult(Boolean successful, String messageContent, String recipient, String deliveryDetails,
		String errorMessage) {
}
