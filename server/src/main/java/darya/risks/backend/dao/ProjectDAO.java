package darya.risks.backend.dao;

import darya.risks.backend.database.ConnectionPool;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.entity.Contact;
import darya.risks.entity.Project;
import darya.risks.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    private static final Logger logger = LogManager.getLogger(ProjectDAO.class);
    private static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String SAVE = "INSERT INTO `project`(`title`, `start_date`, `end_date`, `contact_id`, `employer_id`)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE `project` SET `title` = ?, `start_date` = ?, `end_date` = ?, " +
            " `employer_id` = ? WHERE `project_id` = ?";

    private static final String GET_BY_ID = "SELECT `project_id`, `title`, `start_date`, `end_date`, `contact_id`, `employer_id` " +
            " FROM `project` WHERE `project_id` = ?";
    private static final String GET_ALL = "SELECT `project_id`, `title`, `start_date`, `end_date`, " +
            " `contact_id`, `employer_id` " +
            " FROM `project` ";
    private static final String GET_CONTACT_PROJECTS = "SELECT `project_id`, `title`, `start_date`," +
            " `end_date`, `contact_id`, `employer_id` " +
            " FROM `project` WHERE `contact_id` = ?";
    private static final String DELETE = "DELETE FROM `ticket` WHERE `ticket_id`= ?";

    private static final String COLUMN_PROJECT_ID = "project_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_END_DATE = "end_date";
    private static final String COLUMN_CONTACT_ID = "contact_id";
    private static final String COLUMN_EMPLOYER_ID = "employer_id";

    private EmployerDAO employerDAO;
    private JobDAO jobDAO;

    public ProjectDAO() {
        this.employerDAO = new EmployerDAO();
        this.jobDAO = new JobDAO();
    }

    public Project save(@Valid Project project, @Valid Contact contact) throws ApplicationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, project.getTitle());
            if (project.getStartDate() != null) {
                preparedStatement.setString(2, dateFormat.format(project.getStartDate()));
            }
            if (project.getEndDate() != null) {
                preparedStatement.setString(3, dateFormat.format(project.getEndDate()));
            }
            preparedStatement.setInt(4, contact.getId());
            if (project.getEmployer() != null) {
                preparedStatement.setInt(5, project.getEmployer().getId());
            } else {
                throw new ApplicationException("Cannot create project without employer!", ResponseStatus.BAD_REQUEST);
            }

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new ApplicationException("Cannot save project. " + project, ResponseStatus.NOT_FOUND);
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getInt(1));
                } else {
                    throw new ApplicationException("Cannot save project, no ID obtained. " + project, ResponseStatus.NOT_FOUND);
                }
            }
            return project;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot save project. " + project + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public Project update(@Valid Project project) throws ApplicationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setString(1, project.getTitle());
            if (project.getStartDate() != null) {
                preparedStatement.setString(2, dateFormat.format(project.getStartDate()));
            }
            if (project.getEndDate() != null) {
                preparedStatement.setString(3, dateFormat.format(project.getEndDate()));
            }
            if (project.getEmployer() != null) {
                preparedStatement.setInt(4, project.getEmployer().getId());
            } else {
                throw new ApplicationException("Cannot update project without employer!", ResponseStatus.BAD_REQUEST);
            }
            preparedStatement.setInt(5, project.getId());

            preparedStatement.execute();
            return project;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot update project. " + project + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public Project getById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildProject(resultSet);
            } else {
                throw new ApplicationException("Project with id = " + id + " not found.", ResponseStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ApplicationException("Cannot find project with id = " + id + " in database." + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public List<Project> getAll() throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Project> projects = new ArrayList<>();
            while (resultSet.next()) {
                projects.add(buildProject(resultSet));
            }
            return projects;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot get all projects. " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public List<Project> getContactProjects(@Positive int contactId) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CONTACT_PROJECTS)) {
            preparedStatement.setInt(1, contactId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Project> projects = new ArrayList<>();
            while (resultSet.next()) {
                projects.add(buildProject(resultSet));
            }
            return projects;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot get contact projects. " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void deleteById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Cannot delete project with id = " + id + " " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    private Project buildProject(ResultSet resultSet) throws ApplicationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        try {
            Project project = new Project();
            project.setId(resultSet.getInt(COLUMN_PROJECT_ID));
            project.setEmployer(employerDAO.getById(resultSet.getInt(COLUMN_EMPLOYER_ID)));
            project.setTitle(resultSet.getString(COLUMN_TITLE));
            project.setJobs(jobDAO.getProjectJobs(project.getId()));
            try {
                if (resultSet.getDate(COLUMN_START_DATE) != null) {
                    project.setStartDate(dateFormat.parse(resultSet.getString(COLUMN_START_DATE)));
                }
                if (resultSet.getDate(COLUMN_END_DATE) != null) {
                    project.setEndDate(dateFormat.parse(resultSet.getString(COLUMN_END_DATE)));
                }
            } catch (ParseException exception) {
                logger.error(exception);
            }

            return project;
        } catch (SQLException e) {
            throw new ApplicationException("Error while building project! " + e, ResponseStatus.BAD_REQUEST);
        }
    }
}
