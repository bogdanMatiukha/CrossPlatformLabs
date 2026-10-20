package main.dao.Transfer;

import model.TransferRecord;
import java.util.List;

public interface TransferDAO {
    List<TransferRecord> getAllTransfers();
    TransferRecord getTransferById(int id);
    void addTransfer(TransferRecord transfer);
    void updateTransfer(TransferRecord transfer);
    void deleteTransfer(int id);
}