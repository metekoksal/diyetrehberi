package diyetrehberi.diyetrehberi;

import java.sql.*;

public class Database {
    private static Database instance;
    private Connection connection;

    private static final String DB_URL = "jdbc:sqlite:diyetrehberi.db";

    private Database() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Veritabanına bağlanıldı.");
            createTestTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTestTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS test (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                age INTEGER
            );
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Test tablosu kontrol edildi / oluşturuldu.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Veri ekleme fonksiyonu
    public void insertTest(String name, int age) {
        String sql = "INSERT INTO test(name, age) VALUES(?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.executeUpdate();
            System.out.println("Veri eklendi: " + name + ", " + age);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Veri okuma fonksiyonu
    public void readTestTable() {
        String sql = "SELECT * FROM test";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("Test tablosundaki veriler:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                System.out.println("ID: " + id + ", İsim: " + name + ", Yaş: " + age);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
