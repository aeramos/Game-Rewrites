import java.util.List;
import java.util.Random;

/**
 * A Game object holds and controls all game state data including information about players, enemies,
 * and cities. The game is driven by the UI in that it only advances when the UI interacts with it,
 * but all processing and game logic is handled here.
 */
public class Game {
    public final Player player;
    public City destination;
    public final List<City> cities;
    private final Random random = new Random();
    private TravelData travelData = null;
    private Record lastState = null;
    private NPCParty enemy = null;

    public enum TravelEvent {
        ENEMY, LOOT, NOTHING
    }

    public Game(String playerName) {
        cities = City.generateCities();
        player = new Player(playerName, cities.get(0), 1000);
    }

    /**
     * Advances the current state and returns the new data associated with the current player
     * activity: travel, combat, or none.
     *
     * @return the information for the current "activity" in the form of {@link TravelData} or
     * {@link CombatData}, or null if the player is not traveling.
     */
    public Record getState() {
        if (enemy != null) {
            if (lastState == travelData) {
                return lastState = new CombatData(enemy, -1);
            } else {
                return lastState = new CombatData(enemy, Party.combat(enemy, player));
            }
        } else if (destination != null) {
            if (travelData == null) {
                int totalHours = ((int)(player.location.distanceTo(destination) / 25) + 1) * 24;
                travelData = new TravelData(destination, 1, totalHours, getTravelEvent(player.location, destination, progress(0, totalHours)));
            } else {
                travelData = new TravelData(travelData.destination(), travelData.hoursTravelled() + 1, travelData.totalHours(), getTravelEvent(player.location, travelData.destination(), progress(0, travelData.totalHours())));
                if (travelData.hoursTravelled() == travelData.totalHours()) {
                    player.location = destination;
                    destination = null;

                    // they are now in a city so heal player and change prices
                    player.health = Player.MAX_HEALTH;
                    changePrices();
                }
            }
            return lastState = travelData;
        } else {
            return lastState = travelData = null;
        }
    }

    public int attack() {
        int damage = Party.combat(player, enemy);
        if (enemy.health == 0) {
            enemy = null;
        }
        return damage;
    }

    public boolean flee() {
        if (random.nextBoolean()) {
            enemy = null;
            return true;
        } else {
            return false;
        }
    }

    public int loot() {
        int loot = loot(player.location, destination, progress(travelData.hoursTravelled(), travelData.totalHours()));
        player.money += loot;
        return loot;
    }

    public void buy(Item.Material item) {
        int price = player.location.getPrice(item.getID());
        player.money -= price;
        player.inventory.add(item);
    }

    public int sell(int index) {
        int price;
        if (player.inventory.get(index) instanceof Item.Material) {
            price = player.location.getPrice(((Item.Material)player.inventory.get(index)).getID());
        } else {
            price = player.inventory.get(index).getStandardPrice();
        }
        player.inventory.remove(index);
        player.money += price;
        return price;
    }

    /**
     * @return total player net worth determined by held coins and standard prices of inventory,
     * weapons, and armor.
     */
    public int score() {
        int score = player.money;
        for (int i = 0; i < player.inventory.size(); i++) {
            score += player.inventory.get(i).getStandardPrice();
        }
        if (player.weapon != null) {
            score += player.weapon.getStandardPrice();
        }
        if (player.armor != null) {
            score += player.armor.getStandardPrice();
        }
        return score;
    }

    private void changePrices() {
        for (City city : cities) {
            city.changePrices();
        }
    }

    /**
     * Returns a TravelEvent that accounts for the player's position between two cities. For example, a route will
     * become more dangerous as the player approaches a dangerous city from a safe one, causing more bandits to appear.
     *
     * @param origin      the city the player starts traveling from
     * @param destination the city the player will arrive at
     * @param progress    how close the player is to the destination as a percentage from 0 to 100
     * @return a travel event indicating if the player encountered an enemy, loot, or nothing
     */
    private TravelEvent getTravelEvent(City origin, City destination, byte progress) {
        double danger = travelingAverage(origin.danger, destination.danger, progress);

        int randomValue = random.nextInt(100);
        if (randomValue < 10) {
            if (random.nextInt(101) <= danger) {
                enemy = new NPCParty("Bandit", 1, 5, 2);
                return TravelEvent.ENEMY;
            }
        } else if (randomValue == 99) {
            return TravelEvent.LOOT;
        }
        return TravelEvent.NOTHING;
    }

    private byte progress(int currentHour, int totalHours) {
        return (byte)(((double)currentHour / totalHours) * 100);
    }

    private double travelingAverage(int origin, int destination, byte progress) {
        return (origin * ((100 - progress) / 100d)) + (destination * ((progress / 100d)));
    }

    private int loot(City origin, City destination, byte progress) {
        return (int)(250 * travelingAverage(origin.wealth, destination.wealth, progress) / 100);
    }
}
