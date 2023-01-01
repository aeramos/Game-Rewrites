import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TextUI {
    private final Game game;
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String... args) {
        TextUI textUI = new TextUI(new Game("John"));
        System.out.println("Welcome to Frontier. Copyright (c) Alejandro Ramos, 2022, MIT Licence.");
        System.out.println("You have finished with a score of: " + textUI.play());
    }

    public TextUI(Game game) {
        this.game = game;
    }

    // returns net worth (for high score tracking)
    public int play() {
        System.out.println("You are " + game.player.name);
        System.out.println("You are in " + game.player.location);
        while (true) {
            Record state = game.getState();
            if (state == null) {
                city();
                selectDestination();
            } else {
                if (state.getClass().equals(TravelData.class)) {
                    travel((TravelData)state);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException ignored) {}
                } else if (state.getClass().equals(CombatData.class)) {
                    combat((CombatData)state);
                    if (game.player.health == 0) {
                        return game.score();
                    }
                }
            }
        }
    }

    // todo: this should be controlled by Game more. UI should not know mechanics of how far a player can travel.
    private void selectDestination() {
        System.out.println("You are in: " + game.player.location.name);
        List<City> destinations = game.player.location.destinations(game.cities, game.player.getRange());
        System.out.println("You can go to: ");
        for (int i = 0; i < destinations.size(); i++) {
            System.out.println(i + " - " + destinations.get(i).name + "(" + ((int)(game.player.location.distanceTo(destinations.get(i)) / 25) + 1) + " days)");
        }
        System.out.print("Destination: ");
        int selection = scanner.nextInt();
        game.destination = destinations.get(selection);
    }

    private void travel(TravelData travelData) {
        if (travelData.hoursTravelled() == 0) {
            System.out.println("You are travelling to " + game.destination);
        }
        System.out.println("Hour " + travelData.hoursTravelled() + "/" + travelData.totalHours());
        switch (travelData.travelEvent()) {
            case ENEMY -> System.out.println("You have encountered a bandit.");
            case LOOT -> {
                System.out.println("You have found some unattended crates on the side of the road.");
                System.out.println("Do you (1) loot the crates or (2) leave them?");
                if (scanner.nextInt() == 1) {
                    System.out.println("You loot the crates and find " + game.loot() + " coins.");
                } else {
                    System.out.println("You leave the crates.");
                }
            }
        }
    }

    private void combat(CombatData combatData) {
        System.out.println("You are fighting " + combatData.enemy().name);
        if (combatData.damage() != -1) {
            System.out.println(combatData.enemy().name + " attacks you, dealing " + combatData.damage() + " damage.");
            if (game.player.health == 0) {
                System.out.println("You have died fighting " + combatData.enemy().name);
                return;
            }
        }

        System.out.println("You have " + game.player.health + "% health");
        System.out.println("Enemy has " + combatData.enemy().health + "% health");
        System.out.println("Attack (1) or attempt to flee (2): ");
        int selection = scanner.nextInt();
        switch (selection) {
            case 1 -> {
                System.out.println("You attack " + combatData.enemy().name + " dealing " + game.attack() + " damage.");
                if (combatData.enemy().health == 0) {
                    System.out.println("You have killed " + combatData.enemy().name);
                }
            }
            case 2 -> {
                System.out.println("You attempt to flee.");
                if (game.flee()) {
                    System.out.println("You successfully flee from " + combatData.enemy().name);
                } else {
                    System.out.println("You failed to flee from " + combatData.enemy().name);
                }
            }
            default -> System.out.println("Invalid input. Move wasted.");
        }
    }

    private void city() {
        System.out.println("Welcome to " + game.player.location.name);
        while (true) {
            System.out.print("(1) Buy, 2 Sell, 3 Weapons, 4 Armor, 5 Vessel, 6 Party 7 Tavern. -1 to leave: ");
            int selection = scanner.nextInt();
            switch (selection) {
                case 1 -> buy();
                case 2 -> sell();
                default -> {
                    return;
                }
            }
        }
    }

    private void buy() {
        System.out.println("Buy from the market in " + game.player.location.name);
        while (true) {
            System.out.println("You have " + game.player.money + " coins");

            Item.Material[] items = Item.Material.values();
            printItems(Arrays.asList(items));

            System.out.print("Your pick (-1 to leave): ");
            int selection = scanner.nextInt();
            if (selection == -1) {
                break;
            }

            int price = game.player.location.getPrice(selection);
            if (game.player.money < price) {
                System.out.println("You can not afford this item.");
                continue;
            }
            if (game.player.freeSpace() < 1) {
                System.out.println("You can not fit this item in your inventory.");
                continue;
            }
            game.buy(items[selection]);
            System.out.println("You have purchased one " + items[selection].getName() + " for " + price + " coins!");
        }
    }

    private void sell() {
        System.out.println("Sell to the market in " + game.player.location.name);
        while (true) {
            System.out.println("You have " + game.player.money + " coins");
            printItems(game.player.inventory);
            System.out.print("Your pick (-1 to leave): ");
            int selection = scanner.nextInt();
            if (selection == -1) {
                break;
            }
            System.out.println("You have sold one " + game.player.inventory.get(selection).getName() + " for " + game.sell(selection) + " coins!");
        }
    }

    private void printItems(List<Item> items) {
        for (int i = 0; i < items.size(); i++) {
            System.out.print("(" + i + ")" + " " + items.get(i).getName() + ": ");
            if (items.get(i) instanceof Item.Material) {
                System.out.print(game.player.location.getPrice(((Item.Material)items.get(i)).getID()));
            } else {
                System.out.print(items.get(i).getStandardPrice());
            }
            System.out.println();
        }
    }
}
