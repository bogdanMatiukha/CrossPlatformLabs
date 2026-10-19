package model;

import status.RequestState;

public class IssueRequest {
    private int id;
    private int userId;
    private int propertyId;
    private RequestState requestStatus;
    private int urgencyF1;
    private int criticalityF2;
    private int deficitF3;
    private double integralIndex;

    public IssueRequest() {}

    public IssueRequest(int userId, int propertyId, RequestState requestStatus, int urgencyF1, int criticalityF2, int deficitF3) {
        this.userId = userId;
        this.propertyId = propertyId;
        this.requestStatus = requestStatus != null ? requestStatus : RequestState.CREATED;
        this.urgencyF1 = urgencyF1;
        this.criticalityF2 = criticalityF2;
        this.deficitF3 = deficitF3;
        this.integralIndex = calculateIntegralIndex(urgencyF1, criticalityF2, deficitF3);
    }

    public IssueRequest(int id, int userId, int propertyId, RequestState requestStatus, int urgencyF1, int criticalityF2, int deficitF3, double integralIndex) {
        this.id = id;
        this.userId = userId;
        this.propertyId = propertyId;
        this.requestStatus = requestStatus != null ? requestStatus : RequestState.CREATED;
        this.urgencyF1 = urgencyF1;
        this.criticalityF2 = criticalityF2;
        this.deficitF3 = deficitF3;
        this.integralIndex = integralIndex;
    }

    // Формула розрахунку інтегрального показника: I = 0.40·F1 + 0.35·F2 + 0.25·F3
    public static double calculateIntegralIndex(int f1, int f2, int f3) {
        return 0.40 * f1 + 0.35 * f2 + 0.25 * f3;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getPropertyId() { return propertyId; }
    public void setPropertyId(int propertyId) { this.propertyId = propertyId; }

    public RequestState getRequestStatus() { return requestStatus; }
    public void setRequestStatus(RequestState requestStatus) { this.requestStatus = requestStatus; }

    public int getUrgencyF1() { return urgencyF1; }
    public void setUrgencyF1(int urgencyF1) {
        this.urgencyF1 = urgencyF1;
        this.integralIndex = calculateIntegralIndex(this.urgencyF1, this.criticalityF2, this.deficitF3);
    }

    public int getCriticalityF2() { return criticalityF2; }
    public void setCriticalityF2(int criticalityF2) {
        this.criticalityF2 = criticalityF2;
        this.integralIndex = calculateIntegralIndex(this.urgencyF1, this.criticalityF2, this.deficitF3);
    }

    public int getDeficitF3() { return deficitF3; }
    public void setDeficitF3(int deficitF3) {
        this.deficitF3 = deficitF3;
        this.integralIndex = calculateIntegralIndex(this.urgencyF1, this.criticalityF2, this.deficitF3);
    }

    public double getIntegralIndex() { return integralIndex; }
    public void setIntegralIndex(double integralIndex) { this.integralIndex = integralIndex; }
}