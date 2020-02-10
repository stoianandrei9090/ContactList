package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class DBUserService implements UserService {

    private List<User> contacts = new ArrayList<>();

    @Override
    public List<User> getContacts() {

        return new ArrayList<>();
    }

    @Override
    public void editContact(User contact) {

    }

    @Override
    public User getContact(int userId) {
        Optional<User> optionalUser = contacts.stream().filter(u->u.getUserId()==userId).findFirst();
        if(optionalUser.isPresent()) return optionalUser.get();
        else return null;
    }

    @Override
    public void editContact(int userId) {

    }

    @Override
    public void removeContact(int userId) {

    }

    @Override
    public void addContact(User contact) {

    }

    @Override
    public void removeContact(User Contact) {

    }


    @Override
    public List<User> search( String query) {
        return null;
    }
}
