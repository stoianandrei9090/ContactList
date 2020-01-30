package ro.jademy.contactlist;

import ro.jademy.contactlist.model.User;
import ro.jademy.contactlist.service.UserService;
import java.util.*;


public class Menu {
    private UserService userService;
    private Scanner scanner = new Scanner(System.in);

    public Menu(UserService userService) {this.userService = userService;}

    public void showMenu() {
        String[] listOfOptions = {"Add user", "Remove user", "Update fields",
                "Display favorites", "Print list","Print list grouping by first letters",
                "Print statistics", "Search using query","Exit"};
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
            case 2:Map <Integer, User> m = PrintUtils.printLetterMap(userService.getContacts());
               //    int userChoice = PrintUtils.getUserIdFromMap(m, PrintUtils.insertUser());
               int userChoice = PrintUtils.getUserIdFromMap(m, PrintUtils.insertUser());
               User uToDelete = userService.getContacts().stream().filter(u-> u.getUserId() == userChoice).findFirst()
                       .get();
                System.out.println("Delete user : "+uToDelete.getFirstName()+" "+ uToDelete.getLastName()+
                        " -- UserId : "+userChoice+"? (Y/N)");
                String yesNo = scanner.next();
                if(yesNo.toLowerCase().equals("y")) { System.out.println("Deleting");
                userService.removeContact(userChoice);}
                else if (yesNo.toLowerCase().equals("n")) System.out.println("Delete cancelled.");
                else System.out.println("");

                break;
            case 3: break;
            case 4:
                PrintUtils.printFavorites(userService.getContacts());
                break;
            case 5:
                PrintUtils.printAllFromList(userService.getContacts());
                break;
            case 6:
                PrintUtils.printLetterMap(userService.getContacts());
                break;
            case 7:
                PrintUtils.printStatistics(userService.getContacts());
                break;
            case 8:
                String query = "";
                System.out.println("Insert query : ");
                query = scanner.next();
                PrintUtils.printLetterMap(userService.search(query));
                break;

            case 9:
                return true;

        } return false;
    }


}
