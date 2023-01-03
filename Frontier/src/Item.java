/**
 * An Item object represents anything that can be bought and sold in the game. This interface is
 * meant to be implemented by enums that specify different item types, like a {@link Weapon} with
 * damage values.
 */
public interface Item {
    String getName();
    int getStandardPrice();

    enum Material implements Item {
        CLOTH(0, "Cloth", 15, false), IRON(1, "Iron", 100, false), SILVER(2, "Silver", 2750, false), SPIRITS(3, "Spirits", 350, true);

        /**
         * The total number of different <code>Material</code>s
         */
        public static final int NUMBER = 4;

        private final int id;
        private final String name;
        private final int standardPrice;
        private final boolean blackMarket;

        Material(int id, String name, int standardPrice, boolean blackMarket) {
            this.id = id;
            this.name = name;
            this.standardPrice = standardPrice;
            this.blackMarket = blackMarket;
        }

        public int getID() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getStandardPrice() {
            return standardPrice;
        }

        public boolean isBlackMarket() {
            return blackMarket;
        }
    }

    enum Weapon implements Item {
        STICK("Stick", 1, 1);

        private final String name;
        private final int standardPrice;
        private final int damage;

        Weapon(String name, int standardPrice, int damage) {
            this.name = name;
            this.standardPrice = standardPrice;
            this.damage = damage;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getStandardPrice() {
            return standardPrice;
        }

        public int getDamage() {
            return damage;
        }
    }

    enum Armor implements Item {
        CLOTH("Cloth", 1, 1);

        private final String name;
        private final int standardPrice;
        private final int armor;

        Armor(String name, int standardPrice, int armor) {
            this.name = name;
            this.standardPrice = standardPrice;
            this.armor = armor;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getStandardPrice() {
            return standardPrice;
        }

        public int getArmor() {
            return armor;
        }
    }
}
