package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventDataMapper {
    private final DatabaseConnectionManager databaseConnectionManager;
    public EventDataMapper(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }
    public Event findEventById(int Id) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM events WHERE event_id = ?");
            stmt.setInt(1, Id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Event( rs.getString("title"), rs.getString("description"),rs.getString("venue"),rs.getBigDecimal("cost"),rs.getInt("club_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }

    public Event findEventByTitle(String title) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM events WHERE title = ?");
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Event( rs.getString("title"), rs.getString("description"),rs.getString("venue"),rs.getBigDecimal("cost"),rs.getInt("club_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }

    public boolean saveEvent(Event event) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        String query = "INSERT INTO events (title, description,date,time,venue,cost,club_id) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3,event.getSqlDate());
            stmt.setTime(4,event.getSqlTime());
            stmt.setString(5,event.getVenueName());
            stmt.setBigDecimal(6,event.getCost());
            stmt.setInt(7,event.getClub());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error saving club: " + e.getMessage());
        }finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }

    public List<Event> getAllEvent() throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        List<Event> events = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM events ");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = new Event(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("venue"),
                        rs.getBigDecimal("cost"),
                        rs.getInt("club_id")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);

        }

        return events;
    }
}
