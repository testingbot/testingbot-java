package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * A user within a team.
 *
 * @since 1.1.0
 */
public class TestingbotTeamMember implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    @SerializedName("first_name") private String firstName;
    @SerializedName("last_name") private String lastName;
    private String email;
    private int credits;
    @SerializedName("device_credits") private int deviceCredits;
    private boolean isPaid;
    private boolean verified;
    @SerializedName("parent_id") private int parentId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getDeviceCredits() {
        return deviceCredits;
    }

    public void setDeviceCredits(int deviceCredits) {
        this.deviceCredits = deviceCredits;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
}
