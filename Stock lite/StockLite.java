import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;

// ABSTRACT CLASS: common information shared by all product types.
abstract class Product {
    // ENCAPSULATION: fields are private and accessed through getters/setters.
    private String id;
    private String name;
    private double price;
    private int quantity;

    public Product(String id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // ABSTRACTION: every child class must provide its own category.
    public abstract String getCategory();

    public void displayInfo() {
        System.out.printf("%-8s %-18s %-12s PHP %-10.2f %d%n",
                id, name, getCategory(), price, quantity);
    }
}

// INHERITANCE: FoodProduct receives the common fields and methods from Product.
class FoodProduct extends Product {
    public FoodProduct(String id, String name, double price, int quantity) {
        super(id, name, price, quantity);
    }

    // POLYMORPHISM: this class provides its own getCategory() implementation.
    @Override
    public String getCategory() {
        return "Food";
    }
}

// INHERITANCE: NonFoodProduct also reuses the Product class.
class NonFoodProduct extends Product {
    public NonFoodProduct(String id, String name, double price, int quantity) {
        super(id, name, price, quantity);
    }

    // POLYMORPHISM: same method name, different result.
    @Override
    public String getCategory() {
        return "Non-Food";
    }
}

// Stores the date and description of an inventory activity for the weekly report.
class StockActivity {
    private LocalDateTime dateTime;
    private String type;
    private String description;

    public StockActivity(String type, String description) {
        this.dateTime = LocalDateTime.now();
        this.type = type;
        this.description = description;
    }

    public LocalDateTime getDateTime() { return dateTime; }
    public String getType() { return type; }
    public String getDescription() { return description; }
}

// Handles the inventory records and their operations.
class Inventory {
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<StockActivity> activities = new ArrayList<>();

    // Searches for a product using its unique ID.
    private Product findProduct(String id) {
        for (Product product : products) {
            if (product.getId().equalsIgnoreCase(id)) {
                return product;
            }
        }
        return null;
    }

    // Adds a new product. Product removal is intentionally not included.
    public void addProduct(String id, String name, double price,
                           int quantity, int type) {
        if (findProduct(id) != null) {
            System.out.println("That product ID already exists.");
            return;
        }

        Product product;
        if (type == 1) {
            product = new FoodProduct(id, name, price, quantity);
        } else {
            product = new NonFoodProduct(id, name, price, quantity);
        }

        products.add(product);
        activities.add(new StockActivity("ADD",
                "Added " + name + " (ID: " + id + "), quantity: " + quantity));
        System.out.println("Product added successfully!");
    }

    // Displays every product in the inventory.
    public void viewProducts() {
        if (products.isEmpty()) {
            System.out.println("No inventory records yet.");
            return;
        }

        System.out.println("\n---------------- INVENTORY LIST ----------------");
        System.out.printf("%-8s %-18s %-12s %-15s %s%n",
                "ID", "NAME", "CATEGORY", "PRICE", "QUANTITY");
        for (Product product : products) {
            // Runtime polymorphism: the correct getCategory() is called
            // depending on whether the object is FoodProduct or NonFoodProduct.
            product.displayInfo();
        }
    }

    // Updates the quantity of an existing product.
    public void updateStock(String id, int newQuantity) {
        Product product = findProduct(id);

        if (product == null) {
            System.out.println("Product ID not found.");
            return;
        }

        int oldQuantity = product.getQuantity();
        product.setQuantity(newQuantity);

        activities.add(new StockActivity("UPDATE",
                product.getName() + " (ID: " + id + "): "
                + oldQuantity + " -> " + newQuantity));

        System.out.println("Stock updated successfully!");
    }

    // Shows products with five or fewer items remaining.
    public void checkLowStock() {
        boolean found = false;
        System.out.println("\n----------- LOW-STOCK PRODUCTS (5 OR LESS) -----------");

        for (Product product : products) {
            if (product.getQuantity() <= 5) {
                product.displayInfo();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No low-stock products.");
        }
    }

    // Shows activities recorded from Monday up to the current date.
    public void weeklyReport() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        int addCount = 0;
        int updateCount = 0;
        int totalActivities = 0;

        System.out.println("\n================ WEEKLY REPORT ================");
        System.out.println("Period: " + monday + " to " + today);
        System.out.println("------------------------------------------------");

        for (StockActivity activity : activities) {
            LocalDate activityDate = activity.getDateTime().toLocalDate();

            if (!activityDate.isBefore(monday) && !activityDate.isAfter(today)) {
                totalActivities++;

                if (activity.getType().equals("ADD")) {
                    addCount++;
                } else if (activity.getType().equals("UPDATE")) {
                    updateCount++;
                }

                System.out.println(activity.getDateTime().format(formatter)
                        + " | " + activity.getDescription());
            }
        }

        if (totalActivities == 0) {
            System.out.println("No inventory activities recorded this week.");
        }

        System.out.println("------------------------------------------------");
        System.out.println("Products added: " + addCount);
        System.out.println("Stock updates: " + updateCount);
        System.out.println("Total activities: " + totalActivities);
        System.out.println("Current number of product records: " + products.size());
        System.out.println("================================================");
    }
}

// MAIN CLASS: displays the menu and receives the user's choices.
public class StockLite {
    private static Scanner input = new Scanner(System.in);

    // Reads an integer safely so the program will not stop on invalid input.
    private static int readInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    // Reads a decimal number safely.
    private static double readDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Double.parseDouble(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }

    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        int choice;

        do {
            System.out.println("\n========== STOCKLITE INVENTORY SYSTEM ==========");
            System.out.println("1. Add Product");
            System.out.println("2. View Product List");
            System.out.println("3. Update Stock");
            System.out.println("4. Low-Stock Checker");
            System.out.println("5. Weekly Report");
            System.out.println("6. Exit");
            System.out.println("================================================");

            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    System.out.println("\n--- ADD PRODUCT ---");
                    System.out.print("Product ID: ");
                    String id = input.nextLine();

                    System.out.print("Product name: ");
                    String name = input.nextLine();

                    double price = readDouble("Price: PHP ");
                    int quantity = readInt("Quantity: ");

                    int type;
                    do {
                        System.out.println("Category: 1. Food  2. Non-Food");
                        type = readInt("Choose category: ");
                    } while (type != 1 && type != 2);

                    if (id.trim().isEmpty() || name.trim().isEmpty()
                            || price < 0 || quantity < 0) {
                        System.out.println("Invalid details. Please try again.");
                    } else {
                        inventory.addProduct(id, name, price, quantity, type);
                    }
                    break;

                case 2:
                    inventory.viewProducts();
                    break;

                case 3:
                    System.out.println("\n--- UPDATE STOCK ---");
                    System.out.print("Enter product ID: ");
                    String updateId = input.nextLine();
                    int newQuantity = readInt("Enter new quantity: ");

                    if (newQuantity < 0) {
                        System.out.println("Quantity cannot be negative.");
                    } else {
                        inventory.updateStock(updateId, newQuantity);
                    }
                    break;

                case 4:
                    inventory.checkLowStock();
                    break;

                case 5:
                    inventory.weeklyReport();
                    break;

                case 6:
                    System.out.println("Thank you for using StockLite!");
                    break;

                default:
                    System.out.println("Invalid choice. Please choose 1 to 6.");
            }
        } while (choice != 6);

        input.close();
    }
}
