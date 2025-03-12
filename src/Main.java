import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            System.out.println("Database is connected!");
        } else {
            System.out.println("Failed to connect!");
        }
    }
}
