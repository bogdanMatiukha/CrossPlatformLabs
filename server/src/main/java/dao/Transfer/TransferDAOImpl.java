package dao.Transfer;

import model.TransferRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import dao.DBUtil;
public class TransferDAOImpl implements TransferDAO {

    @Override
    public List<TransferRecord> getAllTransfers() {
        List<TransferRecord> list = new ArrayList<>();
        String sql = "SELECT id, request_id, transfer_date, return_date, notes FROM transfer_records ORDER BY id";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Date retDate = rs.getDate("return_date");
                list.add(new TransferRecord(
                        rs.getInt("id"),
                        rs.getInt("request_id"),
                        rs.getDate("transfer_date").toLocalDate(),
                        retDate != null ? retDate.toLocalDate() : null,
                        rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка читання записів передачі", e);
        }
        return list;
    }

    @Override
    public TransferRecord getTransferById(int id) {
        String sql = "SELECT id, request_id, transfer_date, return_date, notes FROM transfer_records WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date retDate = rs.getDate("return_date");
                    return new TransferRecord(
                            rs.getInt("id"),
                            rs.getInt("request_id"),
                            rs.getDate("transfer_date").toLocalDate(),
                            retDate != null ? retDate.toLocalDate() : null,
                            rs.getString("notes")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка читання запису передачі", e);
        }
        return null;
    }

    @Override
    public void addTransfer(TransferRecord transfer) {
        String sql = "INSERT INTO transfer_records (request_id, transfer_date, return_date, notes) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transfer.getRequestId());
            ps.setDate(2, Date.valueOf(transfer.getTransferDate()));
            ps.setDate(3, transfer.getReturnDate() != null ? Date.valueOf(transfer.getReturnDate()) : null);
            ps.setString(4, transfer.getNotes());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка додавання запису передачі", e);
        }
    }

    @Override
    public void updateTransfer(TransferRecord transfer) {
        String sql = "UPDATE transfer_records SET request_id = ?, transfer_date = ?, return_date = ?, notes = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transfer.getRequestId());
            ps.setDate(2, Date.valueOf(transfer.getTransferDate()));
            ps.setDate(3, transfer.getReturnDate() != null ? Date.valueOf(transfer.getReturnDate()) : null);
            ps.setString(4, transfer.getNotes());
            ps.setInt(5, transfer.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка оновлення запису передачі", e);
        }
    }

    @Override
    public void deleteTransfer(int id) {
        String sql = "DELETE FROM transfer_records WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення запису передачі", e);
        }
    }
}