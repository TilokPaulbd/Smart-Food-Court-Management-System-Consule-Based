public class FoodItem {
    private String id;
    private String name;
    double price;
    private int quantity;

    public FoodItem(String id,String name,double price,int quantity){
        this.id=id;
        this.name=name;
        this.price=price;
        this.quantity=quantity;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static FoodItem fromString(String str){
        String[] parts=str.split(",");
        return new FoodItem(parts[0], parts[1],Double.parseDouble(parts[2]), Integer.parseInt(parts[3]));
    }

    public String toString(){
        return String.format("%s,%s,%.2f,%d",id,name,price,quantity);
    }
    
}
