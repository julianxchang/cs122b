import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/movie-list"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            // connection statement for getting generes and stars
            Statement starsgenresStatement = conn.createStatement();

            String query = "SELECT title, year, director, rating \n" +
                    "FROM movies m, ratings r, genres_in_movies g\n" +
                    "WHERE m.id = g.movieid and m.id = r.movieid\n" +
                    "ORDER BY r.rating DESC\n" +
                    "LIMIT 20";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            // For storing stars and genres later
            ResultSet starsResultSet;
            ResultSet genresResultSet;
            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String title = rs.getString("Title");
                String year = rs.getString("Year");
                String director = rs.getString("Director");
                String rating = rs.getString("Rating");

                // get first three genres
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
                genres = genres.substring(0, genres.length() - 2);

                //get first three stars
                query = "SELECT s.id, name " +
                        "FROM movies m, stars_in_movies sim, stars s " +
                        "WHERE m.title = '" + title + "' AND m.id = sim.movieID AND sim.starID = s.id " +
                        "LIMIT 3";

                starsResultSet = starsgenresStatement.executeQuery(query);

                String stars = "";
                while(starsResultSet.next()) {
                    //String starID = starsResultSet.getString("id");
                    //String name = starsResultSet.getString("name");

                    //String link = "http://localhost:8080/cs122b_project1_star_example_war_exploded/stars?sid=" + starID;
                    //stars += "<a href = " + link + ">" + name + "</a>" + ", ";
                    stars += starsResultSet.getString("name");
                    stars += ", ";
                }

                stars = stars.substring(0, stars.length() - 2);

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_title", title);
                jsonObject.addProperty("movie_year", year);
                jsonObject.addProperty("movie_director", director);
                jsonObject.addProperty("movie_genres", genres);
                jsonObject.addProperty("movie_stars", stars);
                jsonObject.addProperty("movie_rating", rating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
