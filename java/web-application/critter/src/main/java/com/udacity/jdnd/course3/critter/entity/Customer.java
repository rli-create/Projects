package com.udacity.jdnd.course3.critter.entity;

import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer extends User{

    private String phoneNumber;

    @Nationalized
    private String notes;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Pet> pets = new ArrayList<>();
    public Customer(){}

    public Customer(long customerId, String customerName, String phoneNumber, String notes, List<Pet> pets) {
        this.user_id = customerId;
        this.name = customerName;
        this.phoneNumber = phoneNumber;
        this.notes = notes;
        this.pets = pets;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPetIds(List<Pet> pets) {
        this.pets = pets;
    }

    public boolean hasPet(Pet pet) {
        for (Pet p : pets) {
            if (p.getPet_id() == pet.getPet_id()) {
                return true;
            }
        }
        return false;
    }
}
