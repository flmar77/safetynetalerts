package com.safetynetalerts.api.input.model;

import lombok.Getter;

@Getter
public class PersonInputModel {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private String phone;
    private String email;
}