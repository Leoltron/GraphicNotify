package ru.leoltron.graphicnotify.common.command.sendnotification;

class SNException extends Exception {
    SNException(String message) {
        super(message);
    }

    SNException(String message, Throwable cause) {
        super(message, cause);
    }
}