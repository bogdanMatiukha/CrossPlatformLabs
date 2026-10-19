package service;

import dao.IssueRequest.IssueRequestDAO;
import dao.IssueRequest.IssueRequestDAOImpl;
import model.IssueRequest;
import java.util.List;

public class IssueRequestService {
    private final IssueRequestDAO dao = new IssueRequestDAOImpl();

    public List<IssueRequest> getAllRequests() { return dao.getAllRequests(); }
    public IssueRequest getRequestById(int id) { return dao.getRequestById(id); }
    public void addRequest(IssueRequest request) { dao.addRequest(request); }
    public void updateRequest(IssueRequest request) { dao.updateRequest(request); }
    public void deleteRequest(int id) { dao.deleteRequest(id); }
}