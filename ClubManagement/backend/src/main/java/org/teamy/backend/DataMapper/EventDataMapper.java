package org.teamy.backend.DataMapper;

import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.Event;
import org.teamy.backend.model.EventStatus;
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
                return new Event(
                        rs.getInt("event_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("venue"),            // 你可能需要确认 venue 是字符串还是 ID
                        rs.getBigDecimal("cost"),
                        rs.getInt("club_id"),
                        rs.getString("status"), // 将状态从数据库转换为枚举类型
                        rs.getInt("capacity")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
        return null;
    }
    public void deleteEvent(int eventId) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        try {
            // 更新事件状态为 "Cancelled"
            PreparedStatement stmt = connection.prepareStatement("UPDATE events SET status = ?::event_status WHERE event_id = ?");
            stmt.setString(1, "Cancelled");
            stmt.setInt(2, eventId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No event found with id: " + eventId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }
    }

    public List<Event> findEventsByTitle(String title) throws SQLException {
        var connection = databaseConnectionManager.nextConnection();
        List<Event> events = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM events WHERE LOWER(title) LIKE LOWER(?)");
            stmt.setString(1, "%" + title + "%");  // 使用模糊匹配
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("event_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("venue"),            // 你可能需要确认 venue 是字符串还是 ID
                        rs.getBigDecimal("cost"),
                        rs.getInt("club_id"),
                        rs.getString("status"), // 将状态从数据库转换为枚举类型
                        rs.getInt("capacity")
                );
                events.add(event);
            }
        } finally {
            databaseConnectionManager.releaseConnection(connection);
        }

        return events;
    }

    public boolean saveEvent(Event event) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        String query = "INSERT INTO events (title, description,date,time,venue,cost,club_id,status) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3,event.getSqlDate());
            stmt.setTime(4,event.getSqlTime());
            stmt.setInt(5,event.getVenueId());
            stmt.setBigDecimal(6,event.getCost());
            stmt.setInt(7,event.getClub());
            stmt.setString(8, EventStatus.Ongoing.name());
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
                        rs.getInt("event_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("venue"),            // 你可能需要确认 venue 是字符串还是 ID
                        rs.getBigDecimal("cost"),
                        rs.getInt("club_id"),
                        rs.getString("status"), // 将状态从数据库转换为枚举类型
                        rs.getInt("capacity")
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
    public boolean updateEvent(Event event) throws Exception {
        var connection = databaseConnectionManager.nextConnection();

        // SQL 更新语句，更新指定的事件
        String query = "UPDATE events SET title = ?, description = ?, date = ?, time = ?, venue = ?, cost = ?, club_id = ?, status = ?,capacity = ? WHERE event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, event.getSqlDate());
            stmt.setTime(4, event.getSqlTime());
            stmt.setInt(5, event.getVenueId());
            stmt.setBigDecimal(6, event.getCost());
            stmt.setInt(7, event.getClub());
            stmt.setString(8, event.getStatus().name());  // 假设状态是枚举类型
            stmt.setInt(9, event.getId());  // 使用 eventId 作为更新条件
            stmt.setInt(10,event.getCapacity());

            // 执行更新操作
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // 返回是否成功更新
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error updating event: " + e.getMessage());
        } finally {
            // 释放数据库连接
            databaseConnectionManager.releaseConnection(connection);
        }
    }
}
