package Customer;
import java.sql.*;
import java.util.*;
import java.io.*;

public class MainClass {
    
    public static void clearScreen()
    {
        for (int i=0; i<15; i++)
        {
            System.out.println();
        }
    }
    
    public static void main(String[] args)
    {
        Scanner sc=new Scanner(System.in);
        
        int customerId;
        String customerPassword;
        
        char choice;
        int choice1;
        
        CustomerAction customer=new CustomerAction();
        Order order=new Order();
        
        System.out.println("INVOICE SYSTEM");
        System.out.println();
        System.out.println("CUSTOMER LOGIN CREDENTIALS");
        System.out.println("Enter the Customer Id:");
        customerId=sc.nextInt();
        System.out.println("Enter the Customer Password:");
        customerPassword=sc.next();
        
        Customer c=customer.loginCustomer(customerId, customerPassword);
        
        if(c!=null)
        {
            boolean state=true;
            while(state)
           {
             clearScreen();
             System.out.println("INVOICE SYSTEM \n 1.ORDER PRODUCTS \n 2.VIEW PURCHASE HISTORY \n 3.VIEW PROFILE \n 4.EXIT");
             System.out.println("Enter the choice:");
             choice1=sc.nextInt();
            
            switch(choice1)
            {
                case 1:
                     customer.displayProducts();
                     order.orderProducts(c);
                     break;
                    
                case 2: 
                    System.out.println("\nDo you want to download your Purchase History File:Y or N");
                    choice=sc.next().charAt(0);
        
                     if(choice=='y' || choice=='Y')
                     {
                        customer.viewPurchaseHistory(c);
                     } 
                    break;
                    
                case 3:
                    customer.viewCustomerDetails(c);
                    break;  
                    
                case 4:
                    System.exit(0);
                    break;
                    
                default:
                    System.out.println("Invalid choice");               
            }
            
            System.out.println("\nDo you wish to continue:Y or N");
            char ch=sc.next().charAt(0);
            
            if(ch=='y' || ch=='Y')
            {
                state=true;
            }
            else
            {
                state=false;
                System.exit(0);
            }
            
        }
            
      }
     
    }
    
}
