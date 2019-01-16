package Customer;

import java.util.ArrayList;


public class Customer {

    int customerId;
    float points;
    String customerName,customerState;
    
    public Customer() { }
    
    public Customer(int customerId,String customerName,String customerState,float points)
    {
        this.customerId=customerId;
        this.customerName=customerName;
        this.customerState=customerState;
        this.points=points;
    }  
}
