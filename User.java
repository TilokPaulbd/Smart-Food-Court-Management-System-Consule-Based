public abstract class User {
    protected String id;
    protected String name;

    public User(String id,String name){
        this.id=id;
        this.name=name;
    }

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }

    public void setId(String id){
        this.id=id;
    }
    public void setName(String name){
        this.name=name;
    }
    
    public abstract void displayMenu();
}
