package DataBase;

import java.io.IOException;
import java.util.ArrayList;

public class Data {
    private static Data data;
    private final ArrayList<City> cities;
    private final ArrayList<Customer> customers;
    private final ArrayList<Colour> colours;
    private final ArrayList<Category> categories;
    private ArrayList<Shoe> shoes;
    private Customer activeCustomer;

    private Data() throws IOException {
        cities = Repository.getRepository().loadCities();
        customers =  Repository.getRepository().loadCustomers(cities);
        colours = Repository.getRepository().loadColours();
        categories = Repository.getRepository().loadCategories();
        shoes = Repository.getRepository().loadShoes(colours);
        Repository.getRepository().assignShoeCategories(shoes, categories);
    }

    public static Data getData() throws IOException {
        if(data == null){
            data = new Data();
        }
        return data;
    }

    public Customer getActiveCustomer() {
        return activeCustomer;
    }

    public void setActiveCustomer(Customer activeCustomer) {
        this.activeCustomer = activeCustomer;
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

    public void reloadShoes() throws IOException {
        shoes.clear();
        shoes = Repository.getRepository().loadShoes(colours);
    }

}