package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class MemoryUserService implements UserService {

    private List<User> contacts = new ArrayList<>();
    @Override
    public List<User> getContacts() {

        if(contacts.isEmpty())initContactList();
        return contacts;

    }

    private void initContactList() {
        List<User> userList = new ArrayList<>();
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
        u2.setCompany(new Company("Whatever SRL", companyAdr2));

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

        userList.add(u1);
        userList.add(u2);
        userList.add(u3);
        userList.add(u4);
        int i = 0;
        for(User u : userList) {
            u.setUserId(i);
            i++;
        }

        contacts.addAll(userList);

    }

    @Override
    public void addContact(User contact) {
        if(contacts.isEmpty())getContacts();
        contact.setUserId(contacts.stream().map(User::getUserId).max(Comparator.naturalOrder()).get()+1);
        contacts.add(contact);
    }

    @Override
    public User getContact(int userId) {
        Optional<User> optionalUser = contacts.stream().filter(u->u.getUserId()==userId).findFirst();
        if(optionalUser.isPresent()) return optionalUser.get();
        else return null;
    }


    @Override
    public void editContact(User contact) {

    }

    @Override
    public void editContact(int userId) {

    }

    @Override
    public void removeContact(User contact) {
        removeContact(contact.getUserId());

    }

    @Override
    public void removeContact(int userId) {
        getContacts();
        contacts.removeIf(u->u.getUserId()==userId);

    }

    @Override
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
}
