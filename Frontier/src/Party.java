public abstract class Party {
    public final String name;
    public final int numberOfMembers;
    public int health;

    public static int combat(Party attacker, Party defender) {
        int difference = attacker.getAttack() - defender.getDefense();
        if (difference > 0) {
            defender.health = defender.health - difference;
            if (defender.health < 0) {
                defender.health = 0;
            }
            return difference;
        }
        return 0;
    }

    public Party(String name, int numberOfMembers) {
        this.name = name;
        this.numberOfMembers = numberOfMembers;
        this.health = 100;
    }

    public abstract int getAttack();
    public abstract int getDefense();
}
