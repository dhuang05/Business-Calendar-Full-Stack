/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.model.buscalendar;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "ORGANIZATION")
public class Organization {
    @Id
    @Column(name = "ORG_ID")
    private String orgId;

    @Column(name = "PARENT_ORG_ID")
    private String parentOrgId;

    @Column(name = "ORG_NAME")
    private String orgName;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "CATEGORY")
    private String category;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "FIRST_CONTACT_PERSON_ID", referencedColumnName = "PERSON_ID")
    @JsonProperty
    private Person firstContactPerson;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "SECOND_CONTACT_PERSON_ID", referencedColumnName = "PERSON_ID")
    @JsonProperty
    private Person secondContactPerson;

    @OneToMany(
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "PARENT_ORG_ID")
    private List<Organization> children = new ArrayList<>();

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(String parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Organization> getChildren() {
        return children;
    }

    public void setChildren(List<Organization> children) {
        this.children = children;
    }


    public Person getFirstContactPerson() {
        return firstContactPerson;
    }

    public void setFirstContactPerson(Person firstContactPerson) {
        this.firstContactPerson = firstContactPerson;
    }

    public Person getSecondContactPerson() {
        return secondContactPerson;
    }

    public void setSecondContactPerson(Person secondContactPerson) {
        this.secondContactPerson = secondContactPerson;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
