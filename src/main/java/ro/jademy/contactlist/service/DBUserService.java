package ro.jademy.contactlist.service;

import javafx.beans.property.Property;
import ro.jademy.contactlist.Main;
import ro.jademy.contactlist.model.Address;
import ro.jademy.contactlist.model.Company;
import ro.jademy.contactlist.model.PhoneNumber;
import ro.jademy.contactlist.model.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;


public class DBUserService implements UserService {

    String url;
    String user;
    String password;
    Connection c;

    private List<User> contacts;

    public DBUserService() {
        if(url == null || user == null || password==null) {
            try(InputStream inputStream = DBUserService.class.getResourceAsStream("/application.properties")) {
                Properties p = new Properties();
                p.load(inputStream);
                url = p.getProperty("dbURL");
                user = p.getProperty("dbUser");
                password = p.getProperty("dbPassword");

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        try {
            c = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Failed to initialize connection");
        }
    }

    ;

    public int insertAddress(Address address, Connection c) {
        int toReurn = 0;
        boolean cWasNull = (c == null);
        try {

            if (cWasNull) {
                c = DriverManager.getConnection(url, user, password);
            }

            //street_name, street_number, apartment_number, floor, zip_code, city, country, id
            String sql = "INSERT INTO addresses VALUES (?, ?, ?, ?, ?, ?, ?, null);";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, address.getStreetName());
            ps.setInt(2, address.getStreetNumber());
            ps.setInt(3, address.getApartmentNumber());
            ps.setString(4, address.getFloor());
            ps.setString(5, address.getZipCode());
            ps.setString(6, address.getCity());
            ps.setString(7, address.getCountry());
            ps.executeUpdate();
            ResultSet rs = c.createStatement().executeQuery("SELECT max(id) FROM addresses;");
            rs.next();
            toReurn = rs.getInt(1);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

        } finally {
            if (cWasNull && c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                }
                ;
            }
            return toReurn;
        }

    }


