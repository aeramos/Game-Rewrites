import java.util.ArrayList;

public class Player extends Party {
    public static final int MAX_ITEMS = 20;
    public static final int MAX_HEALTH = 100;

    public City location;
    public final ArrayList<Item> inventory;
    public Item.Weapon weapon;
    public Item.Armor armor;
    public int money;

    public Player(String name, City location, int money) {
        super(name, 1);
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

    @Override
    public int getAttack() {
        if (weapon != null) {
            return weapon.getDamage() + 1;
        } else {
            return 10;
        }
    }

    @Override
    public int getDefense() {
        if (armor != null) {
            return armor.getArmor() + 1;
        } else {
            return 1;
        }
    }
}
