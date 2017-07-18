package com.sofientouati.olympio.Objects;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by SOFIEN TOUATI on 23/06/17.
 */

public class UserObject extends RealmObject {
    private String photo;
    private String email;

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    @Required
    private String phone, password, name, lastname;
    private float solde, seuil;
    private RealmList<PhonesObject> sent, received, matched;

    public RealmList<PhonesObject> getSent() {
        return sent;
    }

    public void setSent(RealmList<PhonesObject> sent) {
        this.sent = sent;
    }

    public RealmList<PhonesObject> getReceived() {
        return received;
    }

    public void setReceived(RealmList<PhonesObject> received) {
        this.received = received;
    }

    public RealmList<PhonesObject> getMatched() {
        return matched;
    }

    public void setMatched(RealmList<PhonesObject> matched) {
        this.matched = matched;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public float getSolde() {
        return solde;
    }

    public void setSolde(float solde) {
        this.solde = solde;
    }

    public float getSeuil() {
        return seuil;
    }

    public void setSeuil(float seuil) {
        this.seuil = seuil;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
