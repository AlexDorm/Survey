import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.json.*;

public class QuestionServlet extends HttpServlet{

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
            String sql = "SELECT * FROM QUESTION";
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
            if (request.getParameter("question") != null) {
                String sql = "INSERT INTO QUESTION (QuestionID,Question) VALUES(?,?);";
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, null); //null because of AUTO-INCREMENT in DB
                pstmt.setString(2, request.getParameter("question"));
            }
            else if(request.getParameter("oldQuestion") != null){
                String sql = "UPDATE QUESTION SET Question=? WHERE QUESTION.Question=?";
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1,request.getParameter("newQuestion"));
                pstmt.setString(2,request.getParameter("oldQuestion"));
            }
            else {
                String sql = "DELETE FROM QUESTION WHERE QUESTION.Question=?;";
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1,request.getParameter("removeQuestion"));
            }

            pstmt.executeUpdate();
            pstmt.close();

        } catch (Exception e){
            out.print("<p> " + e.toString() + "</p>");
        }
    }
}
