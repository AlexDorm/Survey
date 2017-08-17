import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.json.*;

/**
 * The page that is used to manage the questions from the DB
 * React differently for every request type
 * GET: get the list of questions
 * POST: add any question to DB
 * PUT: modify the text of an existing question
 * DELETE: delete a question
 */

public class QuestionServlet extends HttpServlet{

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
            String sql = "SELECT * FROM Question";
            ResultSet rs = stmt.executeQuery(sql);


            // Extract data from result set
            while(rs.next()){
                int id  = rs.getInt("QuestionID");
                String question = rs.getString("Question");

                builder.add(Json.createObjectBuilder()
                        .add("QuestionID",id)
                        .add("Question",question));
            }

            JsonArray UserArray = builder.build();
            out.print(UserArray);

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

            String sql = "INSERT INTO Question (QuestionID,Question) VALUES(?,?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, null); //null because of AUTO-INCREMENT in DB
            pstmt.setString(2, request.getParameter("question"));

            out.print("The question has been added to the DB");
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();

        } catch (Exception e){
            out.print(e.toString());
        }
    }

     protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = Connect.connect();

            String sql = "UPDATE Question SET Question.Question=? WHERE Question.Question=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1,request.getParameter("newQuestion"));
            pstmt.setString(2,request.getParameter("oldQuestion"));

            out.print("The question " + request.getParameter("oldQuestion") +
                    " has been modified to " + request.getParameter("newQuestion"));
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            out.print(e.toString());
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = Connect.connect();

            String sql = "DELETE FROM Question WHERE Question.Question=?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1,request.getParameter("questionToRemove"));

            out.print("The question " + request.getParameter("questionToRemove") +
                    " has been deleted");
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            out.print(e.toString());
        }
    }
}
