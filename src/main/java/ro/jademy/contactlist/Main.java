package ro.jademy.contactlist;

import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;
import ro.jademy.contactlist.service.FileUserService;
import ro.jademy.contactlist.service.MemoryUserService;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class Main {

    private static List<User> userList;

    public static void main(String[] args) {

        Menu menu = new Menu(new FileUserService());
        menu.showMenu();
    }


}
