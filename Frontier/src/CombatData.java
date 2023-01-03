/**
 * Data that is sent from {@link Game#getState()} to the UI when the player is currently fighting.
 *
 * @param enemy the enemy the player is currently fighting. This is never null.
 * @param damage the amount of damage the enemy dealt to the player in their last attack. -1 when
 *               the enemy did not attack (only happens on the initial encounter).
 */
public record CombatData(NPCParty enemy, int damage) {}
