
package dao.Transfer;

import model.Transfer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import dao.DBUtil;

public class TransferDAOImpl implements TransferDAO {

    @Override
    public List<Transfer> getAllTransfers() {
        List<Transfer> list = new ArrayList<>();
        String sql = "SELECT id, request_id, transfer_date, return_date, notes FROM transfer_records ORDER BY id";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Date transferDate = rs.getDate("transfer_date");
                Date returnDate = rs.getDate("return_date");

                list.add(new Transfer(
                        rs.getInt("id"),
                        rs.getInt("request_id"),
                        transferDate != null ? transferDate.toLocalDate() : null,
                        returnDate != null ? returnDate.toLocalDate() : null,
                        rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка читання записів передачі", e);
        }

        return list;
    }

    @Override
    public Transfer getTransferById(int id) {
        String sql = "SELECT id, request_id, transfer_date, return_date, notes FROM transfer_records WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date transferDate = rs.getDate("transfer_date");
                    Date returnDate = rs.getDate("return_date");

                    return new Transfer(
                            rs.getInt("id"),
                            rs.getInt("request_id"),
                            transferDate != null ? transferDate.toLocalDate() : null,
                            returnDate != null ? returnDate.toLocalDate() : null,
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
    public void addTransfer(Transfer transfer) {
        String sql = "INSERT INTO transfer_records (request_id, transfer_date, return_date, notes) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, transfer.getRequestId());

            if (transfer.getTransferDate() != null) {
                ps.setDate(2, Date.valueOf(transfer.getTransferDate()));
            } else {
                ps.setNull(2, Types.DATE);
            }
            if (transfer.getReturnDate() != null) {
                ps.setDate(3, Date.valueOf(transfer.getReturnDate()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            ps.setString(4, transfer.getNotes());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Помилка додавання запису передачі", e);
        }
    }

    @Override
    public void updateTransfer(Transfer transfer) {
        String sql = "UPDATE transfer_records SET request_id = ?, transfer_date = ?, return_date = ?, notes = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, transfer.getRequestId());

            if (transfer.getTransferDate() != null) {
                ps.setDate(2, Date.valueOf(transfer.getTransferDate()));
            } else {
                ps.setNull(2, Types.DATE);
            }
            if (transfer.getReturnDate() != null) {
                ps.setDate(3, Date.valueOf(transfer.getReturnDate()));
            } else {
                ps.setNull(3, Types.DATE);
            }

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