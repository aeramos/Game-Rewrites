import java.util.ArrayList;

public class Player {
    public static final int MAX_ITEMS = 20;
    public static final int MAX_HEALTH = 100;

    public String name;
    public City location;
    public final ArrayList<Item> inventory;
    public Item.Weapon weapon;
    public Item.Armor armor;
    public int money;

    public void attack(int damage) {

    }

    public Player(String name, City location, int money) {
        this.name = name;
        this.location = location;
        this.inventory = new ArrayList<>();
        this.money = money;
    }

    public int getRange() {
        // if no vehicle then:
        return 50;
    }

    public int freeSpace() {
        // if no vehicle then:
        return MAX_ITEMS - inventory.size();
    }
}
