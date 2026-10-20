package main.dao.IssueRequest;

import main.model.IssueRequest;
import java.util.List;

public interface IssueRequestDAO {
    List<IssueRequest> getAllRequests();
    IssueRequest getRequestById(int id);
    void addRequest(IssueRequest request);
    void updateRequest(IssueRequest request);
    void deleteRequest(int id);
}