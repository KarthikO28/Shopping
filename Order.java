package Customer;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.text.*;
import java.util.Date;

public class Order {
    
    Scanner sc=new Scanner(System.in);
    FileWriter writer=null;
    BufferedWriter buffer=null;
           
    PreparedStatement ps;
    ResultSet rs;
    static HashMap<String,Integer> SGSTdetails=new HashMap<String,Integer>();
    
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
 
  public int getSGST(String state)
  {
      return SGSTdetails.get(state);
  }
  
  public void setSGST()
  {
      SGSTdetails.put("TN",5);
      SGSTdetails.put("KN",7);
      SGSTdetails.put("KL",6);
      SGSTdetails.put("UP",4);
      SGSTdetails.put("GJ",3);
      SGSTdetails.put("AP", 5);
  }
  
    public void orderProducts(Customer c)
    {
        setSGST();
        
        int i=1;
        
        String productName;
        int productId,noOfProducts,productPrice,cgst,sgst,totalamount=0,amount=0;
        int quantity,productQuantity;
        float netamount=0f;
        float points=0f;
        
        int orderId=generateOrderId();
        
        System.out.println();
        printCustomerInfo(c);
        System.out.println("ORDER");
        System.out.println("Enter the number of products:");
        noOfProducts=sc.nextInt();
        
        while(i<=noOfProducts)
        {
          System.out.println("\nEnter the Product-"+i+" ID:");
          productId=sc.nextInt();
          System.out.println("Enter the Product-"+i+" Quantity:");
          quantity=sc.nextInt();
          
          try
          {
            Connection con=getConnection();
            String query="select * from product where id=? and quantity >= ?";
            ps=con.prepareStatement(query);
            ps.setInt(1, productId);
            ps.setInt(2,quantity);
            rs=ps.executeQuery();
            
            if(rs.next())
            {
                productName=rs.getString(2);
                productPrice=rs.getInt(3);
                productQuantity=rs.getInt(5);
                cgst=rs.getInt(4);
                sgst=getSGST(c.customerState);
                amount=calculateAmount(productPrice,quantity,sgst,cgst);
                Product p=new Product(productId,productName,quantity,productPrice,sgst,cgst,amount);
                
                boolean check=registerOrder(c,p,productQuantity,orderId);
             
                if(check)
                {
                    totalamount+=amount;
                }
                i++;
            }
            else
            {
                System.out.println("Product not found or insufficient");
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
      }
       
        points=(totalamount*10)/100;
                
        System.out.println("\nYou have scored " +Math.round(points)+" points of the Total Amount");
        System.out.println("Do you wish to discount the points from the Toatal Amount:Y or N");
        char ch=sc.next().charAt(0);
        
        if(ch=='Y' || ch=='y')
        {
           netamount=totalamount-points;
           int num=payment(c,orderId,totalamount,netamount,points);
           if(num>0)
           {
               printNetAmount(totalamount,points,netamount);
           }
        }
        else
        {
            netamount=totalamount;
            setPoints(c,points);
            points=0;
            int num=payment(c,orderId,totalamount,netamount,points);
            if(num>0)
            {
                printNetAmount(totalamount,points,netamount);
            }
        }      
    }
    
    public static int generateOrderId()
   {
       int orderId;
       HashSet<Integer> orderidset=new HashSet<Integer>();
       
       while(true)
       {
           orderId=(int)(Math.random() *  50+ 1001);
           try
           {
            Connection con=getConnection();
            PreparedStatement ps=con.prepareStatement("select distinct id from orders");
            ResultSet rs=ps.executeQuery();
            
            while(rs.next())
            {
              orderidset.add(rs.getInt(1));
            }
            
            if(orderidset.contains(orderId))
            {
                continue;
            }
            else
            {
                return orderId;
            }
           }
           catch(Exception ex)
           {
               System.out.println(ex);
           }
       }
   }
     
    public int calculateAmount(int price,int quantity,int sgst,int cgst)
    {
        return (quantity*price)+sgst+cgst;
    }
    
    public boolean registerOrder(Customer c,Product p,int productQuantity,int orderId)
    {
        SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd");  
        SimpleDateFormat formattime = new SimpleDateFormat("HH:mm:ss");  
        Date orderDate=new Date();
        
        try
        {
            Connection con=getConnection();
            String query="insert into orders(id,customerid,productid,productname,productquantity,orderdate,ordertime) values(?,?,?,?,?,?,?)";
            ps=con.prepareStatement(query);
            ps.setInt(1, orderId);
            ps.setInt(2,c.customerId);
            ps.setInt(3,p.id);
            ps.setString(4, p.name);
            ps.setInt(5,p.quantity);
            ps.setString(6, formatdate.format(orderDate));
            ps.setString(7,formattime.format(orderDate));
            int check=ps.executeUpdate();
            
            if(check>0)
            {
                int orgQuantity=productQuantity-p.quantity;
                ps=con.prepareStatement("update product set quantity=? where id=?");
                ps.setInt(1, orgQuantity);
                ps.setInt(2, p.id);
                int check1=ps.executeUpdate();
                
                if(check1>0)
                {
                    System.out.println("Order Confirmed");
                    printProductInfo(p);   
                    return true;
                }
            }
            else
            {
                System.out.println("Order cancelled due to some problem");
                return false;
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        return false;
    }
    
    public int payment(Customer c,int orderId,float totalamount,float netamount,float discount)
   {
       PreparedStatement ps=null;
       ResultSet rs=null;
       int num=0;
       try
       {
           Connection con=getConnection();
           ps=con.prepareStatement("insert into payments(orderid,customerid,totalamount,discount,netamount) values(?,?,?,?,?)");
           ps.setInt(1,orderId);
           ps.setInt(2, c.customerId);
           ps.setFloat(3, totalamount);
           ps.setFloat(4, discount);
           ps.setFloat(5, netamount);
           num=ps.executeUpdate();
                    
            return num;
       }
       catch(Exception ex)
       {
           System.out.println(ex);
       }
      return num;  
   }
    
    public void setPoints(Customer c,float points)
    {
        c.points+=points;
        try
        {
            Connection con=getConnection();
            String query="update customer set points=? where id=?";
            ps=con.prepareStatement(query);
            ps.setInt(1, Math.round(c.points));
            ps.setInt(2, c.customerId);
            int check=ps.executeUpdate();
            
            if(check>0)
            {
                System.out.println("Points updated successfully");
            }
            else
            {
                System.out.println("Points updated failure");
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }
    
    public void printCustomerInfo(Customer c)
    {
        File file=new File("./INVOICE/"+c.customerId+"-"+c.customerName+".txt");
        if(file.exists())
        {
            file.delete();
        }
                try
                {
                    writer = new FileWriter("./INVOICE/"+c.customerId+"-"+c.customerName+".txt",true);  
                    buffer = new BufferedWriter(writer);  
                    buffer.write("CUSTOMER ID:"+c.customerId);  
                    buffer.newLine();
                    buffer.write("CUSTOMER NAME:"+c.customerName);  
                    buffer.newLine();
                    buffer.write("STATE:"+c.customerState);   
                    buffer.newLine();
                    buffer.write("-------------------------------------------------------------------------");
                    buffer.newLine();
                    buffer.write("  PRODUCT ID   PRODUCT NAME   QUANTITY   PRICE   SGST   CGST   AMOUNT  ");
                    buffer.newLine();
                    buffer.write("-------------------------------------------------------------------------");
                    buffer.newLine();
                    buffer.flush();
                 }
                 catch(Exception ex)
                 {
                    System.out.println(ex);
                 }
             
    }
    
    public void printProductInfo(Product p)
    {
                   try 
                   {
                    buffer.write("     "+p.id);
                    buffer.write("\t"+p.name);
                    buffer.write("\t\t"+p.quantity);
                    buffer.write("\t"+p.price);
                    buffer.write("\t"+p.sgst);
                    buffer.write("\t"+p.cgst);
                    buffer.write("\t"+p.amount);
                    buffer.newLine();
                    buffer.flush();
                   }
                   catch(Exception ex)
                   {
                    System.out.println(ex);
                   }
                   
    }
    
    public void printNetAmount(int totalamount,float points,float netamount)
    {
                    try
                    {
                        buffer.newLine();
                        buffer.write("\t\t\t\t\t\t   TOTAL AMOUNT="+totalamount);
                        buffer.newLine();
                        buffer.write("\t\t\t\t\t\t   DISCOUNT="+points);
                        buffer.newLine();
                        buffer.write("\t\t\t\t\t\t   NET AMOUNT="+netamount);
                        buffer.close();
                        writer.close();
                    }
                    catch(Exception ex)
                    {
                        System.out.println(ex);
                    }                    
                    System.out.println("Invoice generated");
    }
}
