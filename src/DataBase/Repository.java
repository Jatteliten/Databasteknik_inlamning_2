package DataBase;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class Repository {
    private static Repository repository;
    private ArrayList<City> cities;
    private ArrayList<Customer> customers;
    private ArrayList<Colour> colours;
    private ArrayList<Category> categories;
    private ArrayList<Shoe> shoes;
    private final Properties p = new Properties();

    private Repository() throws IOException {
        loadCities();
        loadCustomers();
        loadColours();
        loadCategories();
        loadShoes();
        assignShoeCategories();
    }

    public static Repository getRepository() throws IOException {
        if(repository == null){
            repository = new Repository();
        }
        return repository;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<Colour> getColours() {
        return colours;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public ArrayList<Shoe> getShoes() {
        return shoes;
    }

    private void loadCities() throws IOException {
        cities = new ArrayList<>();
        p.load(new FileInputStream("src/Settings.properties"));
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select id, name from cities")) {

            while(rs.next()){
                cities.add(new City(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadCustomers() throws IOException {
        customers = new ArrayList<>();
        p.load(new FileInputStream("src/Settings.properties"));
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select id, name, cityId, password from customers")) {

            while(rs.next()){
                Customer newCustomer = new Customer(rs.getInt("id"), rs.getString("name"),
                        rs.getString("password"));
                for(City c: cities){
                    if(c.getId() == rs.getInt("cityId")){
                        newCustomer.setCity(c);
                        break;
                    }
                }
                customers.add(newCustomer);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadColours() throws IOException {
        colours = new ArrayList<>();
        p.load(new FileInputStream("src/Settings.properties"));
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select id, name from colours")) {

            while(rs.next()){
                colours.add(new Colour(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCategories() throws IOException {
        categories = new ArrayList<>();
        p.load(new FileInputStream("src/Settings.properties"));
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select id, name from categories")) {

            while(rs.next()){
                categories.add(new Category(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadShoes() throws IOException {
        shoes = new ArrayList<>();
        p.load(new FileInputStream("src/Settings.properties"));
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select id, brand, price, colourID, size, stock from shoes")) {

            while(rs.next()){
                Shoe newShoe = new Shoe(rs.getInt("id"), rs.getString("brand"),
                        rs.getInt("price"), rs.getInt("size"), rs.getInt("stock"));
                for(Colour c: colours){
                    if(c.getId() == rs.getInt("colourId")){
                        newShoe.setColour(c);
                        break;
                    }
                }
                shoes.add(newShoe);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        assignShoeCategories();
    }

    private void assignShoeCategories() throws IOException {
        p.load(new FileInputStream("src/Settings.properties"));
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select shoeID, categoryID from shoecategory")) {

            while(rs.next()){
                boolean categoryFound = false;
                for(Shoe s: shoes){
                    if (s.getId() == rs.getInt("shoeID")){
                        for(Category c: categories){
                            if(c.getId() == rs.getInt("categoryID")){
                                s.addToCategories(c);
                                categoryFound = true;
                                break;
                            }
                        }
                    }
                    if (categoryFound){
                        break;
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
