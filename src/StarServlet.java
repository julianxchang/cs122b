import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import io.github.cdimascio.dotenv.Dotenv;

// This annotation maps this Java Servlet Class to a URL
@WebServlet("/stars")
public class StarServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Dotenv dotenv = Dotenv.load();

        String login = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");
        String url = "jdbc:mysql://localhost:3306/moviedb";

        response.setContentType("text/html");
        String sid = "'" + request.getParameter("sid") + "'";

        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            // create database connection
            Connection connection = DriverManager.getConnection(url, login, password);
            // declare statement
            Statement statement = connection.createStatement();
            // prepare query
            String query = "SELECT name, birthYear FROM stars WHERE id = " + sid;
            ResultSet rs = statement.executeQuery(query);

            String name = "none";
            int birthYear = 0;
            while (rs.next()) {
                name = rs.getString("name");
                birthYear = rs.getInt("birthYear");
            }

            out.print("<h1>" + name + "</h1>");
            out.print("<p> Birth Year: " + (birthYear == 0 ? "Not Stated" : birthYear) + "<p>");

            query = "SELECT * FROM stars_in_movies sim, movies m WHERE sim.starID = " + sid + " AND sim.movieID = m.id";
            rs = statement.executeQuery(query);

            out.print("<h2> Stars In: </h2>");

            ArrayList<String> movieIDs = new ArrayList<>();
            while (rs.next()) {
                String movieID = rs.getString("movieID");
                String title = rs.getString("title");
                String link = "http://" + dotenv.get("HOSTNAME") + ":8080/cs122b_project1_star_example_war_exploded/movie?id=" + movieID;
                out.print("<a href = " + link + ">" + title + "</a><br>");
            }
        } catch (Exception e) {
            out.println("<body>");
            out.println("<p>");
            out.println("Exception in doGet: " + e.getMessage());
            out.println("</p>");
            out.print("</body>");
        }
    }


}
