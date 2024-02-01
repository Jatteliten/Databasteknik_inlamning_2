package DataBase;

import java.io.IOException;
import java.util.ArrayList;

public class Data {
    private static Data data;
    private final ArrayList<City> CITIES;
    private final ArrayList<Customer> CUSTOMERS;
    private final ArrayList<Colour> COLOURS;
    private final ArrayList<Category> CATEGORIES;
    private final ArrayList<Shoe> SHOES;
    private Customer activeCustomer;

    private Data() throws IOException {
        CITIES = Repository.getRepository().loadCities();
        CUSTOMERS =  Repository.getRepository().loadCustomers(CITIES);
        COLOURS = Repository.getRepository().loadColours();
        CATEGORIES = Repository.getRepository().loadCategories();
        SHOES = Repository.getRepository().loadShoes(COLOURS);
        Repository.getRepository().assignShoeCategories(SHOES, CATEGORIES);
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

    public ArrayList<City> getCITIES() {
        return CITIES;
    }

    public ArrayList<Customer> getCUSTOMERS() {
        return CUSTOMERS;
    }

    public ArrayList<Colour> getCOLOURS() {
        return COLOURS;
    }

    public ArrayList<Category> getCATEGORIES() {
        return CATEGORIES;
    }

    public ArrayList<Shoe> getSHOES() {
        return SHOES;
    }

}
