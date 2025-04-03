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

// This annotation maps this Java Servlet Class to a URL
@WebServlet("/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Change this to your own mysql username and password
        String loginUser = "root";
        String loginPasswd = "aaron1105";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        // Set response mime type
        response.setContentType("text/html");

        // Get the PrintWriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            // create database connection
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // declare statement
            Statement statement = connection.createStatement();
            Statement starsgenresStatement = connection.createStatement();
            // prepare query
            String query = "SELECT title, year, director, rating \n" +
                    "FROM movies m, ratings r, genres_in_movies g\n" +
                    "WHERE m.id = g.movieid and m.id = r.movieid\n" +
                    "ORDER BY r.rating DESC\n" +
                    "LIMIT 20";
            // execute query
            ResultSet resultSet = statement.executeQuery(query);
            ResultSet starsResultSet;
            ResultSet genresResultSet;
            out.println("<body>");
            out.println("<h1>Movie List</h1>");

            out.println("<table border>");

            // Add table header row
            out.println("<tr>");
            out.println("<td>Title</td>");
            out.println("<td>Year</td>");
            out.println("<td>Director</td>");
            out.println("<td>Genre(s)</td>");
            out.println("<td>Star(s)</td>");
            out.println("<td>Rating</td>");
            out.println("</tr>");

            // Add a row for every movie result
            while (resultSet.next()) {
                // get a movie from result set
                String title = resultSet.getString("Title");
                String year = resultSet.getString("Year");
                String director = resultSet.getString("Director");
                String rating = resultSet.getString("Rating");

//                // get first three genres
                query = "SELECT name " +
                        "FROM movies m, genres_in_movies gim, genres g " +
                        "WHERE m.title = '" + title + "' AND m.id = gim.movieID AND gim.genreID = g.id " +
                        "LIMIT 3";

                genresResultSet = starsgenresStatement.executeQuery(query);
                String genres = "";
                while(genresResultSet.next()) {
                    genres += genresResultSet.getString("name");
                    genres += ", ";
                }

                // get first three stars
                query = "SELECT name " +
                        "FROM movies m, stars_in_movies sim, stars s " +
                        "WHERE m.title = '" + title + "' AND m.id = sim.movieID AND sim.starID = s.id " +
                        "LIMIT 3";

                starsResultSet = starsgenresStatement.executeQuery(query);
                String stars = "";
                while(starsResultSet.next()) {
                    stars += starsResultSet.getString("name");
                    stars += ", ";
                }

                out.println("<tr>");
                out.println("<td>" + title + "</td>");
                out.println("<td>" + year + "</td>");
                out.println("<td>" + director + "</td>");
                out.println("<td>" + genres + "</td>");
                out.println("<td>" + stars + "</td>");
                out.println("<td>" + rating + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body>");

            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
             *
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.out
             * This can help you debug your program after deploying it on AWS.
             */
            request.getServletContext().log("Error: ", e);

            out.println("<body>");
            out.println("<p>");
            out.println("Exception in doGet: " + e.getMessage());
            out.println("</p>");
            out.print("</body>");
        }

        out.println("</html>");
        out.close();

    }


}
