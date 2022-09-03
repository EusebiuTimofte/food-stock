package com.mycompany.app.entities;

import jakarta.persistence.*;

import java.io.Serializable;


@Entity
@Table( name = "USERS" )
public class User implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
