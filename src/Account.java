public class Account {
    int id;
    long balance;
    Customer customer;
    Account(int id,long balance,Customer c){
        this.id=id;
        this.balance=balance;
        customer=c;
        System.out.println("Account Created\n"+getDetails());
    }
    public String getDetails(){
        return "Account Number: "+id+"\nBalance: "+balance+"\nCustomer Id: "+ customer.id;
    }
    public void withdraw(int amt){
        System.out.println("Withdrawing "+amt);
        balance=balance-amt;
        System.out.println("Remaining balance is "+balance);
    }
    public void deposit(int amt){
        System.out.println("Depositing "+amt);
        balance=balance+amt;
        System.out.println("Updated balance is "+balance);
    }
}
