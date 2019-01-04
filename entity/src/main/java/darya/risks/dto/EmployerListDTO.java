package darya.risks.dto;

import darya.risks.entity.Employer;

import java.util.List;

public class EmployerListDTO {
    private List<Employer> employerList;

    public EmployerListDTO() {
    }

    public EmployerListDTO(List<Employer> employerList) {
        this.employerList = employerList;
    }

    public List<Employer> getEmployerList() {
        return employerList;
    }

    public void setEmployerList(List<Employer> employerList) {
        this.employerList = employerList;
    }

    @Override
    public String toString() {
        return "EmployerListDTO{" +
                "employerList=" + employerList +
                '}';
    }
}
