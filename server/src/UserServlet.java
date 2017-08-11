import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.json.*;


/**
 * Created by alexandre on 14/07/17.
 * React differently for every request type
 * GET: get the list of users
 * POST: add any user to DB
 * PUT: modify an user existing and create it otherwise
 * DELETE: delete an user
 */

public class UserServlet extends HttpServlet {

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
            String sql = "SELECT * FROM PARTICIPANT";
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set
            while(rs.next()){
                int id  = rs.getInt("ParticipantID");
                String name = rs.getString("Name");

                builder.add(Json.createObjectBuilder()
                    .add("ParticipantID",id)
                    .add("Name",name));
            }

            JsonArray UserArray = builder.build();
            //request.setAttribute("userArray",UserArray);
            //request.getRequestDispatcher("").forward(request,response);
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
            String oldName = request.getParameter("oldName");

            if (request.getParameter("name") != null) {
                String sql = "INSERT INTO PARTICIPANT (ParticipantID,Password,Name) VALUES(?,?,?);";
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, null); //null because of AUTO-INCREMENT in DB
                pstmt.setString(2,request.getParameter("password"));
                pstmt.setString(3, request.getParameter("name"));
            }
            else if(oldName != null){

                if(Connect.md5Hash(request.getParameter("password")).equals(Connect.getPassword(oldName))){
                    String sql = "UPDATE PARTICIPANT SET Name=? WHERE PARTICIPANT.Name=?";

                    pstmt = conn.prepareStatement(sql);

                    pstmt.setString(1,request.getParameter("newName"));
                    pstmt.setString(2,request.getParameter("oldName"));
                }
                else{
                    pstmt= null;
                    out.print("Password or User error !");
                }
            }
            else {
                if(Connect.md5Hash(request.getParameter("password")).equals(
                        Connect.getPassword(request.getParameter("removeName")))){
                    String sql = "DELETE FROM PARTICIPANT WHERE PARTICIPANT.Name=?;";
                    pstmt = conn.prepareStatement(sql);

                    pstmt.setString(1,request.getParameter("removeName"));
                }
                else{
                    pstmt = null;
                    out.print("Password error can't delete the user");
                }
            }
            if (pstmt != null) {
                pstmt.executeUpdate();
                pstmt.close();
            }
        } catch (Exception e){
           out.print("<p> " + e.toString() + "</p>");
        }
    }
}//end of the class