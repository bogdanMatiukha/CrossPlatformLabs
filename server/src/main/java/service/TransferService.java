package service;

import dao.Transfer.TransferDAO;
import dao.Transfer.TransferDAOImpl;
import model.Transfer;

import java.time.LocalDate;
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
        validateTransfer(transfer);
        dao.addTransfer(transfer);
    }

    public void updateTransfer(Transfer transfer) {
        validateTransfer(transfer);
        dao.updateTransfer(transfer);
    }

    public void deleteTransfer(int id) {
        dao.deleteTransfer(id);
    }

    private void validateTransfer(Transfer transfer) {
        if (transfer == null) {
            throw new IllegalArgumentException(
                    "Запис передачі не може бути порожнім"
            );
        }

        LocalDate transferDate = transfer.getTransferDate();
        LocalDate returnDate = transfer.getReturnDate();

        if (returnDate != null && transferDate == null) {
            throw new IllegalArgumentException(
                    "Не можна вказати дату повернення без дати видачі"
            );
        }

        if (transferDate != null && returnDate != null
                && returnDate.isBefore(transferDate)) {
            throw new IllegalArgumentException(
                    "Дата повернення не може бути раніше дати видачі"
            );
        }
    }
}