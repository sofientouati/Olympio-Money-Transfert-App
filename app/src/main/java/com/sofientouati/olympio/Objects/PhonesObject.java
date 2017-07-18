package com.sofientouati.olympio.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * OLYMPIO
 * Created by SOFIEN TOUATI on 13/07/17.
 */

public class PhonesObject extends RealmObject {
    @PrimaryKey
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
