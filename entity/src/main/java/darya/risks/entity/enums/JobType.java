package darya.risks.entity.enums;

public enum JobType {
    NORMAL_JOB(1, "Job"),
    RISK(2, "Risk");

    private int id;
    private String displayedValue;

    JobType(int id, String displayedValue) {
        this.id = id;
        this.displayedValue = displayedValue;
    }

    public int getId() {
        return id;
    }

    public String getDisplayedValue() {
        return displayedValue;
    }

    public static JobType getByDisplayedValue(String displayedValue) {
        for (JobType type : JobType.values()) {
            if (displayedValue.equals(type.getDisplayedValue())) {
                return type;
            }
        }
        return NORMAL_JOB;
    }
}
