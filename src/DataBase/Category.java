package DataBase;

public class Category {
    private final int ID;
    private final String NAME;

    public Category(int id, String name) {
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
