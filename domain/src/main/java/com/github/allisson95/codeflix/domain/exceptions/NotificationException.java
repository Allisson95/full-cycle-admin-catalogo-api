package com.github.allisson95.codeflix.domain.exceptions;

import com.github.allisson95.codeflix.domain.validation.handler.Notification;

public class NotificationException extends DomainException {

    public NotificationException(final String aMessage, final Notification aNotification) {
        super(aMessage, aNotification.getErrors());
    }

}
