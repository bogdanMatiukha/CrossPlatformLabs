package service;

import dao.Transfer.TransferDAO;
import dao.Transfer.TransferDAOImpl;
import model.Transfer;
import java.util.List;

public class TransferService {
    private final TransferDAO dao = new TransferDAOImpl();

    public List<Transfer> getAllTransfers() {
        return dao.getAllTransfers();
    }

    public Transfer getTransferById(int id) {
        return dao.getTransferById(id);
    }

    public void addTransfer(Transfer transfer) {
        dao.addTransfer(transfer);
    }

    public void updateTransfer(Transfer transfer) {
        dao.updateTransfer(transfer);
    }

    public void deleteTransfer(int id) {
        dao.deleteTransfer(id);
    }
}