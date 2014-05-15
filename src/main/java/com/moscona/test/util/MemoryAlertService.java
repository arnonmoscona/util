package com.moscona.test.util;

import com.moscona.util.IAlertService;
import com.moscona.util.LogItem;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created: Apr 15, 2010 9:45:34 AM
 * By: Arnon Moscona
 * An alert service that is mostly used for testing - accumulates all alerts (without limit) in a list
 */
public class MemoryAlertService implements IAlertService {
    private LinkedList<AlertBean> alerts;

    public MemoryAlertService() {
        clear();
    }

    public void clear() {
        alerts = new LinkedList<AlertBean>();
    }

    public List<AlertBean> getAlerts() {
        return Collections.unmodifiableList(alerts);
    }

    public String[] getAlertsAsStrings() {
        String[] retval = new String[alerts.size()];
        for (int i=0; i<alerts.size(); i++) {
            retval[i] = alerts.get(i).toString();
        }
        return retval;
    }

    public void printAlerts(PrintStream out) {
        if (alerts.size() > 0) {
            for (String alert : getAlertsAsStrings()) {
                out.println(alert);
            }
        }
    }

    public void printAlerts() {
        printAlerts(System.err);
    }

    /**
     * The simplest way to use this. Just alert with a simple message.
     * Warning: avoid using this form for alerts that may come at a high frequency and may need to be filtered.
     *
     * @param message - the alert message
     */
    @Override
    public void sendAlert(String message) {
        AlertBean alert = new AlertBean();
        alert.setMessage(message);
        alerts.add(alert);
    }

    /**
     * The common form of this alert. More frequently issued alert types should have a fixed messageType.
     *
     * @param message     - the alert message
     * @param messageType - the "type" of message. While the text of the message may vary a lot, for the
     *                    same type of message (e.g. stale record alert) the type parameters should remain
     *                    fixed for easier classification and filtering.
     */
    @Override
    public void sendAlert(String message, String messageType) {
        AlertBean alert = new AlertBean();
        alert.setMessage(message);
        alert.setMessageType(messageType);
        alerts.add(alert);
    }

    /**
     * Warning: avoid using this form for alerts that may come at a high frequency and may need to be filtered.
     *
     * @param message - the alert message
     * @param ex      - an exception that is associated with this alert
     */
    @Override
    public void sendAlert(String message, Throwable ex) {
        AlertBean alert = new AlertBean();
        alert.setMessage(message);
        alert.setException(ex);
        alerts.add(alert);
    }

    /**
     * @param message     - the alert message
     * @param messageType - the "type" of message. While the text of the message may vary a lot, for the
     *                    same type of message (e.g. stale record alert) the type parameters should remain
     *                    fixed for easier classification and filtering.
     * @param ex          - an exception that is associated with this alert
     */
    @Override
    public void sendAlert(String message, String messageType, Throwable ex) {
        AlertBean alert = new AlertBean();
        alert.setMessage(message);
        alert.setMessageType(messageType);
        alert.setException(ex);
        alerts.add(alert);
    }

    /**
     * Type-safe conversion of the map that this class dumps to the event publisher.
     *
     * @param metadata the metadata to translate (presumably produced by this class)
     * @return either null (if argument was null) or a translated metadata
     */
    @Override
    public LogItem getEventMetadata(Map<String, Object> metadata) {
        return null;  // this one does not publish
    }

    public class AlertBean {
        private String message;
        private String messageType;
        private Throwable exception;

        public AlertBean() {
            message = null;
            messageType = null;
            exception = null;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public Throwable getException() {
            return exception;
        }

        public void setException(Throwable exception) {
            this.exception = exception;
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            if (messageType != null) {
                buf.append("[").append(messageType).append("]").append("\n");
            }
            buf.append(message==null ? "no message..." : message).append("\n");
            if (exception != null) {
                buf.append(exception.getClass().getName()).append(": ").append(exception.toString()).append("\n");
                for (StackTraceElement el : exception.getStackTrace()) {
                    String className = el.getClassName();
                    // include only intellitrade lines
                    if (className.startsWith("com.intellitrade")) {
                        buf.append("  at ").append(el.getClassName()).append(".").append(el.getMethodName());
                        buf.append("(").append(el.getFileName()).append(":").append(el.getLineNumber()).append(")").append("\n");
                    }
                }
            }

            return buf.toString().trim();
        }
    }
}
