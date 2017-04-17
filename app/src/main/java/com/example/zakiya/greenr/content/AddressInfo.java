package com.example.zakiya.greenr.content;

/**
 * Created by Arize on 4/16/2017.
 */

public class AddressInfo {

    private int id;
    private String title;
    private String addressLine1;
    private String town;
    private String stateOrProvince;
    private String postcode;
    private Long latitude;
    private Long longitude;
    private String contactTelephone1;

    public AddressInfo(){

    }

    public AddressInfo(int id, String title, String addressLine1, String town, String stateOrProvince, String postcode, Long latitude, Long longitude, String contactTelephone1) {
        this.id = id;
        this.title = title;
        this.addressLine1 = addressLine1;
        this.town = town;
        this.stateOrProvince = stateOrProvince;
        this.postcode = postcode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.contactTelephone1 = contactTelephone1;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getStateOrProvince() {
        return stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    public String getContactTelephone1() {
        return contactTelephone1;
    }

    public void setContactTelephone1(String contactTelephone1) {
        this.contactTelephone1 = contactTelephone1;
    }

    @Override
    public String toString() {
        return "AddressInfo{" +
                "\n id=" + id +
                ", \n title='" + title + '\'' +
                ", \n addressLine1='" + addressLine1 + '\'' +
                ", \n town='" + town + '\'' +
                ", \n stateOrProvince='" + stateOrProvince + '\'' +
                ", \n postcode='" + postcode + '\'' +
                ", \n latitude=" + latitude +
                ", \n longitude=" + longitude +
                ", \n contactTelephone1='" + contactTelephone1 + '\'' +
                '}';
    }
}
