package DataBase;

public class Customer {
    private final int ID;
    private final String NAME;
    private City city;

    public Customer(int id, String name) {
        this.ID = id;
        this.NAME = name;
    }

    public int getID() {
        return ID;
    }

    public String getNAME() {
        return NAME;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

}
