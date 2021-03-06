import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * The page that is used to manage the answers from the DB
 * React differently for every request type
 * GET: get the whole list of answers given
 * POST: add an answer to the DB for a user and a question
 * PUT: modify the text of an given answer
 * DELETE: delete an answer
 */

public class AnswerServlet extends HttpServlet{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Base of the JSON array with users that will be returned
        JsonArrayBuilder builder = Json.createArrayBuilder();

        try {
            // Create the link with the DB
            Connection conn = Connect.connect();

            // Execute SQL query
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM QuestionsAsked";
            ResultSet rs = stmt.executeQuery(sql);


            // Extract data from result set
            while(rs.next()){
                int id  = rs.getInt("ID");
                //we can't load JSON if a value is null so I set it manually
                String answer = (rs.getString("Answer") == null) ? "null" : rs.getString("Answer");
                int userId = rs.getInt("UserID");
                int questionId = rs.getInt("QuestionID");

                builder.add(Json.createObjectBuilder()
                        .add("ID",id)
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
            out.print(e.toString());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = Connect.connect();
            PreparedStatement pstmt;

            String sql = "INSERT INTO QuestionsAsked (ID,Answer,UserID,QuestionID) VALUES(?,?,?,?);";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, null); //null because of AUTO-INCREMENT in DB
            pstmt.setString(2, request.getParameter("answer"));
            pstmt.setString(3, request.getParameter("userID"));
            pstmt.setString(4, request.getParameter("questionID"));

            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (Exception e){
            out.print(e.toString());
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = Connect.connect();

            String sql = "UPDATE QuestionsAsked SET QuestionsAsked.Answer= CASE " +
                    "WHEN QuestionsAsked.UserID=? AND QuestionsAsked.Answer=? THEN ? " +
                    "ELSE QuestionsAsked.Answer END";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1,request.getParameter("userID"));
            pstmt.setString(2,request.getParameter("oldAnswer"));
            pstmt.setString(3,request.getParameter("newAnswer"));

            out.print("The answer has been modified from \"" + request.getParameter("oldAnswer")
                + "\" to \"" + request.getParameter("newAnswer") + "\"");
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            out.print(e.toString());
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = Connect.connect();

            String sql = "DELETE FROM QuestionsAsked WHERE " +
                    "QuestionsAsked.Answer = ? AND QuestionsAsked.UserID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, request.getParameter("removeAnswer"));
            pstmt.setString(2, request.getParameter("userID"));

            out.print("The answer has been removed");
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            out.print(e.toString());
        }
    }
}
