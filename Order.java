import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class Order {
    private String orderId;
    private String strudentId;
    private ArrayList<OrderItem>items;
    private LocalDateTime orDateTime;
    private boolean isPaid;

    
    public Order(String strudentId) {

        this.orderId = "66H"+System.currentTimeMillis();
        this.strudentId = strudentId;
        this.items = new ArrayList<>();
        this.orDateTime = LocalDateTime.now();
        this.isPaid = false;
    }

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getStrudentId() {
        return strudentId;
    }
    public void setStrudentId(String strudentId) {
        this.strudentId = strudentId;
    }
    public ArrayList<OrderItem> getItems() {
        return this.items;
    }
    public void setItems(ArrayList<OrderItem> items) {
        this.items = items;
    }
    public LocalDateTime getOrDateTime() {
        return orDateTime;
    }
    public void setOrDateTime(LocalDateTime orDateTime) {
        this.orDateTime = orDateTime;
    }
    public boolean isPaid() {
        return isPaid;
    }
    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }
    


    public void addItem(OrderItem item){
        items.add(item);
    }
    
    public double getTotalAmount(){
        double total=0.0;
        
        for(OrderItem i : items){
            total+=i.getPrice();
        }
        return total;
    }

    public void markAsPaid(){
        this.isPaid=true;
    }

    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append(orderId).append(",");
        sb.append(strudentId).append(",");

        sb.append(orDateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"))).append(",");
        sb.append(isPaid);
        for (OrderItem orderItem : items) {
            sb.append(",").append(orderItem.toString());
        }
        return sb.toString();
    }



    public static Order fromString(String str, ArrayList<FoodItem> menu) {
        String[] parts = str.split(","); 
    
    
        if (parts.length < 4) {
        System.err.println("Invalid order format: " + str);
        return null;
        }
    
        Order order = new Order(parts[1]);
        order.orderId = parts[0];
        order.orDateTime = LocalDateTime.parse(parts[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        order.isPaid = Boolean.parseBoolean(parts[3]);

 
        for (int i = 4; i < parts.length; i += 2) {
            if (i + 1 >= parts.length) {
               System.err.println("Incomplete item-quantity pair in order: " + str);
               break;
            }

            String foodId = parts[i];
            String qtyStr = parts[i + 1];

            FoodItem matchedFoodItem = null;
            for (FoodItem f : menu) {
                if (f.getId().equals(foodId)) {
                    matchedFoodItem = f;
                    break;
                }
            }

            if (matchedFoodItem == null) {
                System.err.println("Food item not found for ID: " + foodId);
                continue;
            }

            try {
                int quantity = Integer.parseInt(qtyStr);
                order.addItem(new OrderItem(matchedFoodItem, quantity));
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity for item " + foodId + ": " + qtyStr);
            } 
        }
    
    return order;
    }
}
