import java.io.IOException;
import java.io.PrintWriter;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();

        String name=request.getParameter("passName");
        String password= Connect.md5Hash(request.getParameter("password"));

        JsonArrayBuilder builder = Json.createArrayBuilder();

        try {
            if(password.equals(Connect.getPassword(name))){
                builder.add(Json.createObjectBuilder()
                        .add("userID",Connect.getIDfromDB(name))
                        .add("name",name));
                JsonArray UserArray = builder.build();
                out.print(UserArray);
             } else {
                out.print("Error during connection : username / password does not match");
            }
        } catch (Exception e) {
            out.print(e.toString());
        }
        out.close();
    }
}