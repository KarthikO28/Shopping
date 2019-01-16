package Customer;
import java.sql.*;
import java.util.*;
import java.io.*;

public class CustomerAction {
    
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
    
    public Customer loginCustomer(int customerId,String customerPassword)
    {
        String customerName,customerState;
        float customerPoints;
        try
        {
            Connection con=getConnection();
            String query="select * from customer where id=? and password=?";
            ps=con.prepareStatement(query);
            ps.setInt(1, customerId);
            ps.setString(2, customerPassword);           
            rs=ps.executeQuery();
            
            if(rs.next())
            {
                System.out.println("Login Success");
                customerName=rs.getString(2);
                customerState=rs.getString(3);
                customerPoints=rs.getFloat(5);
                return new Customer(customerId,customerName,customerState,customerPoints);
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
    
    public void displayProducts()
    {
       try
        {
            Connection con=getConnection();
            String query="select * from product";
            ps=con.prepareStatement(query);           
            rs=ps.executeQuery();
            
            System.out.println("\nPRODUCT DETAILS");
            System.out.println("PRODUCT ID \t PRODUCT NAME \t PRODUCT QUANTITY   PRODUCT PRICE     CGST");
            
            while(rs.next())
            {
                System.out.println("  "+rs.getInt(1)+"\t\t    "+rs.getString(2)+"\t      "+rs.getInt(5)+"\t\t"+rs.getInt(3)+"\t\t"+rs.getInt(4));
                System.out.println();
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        } 
    }
    
    public void viewPurchaseHistory(Customer c)
    {
        FileWriter writer=null;
        BufferedWriter buffer=null;
        
        int orderId,productId,productQuantity;
        String productName,orderDate,orderTime;
        float netamount;
        try
        {      
          Connection con=getConnection();
          String query="select distinct id from orders where customerid=?";
          ps=con.prepareStatement(query);
          ps.setInt(1,c.customerId);
          rs=ps.executeQuery();
          
          if(rs.isBeforeFirst())
          {
              File file=new File("./CUSTOMER/"+c.customerId+"-"+c.customerName+".txt");
              if(file.exists())
              {
                  file.delete();
              }
        
              writer = new FileWriter("./CUSTOMER/"+c.customerId+"-"+c.customerName+".txt",true);  
              buffer = new BufferedWriter(writer);  
              buffer.write("CUSTOMER ID:"+c.customerId);  
              buffer.newLine();
              buffer.write("CUSTOMER NAME:"+c.customerName);  
              buffer.newLine();
              buffer.write("STATE:"+c.customerState);   
              buffer.newLine();
              buffer.flush();  
              
          while(rs.next())
          {
              orderId=rs.getInt(1);
              buffer.newLine();
              buffer.write("ORDER ID:"+orderId); 
              buffer.newLine();
              String query2="select netamount from payments where customerid=? and orderid=?";
              PreparedStatement ps2=con.prepareStatement(query2);
              ps2.setInt(1,c.customerId);
              ps2.setInt(2,orderId);
              ResultSet rs2=ps2.executeQuery();
              
           if(rs2.next())
           {
              netamount=rs2.getFloat(1);
              buffer.write("AMOUNT PAID:"+netamount); 
              buffer.newLine();
              buffer.write("------------------------------------------------------------------------------");
              buffer.newLine();
              buffer.write("   PRODUCT ID \t  PRODUCT NAME\t   QUANTITY\tORDER DATE \t ORDER TIME  ");
              buffer.newLine();
              buffer.write("------------------------------------------------------------------------------");
              buffer.newLine();
              buffer.flush();
              
              String query1="select productid,productname,productquantity,orderdate,ordertime from orders where customerid=? and id=? order by orderdate desc";
              PreparedStatement ps1=con.prepareStatement(query1);
              ps1.setInt(1,c.customerId);
              ps1.setInt(2, orderId);
              ResultSet rs1=ps1.executeQuery();
              
              while(rs1.next())
              {
                  productId=rs1.getInt(1);
                  productName=rs1.getString(2);
                  productQuantity=rs1.getInt(3);
                  orderDate=rs1.getString(4);
                  orderTime=rs1.getString(5);
                  
                  buffer.write("      "+productId);
                  buffer.write("\t      "+productName);
                  buffer.write("\t      "+productQuantity);
                  buffer.write("         "+orderDate);
                  buffer.write("\t  "+orderTime);
                  buffer.newLine();
                  buffer.flush();
              }
           }
          }
          System.out.println("Your Purchase History File has been downloaded as a text file");
       }
       else
       {
           System.out.println("You have not purchased anything till date");
       }
            
    }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
         
    }
    
    public void viewCustomerDetails(Customer c)
    {
        int customerId,customerPoints;
        String customerName,customerState,customerPassword;
        char ch;
        
        String ncustomerName,ncustomerState,ncustomerPassword;
        
        try
        {
            Connection con=getConnection();
            String query="select * from customer where id=?";
            ps=con.prepareStatement(query);  
            ps.setInt(1,c.customerId);
            rs=ps.executeQuery();
            
            if(rs.next())
            {
                customerId=rs.getInt(1);
                customerName=rs.getString(2);
                customerState=rs.getString(3);
                customerPassword=rs.getString(4);
                customerPoints=rs.getInt(5);
                
                System.out.println("\nPROFILE INFO");
                System.out.println("CUSTOMER ID:"+customerId);
                System.out.println("CUSTOMER NAME:"+customerName);
                System.out.println("CUSTOMER STATE:"+customerState);
                System.out.println("CUSTOMER PASSWORD:"+customerPassword);
                System.out.println("CUSTOMER POINTS:"+customerPoints);
                
                System.out.println("\nDo you want to update your Profile Details:Y or N");
                ch=sc.next().charAt(0);
                
                if(ch=='y' || ch=='Y')
                {
                    System.out.println("\nUPDATE PROFILE");
                    System.out.println("Enter your new Customer Name:");
                    ncustomerName=sc.next();
                    System.out.println("Enter your new Customer State:");
                    ncustomerState=sc.next();
                    System.out.println("Enter your new Customer Password:");
                    ncustomerPassword=sc.next();
                    
                    PreparedStatement ps1=con.prepareStatement("update customer set name=?,state=?,password=? where id=?");
                    ps1.setString(1,ncustomerName);
                    ps1.setString(2,ncustomerState);
                    ps1.setString(3,ncustomerPassword);
                    ps1.setInt(4,customerId);
                    int check=ps1.executeUpdate();
                    
                    if(check>0)
                    {
                        System.out.println("Profile updated successfully");
                    }
                    else
                    {
                        System.out.println("Profile updated failure");
                    }
                }
                
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        
    }
    
}
