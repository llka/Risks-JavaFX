package darya.risks.entity;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class Employee implements DatabaseEntity {
    private int id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String companyName;
    private String telephone;

    public Employee() {
    }

    public Employee(@NotBlank String firstName, @NotBlank String lastName, String companyName, String telephone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
        this.telephone = telephone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id &&
                Objects.equals(firstName, employee.firstName) &&
                Objects.equals(lastName, employee.lastName) &&
                Objects.equals(companyName, employee.companyName) &&
                Objects.equals(telephone, employee.telephone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, companyName, telephone);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }

    public String readableValue() {
        return id + ", " + firstName + " " + lastName + ", " + companyName + ", " + telephone;
    }
}
