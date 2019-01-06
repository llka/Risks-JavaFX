package darya.risks.entity.enums;

public enum EmployerPersonType {
    //физ
    INDIVIDUAL(1, "Individual"),

    //юр
    LEGAL_PERSON(2, "Legal person");

    private int id;
    private String displayedValue;

    EmployerPersonType(int id, String displayedValue) {
        this.id = id;
        this.displayedValue = displayedValue;
    }

    public int getId() {
        return id;
    }

    public String getDisplayedValue() {
        return displayedValue;
    }

    public static EmployerPersonType getByDisplayedValue(String displayedValue) {
        for (EmployerPersonType type : EmployerPersonType.values()) {
            if (displayedValue.equals(type.getDisplayedValue())) {
                return type;
            }
        }
        return INDIVIDUAL;
    }
}
