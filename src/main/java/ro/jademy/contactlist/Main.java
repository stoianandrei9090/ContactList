package ro.jademy.contactlist;


import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;


import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class Main {



    public static void main(String[] args) {

        List<User> userList = createUserList();

        // list contact list in natural order
        userList.stream().sorted().forEach(System.out::println);


        // list contact list by a given criteria
        Predicate<User> ageCriteria = value -> value.getAge()>=18;
        Predicate<User> nameStartswith = value -> value.getLastName().startsWith("B");
        userList.stream().filter(ageCriteria).forEach(System.out::println);
        // display a favorites list
        List<User> favoritesList = userList.stream().filter(value-> value.isFavorite()).collect(Collectors.toList());
        for (User u: favoritesList) {
            System.out.println(u);
        }
        // search by a given or multiple criteria
        userList.stream().filter(nameStartswith).forEach(System.out::println);
        // display some statistics for the contact list
        System.out.println("Number of users : "+userList.stream().count());
        System.out.println("Average age :"+userList.stream().mapToDouble(value -> value.getAge()).average().getAsDouble());
        System.out.println("Oldest user :"+userList.stream().max((a,b)-> a.getAge() - b.getAge()).get());
        Predicate <User> overFifty = value -> value.getAge()>50;
        System.out.println("Users over fifty :");
        userList.stream().filter(overFifty).forEach(System.out::println);
        System.out.println("Users under fifty :");
        userList.stream().filter(overFifty.negate()).forEach(System.out::println);
        System.out.println("Print in reverse order :");
        userList.stream().sorted(Comparator.reverseOrder()).forEach(System.out::println);

        // groups user by city of address
        Map<String, List<User>> usersByCity = userList.stream().collect(groupingBy(user -> user.getAddress().getCity()));

        // groups users by first letter of last name
        printLetterMap(userList);

    }

    public static List<User> createUserList () {

        // create a contact list of users
        // user 1
        User u1 = new User("Andrei", "Stoian", "stoianandrei@yahoo.com", 32, new HashMap<>(),"junior programmer", false );
        u1.getPhoneNumbers().put("home", new PhoneNumber("+40", "728570162"));
        Address adrU1 = new Address("Stefan cel Mare", 128, 57, "8", "12700", "Constanta", "Romania");
        u1.setAddress(adrU1);
        Address companyAdr1 = new Address("Ion Mihalache", 17, 0, "1", "23401", "Buhcarest", "Romania");
        u1.setCompany(new Company("Jademy", companyAdr1));
        // user 2

        User u2 = new User("Ion", "Iliescu", "iliescu@yahoo.com", 82, new HashMap<>(), "ex-president", false );
        u2.getPhoneNumbers().put("home", new PhoneNumber("+40", "823645690"));
        Address adrU2 = new Address("Primaverii", 55, 11, "3", "23230", "Bucharest", "Romania");
        u2.setAddress(adrU2);
        Address companyAdr2 = new Address("Magheru", 171, 40, "5", "21200", "Buhcarest", "Romania");
        u2.setCompany(new Company ("Whatever SRL", companyAdr2));

        //user 3
        User u3 = new User("Traian", "Basescu", "tbs@yahoo.com", 70, new HashMap<>(),"ex-president",true );
        u3.getPhoneNumbers().put("home", new PhoneNumber("+40", "60440060"));
        Address adrU3 = new Address("Calea Victoriei", 48, 20, "3", "40030", "Bucharest", "Romania");
        u3.setAddress(adrU3);
        Address companyAdr3 = new Address("Bd. Unirii", 141, 12, "3", "217250", "Buhcarest", "Romania");
        u3.setCompany(new Company ("Another Company SRL", companyAdr3));

        List<User> userList = new ArrayList<>();
        userList.add(u1);
        userList.add(u2);
        userList.add(u3);
        return userList;

    }

    public static void printLetterMap(List<User> userList) {

        Map<Character, List<User>> mapPrint  = userList.stream().sorted().collect(groupingBy(user -> user.getLastName().charAt(0)));;
        for (Map.Entry<Character, List<User>> entry: mapPrint.entrySet()) {

            System.out.println("---------------------------------------------------------------------------------------");
            System.out.println(String.format("| %-"+85+"s|",entry.getKey()));

            for (User userPrint : entry.getValue()) {
                String output = String.format("| %-"+85+ "s|", userPrint.getFirstName()+" "+userPrint.getLastName());
                System.out.println(output);
            }

        }

        System.out.println("---------------------------------------------------------------------------------------");
    }

}
