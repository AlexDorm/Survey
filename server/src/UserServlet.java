import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.json.*;

/**
 * The page that is used to manage the users from the DB
 * React differently for every request type
 * GET: get the list of users
 * POST: add any user to DB
 * PUT: modify the name of an existing user
 * DELETE: delete a user
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = Connect.connect();

            String sql = "INSERT INTO PARTICIPANT (ParticipantID,Password,Name) VALUES(?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, null); //null because of AUTO-INCREMENT in DB
            pstmt.setString(2, request.getParameter("password"));
            pstmt.setString(3, request.getParameter("name"));

            out.print("The user has been added to the DB");
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            out.print(e.toString());
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = Connect.connect();
            PreparedStatement pstmt = null;
            if(Connect.md5Hash(request.getParameter("password"))
                    .equals(Connect.getPassword(request.getParameter("oldName")))){
                String sql = "UPDATE PARTICIPANT SET Name=? WHERE PARTICIPANT.Name=?";

                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1,request.getParameter("newName"));
                pstmt.setString(2,request.getParameter("oldName"));
                out.print("The user name " + request.getParameter("oldName") + " has been set to "
                    + request.getParameter("newName"));
                pstmt.executeUpdate();
                pstmt.close();
                conn.close();
            }
            else{
                out.print("Password or User error !");
            }
        } catch (Exception e) {
            out.print(e.toString());
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = Connect.connect();
            PreparedStatement pstmt = null;
            if(Connect.md5Hash(request.getParameter("password")).equals(
                    Connect.getPassword(request.getParameter("nameToRemove")))){
                String sql = "DELETE FROM PARTICIPANT WHERE PARTICIPANT.Name=? AND " +
                        "PARTICIPANT.Password = ?;";
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1,request.getParameter("nameToRemove"));
                pstmt.setString(2,Connect.md5Hash(request.getParameter("password")));
                out.print("The user " + request.getParameter("nameToRemove") + " has been removed");
                pstmt.executeUpdate();
                pstmt.close();
                conn.close();
            }
            else{
                out.print("Password error can't delete the user");
            }
        } catch (Exception e) {
            out.print(e.toString());
        }
    }
}