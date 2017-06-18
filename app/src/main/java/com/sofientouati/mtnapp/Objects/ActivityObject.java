package com.sofientouati.mtnapp.Objects;

/**
 * Created by sofirntouati on 18/06/17.
 */

public class ActivityObject {
    private String id, sourceNumber, destinationNumber, status, date, action;
    private float amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceNumber() {
        return sourceNumber;
    }

    public void setSourceNumber(String sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    public String getDestinationNumber() {
        return destinationNumber;
    }

    public void setDestinationNumber(String destinationNumber) {
        this.destinationNumber = destinationNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public ActivityObject(String id, String sourceNumber, String destinationNumber, String status, String date, String action, float amount) {

        this.id = id;
        this.sourceNumber = sourceNumber;
        this.destinationNumber = destinationNumber;
        this.status = status;
        this.date = date;
        this.action = action;
        this.amount = amount;
    }
}
