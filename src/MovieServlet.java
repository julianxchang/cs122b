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

@WebServlet("/movie")
public class MovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Dotenv dotenv = Dotenv.load();

        String login = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");
        String url = "jdbc:mysql://localhost:3306/moviedb";

        response.setContentType("text/html");
        String id = "'" + request.getParameter("id") + "'";

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
            String query = "SELECT * FROM movies WHERE id = " + id;
            ResultSet rs = statement.executeQuery(query);

            String title = "title";
            int year = 0;
            String director = "director";
            while (rs.next()) {
                title = rs.getString("title");
                year = rs.getInt("year");
                director = rs.getString("director");
            }

            out.print("<h1>" + title + "</h1>");
            out.print("<h2> Director: " + director + "<h2>");
            out.print("<p> Year: " + year + "</p>");

            query = "SELECT name FROM genres_in_movies gim JOIN genres g ON gim.genreID = g.id WHERE gim.movieID=" + id;
            rs = statement.executeQuery(query);
            out.print("<h2> Genres: </h2>");

            out.print("<ol>");
            while (rs.next()) {
                String genre = rs.getString("name");
                out.print(" <li> " + genre + " </li>");
            }
            out.print("</ol>");

            query = "SELECT * FROM stars_in_movies sim JOIN stars s ON sim.starID = s.id WHERE sim.movieID=" + id;
            rs = statement.executeQuery(query);

            out.print("<h2> Stars: </h2>");
            while (rs.next()) {
                String star = rs.getString("name");
                String sid = rs.getString("starID");
                String link = "http://" + dotenv.get("HOSTNAME") + "localhost:8080/cs122b_project1_star_example_war_exploded/stars?sid=" + sid;
                out.print("<a href = " + link + ">" + star + "</a><br>");
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
