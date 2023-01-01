// if damage is -1 that means the enemy did not attack. this only happens on the initial encounter
public record CombatData(NPCParty enemy, int damage) {}
