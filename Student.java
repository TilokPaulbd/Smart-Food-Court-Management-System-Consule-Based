public class Student extends User {
    private Order currentOrder;
    
    public Student(String id,String name){
        super(id, name);
    }
    
    public void displayMenu(){
        
    }

    public void startNewOrder(){
        this.currentOrder=new Order(this.id);
    }
    public Order getCurrentOrder(){
        return currentOrder;
    }

    public void addToOrder(FoodItem foodItem,int quantity){
        if(currentOrder==null){
            startNewOrder();
        }
        
        currentOrder.addItem(new OrderItem(foodItem, quantity));
        
    }


    public void checkOut(){
        if (currentOrder !=null) {
            currentOrder.markAsPaid();
            currentOrder=null;
        }
    }
}
