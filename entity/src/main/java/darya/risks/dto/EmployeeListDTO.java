package darya.risks.dto;

import darya.risks.entity.Employee;

import java.util.List;

public class EmployeeListDTO {
    private List<Employee> employeeList;

    public EmployeeListDTO() {
    }

    public EmployeeListDTO(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    @Override
    public String toString() {
        return "EmployeeListDTO{" +
                "employeeList=" + employeeList +
                '}';
    }
}
