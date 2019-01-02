package darya.risks.entity;

import darya.risks.entity.enums.EmployerPersonType;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class Employer implements DatabaseEntity {
    private int id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private EmployerPersonType personType;
    private String companyName;
    private String telephone;
    private String address;
    private String scopeOfWork;

    public Employer() {
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

    public EmployerPersonType getPersonType() {
        return personType;
    }

    public void setPersonType(EmployerPersonType personType) {
        this.personType = personType;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getScopeOfWork() {
        return scopeOfWork;
    }

    public void setScopeOfWork(String scopeOfWork) {
        this.scopeOfWork = scopeOfWork;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employer employer = (Employer) o;
        return id == employer.id &&
                Objects.equals(firstName, employer.firstName) &&
                Objects.equals(lastName, employer.lastName) &&
                personType == employer.personType &&
                Objects.equals(companyName, employer.companyName) &&
                Objects.equals(telephone, employer.telephone) &&
                Objects.equals(address, employer.address) &&
                Objects.equals(scopeOfWork, employer.scopeOfWork);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, personType, companyName, telephone, address, scopeOfWork);
    }

    @Override
    public String toString() {
        return "Employer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", personType=" + personType +
                ", companyName='" + companyName + '\'' +
                ", telephone='" + telephone + '\'' +
                ", address='" + address + '\'' +
                ", scopeOfWork='" + scopeOfWork + '\'' +
                '}';
    }
}
