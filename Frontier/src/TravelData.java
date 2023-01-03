/**
 * Data that is sent from {@link Game#getState()} to the UI when the player is currently traveling
 * and not fighting.
 *
 * @param destination the city that the player is traveling to. This is never null.
 * @param hoursTravelled the current hour the player is on in the journey. Begins at 1, ends at
 *                       <code>totalHours</code>.
 * @param totalHours the total number of hours to get from the player's current location to the
 *                   destination. Is always a positive multiple of 24.
 * @param travelEvent an encounter that the player faced on this hour of travel
 */
public record TravelData(City destination, int hoursTravelled, int totalHours, Game.TravelEvent travelEvent) {}
