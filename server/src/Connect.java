import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

class Connect {
    private final static String salt="DGE$5SGr@3VsHYUMas2323E4d57vfBfFSTRU@!DSH(*%FDSdfg13sgfsg";

    static Connection connect() throws SQLException, ClassNotFoundException {
        // JDBC driver name and database URL
        String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        String DB_URL="jdbc:mysql://172.17.0.1:3306/app_db";

        //  Database credentials
        String USER = "app_user";
        String PASS = "app_pwd";

        // Register JDBC driver
        Class.forName(JDBC_DRIVER);

        // Open a connection
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    //http://www.codexpedia.com/java/java-md5-hash-example-one-way-hash/
    static String md5Hash(String message) {
        String md5 = "";
        if(null == message) return null;

        message = message+salt;//adding a salt to the string before it gets hashed.
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");//Create MessageDigest object for MD5
            digest.update(message.getBytes(), 0, message.length());//Update input string in message digest
            md5 = new BigInteger(1, digest.digest()).toString(16);//Converts message digest value in base 16 (hex)

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    static String getPassword(String name) throws SQLException, ClassNotFoundException {

        Connection co = connect();
        String sql = "SELECT PARTICIPANT.Password FROM PARTICIPANT WHERE PARTICIPANT.Name='"+name+"'";
        Statement stmt = co.createStatement();

        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String password = rs.getString("Password");

        rs.close();
        stmt.close();
        co.close();

        return password;
    }

    static String getIDfromDB(String name) throws SQLException, ClassNotFoundException {

        Connection co = connect();
        String sql = "SELECT PARTICIPANT.ParticipantID FROM PARTICIPANT WHERE PARTICIPANT.Name='"+name+"'";
        Statement stmt = co.createStatement();

        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String ID = rs.getString("ParticipantID");

        rs.close();
        stmt.close();
        co.close();

        return ID;
    }

    static String getIDfromCookies(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        String id=null;
        for (int i =0; i< cookies.length; i++){
            if (cookies[i].getName().equals("id"))  id = cookies[i].getValue();
        }
        return id;
    }
}
