package ro.jademy.contactlist;

import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.User;
import ro.jademy.contactlist.service.FileUserService;
import ro.jademy.contactlist.service.UserService;
import java.util.*;


public class Menu {
    private UserService userService;
    private Scanner scanner = new Scanner(System.in);

    public Menu(UserService userService) {this.userService = userService;}

    public void showMenu() {
        String[] listOfOptions = {"Add user", "Remove user", "Update fields",
                "Display favorites", "Print list","Print list grouping by first letters",
                "Print statistics", "Search using query","Create backup file","Exit"};
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
            case 3:
                Map <Integer, User> m2 = PrintUtils.printLetterMap(userService.getContacts());
                int userChoice2 = PrintUtils.getUserIdFromMap(m2, PrintUtils.insertUser());
                User uToEdit = userService.getContacts().stream().filter(u-> u.getUserId() == userChoice2).findFirst()
                        .get();
                editMenu(uToEdit);
                userService.editContact(uToEdit);


                break;
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
                if(userService instanceof FileUserService) {
                    System.out.println("Creating backup file");
                    String pathName = System.currentTimeMillis() +"_"+UUID.randomUUID().toString().replaceAll("-","")
                            +"_users.config";
                    ((FileUserService) userService).writeToFile(userService.getContacts(), pathName);
                } else System.out.println("User service is not FileUserService");
                break;
            case 10:
                return true;

        } return false;
    }

    public void editMenu(User user) {
        boolean exit = false;

        while(!exit) {
            System.out.println("User : "+user.getFirstName()+" "+user.getLastName());
            System.out.println("Choose what to edit :" + "\r\n" + "\r\n");
            System.out.println("1) Edit first name ");
            System.out.println("2) Edit second name ");
            System.out.println("3) Edit email");
            System.out.println("4) Change age");
            System.out.println("5) Replace address");
            System.out.println("6) Replace company");
            System.out.println("7) Change phoneNumbers map");
            System.out.println("8) Back to main menu");

            String choice ="";
            while(!choice.matches("^[1-8]")) {
                System.out.println("Enter (valid) choice :");
                choice = scanner.next();
            }

            switch (Integer.valueOf(choice)) {
                case 1 :
                    System.out.println("Enter new first name : ");
                    user.setFirstName(scanner.next());
                    break;
                case 2 : System.out.println("Enter new last name : ");
                    user.setLastName(scanner.next());
                    break;

                case 3 : System.out.println("Enter new last name : ");
                    user.setEmail(scanner.next());
                    break;
                case 4 :
                    System.out.println("Enter new age : ");
                    user.setAge(scanner.nextInt());
                    scanner.nextLine();
                    break;
                case 5 :
                    System.out.println("Replace address");
                    user.setAddress(PrintUtils.insertAddress());
                    break;
                case 6 :
                    System.out.println("Replace company");
                    System.out.println("Insert company name : ");
                    user.setCompany(new Company(scanner.nextLine(), PrintUtils.insertAddress()));
                    break;
                case 7 :
                    user.setPhoneNumbers(PrintUtils.createPhoneNumberMap());
                    break;
                case 8 :
                    exit = true;
                    break;

            }

        }
    }

}
