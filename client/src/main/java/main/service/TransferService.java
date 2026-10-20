package main.service;

import main.dao.Transfer.TransferDAO;
import main.dao.Transfer.TransferDAOImpl;
import main.model.TransferRecord;
import java.util.List;

public class TransferService {
    private final TransferDAO dao = new TransferDAOImpl();

    public List<TransferRecord> getAllTransfers() {
        return dao.getAllTransfers();
    }

    public TransferRecord getTransferById(int id) {
        return dao.getTransferById(id);
    }

    public void addTransfer(TransferRecord transfer) {
        dao.addTransfer(transfer);
    }

    public void updateTransfer(TransferRecord transfer) {
        dao.updateTransfer(transfer);
    }

    public void deleteTransfer(int id) {
        dao.deleteTransfer(id);
    }
}