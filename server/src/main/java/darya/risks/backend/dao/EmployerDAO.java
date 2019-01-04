package darya.risks.backend.dao;

import darya.risks.backend.database.ConnectionPool;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.entity.Employer;
import darya.risks.entity.enums.EmployerPersonType;
import darya.risks.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployerDAO {
    private static final Logger logger = LogManager.getLogger(EmployerDAO.class);

    private static final String SAVE = "INSERT INTO `employer` (`first_name`, `last_name`, `person_type`, `company_name`, " +
            " `telephone`, `address`, `scope_of_work`, `passport_number`) " +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE `employer` SET `first_name` = ?, `last_name` = ?, `person_type` = ?, `company_name` = ?, " +
            " `telephone` = ?, `address` = ?, `scope_of_work` = ?, `passport_number = ?` " +
            " WHERE `employer_id` = ?";
    private static final String GET_BY_ID = "SELECT `employer_id`, `first_name`, `last_name`, `person_type`, " +
            " `company_name`, `telephone`, `address`, `scope_of_work`, `passport_number` " +
            " FROM `employer` " +
            " WHERE `employer_id` = ?";
    private static final String GET_ALL = "SELECT `employer_id`, `first_name`, `last_name`, `person_type`, " +
            " `company_name`, `telephone`, `address`, `scope_of_work`, `passport_number` " +
            " FROM `employer` ";
    private static final String DELETE = "DELETE FROM `employer` WHERE `employer_id`= ?";

    private static final String COLUMN_EMPLOYER_ID = "employer_id";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_PERSON_TYPE = "person_type";
    private static final String COLUMN_COMPANY_NAME = "company_name";
    private static final String COLUMN_TEPEPHONE = "telephone";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_SCOPE_OF_WORK = "scope_of_work";
    private static final String COLUMN_PASSPORT_NUMBER = "passport_number";

    public Employer save(@Valid Employer employer) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, employer.getFirstName());
            preparedStatement.setString(2, employer.getLastName());
            preparedStatement.setString(3, employer.getPersonType().toString());
            preparedStatement.setString(4, employer.getCompanyName());
            preparedStatement.setString(5, employer.getTelephone());
            preparedStatement.setString(6, employer.getAddress());
            preparedStatement.setString(7, employer.getScopeOfWork());
            preparedStatement.setString(8, employer.getPassportNumber());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new ApplicationException("Cannot save employer. " + employer, ResponseStatus.NOT_FOUND);
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employer.setId(generatedKeys.getInt(1));
                } else {
                    throw new ApplicationException("Cannot save employer, no ID obtained. " + employer, ResponseStatus.NOT_FOUND);
                }
            }
            return employer;

        } catch (SQLException e) {
            throw new ApplicationException("Cannot save employer. " + employer + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public Employer update(@Valid Employer employer) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setString(1, employer.getFirstName());
            preparedStatement.setString(2, employer.getLastName());
            preparedStatement.setString(3, employer.getPersonType().toString());
            preparedStatement.setString(4, employer.getCompanyName());
            preparedStatement.setString(5, employer.getTelephone());
            preparedStatement.setString(6, employer.getAddress());
            preparedStatement.setString(7, employer.getScopeOfWork());
            preparedStatement.setString(8, employer.getPassportNumber());
            preparedStatement.setInt(9, employer.getId());
            preparedStatement.execute();
            return employer;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot update employer. " + employer + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public Employer getById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildEmployer(resultSet);
            } else {
                throw new ApplicationException("Employer with id = " + id + " not found.", ResponseStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ApplicationException("Cannot find employer with id = " + id + " in database." + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public List<Employer> getAll() throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Employer> employers = new ArrayList<>();
            while (resultSet.next()) {
                employers.add(buildEmployer(resultSet));
            }
            return employers;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot get all employers. " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void deleteById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Cannot delete employer with id = " + id + " " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    private Employer buildEmployer(ResultSet resultSet) throws ApplicationException {
        try {
            Employer employer = new Employer();
            employer.setId(resultSet.getInt(COLUMN_EMPLOYER_ID));
            employer.setFirstName(resultSet.getString(COLUMN_FIRST_NAME));
            employer.setLastName(resultSet.getString(COLUMN_LAST_NAME));
            employer.setPersonType(EmployerPersonType.valueOf(resultSet.getString(COLUMN_PERSON_TYPE)));
            employer.setCompanyName(resultSet.getString(COLUMN_COMPANY_NAME));
            employer.setTelephone(resultSet.getString(COLUMN_TEPEPHONE));
            employer.setAddress(resultSet.getString(COLUMN_ADDRESS));
            employer.setScopeOfWork(resultSet.getString(COLUMN_SCOPE_OF_WORK));
            employer.setPassportNumber(resultSet.getString(COLUMN_PASSPORT_NUMBER));
            return employer;
        } catch (SQLException e) {
            throw new ApplicationException("Error while building employer! " + e, ResponseStatus.BAD_REQUEST);
        }
    }
}
