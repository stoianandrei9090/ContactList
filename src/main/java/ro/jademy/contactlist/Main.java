package ro.jademy.contactlist;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class Main {

    private static List<User> userList;

    public static void main(String[] args) {


        userList = createUserList();
//        userList.addAll(createUserListFromFile("users.config"));
        //Map <Integer, User> userIndexMap = printLetterMap(userList);
        //System.out.println(userIndexMap.get(insertUser()));
       // createUserFile(userList, "output.config");



      /*  try {
        User u = createUser("A1212lex", "Popescu", "apopescu@gmail.com", 29, new HashMap<>(), "programmer", true) ;
        }
        catch (InputNotValidException e) {
            System.out.println(e.getMessage());
        }


       */
   User u = InsertUserKeyboard();


    }

    public static List<User> createUserListFromFile(String filePath) {
        BufferedReader in = null;
        List<User> listOfUsers = new ArrayList<>();
        List<String> listOfLines = new ArrayList<>();
        try {
            in = new BufferedReader(new FileReader(filePath));
            String line = in.readLine();
            while (line != null) {
                listOfLines.add(line);
                line = in.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String line:listOfLines) {
            User u = new User();
            u.setPhoneNumbers(new HashMap<>());
            String[] lineFields = line.split("\\|");
            u.setFirstName(lineFields[0]);
            u.setLastName(lineFields[1]);
            u.setEmail(lineFields[2]);
            u.setAge(Integer.parseInt(lineFields[3]));
            String[] phoneNumbers = lineFields[4].split(",");
            for(String phoneNumber : phoneNumbers) {
                String[] phoneNumberField = phoneNumber.split("_");
                PhoneNumber ph = new PhoneNumber(phoneNumberField[1], phoneNumberField[2]);
                u.getPhoneNumbers().put(phoneNumberField[0], ph);
            }
            String[] addressFields = lineFields[5].split("_");
            Address a = new Address(addressFields[0], Integer.parseInt(addressFields[1]), Integer.parseInt(addressFields[2]),
                    addressFields[3], addressFields[4], addressFields[5], addressFields[6]);
            u.setAddress(a);
            u.setJobTitle(lineFields[6]);
            String[] companyFields = lineFields[7].split("_");
            Address companyAdr = new Address(companyFields[1], Integer.parseInt(companyFields[2]),
                    Integer.parseInt(companyFields[3]), companyFields[4], companyFields[5], companyFields[6], companyFields[7]);
            u.setCompany(new Company(companyFields[0], companyAdr));
            boolean isFavorite = (lineFields[8].equals("true")) ? true : false;
            u.setFavorite(isFavorite);
            listOfUsers.add(u);

        }

        return listOfUsers;
    }

    public static void createUserFile(List<User> inputUserList, String path) {

        List<String> lineList = new ArrayList<>();

        for(User u : inputUserList) {
            String line = u.getFirstName()+"|"+u.getLastName()+"|"+u.getEmail()+"|"+u.getAge()+"|";
            Set <Map.Entry<String, PhoneNumber>> entrySet = u.getPhoneNumbers().entrySet();
            int count = 1;
            for(Map.Entry<String, PhoneNumber> entry : entrySet) {
                line+=entry.getKey()+"_"+entry.getValue().getCountryCode()+"_"+entry.getValue().getNumber();
                if(count < entrySet.size()) line+=",";
                count++;
            }
            Address companyAdress = u.getCompany().getAddress();
            line+="|"+u.getAddress().getStreetName()+"_"+u.getAddress().getStreetNumber()+"_"+
                    u.getAddress().getApartmentNumber()+"_"+u.getAddress().getFloor()+
                    "_"+u.getAddress().getZipCode()+"_"+u.getAddress().getCity()+"_"+u.getAddress().getCountry()+"|"+
                    u.getJobTitle()+"|"+u.getCompany().getName()+"_"+companyAdress.getStreetName()+"_"+
                    companyAdress.getStreetNumber()+"_"+companyAdress.getApartmentNumber()+
                    "_"+companyAdress.getFloor()+"_"+companyAdress.getZipCode()+"_"+
                    companyAdress.getCity()+"_"+companyAdress.getCountry()+"|"+u.isFavorite();
            lineList.add(line);
        }

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(path));
            int count = 1;
            for(String line : lineList) {
                out.write(line);
                if( count< lineList.size()) out.newLine();
                count++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static User InsertUserKeyboard () {
        Scanner sc = new Scanner(System.in);
        System.out.print("First name : ");
        String firstName = sc.next();
        System.out.print("Second name: ");
        String lastName = sc.next();
        System.out.print("Email: ");
        String email = sc.next();
        System.out.print("Age: ");
        Integer age = Integer.parseInt(sc.next());
        System.out.print("Job title :");
        String jobTitle = sc.next();


        String yesNo = "";
        while(!yesNo.toLowerCase().equals("y") && !yesNo.toLowerCase().equals("n")) {
            System.out.print("Favorite (Y/N)? ");
            yesNo = sc.next();
        }
        boolean isFavorite = yesNo.toLowerCase().equals("y") ? true : false;

        User u;
        try{
           u = createUser(firstName, lastName, email, age, new HashMap<>(), jobTitle, isFavorite);
        } catch (InputNotValidException e) {
            e.printMessageList();
            return InsertUserKeyboard();
        }

        return u;

    }


    public static User createUser(String firstName, String lastName, String email, Integer age, Map<String, PhoneNumber> phoneNumbers, String jobTitle, boolean isFavorite) throws InputNotValidException {

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        List<String> messageList = new ArrayList<>();

        if(firstName.length() <= 1 || !firstName.matches("^[a-zA-Z]*$")) messageList.add("First name is not valid");
        if(lastName.isEmpty()) messageList.add("Last name can not be empty");
        if(email.matches(emailRegex)== false) messageList.add("E-mail does not have required format");
        if(age<=0) messageList.add("Age can not be zero or negative");
        if(phoneNumbers == null) messageList.add("Phone number map can not be null");
        if(jobTitle.isEmpty()) messageList.add("Job title can not be empty");

        if (!messageList.isEmpty())throw new InputNotValidException(messageList);

        return new User(firstName, lastName, email, age, phoneNumbers, jobTitle, isFavorite);

    }

    public static void printStuff(List<User> userList) {
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

        //user 4
        User u4 = new User("Ilie", "Ionescu", "ionescu@yahoo.com", 66, new HashMap<>(), "plumber", false );
        u4.getPhoneNumbers().put("home", new PhoneNumber("+40", "6344566"));
        Address adrU4 = new Address("Mihai Eminescu", 12, 33, "2", "64221", "Bucharest", "Romania");
        u4.setAddress(adrU4);
        Address companyAdr4 = new Address("Mosilor", 100, 30, "3", "23000", "Bucharest", "Romania");
        u4.setCompany(new Company ("Another Company SRL", companyAdr4));

        List<User> userList = new ArrayList<>();
        userList.add(u1);
        userList.add(u2);
        userList.add(u3);
        userList.add(u4);

        return userList;

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
                String output = String.format("| %-"+85+ "s|", userIndex+" "+userPrint.getFirstName()+" "+userPrint.getLastName());
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
