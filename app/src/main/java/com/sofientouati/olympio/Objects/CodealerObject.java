package com.sofientouati.olympio.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * OLYMPIO
 * Created by SOFIEN TOUATI on 10/07/17.
 */

public class CodealerObject extends RealmObject {

    @PrimaryKey
    private String id;
    @Required
    private String sender;
    @Required
    private String receiver;
    @Required
    private String status;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
