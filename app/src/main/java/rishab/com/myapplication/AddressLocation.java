package rishab.com.myapplication;

/**
 * Created by RISHAB on 19-Jun-17.
 */
// it is used to send the location object to the firebase and
//  to get the location name
class AddressLocation {

    public String locationAddress;
    public Double lat;
    public Double lon;


    public AddressLocation() {

    }

    public AddressLocation(String address, Double lat, Double lon) {
        this.locationAddress = address;
        this.lat = lat;
        this.lon = lon;

    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }
}
