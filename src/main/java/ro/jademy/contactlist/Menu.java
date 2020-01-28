package ro.jademy.contactlist;

import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.service.UserService;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class Menu {
    private UserService userService;
    private Scanner scanner = new Scanner(System.in);


    public Menu(UserService userService) {this.userService = userService;}



    public void showMenu() {
        String[] listOfOptions = {"Add user", "Remove user", "Update fields",  "Search"
                , "Display favorites", "Print list", "Print statistics", "Exit","Test stuff"};
        String question = "Choose what to do: " + "\r\n";
        boolean exit = false;
        while (!exit) {
            System.out.println(question);
            for (int i = 1; i <= listOfOptions.length; i++)
                System.out.println(i + ") " + listOfOptions[i - 1]);
            String option = "";

            while(!option.matches("^[1-9]\\d*$") || Integer.valueOf(option) > listOfOptions.length) {
                System.out.print("Enter choice : ");
                option = scanner.next();
                }

            exit = followOption(Integer.valueOf(option));

            }


        }


    private boolean followOption (int i) {

        switch (i) {


            case 1: userService.addContact(PrintUtils.InsertUserKeyboard());
                break;
            case 2: break;
            case 3: break;
            case 4: break;
            case 5:
                PrintUtils.printFavorites(userService.getContacts());
                break;
            case 6:
                PrintUtils.printAllFromList(userService.getContacts());
                break;
            case 7:
                PrintUtils.printStatistics(userService.getContacts());
                break;
            case 8:
                return true;
            case 9:


                break;
        } return false;
    }








}
