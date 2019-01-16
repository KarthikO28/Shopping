package Customer;

public class Product {

    String name;
    int price,id,cgst,sgst,amount,quantity;
  
    public Product(){ }
    
    public Product(int id,String name,int quantity,int price,int cgst,int sgst,int amount)
    {
        this.id=id;
        this.name=name;
        this.quantity=quantity;
        this.price=price;
        this.cgst=cgst;
        this.sgst=sgst;
        this.amount=amount;
    }
    
}
