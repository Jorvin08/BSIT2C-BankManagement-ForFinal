package Pengoc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.sql.*;

public class Transaction_Management {
    Scanner input = new Scanner(System.in);
    config conf = new config();
    Customer_Management cm = new Customer_Management();
    
    public void t_manage(){
        boolean exit = true;
        do{
            System.out.println("+----------------------------------------------------------------------------------------------------+");
            System.out.printf("|%-25s%-50s%-25s|\n","","**Manage Transaction**","");
            System.out.printf("|%-5s%-95s|\n","","1. Add");
            System.out.printf("|%-5s%-95s|\n","","2. View");
            System.out.printf("|%-5s%-95s|\n","","3. Exit");
            System.out.printf("|%-5sEnter Choice: ","");
            int choice;
            while(true){
                try{
                    choice = input.nextInt();
                    if(choice>0 && choice<4){
                        break;
                    }else{
                        System.out.printf("|%-5sEnter Choice Again: ","");
                    }
                }catch(Exception e){
                    input.next();
                    System.out.printf("|%-5sEnter Choice Again: ","");
                }
            }
            switch(choice){
                case 1:
                    cm.view();
                    add();
                    break;
                case 2:
                    view();
                    break;
                default:
                    exit = false;
                    break;
            }
        }while(exit);
    }
    
    private void add(){
        boolean exit = true;
        LocalDate cdate = LocalDate.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate bdate;
        System.out.println("+----------------------------------------------------------------------------------------------------+");
        System.out.printf("|%-25s%-50s%-25s|\n","","**Add Transaction**","");
        System.out.printf("|%-25s%-50s%-25s|\n","","**!Enter 0 in ID to Exit!**","");
        System.out.print("|\tEnter ID to Transact: ");
        int id;
        while(true){
            try{
                id = input.nextInt();
                if(doesIDexists(id, conf)){
                    break;
                }else if(id == 0){
                    exit = false;
                    break;
                }else{
                    System.out.print("|\tEnter ID to Transact Again: ");
                }
            }catch(Exception e){
                input.next();
                System.out.print("|\tEnter ID to Transact Again: ");
            }
        }
        while(exit){
            System.out.print("|\tEnter Status (Withdraw/Deposit): ");
            String stat;
            while(true){
                try{
                    stat = input.next();
                    if(stat.equalsIgnoreCase("withdraw") || stat.equalsIgnoreCase("deposit")){
                        break;
                    }else{
                        System.out.print("|\tEnter Status Again(Withdraw/Deposit): ");
                    }
                }catch(Exception e){
                    System.out.print("|\tEnter Status Again(Withdraw/Deposit): ");
                }
            }
            double cbalance = getCurrentBalance(id);
            double cash;
            if(stat.equalsIgnoreCase("withdraw")){
                System.out.print("|\tEnter Amount to Withdraw: ");
                while(true){
                    try{
                        cash = input.nextDouble();
                        if(cash>=0 && cash<=cbalance){
                            cbalance -= cash;
                            break;
                        }else{
                            System.out.printf("|%-25s%-32s%-18.2lf%-25s|\n","","**Warning Your Balance is only: ",cbalance,"");
                            System.out.print("|\tEnter Amount to Withdraw Again: ");
                        }
                    }catch(Exception e){
                        input.next();
                        System.out.print("|\tEnter Amount to Withdraw Again: ");
                    }
                }
            }else{
                System.out.print("|\tEnter Amount to deposit: ");
                while(true){
                    try{
                        cash = input.nextDouble();
                        if(cash>=0){
                            cbalance += cash;
                            break;
                        }else{
                            System.out.print("|\tEnter Amount to deposit Again: ");
                        }
                    }catch(Exception e){
                        input.next();
                        System.out.print("|\tEnter Amount to deposit Again: ");
                    }
                }
            }
            String SQL = "INSERT INTO Transaction_History (Cms_Id, Tm_Transaction_Date, Tm_Status, Tm_Amount, Tm_Balance) Values (?,?,?,?,?)";
            String SQL2 = "UPDATE Customer_Management SET Cm_Balance = ?, Cm_Update_Date = ? Where Cm_Id = ?";
            
            conf.addRecord(SQL, id, cdate, stat, cash, cbalance);
            conf.updateRecord(SQL2, cbalance, cdate, id);
            exit = false;
        }
    }
    
    public void view(){
        String tbl_view = "SELECT * FROM Customer_Management";
        String[] tbl_Headers = {"ID", "First Name", "Last Name", "Balance"};
        String[] tbl_Columns = {"Cm_Id", "Cm_fname", "Cm_lname", "Cm_Balance"};
        config conf = new config();
        conf.viewRecords(tbl_view, tbl_Headers, tbl_Columns);
    }
    
    //validation tanan ubos
    private boolean doesIDexists(int id, config conf) {
        String query = "SELECT COUNT(*) FROM CM_summary Where Cms_Id = ?";
        try (Connection conn = conf.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("|\tError checking Report ID: " + e.getMessage());
        }
        return false;
    }
    private double getCurrentBalance(int id) {
        String query = "SELECT Cm_Balance FROM Customer_Management WHERE Cm_Id = ?";
        try (Connection conn = conf.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Cm_Balance");
            }
        } catch (SQLException e) {
            System.out.println("|\tError retrieving balance: " + e.getMessage());
        }
        return 0.0;
    }
}
