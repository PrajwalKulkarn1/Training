public class Customer {
    static int counter;
    static {
        counter=1000;
    }
    int id;
    String name;
    String address;
    String phone;
    Customer(String name,String address,String phone){
        this.id=counter++;
        this.name=name;
        this.address=address;
        this.phone=phone;
        System.out.println("Customer created\n"+getDetails());
    }
    public String getDetails(){
        String res="Customer Id: "+id+"\nName: "+name+"\nAddress: "+address+"\nPhone: "+phone;
        return res;
    }
}
