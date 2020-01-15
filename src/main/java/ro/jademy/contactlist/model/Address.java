package ro.jademy.contactlist.model;

public class Address {

    String streetName;
    Integer streetNumber;
    Integer apartmentNumber;
    String floor;
    String zipCode;
    String city;
    String country;

    public Address(){};

    public Address(String streetName, Integer streetNumber, Integer apartmentNumber, String floor, String zipCode, String city, String country) {
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.apartmentNumber = apartmentNumber;
        this.floor = floor;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
    }
}
