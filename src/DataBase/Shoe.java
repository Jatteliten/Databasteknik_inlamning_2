package DataBase;

import java.util.ArrayList;

public class Shoe {
    int id;
    String brand;
    int price;
    Colour colour;
    int size;
    int stock;
    ArrayList<Category> categories = new ArrayList<>();

    public Shoe(int id, String brand, int price, int size, int stock) {
        this.id = id;
        this.brand = brand;
        this.price = price;
        this.size = size;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public int getPrice() {
        return price;
    }

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    public int getSize() {
        return size;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void addToCategories(Category c){
        categories.add(c);
    }
}
