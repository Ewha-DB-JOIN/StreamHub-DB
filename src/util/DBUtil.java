package util;

import config.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * MySQL JDBC 연결 관리 유틸리티 (싱글톤)
 *
 * ⚠ 사용 전 아래 3가지를 본인 환경에 맞게 수정하세요:
 *   DB_URL  : 포트, DB 이름 확인
 *   DB_USER : MySQL 계정
 *   DB_PASS : MySQL 비밀번호
 */
public class DBUtil {

    private static final String DB_URL  = DBConfig.DB_URL;
    private static final String DB_USER = DBConfig.DB_USER;
    private static final String DB_PASS = DBConfig.DB_PASS;

    private static DBUtil instance;
    private Connection connection;

    private DBUtil() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("[DB] 연결 성공");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] JDBC 드라이버를 찾을 수 없습니다. lib/ 폴더에 .jar가 있는지 확인하세요.");
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.err.println("[DB] 연결 실패: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static DBUtil getInstance() {
        if (instance == null) {
            instance = new DBUtil();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            }
        } catch (SQLException e) {
            System.err.println("[DB] 재연결 실패: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] 연결 종료");
            }
        } catch (SQLException e) {
            System.err.println("[DB] 종료 오류: " + e.getMessage());
        }
    }
}
