import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private final Scanner scanner;
    private final Player player;
    private final List<City> cities;
    private final Random random = new Random();

    public enum TravelEvent {
        ENEMY, LOOT, NOTHING
    }

    public Game() {
        scanner = new Scanner(System.in);
        cities = City.generateCities();
        player = new Player("John", cities.get(0), 1000);
    }

    public void play() {
        System.out.println("Welcome to Frontier. Copyright (c) Alejandro Ramos, 2022, MIT Licence.");
        System.out.println("You are " + player.name);
        while (true) {
            changePrices();
            travel();
            city();
        }
    }

    public static void main(String... args) {
        Game game = new Game();
        game.play();
    }

    public void travel() {
        System.out.println("You are in: " + player.location.name);
        List<City> destinations = player.location.destinations(cities, player.getRange());
        System.out.println("You can go to: ");
        for (int i = 0; i < destinations.size(); i++) {
            System.out.println(i + " - " + destinations.get(i).name + "(" + ((int)(player.location.distanceTo(destinations.get(i)) / 25) + 1) + " days)");
        }
        System.out.print("Destination: ");
        int selection = scanner.nextInt();
        City origin = player.location;
        player.location = destinations.get(selection);

        int hours = ((int)(origin.distanceTo(destinations.get(selection)) / 25) + 1) * 24;
        int currentHour = 0;
        player.health = Player.MAX_HEALTH;
        while (currentHour < hours) {
            System.out.println("Hour " + currentHour++);
            TravelEvent event = getTravelEvent(origin, player.location, progress(currentHour, hours));
            switch (event) {
                case ENEMY -> {
                    System.out.println("You have encountered a bandit.");
                    if (!combat(new NPCParty("Bandit", 1, 5, 2))) {
                        System.out.println("You have died. Game over.");
                        System.exit(0);
                    }
                }
                case LOOT -> {
                    System.out.println("You have found some unattended crates on the side of the road.");
                    System.out.println("Do you (1) loot the crates or (2) leave them?");
                    selection = scanner.nextInt();
                    if (selection == 1) {
                        int loot = loot(origin, player.location, progress(currentHour, hours));
                        System.out.println("You loot the crates and find " + loot + " coins.");
                        player.money += loot;
                    } else {
                        System.out.println("You leave the crates.");
                    }
                }
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {}
        }
    }

    /**
     * Returns a TravelEvent that accounts for the player's position between two cities. This means that a route will
     * become more dangerous as the player approaches a dangerous city from a safe one.
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

    public boolean combat(NPCParty enemy) {
        while (true) {
            System.out.println("You are fighting " + enemy.name);
            System.out.println("You have " + player.health + "% health");
            System.out.println("Enemy has " + enemy.health + "% health");
            System.out.print("Attack (1) or attempt to flee (2): ");
            int selection = scanner.nextInt();
            switch (selection) {
                case 1 -> {
                    System.out.println("You attack " + enemy.name + " dealing " + Party.combat(player, enemy) + " damage.");
                    if (enemy.health == 0) {
                        System.out.println("You have killed " + enemy.name);
                        return true;
                    }
                }
                case 2 -> {
                    System.out.println("You attempt to flee.");
                    if (random.nextBoolean()) {
                        System.out.println("You successfully flee from " + enemy.name);
                        return true;
                    } else {
                        System.out.println("You failed to flee from " + enemy.name);
                    }
                }
                default -> System.out.println("Invalid input. Move wasted.");
            }

            System.out.println(enemy.name + " attacks you, dealing " + Party.combat(enemy, player) + " damage.");
            if (player.health == 0) {
                System.out.println("You have died fighting " + enemy.name);
                return false;
            }
        }
    }

    public void city() {
        System.out.println("Welcome to " + player.location.name);
        while (true) {
            System.out.print("(1) Buy, 2 Sell, 3 Weapons, 4 Armor, 5 Vessel, 6 Party 7 Tavern. -1 to leave: ");
            int selection = scanner.nextInt();
            switch (selection) {
                case 1:
                    buy();
                    break;
                case 2:
                    sell();
                    break;
                default:
                    return;
            }
        }
    }

    public void buy() {
        System.out.println("Buy from the market in " + player.location.name);
        while (true) {
            System.out.println("You have " + player.money + " coins");

            Item.Material[] items = Item.Material.values();
            printItems(Arrays.asList(items));

            System.out.print("Your pick (-1 to leave): ");
            int selection = scanner.nextInt();
            if (selection == -1) {
                break;
            }

            int price = player.location.getPrice(selection);
            if (player.money < price) {
                System.out.println("You can not afford this item.");
                continue;
            }
            if (player.freeSpace() < 1) {
                System.out.println("You can not fit this item in your inventory.");
                continue;
            }
            player.money -= price;
            player.inventory.add(items[selection]);
            System.out.println("You have purchased one " + items[selection].getName() + " for " + price + " coins!");
        }
    }

    public void sell() {
        System.out.println("Sell to the market in " + player.location.name);
        while (true) {
            System.out.println("You have " + player.money + " coins");
            printItems(player.inventory);
            System.out.print("Your pick (-1 to leave): ");
            int selection = scanner.nextInt();
            if (selection == -1) {
                break;
            }

            int price;
            if (player.inventory.get(selection) instanceof Item.Material) {
                price = player.location.getPrice(((Item.Material)player.inventory.get(selection)).getID());
            } else {
                price = player.inventory.get(selection).getStandardPrice();
            }

            System.out.println("You have sold one " + player.inventory.get(selection).getName() + " for " + price + " coins!");
            player.inventory.remove(selection);
            player.money += price;
        }
    }

    public void printItems(List<Item> items) {
        for (int i = 0; i < items.size(); i++) {
            System.out.print("(" + i + ")" + " " + items.get(i).getName() + ": ");
            if (items.get(i) instanceof Item.Material) {
                System.out.print(player.location.getPrice(((Item.Material)items.get(i)).getID()));
            } else {
                System.out.print(items.get(i).getStandardPrice());
            }
            System.out.println();
        }
    }

    public void changePrices() {
        for (City city : cities) {
            city.changePrices();
        }
    }
}
