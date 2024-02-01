package DataBase;

import java.util.ArrayList;

public class Shoe {
    private final int ID;
    private final String BRAND;
    private final int PRICE;
    private final int SIZE;
    private final ArrayList<Category> CATEGORIES = new ArrayList<>();
    private Colour colour;
    private int stock;

    public Shoe(int id, String brand, int price, int size, int stock) {
        this.ID = id;
        this.BRAND = brand;
        this.PRICE = price;
        this.SIZE = size;
        this.stock = stock;
    }

    public int getID() {
        return ID;
    }

    public String getBRAND() {
        return BRAND;
    }

    public int getPRICE() {
        return PRICE;
    }

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    public int getSIZE() {
        return SIZE;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    public ArrayList<Category> getCATEGORIES() {
        return CATEGORIES;
    }

    public void addToCategories(Category c){
        CATEGORIES.add(c);
    }
}
