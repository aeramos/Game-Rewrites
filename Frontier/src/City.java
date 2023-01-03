import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * A City object contains all information about a city including its name, location, wealth,
 * danger level, and prices.
 */
public class City {
    public static List<City> generateCities() {
        ArrayList<City> cities = new ArrayList<>(6);
        cities.add(new City("Alpha", 90, 80, (byte)90, (byte)10));
        cities.add(new City("Bravo", 100, 100, (byte)85, (byte)50));
        cities.add(new City("Charlie", 60, 80, (byte)60, (byte)100));
        cities.add(new City("Delta", 70, 60, (byte)65, (byte)20));
        cities.add(new City("Echo", 0, 0, (byte)20, (byte)40));
        cities.add(new City("Foxtrot", 50, 85, (byte)80, (byte)80));
        return cities;
    }

    public final String name;
    public final int coordinateX;
    public final int coordinateY;
    public final byte wealth;
    public final byte danger;

    public final int[] prices;

    private final Random random = new Random();

    /**
     * @param name the displayed name of the city
     * @param coordinateX the x coordinate with left being 0
     * @param coordinateY the y coordinate with down being 0
     * @param wealth the wealth percentage level to determine the number of abandoned crates
     * @param danger the danger percentage level to determine the number of bandits
     */
    public City(String name, int coordinateX, int coordinateY, byte wealth, byte danger) {
        this.name = name;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.wealth = wealth;
        this.danger = danger;

        this.prices = new int[Item.Material.NUMBER];
        Arrays.fill(prices, 100);
    }

    @Override
    public String toString() {
        return name + " (" + coordinateX + ", " + coordinateY + ")";
    }

    public List<City> destinations(List<City> options, int range) {
        ArrayList<City> destinations = new ArrayList<>(0);
        for (City city : options) {
            if (city != this && this.distanceTo(city) <= range) {
                destinations.add(city);
            }
        }
        return destinations;
    }

    public double distanceTo(City city) {
        return Math.hypot(city.coordinateX - this.coordinateX, city.coordinateY - this.coordinateY);
    }

    /**
     * Randomly adjust the prices of each item by +/- 10% of their {@link Item#getStandardPrice()}.
     * Minimum 10% their standard price, maximum 200%.
     */
    public void changePrices() {
        for (int i = 0; i < prices.length; i++) {
            if (random.nextBoolean()) {
                prices[i] += 10;
            } else {
                prices[i] -= 10;
            }
            if (prices[i] < 10) {
                prices[i] = 10;
            } else if (prices[i] > 200) {
                prices[i] = 200;
            }
        }
    }

    /**
     * @param id the ID number of an {@link Item.Material}
     * @return this city's price of the specified material
     */
    public int getPrice(int id) {
        return (int)(Item.Material.values()[id].getStandardPrice() * (prices[id] / 100d));
    }
}
