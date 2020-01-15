package ro.jademy.contactlist.model;

public class Company {

    private String name;
    private Address address;

    public Company(){};

    public Company(String name, Address address) {
        this.name = name;
        this.address = address;
    }
}
