package ro.jademy.contactlist.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Comparable <User> {

    private String firstName;
    private String lastName;
    private String email;
    private Integer age;

    private Map<String, PhoneNumber> phoneNumbers;
    private Address address;

    private String jobTitle;
    private Company company;

    private transient Integer userId;
    private boolean isFavorite;



    public static User getInstance() {
        Address defaultAddress = new Address("Default Street", 1, 1, "1", "100000",
            "Bucharest", "Romania");
        Address defaultAddress2 = new Address("Default Street", 1, 1, "1", "100000",
                "Bucharest", "Romania");
        PhoneNumber defaultPhoneNumber = new PhoneNumber("+40", "600000");
        Company defaultCompany = new Company("Default Company", defaultAddress2);



        Map <String, PhoneNumber> map = new HashMap<>();
        map.put("default", defaultPhoneNumber);
        User u = new User("First Name", "Last Name", "email@gmail.com", 40, map,
                "defaultJobTitle", false);
        u.setAddress(defaultAddress);
        u.setCompany(defaultCompany);
        return u;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public User(){};

    public User(String firstName, String lastName, String email, Integer age, Map<String, PhoneNumber> phoneNumbers, String jobTitle, boolean isFavorite) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.phoneNumbers = phoneNumbers;
        this.jobTitle = jobTitle;
        this.isFavorite = isFavorite;

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Map<String, PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Map<String, PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public String toString() {
        return
                "first name : " + firstName + '\'' + "\n" +
                "last name : " + lastName + '\'' + "\n" +
                "email : " + email + '\'' + "\n" +
                "age : " + age + "\n" + "\n" +
                "phone number : " + phoneNumbers + "\n" +
                "address : " + address + "\n" +
                "job title : " + jobTitle + '\'' + "\n" +
                "company : " + company + "\n" +
                "favorite : " + isFavorite + "\n"
                ;
    }

    @Override
    public int compareTo(User o) {
        int result = this.getLastName().compareTo(o.getLastName());
        if(result != 0) return result;
        else return this.getFirstName().compareTo(o.getFirstName());
    }
}
