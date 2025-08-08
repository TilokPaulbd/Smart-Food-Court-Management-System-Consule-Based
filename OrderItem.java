

public class OrderItem {
    private FoodItem foodItem;
    private int quantity;

    public OrderItem(FoodItem foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
    }
    public FoodItem getFoodItem() {
        return foodItem;
    }
    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String toString(){
        return foodItem.getId()+","+quantity;
    }

    public double getPrice(){
        return foodItem.getPrice();
    }

    public double getTotalAmount(){
        return foodItem.getPrice()*quantity;
    }
   
    

}