    public int insertCompany(Company c, Connection con) {

        int toReturn = 0;
        boolean conWasNull = false;
        if (con == null) conWasNull = true;
        try {
            if (conWasNull) {
                con = DriverManager.getConnection(url, user, password);
            }
            String sql = "INSERT INTO companies VALUE (null, ?, ?);";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, c.getName());
            ps.setInt(2, insertAddress(c.getAddress(), con));
            ps.executeUpdate();
            ResultSet rs = con.createStatement().
                    executeQuery("SELECT max(company_id) FROM companies;");
            rs.next();
            toReturn = rs.getInt(1);
        } catch (SQLException e) {
            e.getMessage();
        } finally {
            if (conWasNull && con != null) {
                try {
                    con.close();
                } catch (Exception e2) {
                }
            }
            return toReturn;
        }

    }


    public void writeToDatabse(List<User> userList) {
        try {
            for (User u : userList) {
                //null, first_name, last_name, email, age, job_title, is_favorite, address_id, company_id
                String sql = "INSERT INTO users VALUES (null, ?, ?, ?, ?, ?, ?, ?,?);";
                PreparedStatement ps = c.prepareStatement(sql);
                ps.setString(1, u.getFirstName());
                ps.setString(2, u.getLastName());
                ps.setString(3, u.getEmail());
                ps.setInt(4, u.getAge());
                ps.setString(5, u.getJobTitle());
                int intBoolean = u.isFavorite() ? 1 : 0;
                ps.setInt(6, intBoolean);
                ps.setInt(7, insertAddress(u.getAddress(), c));
                ps.setInt(8, insertCompany(u.getCompany(), c));
                ps.executeUpdate();
                sql = "SELECT max(user_id) FROM users;";
                ResultSet rs = c.createStatement().executeQuery(sql);
                rs.next();
                int addedUserID = rs.getInt(1);
                for (Map.Entry<String, PhoneNumber> me : u.getPhoneNumbers().entrySet()) {
                    sql = "INSERT INTO phonenumbers VALUES (null, ?, ?, ?, ?);";
                    ps = c.prepareStatement(sql);
                    ps.setInt(1, addedUserID);
                    ps.setString(2, me.getValue().getCountryCode());
                    ps.setString(3, me.getValue().getNumber());
                    ps.setString(4, me.getKey());
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<User> getContacts() {


        if (contacts == null) {
            contacts = new ArrayList<>();
            try {
                Statement si = c.createStatement();
                String sql = "SELECT user_id FROM users;";
                ResultSet rs = si.executeQuery(sql);
                List<Integer> uidList = new ArrayList<>();
                while (rs.next()) {
                    uidList.add(rs.getInt(1));
                }

                for (Integer i : uidList) {
                    Statement s = c.createStatement();
                    sql = "SELECT * FROM users WHERE user_id=" + i + ";";
                    rs = s.executeQuery(sql);
                    rs.next();
                    User u = new User();
                    u.setUserId(rs.getInt(1));
                    u.setFirstName(rs.getString(2));
                    u.setLastName(rs.getString(3));
                    u.setEmail(rs.getString(4));
                    u.setAge(rs.getInt(5));
                    u.setJobTitle(rs.getString(6));
                    boolean isFavorite = (rs.getInt(7) == 1) ? true : false;
                    u.setFavorite(isFavorite);

                    //add addresses
                    Address userAddress = readAddressFromDB(rs.getInt(8), c.createStatement());
                    u.setAddress(userAddress);
                    sql = "SELECT * FROM companies WHERE company_id = "
                            + rs.getInt(9) + ";";
                    Statement s2 = c.createStatement();
                    ResultSet rs2 = s2.executeQuery(sql);
                    rs2.next();
                    Company userCompany = new Company();
                    userCompany.setName(rs2.getString(2));
                    userCompany.setAddress(readAddressFromDB(rs2.
                            getInt(3), s));
                    u.setCompany(userCompany);

                    //adding phonenumbers
                    sql = "SELECT * FROM phonenumbers WHERE user_id=" + u.getUserId() + ";";
                    Statement s3 = c.createStatement();
                    ResultSet rs3 = s3.executeQuery(sql);
                    Map<String, PhoneNumber> map = new HashMap<>();

                    while (rs3.next()) {

                        PhoneNumber ph = new PhoneNumber(rs3.getString(3),
                                rs3.getString(4));
                        map.put(rs3.getString(5), ph);
                    }
                    u.setPhoneNumbers(map);
                    contacts.add(u);


                }

            } catch (SQLException sqe) {
                System.out.println(sqe.getMessage());
            }
        }

        return contacts;
    }

    private Address readAddressFromDB(int addressID, Statement s) {

        Address a = new Address();
        try {
            String sql = "SELECT * FROM addresses WHERE id =" + addressID;
            ResultSet rs = s.executeQuery(sql);
            rs.next();
            a.setStreetName(rs.getString(1));
            a.setStreetNumber(rs.getInt(2));
            a.setApartmentNumber(rs.getInt(3));
            a.setFloor(rs.getString(4));
            a.setZipCode(rs.getString(5));
            a.setCity(rs.getString(6));
            a.setCountry(rs.getString(7));


        } catch (SQLException sq) {
            System.out.println(sq.getMessage());
        }

        return a;
    }

    @Override
    public void editContact(User contact) {

       /* removeContact(contact);
        addContact(contact);*/
       contacts.removeIf(u-> u.getUserId()==contact.getUserId());
       contacts.add(contact);

        int userId = contact.getUserId();
        Address userAddress = contact.getAddress();
        Company company = contact.getCompany();
        Address companyAddress = contact.getCompany().getAddress();

        try {

            String getIDSSql = "SELECT u.address_id, u.company_id, companies.address_id " +
                    "AS cadid FROM users u INNER JOIN companies " +
                    "ON u.company_id = companies.company_id WHERE u.user_id=" + userId + ";";

            ResultSet rs = c.createStatement().executeQuery(getIDSSql);
            rs.next();
            int addressId = rs.getInt(1);
            int companyId = rs.getInt(2);
            int companyAddressId = rs.getInt(3);

            String sql = "UPDATE users SET first_name=?, " +
                    "last_name=?, email=?, age=?, job_title=?, is_favorite=?" +
                    " WHERE user_id=?;";

            c.setAutoCommit(false);
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, contact.getFirstName());
            ps.setString(2, contact.getLastName());
            ps.setString(3, contact.getEmail());
            ps.setInt(4, contact.getAge());
            ps.setString(5, contact.getJobTitle());
            int isF = (contact.isFavorite()) ? 1 : 0;
            ps.setInt(6, isF);
            ps.setInt(7, userId);
            ps.execute();

            String userAddressSql = "UPDATE addresses SET street_name =? , street_number=?" +
                    ", apartment_number=?, floor=?, zip_code=?, city=?, country=? where" +
                    " id=" + addressId + ";";

            ps = c.prepareStatement(userAddressSql);
            ps.setString(1, userAddress.getStreetName());
            ps.setInt(2, userAddress.getStreetNumber());
            ps.setInt(3, userAddress.getApartmentNumber());
            ps.setString(4, userAddress.getFloor());
            ps.setString(5, userAddress.getZipCode());
            ps.setString(6, userAddress.getCity());
            ps.setString(7, userAddress.getCountry());
            ps.execute();

            String companyAddressSql = "UPDATE addresses SET street_name =? , street_number=?" +
                    ", apartment_number=?, floor=?, zip_code=?, city=?, country=? where" +
                    " id=" + companyAddressId + ";";

            ps = c.prepareStatement(companyAddressSql);
            ps.setString(1, companyAddress.getStreetName());
            ps.setInt(2, companyAddress.getStreetNumber());
            ps.setInt(3, companyAddress.getApartmentNumber());
            ps.setString(4, companyAddress.getFloor());
            ps.setString(5, companyAddress.getZipCode());
            ps.setString(6, companyAddress.getCity());
            ps.setString(7, companyAddress.getCountry());
            ps.execute();

            String companySql = "UPDATE companies SET company_name ='" + contact.getCompany().getName() + "' WHERE" +
                    " company_id='" + companyId + "';";

            //edit phonenumbers

            Statement s = c.createStatement();
            ResultSet rs2 = c.
                    createStatement().
                    executeQuery("SELECT id FROM phonenumbers WHERE user_id="+userId+";");

            List<Integer> idsList = new ArrayList<>();
            while(rs2.next()) {
                idsList.add(rs2.getInt(1));
            }
            s.addBatch("DELETE FROM phonenumbers WHERE user_id="+userId+";");
            int count = 1;

            for(Map.Entry<String, PhoneNumber> me : contact.getPhoneNumbers().entrySet())
            {
                String sqlIdPh;
                if(count <= idsList.size()) sqlIdPh ="'" + String.valueOf(idsList.get(count-1)) +"'";
                else sqlIdPh = "null";
                String phSQL = "INSERT INTO phonenumbers VALUES ("+sqlIdPh
                        +", '"+userId
                        +"', '"+me.getValue().getCountryCode()+"', '"+
                        me.getValue().getNumber()+"', '"+me.getKey()
                        +"');";

                s.addBatch(phSQL);
                count++;
            }

            s.executeBatch();
            c.createStatement().executeUpdate(companySql);

            c.commit();


        } catch (SQLException e) {
            try {
                c.rollback();
            } catch (SQLException ex) {
                System.out.println("cannot rollback : " + ex.getMessage());
            }
            System.out.println(e.getMessage() + " PROBLEM AT EDIT");
        }
        try {
            c.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("cannot set autocommit to true" + e.getMessage());
        }

    }

    @Override
    public User getContact(int userId) {
        Optional<User> optionalUser = contacts.stream().filter(u -> u.getUserId() == userId).findFirst();
        if (optionalUser.isPresent()) return optionalUser.get();
        else return null;
    }

    @Override
    public void editContact(int userId) {
        //not needed
    }

    @Override
    public void removeContact(int userId) {


        contacts.removeIf(u -> u.getUserId() == userId);
        try {
            Statement s = c.createStatement();
            String sql = "SELECT u.address_id, u.company_id, companies.address_id AS cadid " +
                    "FROM users u INNER JOIN companies " +
                    "ON u.company_id = companies.company_id " +
                    "WHERE u.user_id ="
                    + userId + ";";

            ResultSet rs = s.executeQuery(sql);
            rs.next();

            int companyID = rs.getInt(2), addressID = rs.getInt(1), cadID = rs.getInt(3);

            s.clearBatch();
            s.addBatch("DELETE FROM phonenumbers WHERE user_id = " + userId + ";");
            s.addBatch("DELETE FROM users WHERE user_id =" + userId + ";");
            s.addBatch("DELETE FROM companies WHERE company_id=" + companyID + ";");
            s.addBatch("DELETE FROM addresses WHERE id=" + cadID + ";");
            s.addBatch("DELETE FROM addresses WHERE id=" + addressID + ";");
            s.executeBatch();

        } catch (SQLException e) {
            System.out.println(e.getMessage() + "");
        }
    }

    @Override
    public void addContact(User contact) {

        writeToDatabse(List.of(contact));
        int userId = 0;
        try {
            String sql = "SELECT max(user_id) FROM users";
            ResultSet rs = c.createStatement().executeQuery(sql);
            rs.next();
            userId = rs.getInt(1);
        } catch (SQLException e) {
            e.getMessage();
        }
        ;
        contact.setUserId(userId);
        contacts.add(contact);
    }

    @Override
    public void removeContact(User contact) {
        removeContact(contact.getUserId());
    }

    @Override
    public List<User> search(String query) {
        return null;
    }

    public void closeConnection(){
        try{
            c.close();
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
