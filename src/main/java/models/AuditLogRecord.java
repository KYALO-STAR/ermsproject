package models;

public class AuditLogRecord {
    private final int userId;
    private final String actorName;
    private final String actionType;
    private final String description;

    public AuditLogRecord(int userId, String actorName, String actionType, String description) {
        this.userId = userId;
        this.actorName = actorName;
        this.actionType = actionType;
        this.description = description;
    }

    public int getUserId() { return userId; }

    public String getActorName() { return actorName; }

    public String getActionType() { return actionType; }

    public String getDescription() { return description; }
}