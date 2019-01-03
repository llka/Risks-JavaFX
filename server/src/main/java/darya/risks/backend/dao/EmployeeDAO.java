package darya.risks.backend.dao;

import darya.risks.backend.database.ConnectionPool;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.entity.Employee;
import darya.risks.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private static final Logger logger = LogManager.getLogger(EmployeeDAO.class);

    private static final String SAVE = "INSERT INTO `employee` (`first_name`, `last_name`, `company_name`, `telephone`) " +
            " VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE `employee` SET `first_name` = ?, `last_name` = ?, `company_name` = ?, `telephone` = ? " +
            " WHERE `employee_id` = ?";
    private static final String GET_BY_ID = "SELECT `employee_id`, `first_name`, `last_name`, `company_name`, `telephone` " +
            " FROM `employee` WHERE `employee_id` = ?";
    private static final String GET_ALL = "SELECT `employee_id`, `first_name`, `last_name`, `company_name`, `telephone` " +
            " FROM `employee` ";
    private static final String DELETE = "DELETE FROM `employee` WHERE `employee_id`= ?";

    private static final String COLUMN_EMPLOYEE_ID = "employee_id";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_COMPANY_NAME = "company_name";
    private static final String COLUMN_TEPEPHONE = "telephone";

    public Employee save(@Valid Employee employee) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            preparedStatement.setString(3, employee.getCompanyName());
            preparedStatement.setString(4, employee.getTelephone());


            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new ApplicationException("Cannot save employee. " + employee, ResponseStatus.NOT_FOUND);
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employee.setId(generatedKeys.getInt(1));
                } else {
                    throw new ApplicationException("Cannot save employee, no ID obtained. " + employee, ResponseStatus.NOT_FOUND);
                }
            }
            return employee;

        } catch (SQLException e) {
            throw new ApplicationException("Cannot save employee. " + employee + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public Employee update(@Valid Employee employee) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            preparedStatement.setString(3, employee.getCompanyName());
            preparedStatement.setString(4, employee.getTelephone());
            preparedStatement.setInt(5, employee.getId());
            preparedStatement.execute();
            return employee;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot update employee. " + employee + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public Employee getById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildEmployee(resultSet);
            } else {
                throw new ApplicationException("Employee with id = " + id + " not found.", ResponseStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ApplicationException("Cannot find employee with id = " + id + " in database." + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public List<Employee> getAll() throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Employee> employees = new ArrayList<>();
            while (resultSet.next()) {
                employees.add(buildEmployee(resultSet));
            }
            return employees;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot get all employees. " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void deleteById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Cannot delete employee with id = " + id + " " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    private Employee buildEmployee(ResultSet resultSet) throws ApplicationException {
        try {
            Employee employee = new Employee();
            employee.setId(resultSet.getInt(COLUMN_EMPLOYEE_ID));
            employee.setFirstName(resultSet.getString(COLUMN_FIRST_NAME));
            employee.setLastName(resultSet.getString(COLUMN_LAST_NAME));
            employee.setCompanyName(resultSet.getString(COLUMN_COMPANY_NAME));
            employee.setTelephone(resultSet.getString(COLUMN_TEPEPHONE));
            return employee;
        } catch (SQLException e) {
            throw new ApplicationException("Error while building employee! " + e, ResponseStatus.BAD_REQUEST);
        }
    }
}
