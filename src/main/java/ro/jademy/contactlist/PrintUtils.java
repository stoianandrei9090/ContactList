package ro.jademy.contactlist;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class PrintUtils {

    public static User InsertUserKeyboard () {
        Scanner sc = new Scanner(System.in);
        System.out.print("First name : ");
        String firstName = sc.next();
        System.out.print("Second name : ");
        String lastName = sc.next();
        System.out.print("Email: ");
        String email = sc.next();
        System.out.print("Age : ");
        Integer age = Integer.parseInt(sc.next());
        sc.nextLine();
        System.out.print("Job title : ");
        String jobTitle = sc.nextLine();
     //   sc.nextLine();


        String yesNo = "";
        while(!yesNo.toLowerCase().equals("y") && !yesNo.toLowerCase().equals("n")) {
            System.out.print("Favorite (Y/N)? ");
            yesNo = sc.next();
        }
        boolean isFavorite = yesNo.toLowerCase().equals("y");

        User u;
        try{
            u = validateUserInfo(firstName, lastName, email, age, new HashMap<>(), jobTitle, isFavorite);
        } catch (InputNotValidException e) {
            e.printMessageList();
            return InsertUserKeyboard();
        }

        System.out.println("Insert user phone numbers : ");
        u.setPhoneNumbers(createPhoneNumberMap());
        System.out.println("Insert user address : ");
        u.setAddress(insertAddress());
        sc.nextLine();
        System.out.println("Insert company name : ");
        String companyName = sc.nextLine();
        u.setCompany(new Company(companyName, insertAddress()));

        return u;

    }

    public static Map<String, PhoneNumber> createPhoneNumberMap() {
        boolean stop = false;
        Scanner sc = new Scanner(System.in);
        Map<String, PhoneNumber> phoneNumberMap = new HashMap<>();
        System.out.println("Insert phone number : ");
        while (!stop) {

            System.out.print("Place : ");
            String place = sc.nextLine();
            if(phoneNumberMap.containsKey(place)) {
                System.out.println("Key already exists");
                continue;
            }
            phoneNumberMap.put(place, insertPhoneNumber());
            String yesNo = "";
            while(!yesNo.toLowerCase().equals("y") && !yesNo.toLowerCase().equals("n")) {
                System.out.print("Insert another phone number (Y/N)? ");
                yesNo = sc.next();
            }
            stop = yesNo.toLowerCase().equals("y");


        }

        return phoneNumberMap;

    }

    public static PhoneNumber insertPhoneNumber () {
        Scanner sc = new Scanner(System.in);
        System.out.print("Country code : ");
        String cCode = sc.next();
        if(cCode.charAt(0) != '+' || !cCode.substring(1).matches("^[0-9]\\d*$")) {
            System.out.println("Country code not valid.");
            return insertPhoneNumber();
        }

        String number = "";
        while(!number.matches("^[0-9]\\d*$")) {
            System.out.print("Number : ");
            number = sc.next();
        }

        return new PhoneNumber(cCode, number);

    }

    public static User validateUserInfo(String firstName, String lastName, String email, Integer age, Map<String, PhoneNumber> phoneNumbers, String jobTitle, boolean isFavorite) throws InputNotValidException {

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        List<String> messageList = new ArrayList<>();

        if(firstName.length() <= 1 || !firstName.matches("^[a-zA-Z]*$")) messageList.add("First name is not valid");
        if(lastName.isEmpty()) messageList.add("Last name can not be empty");
        if(!email.matches(emailRegex)) messageList.add("E-mail does not have required format");
        if(age<=0) messageList.add("Age can not be zero or negative");
        if(phoneNumbers == null) messageList.add("Phone number map can not be null");
        if(jobTitle.isEmpty()) messageList.add("Job title can not be empty");

        if (!messageList.isEmpty())throw new InputNotValidException(messageList);

        return new User(firstName, lastName, email, age, phoneNumbers, jobTitle, isFavorite);

    }

    public static Address insertAddress() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Insert address info ---");
        System.out.print("Street name");
        String streetName = sc.nextLine();
        String streetNumber = "";
        while(!streetNumber.matches("^[0-9]\\d*$")) {
            System.out.print("Street number : ");
            streetNumber = sc.next();
        }
        String floor = "";
        while(!floor.matches("^[0-9]\\d*$")) {
            System.out.print("Floor number : ");
            floor = sc.next();
        }
        String apartmentNumber = "";
        while(!apartmentNumber.matches("^[0-9]\\d*$")) {
            System.out.print("Apartment number : ");
            apartmentNumber = sc.next();
        }

        sc.nextLine();
        String city = "";
        //could do validation
        while(city.equals("")) {
            System.out.print("City : ");
            city = sc.nextLine();
        }

        String zipCode = "";
        while(!zipCode.matches("^[0-9]\\d*$")) {
            System.out.print("Zip Code : ");
            zipCode = sc.next();
        }

        sc.nextLine();
        String country = "";
        //could do validation
        while(country.equals("")) {
            System.out.print("Country : ");
            country = sc.nextLine();
        }

        return new Address(streetName, Integer.valueOf(streetNumber), Integer.valueOf(apartmentNumber), floor,
                zipCode, city, country);

    }

    public static void printStatistics(List<User> userList) {

    System.out.println("Number of users : "+userList.stream().count());
    System.out.println("Average age :"+userList.stream().mapToDouble(value -> value.getAge()).average().getAsDouble());
    System.out.println("Oldest user :"+userList.stream().max((a,b)-> a.getAge() - b.getAge()).get());
    Predicate<User> overFifty = value -> value.getAge()>50;
    System.out.println("Users over fifty :");
    userList.stream().filter(overFifty).map(u-> u.getFirstName()+" "+u.getLastName()).forEach(System.out::println);
    System.out.println("Users under fifty :");
    userList.stream().filter(overFifty.negate()).
            map(u-> u.getFirstName()+" "+u.getLastName()).forEach(System.out::println);
    }

    public static void printFavorites(List<User> userList) {
        List<User> favoritesList = userList.
                stream().filter(value -> value.isFavorite()).collect(Collectors.toList());
        for (User u : favoritesList) {
            System.out.println(u);
        }
    }

    public static void printAllFromList (List<User> userList) {
        System.out.println();
        userList.stream().sorted().forEach(System.out::println);
        System.out.println();
    }

    public static Map<Integer, User> printLetterMap(List<User> userList) {


        TreeMap<Character, List<User>> mapPrint  = new TreeMap<>();
        mapPrint.putAll(userList.stream().sorted().collect(groupingBy(user -> user.getLastName().charAt(0))));
        int userIndex = 0;
        Map<Integer, User> userIndexMap= new HashMap<>();

        for (Map.Entry<Character, List<User>> entry : mapPrint.entrySet()) {

            System.out.println("---------------------------------------------------------------------------------------");
            System.out.println(String.format("| %-"+85+"s|",entry.getKey()));
            System.out.println("---------------------------------------------------------------------------------------");

            for (User userPrint : entry.getValue()) {

                userIndex++;
                String output = String.format("| %-"+85+ "s|", userIndex+" "+userPrint.getFirstName()+" "+userPrint.getLastName()+" --- UserID : "+userPrint.getUserId());
                userIndexMap.put(userIndex, userPrint);
                System.out.println(output);
            }
            System.out.println(String.format("| %-"+85+"s|","Users : "+entry.getValue().size()));

        }

        System.out.println("---------------------------------------------------------------------------------------");
        return userIndexMap;

    }

    public static void printUserFromMap (Map <Integer, User> map, int i) {
        System.out.println(map.get(i));
    }

    public static Integer getUserIdFromMap(Map<Integer, User> map, int i) {
        return map.get(i).getUserId();
    }

    public static int insertUser() {
        System.out.print("Insert user : ");
        Scanner sc = new Scanner(System.in);
        int intUser = 0;
        try {
            intUser = sc.nextInt();

        } catch (InputMismatchException e) {
            System.out.println("Insert int value!");
            return insertUser();
        }
        return intUser;

    }

}
