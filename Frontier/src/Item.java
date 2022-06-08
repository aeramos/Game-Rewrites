public interface Item {

    String getName();
    int getStandardPrice();

    enum Material implements Item {
        CLOTH(0, "Cloth", 15), IRON(1, "Iron", 100), SILVER(2, "Silver", 2750);

        public static final int NUMBER = 3  ;
        private final int id;
        private final String name;
        private final int standardPrice;

        Material(int id, String name, int standardPrice) {
            this.id = id;
            this.name = name;
            this.standardPrice = standardPrice;
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
