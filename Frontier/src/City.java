import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class City {
    public static List<City> generateCities() {
        ArrayList<City> cities = new ArrayList<>(6);
        cities.add(new City("New York", 90, 80));
        cities.add(new City("Boston", 100, 100));
        cities.add(new City("Chicago", 60, 80));
        cities.add(new City("Washington, D.C.", 70, 60));
        cities.add(new City("Los Angeles", 0, 0));
        cities.add(new City("Detroit", 50, 85));
        return cities;
    }

    public final String name;
    public final int coordinateX;
    public final int coordinateY;
    public final int[] prices;

    private final Random random = new Random();

    public City(String name, int coordinateX, int coordinateY) {
        this.name = name;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
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

    public void changePrices() {
        for (int i = 0; i < prices.length; i++) {
            if (random.nextBoolean()) {
                prices[i] += 10;
            } else {
                prices[i] -= 10;
            }
            if (prices[i] <= 0) {
                prices[i] = 10;
            } else if (prices[i] > 200) {
                prices[i] = 200;
            }
        }
    }

    public int getPrice(int id) {
        return (int)(Item.Material.values()[id].getStandardPrice() * (prices[id] / 100d));
    }
}
