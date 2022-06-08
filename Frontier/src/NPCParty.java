public final class NPCParty extends Party {
    private final int attack;
    private final int defense;

    public NPCParty(String name, int numberOfMembers, int attack, int defense) {
        super(name, numberOfMembers);

        this.attack = attack;
        this.defense = defense;
        this.health = 100;
    }

    @Override
    public int getAttack() {
        return attack;
    }

    @Override
    public int getDefense() {
        return defense;
    }
}
