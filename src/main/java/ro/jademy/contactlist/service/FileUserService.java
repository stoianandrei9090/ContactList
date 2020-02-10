package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


public class FileUserService implements UserService {

    private List<User> contacts = new ArrayList<>();

    public List<User> getContacts() {
       if(contacts.isEmpty()) {
           String filePath = "users.config";
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

           contacts.addAll(listOfUsers);
           for (int i = 0; i < contacts.size() ; i++) {
               contacts.get(i).setUserId(i);
           }
       };
           return contacts;

    }

    public void addContact(User user) {

        if(contacts.isEmpty()) contacts = getContacts();
        user.setUserId(contacts.stream().map(v -> v.getUserId()).max(Comparator.naturalOrder()).get()+1);
        contacts.add(user);
        System.out.println("Reached this point");
//        contacts.add(user);
//        Integer maxId = contacts.stream().map(u->u.getUserId()).max(Comparator.naturalOrder()).get();
//        contacts.get(maxId+1).setUserId(maxId+1);
        // overwrite the whole list of contacts to file
        writeToFile(contacts);
    }

    @Override
    public void removeContact(User Contact) {
         removeContact(Contact.getUserId());

    }

    @Override
    public void editContact(User contact) {

        contacts.removeIf(u -> u.getUserId() == contact.getUserId());
        contacts.add(contact);
        writeToFile(contacts);

    }

    @Override
    public void editContact(int userId) {
        //not needed
         writeToFile(contacts);
    }

    @Override
    public void removeContact(int userId) {
        List<User>temp = contacts.stream().filter(user -> user.getUserId()!=userId).collect(Collectors.toList());
        contacts.clear();
        contacts.addAll(temp);
        writeToFile(contacts);

    }

    public List<User> search(String query) {
        contacts = getContacts();
        List<User> returnList = new ArrayList<>();
        Field[] fields = User.class.getDeclaredFields();
        for (User u : contacts) {
            boolean found = false;
            for (Field f : fields) {
                f.setAccessible(true);
                try {
                    if(f.get(u).toString().contains(query)) {
                        returnList.add(u);
                        found = true;
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if(found) break;
            }
        }

    return returnList;
    }

    @Override
    public User getContact(int userId) {
        Optional<User> optionalUser = contacts.stream().filter(u->u.getUserId()==userId).findFirst();
        if(optionalUser.isPresent()) return optionalUser.get();
        else return null;
    }

    private void writeToFile(List<User> contacts) {
        writeToFile(contacts, "users.config");
    }

    public void writeToFile(List<User> contacts, String path){

        List<String> lineList = new ArrayList<>();
        List<User> inputUserList = new ArrayList<>();
        inputUserList.addAll(contacts);
      //  String path = "users.config";

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

}
