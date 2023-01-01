import java.util.List;
import java.util.Random;

/**
 * Handles game logic and stores game information. The game is driven by the UI since it waits for
 * user input before doing anything.
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
                travelData = new TravelData(destination, totalHours, 1, getTravelEvent(player.location, destination, progress(0, totalHours)));
            } else {
                travelData = new TravelData(travelData.destination(), travelData.totalHours(), travelData.hoursTravelled() + 1, getTravelEvent(player.location, travelData.destination(), progress(0, travelData.totalHours())));
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

    public void changePrices() {
        for (City city : cities) {
            city.changePrices();
        }
    }

    // determine net worth from standard prices of inventory and equipment
    public int score() {
        return -1;
    }

    /**
     * Returns a TravelEvent that accounts for the player's position between two cities. For example, a route will
     * become more dangerous as the player approaches a dangerous city from a safe one, causing more bandits to appear.
     *
     * @param origin      the city the player starts traveling from
     * @param destination the city the player will arrive at
     * @param progress    how close the player is to the destination, as a percentage (from 0 to 100)
     * @return a travel event corresponding with the player's position between the two cities
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
