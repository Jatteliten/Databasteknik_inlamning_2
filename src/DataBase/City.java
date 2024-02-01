package DataBase;

public class City {
    private final int ID;
    private final String NAME;

    public City(int id, String name) {
        this.ID = id;
        this.NAME = name;
    }

    public int getID() {
        return ID;
    }

    public String getNAME() {
        return NAME;
    }

}
