package com.trungvu.chatapp.Model;


public class Messages {
    private String message;
    private String type;
    private long timestamp;
    private boolean seen;
    private String from;
    private String receiver;
    private String time;
    private String date;

    public Messages() {
    }

    public Messages(String message, String type, long timestamp, boolean seen, String from, String receiver, String time, String date) {
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
        this.seen = seen;
        this.from = from;
        this.receiver = receiver;
        this.time = time;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
