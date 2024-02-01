package DataBase;

public class Colour {
    private final int ID;
    private final String NAME;

    public Colour(int id, String name) {
        this.ID = id;
        this.NAME = name;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return NAME;
    }

}
