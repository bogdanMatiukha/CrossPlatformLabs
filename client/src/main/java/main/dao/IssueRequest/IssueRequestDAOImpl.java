package main.dao.IssueRequest;

import dao.DBUtil;
import model.IssueRequest;
import status.RequestState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IssueRequestDAOImpl implements IssueRequestDAO {

    @Override
    public List<IssueRequest> getAllRequests() {
        List<IssueRequest> requests = new ArrayList<>();
        String sql = "SELECT id, user_id, property_id, request_status, urgency_f1, criticality_f2, deficit_f3, integral_index FROM issue_requests ORDER BY id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requests.add(new IssueRequest(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("property_id"),
                        RequestState.valueOf(rs.getString("request_status")),
                        rs.getInt("urgency_f1"),
                        rs.getInt("criticality_f2"),
                        rs.getInt("deficit_f3"),
                        rs.getDouble("integral_index")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка читання заявок", e);
        }
        return requests;
    }

    @Override
    public IssueRequest getRequestById(int id) {
        String sql = "SELECT id, user_id, property_id, request_status, urgency_f1, criticality_f2, deficit_f3, integral_index FROM issue_requests WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new IssueRequest(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("property_id"),
                            RequestState.valueOf(rs.getString("request_status")),
                            rs.getInt("urgency_f1"),
                            rs.getInt("criticality_f2"),
                            rs.getInt("deficit_f3"),
                            rs.getDouble("integral_index")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка читання заявки", e);
        }
        return null;
    }

    @Override
    public void addRequest(IssueRequest request) {
        String sql = "INSERT INTO issue_requests (user_id, property_id, request_status, urgency_f1, criticality_f2, deficit_f3, integral_index) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, request.getUserId());
            ps.setInt(2, request.getPropertyId());
            ps.setString(3, request.getRequestStatus().name());
            ps.setInt(4, request.getUrgencyF1());
            ps.setInt(5, request.getCriticalityF2());
            ps.setInt(6, request.getDeficitF3());
            ps.setDouble(7, request.getIntegralIndex());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка додавання заявки", e);
        }
    }

    @Override
    public void updateRequest(IssueRequest request) {
        String sql = "UPDATE issue_requests SET user_id = ?, property_id = ?, request_status = ?, urgency_f1 = ?, criticality_f2 = ?, deficit_f3 = ?, integral_index = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, request.getUserId());
            ps.setInt(2, request.getPropertyId());
            ps.setString(3, request.getRequestStatus().name());
            ps.setInt(4, request.getUrgencyF1());
            ps.setInt(5, request.getCriticalityF2());
            ps.setInt(6, request.getDeficitF3());
            ps.setDouble(7, request.getIntegralIndex());
            ps.setInt(8, request.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка оновлення заявки", e);
        }
    }

    @Override
    public void deleteRequest(int id) {
        String sql = "DELETE FROM issue_requests WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення заявки", e);
        }
    }
}