package Pengoc;

import java.util.Scanner;
import java.sql.*;

public class Workers {
    Scanner input = new Scanner(System.in);
    config conf = new config();
    
    public void worker(){
        boolean exit = true;
        do{
            System.out.println("+----------------------------------------------------------------------------------------------------+");
            System.out.printf("|%-25s%-50s%-25s|\n","","**Worker Management**","");
            System.out.printf("|%-5s%-95s|\n","","1. Employ");
            System.out.printf("|%-5s%-95s|\n","","2. Edit");
            System.out.printf("|%-5s%-95s|\n","","3. unemploy");
            System.out.printf("|%-5s%-95s|\n","","4. View Employed");
            System.out.printf("|%-5s%-95s|\n","","5. View Unemployed");
            System.out.printf("|%-5s%-95s|\n","","6. Exit");
            System.out.printf("|%-5sEnter Choice: ","");
            int choice;
            while(true){
                try{
                    choice = input.nextInt();
                    if(choice>0 && choice<7){
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
                    Employ();
                    break;
                case 2:
                    viewEmployed();
                    edit();
                    viewEmployed();
                    break;
                case 3:
                    viewEmployed();
                    unemploy();
                    viewEmployed();
                    break;
                case 4:
                    viewEmployed();
                    break;
                case 5:
                    viewUnemployed();
                    break;
                default:
                    exit = false;
                    break;
            }
        }while(exit);
    }
    public void Employ(){
        System.out.println("+----------------------------------------------------------------------------------------------------+");
        System.out.printf("|%-25s%-50s%-25s|\n","","**Employ Workers**","");
        System.out.print("| Enter First Name: ");
        String fname = input.next();
        System.out.print("| Enter Last Name: ");
        String lname = input.next();
        String gender;
        while(true){
            System.out.print("|\tGender (Male/Female): ");
            try{
                gender = input.next();
                if(gender.equalsIgnoreCase("Male")||gender.equalsIgnoreCase("Female")){
                    break;
                }
            }catch(Exception e){
                
            }
        }
        String status = "Employed"; 
        
        String sql = "INSERT INTO Worker_List (w_fname, w_lname, w_gender, w_status) VALUES (?, ?, ?, ?)";
        conf.addRecord(sql, fname, lname, gender, status);
    }
    public void edit(){
        boolean exit = true;
        System.out.println("+----------------------------------------------------------------------------------------------------+");
        System.out.printf("|%-25s%-50s%-25s|\n","","**Edit Customer**","");
        System.out.printf("|%-25s%-50s%-25s|\n","","**!Enter 0 in ID to Exit!**","");
        System.out.print("|\tEnter ID to Edit: ");
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
                    System.out.print("|\tEnter ID to Edit Again: ");
                }
            }catch(Exception e){
                input.next();
                System.out.print("|\tEnter ID to Edit Again: ");
            }
        }
        while(exit){
            System.out.print("| Enter New First Name: ");
            String fname = input.next();
            System.out.print("| Enter New Last Name: ");
            String lname = input.next();
            String gender;
            while(true){
                System.out.print("|\tGender (Male/Female): ");
                try{
                    gender = input.next();
                    if(gender.equalsIgnoreCase("Male")||gender.equalsIgnoreCase("Female")){
                        break;
                    }
                }catch(Exception e){

                }
            }
            String sql = "UPDATE Worker_List SET w_fname = ?, w_lname = ?, w_gender = ? WHERE w_id = ?";
            conf.updateRecord(sql, fname, lname, gender, id);
            exit = false;
        }
    }
    public void unemploy(){
        boolean exit = true;
        System.out.println("+----------------------------------------------------------------------------------------------------+");
        System.out.printf("|%-25s%-50s%-25s|\n","","**Unemploy Worker**","");
        System.out.printf("|%-25s%-50s%-25s|\n","","**!Enter 0 in ID to Exit!**","");
        System.out.print("|\tEnter ID to Unemploy: ");
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
                    System.out.print("|\tEnter ID to Unemploy Again: ");
                }
            }catch(Exception e){
                input.next();
                System.out.print("|\tEnter ID to Unmploy Again: ");
            }
        }
        while(exit){
            String status = "Unemployed";
            String sql = "UPDATE Worker_List SET w_status = ? WHERE w_id = ?";
            conf.updateRecord(sql, status, id);
            exit = false;
        }
    }
    public void viewEmployed(){
        String tbl_view = "SELECT * FROM Worker_List Where w_status = 'Employed'";
        String[] tbl_Headers = {"ID", "First Name", "Last Name", "Gender"};
        String[] tbl_Columns = {"w_id", "w_fname", "w_lname", "w_gender"};
        config conf = new config();
        conf.viewRecords(tbl_view, tbl_Headers, tbl_Columns);
    }
    public void viewUnemployed(){
        String tbl_view = "SELECT * FROM Worker_List Where w_status = 'Unemployed'";
        String[] tbl_Headers = {"ID", "First Name", "Last Name", "Gender"};
        String[] tbl_Columns = {"w_id", "w_fname", "w_lname", "w_gender"};
        config conf = new config();
        conf.viewRecords(tbl_view, tbl_Headers, tbl_Columns);
    }
    private boolean doesIDexists(int id, config conf) {
        String query = "SELECT COUNT(*) FROM Worker_List Where w_id = ? And w_status = 'Employed'";
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
