package mlb;
/**
 * @author Roman Yasinovskyy
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseReader {
    private Connection db_connection;
    private final String SQLITEDBPATH = "jdbc:sqlite:data/mlb.sqlite";
    
    public DatabaseReader() { }
    /**
     * Connect to a database (file)
     */
    public void connect() {
        try {
            this.db_connection = DriverManager.getConnection(SQLITEDBPATH);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseReaderGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Disconnect from a database (file)
     */
    public void disconnect() {
        try {
            this.db_connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseReaderGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Populate the list of divisions
     * @param divisions
     */
    public void getDivisions(ArrayList<String> divisions) {
        Statement stat;
        ResultSet results;
        
        this.connect();
        try {
            stat = this.db_connection.createStatement();
            // TODO: Write an SQL statement to retrieve a league (conference) and a division
            String sql = "SELECT DISTINCT conference, division FROM team;";
            results = stat.executeQuery(sql);
            
            // should have 6 combinations
            // TODO: Add all 6 combinations to the ArrayList divisions
            while (results.next()) {
                divisions.add(results.getString(1) + " | " + results.getString(2));
            }
            results.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.disconnect();
        }
    }
    /**
     * Read all teams from the database
     * @param confDiv
     * @param teams
     */
    public void getTeams(String confDiv, ArrayList<String> teams) {
        Statement stat;
        ResultSet results;
        String conference = confDiv.split(" | ")[0];
        String division = confDiv.split(" | ")[2];
        
        this.connect();
        try {
            stat = this.db_connection.createStatement();
            // TODO: Write an SQL statement to retrieve a teams from a specific division
            String sql = "SELECT name FROM team WHERE conference = '" + conference + "'AND division = '" + division + "';";
            results = stat.executeQuery(sql);
            // TODO: Add all 5 teams to the ArrayList teams
            while (results.next()) {
                teams.add(results.getString(1));
            }
            results.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.disconnect();
        }
    }
    /**
     * @param teamName
     * @return Team info
     */
    public Team getTeamInfo(String teamName) {
        Team team = null;
        ArrayList<Player> roster = new ArrayList();
        // TODO: Retrieve team info (roster, address, and logo) from the database
        Statement stat;
        ResultSet results;
        this.connect();
        try {
            stat = this.db_connection.createStatement();
            String sql = "SELECT * FROM team WHERE name = '" + teamName + "';";
            results = stat.executeQuery(sql);
            int idpk = results.getInt(1);
            System.out.println(idpk);
            team = new Team(results.getString(2), results.getString(3), results.getString(4), results.getString(5), results.getString(6));
            team.setLogo(results.getBytes(7));
            sql = "SELECT * FROM address WHERE address.team = " + idpk + ";";
            results = stat.executeQuery(sql);
            Address address = new Address(teamName, results.getString(3), results.getString(4), results.getString(5), results.getString(6), results.getString(7), results.getString(8), results.getString(9));
            team.setAddress(address);
            System.out.println(idpk);
            sql = "SELECT * FROM player WHERE player.team = " + idpk + ";";
            results = stat.executeQuery(sql);

            while (results.next()) {
                Player player = new Player(results.getString(2), results.getString(3), teamName, results.getString(5));
                roster.add(player);
            }
            team.setRoster(roster);
            results.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.disconnect();
        }
        return team;
    }
}
