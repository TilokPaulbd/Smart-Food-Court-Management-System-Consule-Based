import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Admin extends User{
    private static final String ADMIN_PASSWORD = "tilok"; 
    
    public Admin(String id,String name){
        super(id,name);
    }

    public void displayMenu(){
        System.out.println("\nAdmin Menu:");
        System.out.println("1. Add new food item");
        System.out.println("2. Increase item quantity");
        System.out.println("3. View all items");
        System.out.println("4. View order history");
        System.out.println("5. Exit");
    }

    public static boolean checkPassword(String PASSWORD){
        return ADMIN_PASSWORD.equals(PASSWORD);
    }

    public void addFoodItem(Scanner scanner,ArrayList<FoodItem>menu){
        try{
            System.out.print("Enter Food Number :");
            String id=scanner.next();
            scanner.nextLine();
            System.out.print("Enter Food Name :");
            String name=scanner.nextLine();
            System.out.print("Enter Food Price :");
            double price =scanner.nextDouble();
            System.out.print("Enter Quantity :");
            int quantity=scanner.nextInt();

            menu.add(new FoodItem(id, name, price, quantity));
            System.out.println("Food item added successfully");
        }
        catch(Exception e){
          System.out.println("Invalid Input !!");
          scanner.nextLine();
        }  
    }

    public void inreaseItemQuantity(Scanner scanner,ArrayList<FoodItem>menu){
        try{

            System.out.println("Enter Food Number :");
            String id=scanner.next();
            System.out.println("Enter Quantity To Add ;");
            int quantity=scanner.nextInt();

            for (FoodItem foodItem : menu) {
                if(foodItem.getId().equals(id)){
                    foodItem.setQuantity(foodItem.getQuantity()+quantity);
                    System.out.println("Quantity updated successfully.");
                    return;
                }
            }
            System.out.println("Food item not found.");
        }catch(Exception e){
            System.out.println("Invalid input !!");
            scanner.nextLine();
        }
    }

    public void viewAllItems(ArrayList<FoodItem>menu){
        System.out.printf("%-5s %-20s %-10s %-5s\n", "ID", "Name", "Price", "Qty");
        for (FoodItem item : menu) {
            System.out.printf("%-5s %-20s Taka %-8.2f %-5d\n",item.getId(), item.getName(), item.getPrice(), item.getQuantity());
        }
    }

    public void viewOrderHistory(ArrayList<Order>orderHistory){
        System.out.println("\nOrder History :");
        
        if (orderHistory.isEmpty()) {
            System.out.println("No one order yet .");
        }else{
            for (Order order : orderHistory) {
                System.out.println("Order ID :"+order.getOrderId());
                System.out.println("Student ID:"+order.getStrudentId());
                System.out.println("Order Date :"+order.getOrDateTime().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")));
                
                
                try{
                    
                   
                    System.out.println("Items :");
                    double total=0;

                    for (OrderItem item : order.getItems()) {
                
                       System.out.printf("  %-20s x%-5d %-8.2fTaka\n", item.getFoodItem().getName(), item.getQuantity(), item.getTotalAmount());
                       total+=item.getTotalAmount();
                    }
                    System.out.println("Total Amount: " + String.format("%.2f Taka",total));
                     
                

                    System.out.println("Status: " + (order.isPaid() ? "Paid" : "Pending"));
                    System.out.println("-----------------------------");
                }catch(Exception e){
                    System.out.println(e.getMessage());

                }
                
                


            }
        }
    }
}
