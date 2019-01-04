package darya.risks.entity;

import darya.risks.entity.enums.EmployerPersonType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Employer implements DatabaseEntity {
    private int id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotNull
    private EmployerPersonType personType;
    @NotBlank
    private String companyName;
    private String telephone;
    private String address;
    private String scopeOfWork;
    private String passportNumber;

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

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
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
                Objects.equals(scopeOfWork, employer.scopeOfWork) &&
                Objects.equals(passportNumber, employer.passportNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, personType, companyName, telephone, address, scopeOfWork, passportNumber);
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

    public String readableValueForComboBox() {
        return id + ", " + firstName + " " + lastName + ", " + companyName;
    }
}
