package Admin;
import java.util.*;
import java.sql.*;

public class Admin {
    
    int adminId;
    String adminName,adminPassword;
    
    public Admin(){}
    
    public Admin(int id,String name,String password)
    {
        this.adminId=id;
        this.adminName=name;
        this.adminPassword=password;
    }
}
