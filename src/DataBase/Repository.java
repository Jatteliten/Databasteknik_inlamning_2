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
    private final Properties p = new Properties();

    private Repository() throws IOException {}

    public static Repository getRepository() throws IOException {
        if(repository == null){
            repository = new Repository();
        }
        return repository;
    }

    protected ArrayList<City> loadCities() throws IOException {
        ArrayList<City> tempList = new ArrayList<>();
        p.load(new FileInputStream("src/Settings.properties"));
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
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

    protected ArrayList<Customer> loadCustomers(ArrayList<City> cities) throws IOException {
        ArrayList<Customer> tempList = new ArrayList<>();
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
                tempList.add(newCustomer);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tempList;
    }

    protected ArrayList<Colour> loadColours() throws IOException {
        ArrayList<Colour> tempList = new ArrayList<>();
        p.load(new FileInputStream("src/Settings.properties"));
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
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

    protected ArrayList<Category> loadCategories() throws IOException {
        ArrayList<Category> tempList = new ArrayList<>();
        p.load(new FileInputStream("src/Settings.properties"));
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
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

    protected ArrayList<Shoe> loadShoes(ArrayList<Colour> colours) throws IOException {
        ArrayList<Shoe> tempList = new ArrayList<>();
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
                tempList.add(newShoe);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tempList;
    }

    protected void assignShoeCategories(ArrayList<Shoe> shoes, ArrayList<Category> categories) throws IOException {
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
