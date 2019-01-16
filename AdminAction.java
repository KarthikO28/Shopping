package Admin;

import java.util.*;
import java.sql.*;

public class AdminAction {
    
    Scanner sc=new Scanner(System.in);
    
    PreparedStatement ps;
    ResultSet rs;
    
    public static Connection getConnection()
    {  
    Connection con=null;  
    try
    {  
        Class.forName("org.postgresql.Driver");
        con=DriverManager.getConnection("jdbc:postgresql://localhost:5433/InvoiceSystem","karthik","o08m16o28");
    }
    catch(Exception e)
    {
        System.out.println(e);
    }  
    return con;  
    }  
    
     public Admin loginAdmin(int id,String password)
    {
        int adminId;
        String adminName,adminPassword;
        try
        {
            Connection con=getConnection();
            String query="select * from admin where id=? and password=?";
            ps=con.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, password);           
            rs=ps.executeQuery();
            
            if(rs.next())
            {
                adminId=rs.getInt(1);
                adminName=rs.getString(2);
                adminPassword=rs.getString(3);
                
                Admin admin=new Admin(adminId,adminName,adminPassword);
                return admin;
            }
            else
            {
                System.out.println("Login Failure");
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        return null;
    }
     
     public void storeCustomerDetails()
     {
         int customerId;
         String customerName,customerState,customerPassword;
         
         System.out.println("\nEnter the Customer Details:");
         System.out.print("ID=");
         customerId=sc.nextInt();
         System.out.print("NAME=");
         customerName=sc.next();
         System.out.print("STATE=");
         customerState=sc.next();
         System.out.print("PASSWORD=");
         customerPassword=sc.next();
         
         try
         {
            Connection con=getConnection();
            String query="insert into customer(id,name,state,password,points) values(?,?,?,?,0)";
            ps=con.prepareStatement(query);
            ps.setInt(1, customerId);
            ps.setString(2, customerName); 
            ps.setString(3,customerState);
            ps.setString(4, customerPassword);  
            
            int num=ps.executeUpdate();
            if(num>0)
            {
                System.out.println("Customer Added Successfully");
            }
            else
            {
                System.out.println("Customer Added failed");
            }
         }
         catch(Exception ex)
         {
             System.out.println(ex);
         }
     }
     
     public void storeProductDetails()
     {
         int productId,productPrice,cgst,productQuantity;
         String productName;
         
         System.out.println("\nEnter the Product Details:");
         System.out.print("ID=");
         productId=sc.nextInt();
         System.out.print("NAME=");
         productName=sc.next();
         System.out.print("PRICE=");
         productPrice=sc.nextInt();
         System.out.print("QUANTITY=");
         productQuantity=sc.nextInt();
         System.out.print("CGST=");
         cgst=sc.nextInt();
         
         try
         {
            Connection con=getConnection();
            String query="insert into product(id,name,price,cgst,quantity) values(?,?,?,?,?)";
            ps=con.prepareStatement(query);
            ps.setInt(1, productId);
            ps.setString(2, productName); 
            ps.setInt(3,productPrice);
            ps.setInt(4, cgst);  
            ps.setInt(5,productQuantity);
            
            int num=ps.executeUpdate();
            if(num>0)
            {
                System.out.println("Product Added Successfully");
            }
            else
            {
                System.out.println("Product Added failed");
            }
         }
         catch(Exception ex)
         {
             System.out.println(ex);
         }
     }
     
}
