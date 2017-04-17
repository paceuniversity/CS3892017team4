package com.example.zakiya.greenr.content;

/**
 * Created by Arize on 4/16/2017.
 */

public class OpenChargeStation {

    private int id;
    private AddressInfo addressInfo;

    public OpenChargeStation() {
    }

    public OpenChargeStation(int id, AddressInfo addressInfo) {
        this.id = id;
        this.addressInfo = addressInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AddressInfo getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(AddressInfo addressInfo) {
        this.addressInfo = addressInfo;
    }
}
