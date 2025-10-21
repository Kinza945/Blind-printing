package com.example.demo.databases;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DBUserResults {
    private static final String DB_URL = "jdbc:sqlite:usersresultbase.db";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS typing_results (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "target_text TEXT NOT NULL, " +
                "typed_text TEXT NOT NULL, " +
                "elapsed_seconds REAL NOT NULL, " +
                "wpm REAL NOT NULL, " +
                "accuracy REAL NOT NULL, " +
                "created_at TEXT NOT NULL" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertResult(String targetText,
                                    String typedText,
                                    double elapsedSeconds,
                                    double wpm,
                                    double accuracy) {
        String sql = "INSERT INTO typing_results(target_text, typed_text, elapsed_seconds, wpm, accuracy, created_at) " +
                "VALUES(?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, targetText);
            pstmt.setString(2, typedText);
            pstmt.setDouble(3, elapsedSeconds);
            pstmt.setDouble(4, wpm);
            pstmt.setDouble(5, accuracy);
            pstmt.setString(6, DATE_TIME_FORMATTER.format(LocalDateTime.now()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<TypingResult> getRecentResults(int limit) {
        String sql = "SELECT id, target_text, typed_text, elapsed_seconds, wpm, accuracy, created_at " +
                "FROM typing_results ORDER BY datetime(created_at) DESC LIMIT ?";
        List<TypingResult> results = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String targetText = rs.getString("target_text");
                    String typedText = rs.getString("typed_text");
                    double elapsedSeconds = rs.getDouble("elapsed_seconds");
                    double wpm = rs.getDouble("wpm");
                    double accuracy = rs.getDouble("accuracy");
                    String createdAtText = rs.getString("created_at");
                    LocalDateTime createdAt = LocalDateTime.parse(createdAtText, DATE_TIME_FORMATTER);

                    results.add(new TypingResult(
                            id,
                            targetText,
                            typedText,
                            elapsedSeconds,
                            wpm,
                            accuracy,
                            createdAt
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}
