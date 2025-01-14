package org.example.demo1;

import java.sql.*;
import java.time.LocalDate;

public class Donor{
    // Private fields
    private double lat;
    private double lon;
    private static int donorId;
    private int userId;
    private String fullName;
    private String bloodType;
    private int age;
    private String gender;
    private String contactNumber;
    private String address;
    private LocalDate lastDonationDate;
    private boolean availabilityStatus;
    private TimeStamp timeStamp;

    // Constructor

    public Donor(String fullName, String bloodType, int age, String gender, String contactNumber, String address, LocalDate lastDonationDate,double lat,double lon) {
        this.userId = User.id;
        this.fullName = fullName;
        this.bloodType = bloodType;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.lastDonationDate = lastDonationDate;
        this.availabilityStatus = true;
        this.lat=lat;
        this.lon=lon;
    }


    // Getters and Setters

    public int getDonorId() {
        return donorId;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public boolean isAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(TimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Donor{" +
                "donorId=" + donorId +
                ", userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", address='" + address + '\'' +
                ", lastDonationDate=" + lastDonationDate +
                ", availabilityStatus=" + availabilityStatus +
                '}';
    }
}
