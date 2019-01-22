import java.util.*;
import java.io.IOException;
public class bank {
    public static void main(String args[]) throws NullPointerException{
        Scanner sc=new Scanner(System.in);
        Map<Integer,Customer> customerMap=new HashMap<>();
        Map<Integer,Account> accountMap=new HashMap<>();
        while(true){
            System.out.println("1:Create a customer\n2:Create an account\n3:Withdraw\n4:Get Account Details\n5:exit");
            System.out.println("Enter your choice");
            int ch=sc.nextInt();
            switch(ch){
                case 1:System.out.print("Enter the Customer Details:");
                System.out.print("Enter the name of the customer: ");
                String name=sc.next();
                System.out.print("Enter the address of the customer: ");
                String address=sc.next();
                System.out.print("Enter the phone of the customer: ");
                String phone=sc.next();
                Customer customer=new Customer(name,address,phone);
                customerMap.put(customer.id,customer);
                break;
                case 2:System.out.println("Enter the Account Details: ");
                System.out.print("Enter the opening balance: ");
                int balance=sc.nextInt();
                System.out.print("\nEnter the customer id to which the account belongs: ");
                int id=sc.nextInt();
                try {
                    Customer c = customerMap.get(new Integer(id));
                    Account account = new Account(balance, c);
                    accountMap.put(account.id,account);
                }
                catch(NullPointerException e){
                    System.out.println("Wrong Customer Id Entered!!!");
                }
                break;
                case 3:System.out.print("Enter the account id: ");
                int accountId=sc.nextInt();
                try{
                    Account account=accountMap.get(accountId);
                    System.out.print("Enter the amount to withdraw: ");
                    int amt=sc.nextInt();
                    account.balance=account.balance-amt;
                }
                catch(NullPointerException e){
                    System.out.println("Wrong account id entered!!!");
                }
                break;
                case 4:System.out.print("Enter the account id: ");
                    accountId=sc.nextInt();
                    try{
                        Account account=accountMap.get(accountId);
                        System.out.println(account.getDetails());
                    }
                    catch(NullPointerException e){
                        System.out.println("Wrong account id entered!!!");
                    }
                case 5:System.exit(0);
            }
        }
    }
}
