package dao.Transfer;

import model.Transfer;
import java.util.List;

public interface TransferDAO {
    List<Transfer> getAllTransfers();
    Transfer getTransferById(int id);
    void addTransfer(Transfer transfer);
    void updateTransfer(Transfer transfer);
    void deleteTransfer(int id);
}