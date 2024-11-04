package Pengoc;

import java.util.Scanner;
import java.sql.*;

public class Report {
    Scanner input = new Scanner(System.in);
    Transaction_Management tm = new Transaction_Management();
    Customer_Management cm = new Customer_Management();
    config conf = new config();
    
    public void report_type(){
        boolean exit = true;
        do{
            System.out.println("+----------------------------------------------------------------------------------------------------+");
            System.out.printf("|%-25s%-50s%-25s|\n","","**Report**","");
            System.out.printf("|%-5s%-95s|\n","","1. General Report");
            System.out.printf("|%-5s%-95s|\n","","2. Individual Report");
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
                    tm.view();
                    break;
                case 2:
                    tm.view();
                    IndividualView();
                    break;
                default:
                    exit = false;
                    break;
            }
        }while(exit);
    }
    private void IndividualView() {
        boolean exit = true;
        System.out.println("+----------------------------------------------------------------------------------------------------+");
        System.out.printf("|%-25s%-50s%-25s|\n", "", "**Individual Report**", "");
        System.out.printf("|%-25s%-50s%-25s|\n", "", "**!Enter 0 in ID to Exit!**", "");
        System.out.print("|\tEnter ID to View: ");

        int id;
        while (true) {
            try {
                id = input.nextInt();
                if (doesIDexists(id, conf)) {
                    break;
                } else if (id == 0) {
                    exit = false;
                    break;
                } else {
                    System.out.print("|\tEnter ID to View Again: ");
                }
            } catch (Exception e) {
                input.next();
                System.out.print("|\tEnter ID to View Again: ");
            }
        }

        if (exit) {
            try {
                String customerSQL = "SELECT Cm_fname, Cm_mname, Cm_lname, Cm_gender, Cm_Contact, Cm_Birth_Date, Cm_Balance, Cm_Update_Date FROM Customer_Management WHERE Cm_Id = ?";
                PreparedStatement customerStmt = conf.connectDB().prepareStatement(customerSQL);
                customerStmt.setInt(1, id);
                ResultSet customerRs = customerStmt.executeQuery();

                if (customerRs.next()) {
                    System.out.println("+----------------------------------------------------------------------------------------------------+");
                    System.out.printf("|%-25s%-50s%-25s|\n", "", "Individual Customer Information", "");
                    System.out.printf("|%-15s: %-60s|\n", "First Name", customerRs.getString("Cm_fname"));
                    System.out.printf("|%-15s: %-60s|\n", "Middle Name", customerRs.getString("Cm_mname"));
                    System.out.printf("|%-15s: %-60s|\n", "Last Name", customerRs.getString("Cm_lname"));
                    System.out.printf("|%-15s: %-60s|\n", "Gender", customerRs.getString("Cm_gender"));
                    System.out.printf("|%-15s: %-60s|\n", "Contact", customerRs.getString("Cm_Contact"));
                    System.out.printf("|%-15s: %-60s|\n", "Birthdate", customerRs.getString("Cm_Birth_Date"));
                    System.out.printf("|%-15s: %-60s|\n", "Balance", customerRs.getString("Cm_Balance"));
                    System.out.printf("|%-15s: %-60s|\n", "Updated Date", customerRs.getString("Cm_Update_Date"));
                    System.out.println("+----------------------------------------------------------------------------------------------------+");

                    String transactionSQL = "SELECT Tm_Transaction_Date, Tm_Status, Tm_Amount FROM Transaction_History WHERE Cms_Id = ?";
                    PreparedStatement transactionStmt = conf.connectDB().prepareStatement(transactionSQL);
                    transactionStmt.setInt(1, id);
                    ResultSet transactionRs = transactionStmt.executeQuery();

                    System.out.printf("|%-25s%-50s%-25s|\n", "", "**Transaction History**", "");
                    System.out.println("+-------------------------------+-------------------------------+-------------------------------+");
                    System.out.printf("| %-28s | %-28s | %-28s |\n", "Tm_Transaction_Date", "Tm_Status", "Tm_Amount");
                    System.out.println("+-------------------------------+-------------------------------+-------------------------------+");

                    boolean hasTransactions = false;
                    while (transactionRs.next()) {
                        hasTransactions = true;
                        System.out.printf("| %-28s | %-28s | %-28s |\n", 
                            transactionRs.getString("Tm_Transaction_Date"), 
                            transactionRs.getString("Tm_Status"), 
                            transactionRs.getString("Tm_Amount"));
                    }

                    if (!hasTransactions) {
                        System.out.println("| No transaction history available for this customer.");
                    }

                    System.out.println("+-------------------------------+-------------------------------+-------------------------------+");

                    transactionRs.close();
                    transactionStmt.close();
                } else {
                    System.out.println("|\tNo record found for ID: " + id + " |");
                }

                customerRs.close();
                customerStmt.close();
            } catch (Exception e) {
                System.out.println("|\tError retrieving data: " + e.getMessage() + " |");
            }
        }
    }

    
    private boolean doesIDexists(int id, config conf) {
        String query = "SELECT COUNT(*) FROM Customer_Management Where Cm_Id = ?";
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
    
}
