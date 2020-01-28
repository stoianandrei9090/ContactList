package ro.jademy.contactlist.service;

import ro.jademy.contactlist.model.User;

import java.util.List;

public interface UserService {
    public List<User> getContacts();
    public void addContact(User contact);
    void editContact(User contact);
    void editContact(int userId);
    void removeContact(User contact);
    void removeContact(int userId);

    public List<User> search (String query);
}
