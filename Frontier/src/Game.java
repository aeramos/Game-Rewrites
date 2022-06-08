import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Game {
    private final Scanner scanner;
    private final Player player;
    private final List<City> cities;

    public Game() {
        scanner = new Scanner(System.in);
        cities = City.generateCities();
        player = new Player("John", cities.get(0), 1000);
    }

    public void play() {
        System.out.println("Welcome to Frontier. Copyright (c) Alejandro Ramos, 2022, MIT Licence.");
        System.out.println("You are " + player.name);
        while(true) {
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
        System.out.println("You can go to " + Arrays.toString(destinations.toArray()));
        System.out.print("Destination (index starting at zero): ");
        player.location = destinations.get(scanner.nextInt());
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
            System.out.println("You have " + player.money);

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
            System.out.println("You have " + player.money);
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
