import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AnswerServlet extends HttpServlet{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Base of the JSON array with users that will be returned
        JsonArrayBuilder builder = Json.createArrayBuilder();

        try {
            // Create the link with the DB
            Connection conn = Connect.connect();

            // Execute SQL query
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM ANSWER";
            ResultSet rs = stmt.executeQuery(sql);


            // Extract data from result set
            while(rs.next()){
                int id  = rs.getInt("AnswerID");
                String answer = rs.getString("Answer");
                int userId = rs.getInt("UserID");
                int questionId = rs.getInt("QuestionID");

                builder.add(Json.createObjectBuilder()
                        .add("AnswerID",id)
                        .add("Answer",answer)
                        .add("UserID",userId)
                        .add("QuestionID",questionId));
            }

            JsonArray UserArray = builder.build();
            out.print(UserArray.toString());

            // Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch(Exception e) {
            out.print("<p> " + e.toString() + "</p>");
        }
    }
    /**
     * We use the post method for DELETE,PUT,POST because the form can't send these types of request
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = Connect.connect();
            PreparedStatement pstmt;

            if (request.getParameter("answer") != null) {
                String sql = "INSERT INTO ANSWER (AnswerID,Answer,UserID,QuestionID) VALUES(?,?,?,?);";
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, null); //null because of AUTO-INCREMENT in DB
                pstmt.setString(2, request.getParameter("answer"));
                pstmt.setString(3, request.getParameter("userID"));
                pstmt.setString(4, request.getParameter("questionID"));
            } else if(request.getParameter("oldAnswer") != null){
                String sql = "UPDATE ANSWER SET ANSWER.Answer= CASE " +
                        "WHEN ANSWER.UserID=? AND ANSWER.Answer=? THEN ? " +
                        "ELSE ANSWER.Answer END";
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1,request.getParameter("userID"));
                pstmt.setString(2,request.getParameter("oldAnswer"));
                pstmt.setString(3,request.getParameter("newAnswer"));
            } else {
                String sql = "DELETE FROM ANSWER WHERE " +
                        "ANSWER.Answer = ? AND ANSWER.UserID = ?";
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, request.getParameter("removeAnswer"));
                pstmt.setString(2, request.getParameter("userID"));
            }
            pstmt.executeUpdate();
            pstmt.close();

        } catch (Exception e){
            out.print("<p> " + e.toString() + "</p>");
        }
    }
}
