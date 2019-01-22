public class Account {
    static int counter;
    int id;
    long balance;
    Customer customer;
    static
    {
        counter=200;
    }
    Account(long balance,Customer c){
        this.id=Account.counter++;
        this.balance=balance;
        customer=c;
        System.out.println("Account Created\n"+getDetails());
    }
    public String getDetails(){
        return "Account Number: "+id+"\nBalance: "+balance+"\nCustomer Id: "+ customer.id;
    }
    public void withdraw(int amt){
        System.out.println("Withdrawing "+amt);
        if(amt>=balance){
            balance=balance-amt;
            System.out.println("Remaining balance is "+balance);
        }
        else{
            System.out.println("Insufficient balance");
        }
    }
    public void deposit(int amt){
        System.out.println("Depositing "+amt);
        balance=balance+amt;
        System.out.println("Updated balance is "+balance);
    }
}
