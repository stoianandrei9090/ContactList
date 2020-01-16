package ro.jademy.contactlist.model;

public class PhoneNumber {

    private String countryCode; // ex: +40
    private String number; // ex: 740123456

    public PhoneNumber(String countryCode, String number) {
        this.countryCode = countryCode;
        this.number = number;
    }

    @Override
    public String toString() {
        return "PhoneNumber{" +
                "countryCode='" + countryCode + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
