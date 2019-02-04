import com.mysql.cj.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JDBCConnectivity {
    private static final String DB_URL = "jdbc:mysql://localhost/moneyview";
    private com.mysql.cj.jdbc.Driver driver;
    private Connection conn=null;
    private Statement statement=null;

    public JDBCConnectivity(String username, String password) throws Exception{
        driver=new Driver();
        conn= DriverManager.getConnection(DB_URL,username,password);
        if(conn!=null){
            statement=conn.createStatement();
        }
    }

    public void insertHdfc(HashMap<Integer, List<String>> transactions,int lineLength){
        try{
            String sql="create table if not exists hdfc_transactions (Date_of_transaction text , Narration text , Ref_No text ," +
                    "Value_date text , Withdrawal_amt text , Deposit_amt text , Closing_bal text )";
            statement.executeUpdate(sql);
            sql = "truncate table hdfc_transactions";
            statement.executeUpdate(sql);
            for(int in=0;in<transactions.size();in++){
                List<String> transactionList = transactions.get(in);
                Iterator<String> iterator = transactionList.iterator();
                sql = "insert into hdfc_transactions values(";
                while(iterator.hasNext()){
                    String next = iterator.next();
                    if(!next.equals(""))
                        sql=sql+"'"+next+"'";
                    else
                        sql=sql+"' '";
                    if(iterator.hasNext()){
                        sql=sql+", ";
                    }
                }
                sql=sql+");";
                if(transactionList.size()==lineLength)
                    statement.executeUpdate(sql);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void insertSbi(HashMap<Integer, List<String>> transactions,int lineLength){
        try{
            String sql="create table if not exists sbi_transactions (Date_of_transaction text , Value_date text , Narration text , Ref_No text ," +
                    " Withdrawal_amt text , Deposit_amt text , Closing_bal text )";
            statement.executeUpdate(sql);
            sql = "truncate table sbi_transactions";
            statement.executeUpdate(sql);
            for(int in=0;in<transactions.size();in++){
                List<String> transactionList = transactions.get(in);
                Iterator<String> iterator = transactionList.iterator();
                sql = "insert into sbi_transactions values(";
                while(iterator.hasNext()){
                    String next = iterator.next();
                    if(!next.equals(""))
                        sql=sql+"'"+next+"'";
                    else
                        sql=sql+"' '";
                    if(iterator.hasNext()){
                        sql=sql+", ";
                    }
                }
                sql=sql+");";
                if(transactionList.size()==lineLength)
                    statement.executeUpdate(sql);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
}
