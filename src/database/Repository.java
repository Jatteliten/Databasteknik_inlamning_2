package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Properties;

public class Repository {
    private static Repository repository;
    private static final int NULL_ORDER = -1;
    private static final String PROPERTIES_FILE_PATH = "src/Settings.properties";
    private static final String PROPERTIES_CONNECTION_STRING = "connectionString";
    private static final String PROPERTIES_USER_NAME = "name";
    private static final String PROPERTIES_PASSWORD = "password";
    private final Properties P = new Properties();

    private Repository() {
        try(FileInputStream f = new FileInputStream(PROPERTIES_FILE_PATH)){
            P.load(f);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public synchronized static Repository getRepository() {
        if(repository == null){
            repository = new Repository();
        }
        return repository;
    }

    protected ArrayList<City> loadCities() {
        ArrayList<City> tempList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(
                P.getProperty(PROPERTIES_CONNECTION_STRING),
                P.getProperty(PROPERTIES_USER_NAME),
                P.getProperty(PROPERTIES_PASSWORD));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select id, name from cities")) {

            while(rs.next()){
                tempList.add(new City(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tempList;
    }

    protected ArrayList<Customer> loadCustomers(ArrayList<City> cities) {
        ArrayList<Customer> tempList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(
                P.getProperty(PROPERTIES_CONNECTION_STRING),
                P.getProperty(PROPERTIES_USER_NAME),
                P.getProperty(PROPERTIES_PASSWORD));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select id, name, cityId from customers")) {

            while(rs.next()){
                Customer newCustomer = new Customer(rs.getInt("id"), rs.getString("name"));
                for(City c: cities){
                    if(c.id() == rs.getInt("cityId")){
                        newCustomer.setCity(c);
                        break;
                    }
                }
                tempList.add(newCustomer);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tempList;
    }

    protected ArrayList<Colour> loadColours() {
        ArrayList<Colour> tempList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(
                P.getProperty(PROPERTIES_CONNECTION_STRING),
                P.getProperty(PROPERTIES_USER_NAME),
                P.getProperty(PROPERTIES_PASSWORD));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select id, name from colours")) {

            while(rs.next()){
                tempList.add(new Colour(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tempList;
    }

    protected ArrayList<Category> loadCategories() {
        ArrayList<Category> tempList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(
                P.getProperty(PROPERTIES_CONNECTION_STRING),
                P.getProperty(PROPERTIES_USER_NAME),
                P.getProperty(PROPERTIES_PASSWORD));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select id, name from categories")) {

            while(rs.next()){
                tempList.add(new Category(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tempList;
    }

    protected ArrayList<Shoe> loadShoes(ArrayList<Colour> colours) {
        ArrayList<Shoe> tempList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(
                P.getProperty(PROPERTIES_CONNECTION_STRING),
                P.getProperty(PROPERTIES_USER_NAME),
                P.getProperty(PROPERTIES_PASSWORD));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select id, brand, price, colourID, size, stock from shoes")) {

            while(rs.next()){
                Shoe newShoe = new Shoe(rs.getInt("id"), rs.getString("brand"),
                        rs.getInt("price"), rs.getInt("size"), rs.getInt("stock"));
                for(Colour c: colours){
                    if(c.id() == rs.getInt("colourId")){
                        newShoe.setColour(c);
                        break;
                    }
                }
                tempList.add(newShoe);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tempList;
    }

    protected void assignShoeCategories(ArrayList<Shoe> shoes, ArrayList<Category> categories) {
        try (Connection con = DriverManager.getConnection(
                P.getProperty(PROPERTIES_CONNECTION_STRING),
                P.getProperty(PROPERTIES_USER_NAME),
                P.getProperty(PROPERTIES_PASSWORD));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select shoeID, categoryID from shoecategory")) {

            while(rs.next()){
                int shoeIDCheck = rs.getInt("shoeID");
                int categoryIdCheck = rs.getInt("categoryID");

                shoes.stream().filter(s -> s.getId() == shoeIDCheck).findFirst()
                        .ifPresent(s -> categories.stream().filter(c -> c.id() == categoryIdCheck)
                                .findFirst().ifPresent(s::addToCategories));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean findPassword(Customer customer, String password) {
        try (Connection con = DriverManager.getConnection(
                P.getProperty(PROPERTIES_CONNECTION_STRING),
                P.getProperty(PROPERTIES_USER_NAME),
                P.getProperty(PROPERTIES_PASSWORD));
                PreparedStatement stmt = con.prepareStatement("select customers.password from customers where id = ?")) {
            stmt.setInt(1, customer.getId());

            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password").equals(password);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public int placeOrder(Customer customer, int orderNumber, Shoe shoe) {
        int orderNumberOut;
        try(Connection con = DriverManager.getConnection(
                P.getProperty(PROPERTIES_CONNECTION_STRING),
                P.getProperty(PROPERTIES_USER_NAME),
                P.getProperty(PROPERTIES_PASSWORD))){
            CallableStatement stmt = con.prepareCall("CALL addToCart(?, ?, ?)");
            stmt.setInt(1, customer.getId());
            stmt.registerOutParameter(2, Types.INTEGER);
            if(orderNumber != NULL_ORDER) {
                stmt.setInt(2, orderNumber);
            }
            stmt.setInt(3, shoe.getId());
            stmt.execute();
            orderNumberOut = stmt.getInt(2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        shoe.setStock(shoe.getStock() - 1);
        return orderNumberOut;
    }

}