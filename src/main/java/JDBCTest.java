import com.mysql.cj.jdbc.Driver;

import java.sql.*;

public class JDBCTest {
    static final String DB_URL = "jdbc:mysql://localhost/student";
    public static void main(String args[]){
        Connection conn=null;
        try{
            Driver driver=new Driver();
            System.out.println("Connecting to database...");
            conn=DriverManager.getConnection(DB_URL,"root","moneyview");
            if(conn!=null){
                System.out.println("Connected to the db");
            }
            Statement statement=conn.createStatement();
            String sql="select * from bank";
            ResultSet rs=statement.executeQuery(sql);
            while(rs.next()){
                int id=rs.getInt(1);
                String name=rs.getString(2);
                String address=rs.getString(3);

                System.out.println("ID: "+id+"\nName: "+name+"\nAddress: "+address);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
}
