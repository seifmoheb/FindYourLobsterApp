package com.app.findyourlobster.data;

public class Messages {
    public String date;
    public String sender;
    public String message;
    public String time;
    public String to;
    public String type;
    public String SenderTimeZone;

    public String getTimeZone() {
        return SenderTimeZone;
    }

    public void setTimeZone(String timeZone) {
        this.SenderTimeZone = timeZone;
    }

    public String getDelivered() {
        return delivered;
    }

    public void setDelivered(String delivered) {
        this.delivered = delivered;
    }

    public String delivered;

    public Messages() {
    }

    public Messages(String date, String sender, String message, String time, String to) {
        this.date = date;
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
