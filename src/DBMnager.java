import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBManager {
    private static final String URL = "jdbc:mysql://localhost:3306/librarydb";
    private static final String USER = "root";
    private static final String PASSWORD = "1379";

    // Establish database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Verify login credentials
    public static boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM account WHERE uname = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if credentials match

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // Insert new user (Signup)
    public static boolean insertUser(String username, String name, String password, String question, String answer) {
        String checkSql = "SELECT COUNT(*) FROM account WHERE uname = ?";
        String insertSql = "INSERT INTO account (uname, name, password, s_q, s_a) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                System.out.println("Username already exists. Please choose another.");
                return false; 
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, name);
                insertStmt.setString(3, password);
                insertStmt.setString(4, question);
                insertStmt.setString(5, answer);
                return insertStmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }
    
    // Fetch issued BOOK
    public static List<String[]> getIssuedBooks() {
        List<String[]> books = new ArrayList<>();
        String sql = "SELECT date, b_id, s_id, sname, sbranch, ssem FROM b_issued";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(new String[]{rs.getString("date"), rs.getString("b_id"), rs.getString("s_id") , rs.getString("sname"), rs.getString("sbranch"), rs.getString("ssem")});
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return books;
    }
    
    //fetch retuned books 
        public static List<String[]> getReturnedBooks() {
        List<String[]> books = new ArrayList<>();
        String sql = "SELECT date, b_id, s_id, sname, sbranch, ssem FROM b_returned";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(new String[]{rs.getString("date"), rs.getString("b_id"), rs.getString("s_id") , rs.getString("sname"), rs.getString("sbranch"), rs.getString("ssem")});
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return books;
    }

    // Fetch all BOOK details
    public static List<String[]> getBooks() {
        List<String[]> books = new ArrayList<>();
        String sql = "SELECT bid, bname, bauthor, bprice, bcopies FROM book";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(new String[]{rs.getString("bid"),rs.getString("bname") , rs.getString("bauthor"), rs.getString("bprice"), rs.getString("bcopies")});
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return books;
    }
    
    // Fetch all student usernames and names
    public static List<String[]> getStudents() {
        List<String[]> students = new ArrayList<>();
        String sql = "SELECT sid, sname,sbranch FROM student";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                students.add(new String[]{rs.getString("sid"),rs.getString("sname") , rs.getString("sbranch")});
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return students;
    }
    
    // Fetch and return the password for a given username
    public static String getPassword(String username,  String question, String answer) {
            String sql = "SELECT password FROM account WHERE uname = ? AND s_q = ? AND s_a = ?";

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

             stmt.setString(1, username);
             stmt.setString(2, question);
             stmt.setString(3, answer);
             ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
            return rs.getString("password"); 
            }
     } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    }
    return null; 
    }
    
    //add new book
    public static boolean insertBook(String bid, String bname, String bauthor, String bprice, String bcopies) {
        String checkSql = "SELECT COUNT(*) FROM book WHERE bid = ?";
        String insertSql = "INSERT INTO book (bid, bname, bauthor, bprice, bcopies) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, Integer.parseInt(bid));
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                System.out.println("Book ID already exists. Cannot insert duplicate.");
                return false; 
                
            }

            
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, Integer.parseInt(bid));
                insertStmt.setString(2, bname);
                insertStmt.setString(3, bauthor);
                insertStmt.setInt(4, Integer.parseInt(bprice));
                insertStmt.setInt(5, Integer.parseInt(bcopies));

                return insertStmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + e.getMessage());
            return false;
        }
    }
    public static String[] SearchBook(String bid){
        String searchSql = "SELECT bname,bauthor FROM book WHERE bid = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(searchSql)) {

             stmt.setInt(1, Integer.parseInt(bid));
             ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
            String bname = rs.getString("bname");
            String bauthor = rs.getString("bauthor");
            return new String[]{bname, bauthor};
            }
     } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    } catch (NumberFormatException e) {
        System.out.println("Invalid book ID format: " + e.getMessage());
    }
    
    return null; 
}
    
    //removeBook
    
    public static boolean deleteBook(String bid) {
    String sql = "DELETE FROM book WHERE bid = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, Integer.parseInt(bid));
        return stmt.executeUpdate() > 0; 

    } catch (SQLException e) {
        System.err.println("Database error: " + e.getMessage());
    } catch (NumberFormatException e) {
        System.err.println("Invalid book ID format: " + e.getMessage());
    }
    return false; 
    }

     //insert new student
        public static boolean insertStudent(String sid, String sname, String branch, String sem) {
        String checkSql = "SELECT COUNT(*) FROM student WHERE sid = ?";
        String insertSql = "INSERT INTO student (sid, sname, sbranch, sem) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, Integer.parseInt(sid));
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                System.out.println("Student ID already exists. Cannot insert duplicate.");
                return false; 
                
            }

            
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, Integer.parseInt(sid));
                insertStmt.setString(2, sname);
                insertStmt.setString(3, branch);
                insertStmt.setInt(4, Integer.parseInt(sem));

                return insertStmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + e.getMessage());
            return false;
        }
    }
    //issue book
    public static boolean issueBook(Date issueDate, String bookId, String studentId, String studentName, String studentBranch, String studentSemester) {
        String checkAvailabilitySql = "SELECT bcopies FROM book WHERE bid = ?";
        String updateBookSql = "UPDATE book SET bcopies = bcopies - 1 WHERE bid = ?";
        String insertsql = "INSERT INTO b_issued (date, b_id, s_id, sname, sbranch, ssem) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkAvailabilitySql);
             PreparedStatement updateStmt = conn.prepareStatement(updateBookSql);
             PreparedStatement stmt = conn.prepareStatement(insertsql)) {
        
        // Check available copies
        checkStmt.setString(1, bookId);
        ResultSet rs = checkStmt.executeQuery();
        
        if (rs.next()) {
            int availableCopies = rs.getInt("bcopies");

            if (availableCopies > 0) {
            updateStmt.setString(1, bookId);
            updateStmt.executeUpdate();
            stmt.setDate(1, new java.sql.Date(issueDate.getTime())); 
            stmt.setString(2, bookId);
            stmt.setString(3, studentId);
            stmt.setString(4, studentName);
            stmt.setString(5, studentBranch);
            stmt.setString(6, studentSemester);
            
            stmt.executeUpdate();
                return true; 
            } else {
                return false; 
            }
        } else {
            return false; 
        }
    } catch (SQLException e) {
        System.err.println("Database error: " + e.getMessage());
        return false;
    }
    }
    
    //return book
    public static boolean returnBook(Date issueDate, String bookId, String studentId, String studentName, String studentBranch, String studentSemester) {
    String checkIssuedSql = "SELECT * FROM b_issued WHERE b_id = ? AND s_id = ?";
    String updateBookSql = "UPDATE book SET bcopies = bcopies + 1 WHERE bid = ?";
    String RemoveIssued = "DELETE FROM b_issued WHERE `date` = ? AND b_id = ? AND s_id = ?";
    String returnsql = "INSERT INTO b_returned (date, b_id, s_id, sname, sbranch, ssem) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = getConnection();
        PreparedStatement updateStmt = conn.prepareStatement(updateBookSql);
        PreparedStatement returnStmt = conn.prepareStatement(returnsql);
        PreparedStatement checkStmt = conn.prepareStatement(checkIssuedSql);
        PreparedStatement removestmt = conn.prepareStatement(RemoveIssued)) {
        
        // Check if the book was actually issued
        checkStmt.setString(1, bookId);
        checkStmt.setString(2, studentId);
        ResultSet rs = checkStmt.executeQuery();
        
        if (!rs.next()) {
            System.err.println("Book with ID " + bookId + " was not issued to student ID " + studentId);
            return false; // Book was not issued
        }
        
        //for adding in returned
        returnStmt.setDate(1, new java.sql.Date(issueDate.getTime())); 
        returnStmt.setString(2, bookId);
        returnStmt.setString(3, studentId);
        returnStmt.setString(4, studentName);
        returnStmt.setString(5, studentBranch);
        returnStmt.setString(6, studentSemester);
        
        //for removing in issued
        removestmt.setDate(1, new java.sql.Date(issueDate.getTime())); 
        removestmt.setString(2, bookId);
        removestmt.setString(3, studentId);
        removestmt.executeUpdate();
        
        //update
        updateStmt.setString(1, bookId);
        updateStmt.executeUpdate();

        
        return returnStmt.executeUpdate() > 0; 

    } catch (SQLException e) {
        System.err.println("Database error: " + e.getMessage());
    } catch (NumberFormatException e) {
        System.err.println("Invalid book ID format: " + e.getMessage());
    }
    return false; 
    }
    
    //find book
    public static String[] FindBook(String bid){
        String searchSql = "SELECT bname,bauthor,bprice FROM book WHERE bid = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(searchSql)) {

             stmt.setInt(1, Integer.parseInt(bid));
             ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
            String bname = rs.getString("bname");
            String bauthor = rs.getString("bauthor");
            String bprice = rs.getString("bprice");
            return new String[]{bname, bauthor, bprice};
            }
     } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    } catch (NumberFormatException e) {
        System.out.println("Invalid book ID format: " + e.getMessage());
    }
    
    return null; 
    }
    
    //find student
    public static String[] FindStudent(String sid){
        String searchSql = "SELECT sname,sbranch,sem FROM student WHERE sid = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(searchSql)) {

             stmt.setInt(1, Integer.parseInt(sid));
             ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
            String sname = rs.getString("sname");
            String sbranch = rs.getString("sbranch");
            String sem = rs.getString("sem");
            return new String[]{sname, sbranch, sem};
            }
     } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    } catch (NumberFormatException e) {
        System.out.println("Invalid book ID format: " + e.getMessage());
    }
    
    return null; 
    }
}
