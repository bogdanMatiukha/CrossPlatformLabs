package model;

import java.time.LocalDate;

public class TransferRecord {
    private int id;
    private int requestId;
    private LocalDate transferDate;
    private LocalDate returnDate;
    private String notes;

    public TransferRecord() {}

    public TransferRecord(int id, int requestId, LocalDate transferDate, LocalDate returnDate, String notes) {
        this.id = id;
        this.requestId = requestId;
        this.transferDate = transferDate;
        this.returnDate = returnDate;
        this.notes = notes;
    }

    public TransferRecord(int id, int requestId, String transferDateStr, String returnDateStr, String notes) {
        this.id = id;
        this.requestId = requestId;
        this.transferDate = transferDateStr != null ? LocalDate.parse(transferDateStr) : null;
        this.returnDate = returnDateStr != null ? LocalDate.parse(returnDateStr) : null;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public LocalDate getTransferDate() { return transferDate; }
    public void setTransferDate(LocalDate transferDate) { this.transferDate = transferDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}