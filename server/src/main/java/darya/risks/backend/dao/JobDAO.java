package darya.risks.backend.dao;

import darya.risks.backend.database.ConnectionPool;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.entity.Job;
import darya.risks.entity.enums.JobType;
import darya.risks.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {
    private static final Logger logger = LogManager.getLogger(JobDAO.class);

    private static final String SAVE = "INSERT INTO `job` (`job_type`, `title`, `description`, `duration_in_days`, " +
            " `responsible_employee_id`) " +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String SAVE_PROJECT_JOB = "INSERT INTO `project_has_job` (`job_job_id`, `project_project_id`) " +
            " VALUES (?, ?)";

    private static final String UPDATE = "UPDATE `job` SET `job_type` = ?, `title` = ?, `description` = ?, " +
            " `duration_in_days` = ?, `responsible_employee_id` = ?  " +
            " WHERE `job_id` = ?";
    private static final String GET_BY_ID = "SELECT `job_id`, `job_type`, `title`, `description`, `duration_in_days`, " +
            " `responsible_employee_id` " +
            " FROM `job` WHERE `job_id` = ?";
    private static final String GET_ALL = "SELECT `job_id`, `job_type`, `title`, `description`, `duration_in_days`, " +
            " `responsible_employee_id` FROM `job`";
    private static final String GET_PROJECT_JOBS = "SELECT `job_id`, `job_type`, `title`, `description`, `duration_in_days`, " +
            " `responsible_employee_id` " +
            " FROM `job` JOIN `project_has_job` " +
            " ON `job`.`job_id` =  `project_has_job`.`job_job_id` " +
            " WHERE `project_has_job`.`project_project_id` = ?";
    private static final String DELETE = "DELETE FROM `job` WHERE `job_id`= ?";

    private static final String COLUMN_JOB_ID = "job_id";
    private static final String COLUMN_JOB_TYPE = "job_type";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DURATION = "duration_in_days";
    private static final String COLUMN_RESPONSIBLE_EMPL_ID = "responsible_employee_id";

    private EmployeeDAO employeeDAO;

    public JobDAO() {
        this.employeeDAO = new EmployeeDAO();
    }

    public Job save(@Valid Job job) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, job.getJobType().toString());
            preparedStatement.setString(2, job.getTitle());
            preparedStatement.setString(3, job.getDescription());
            preparedStatement.setInt(4, job.getDurationInDays());
            preparedStatement.setInt(5, job.getResponsibleEmployee().getId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new ApplicationException("Cannot save job. " + job, ResponseStatus.NOT_FOUND);
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    job.setId(generatedKeys.getInt(1));
                } else {
                    throw new ApplicationException("Cannot save job, no ID obtained. " + job, ResponseStatus.NOT_FOUND);
                }
            }
            return job;

        } catch (SQLException e) {
            throw new ApplicationException("Cannot save job. " + job + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void saveProjectJob(@Valid Job job, int projectId) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_PROJECT_JOB)) {
            preparedStatement.setInt(1, job.getId());
            preparedStatement.setInt(2, projectId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Cannot save job. " + job + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public Job update(@Valid Job job) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setString(1, job.getJobType().toString());
            preparedStatement.setString(2, job.getTitle());
            preparedStatement.setString(3, job.getDescription());
            preparedStatement.setInt(4, job.getDurationInDays());
            preparedStatement.setInt(5, job.getResponsibleEmployee().getId());
            preparedStatement.setInt(6, job.getId());
            preparedStatement.execute();
            return job;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot update job. " + job + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public Job getById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildJob(resultSet);
            } else {
                throw new ApplicationException("Job with id = " + id + " not found.", ResponseStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ApplicationException("Cannot  get job with id = " + id + " in database." + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public List<Job> getAll() throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Job> jobList = new ArrayList<>();
            while (resultSet.next()) {
                jobList.add(buildJob(resultSet));
            }
            return jobList;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot get all jobs. " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void deleteById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Cannot delete job with id = " + id + " " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public List<Job> getProjectJobs(@Positive int projectId) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_PROJECT_JOBS)) {
            preparedStatement.setInt(1, projectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Job> jobList = new ArrayList<>();
            while (resultSet.next()) {
                jobList.add(buildJob(resultSet));
            }
            return jobList;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot get jobs for project with id = " + projectId + " " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    private Job buildJob(ResultSet resultSet) throws ApplicationException {
        try {
            Job job = new Job();
            job.setId(resultSet.getInt(COLUMN_JOB_ID));
            job.setJobType(JobType.valueOf(resultSet.getString(COLUMN_JOB_TYPE)));
            job.setTitle(resultSet.getString(COLUMN_TITLE));
            job.setDescription(resultSet.getString(COLUMN_DESCRIPTION));
            job.setDurationInDays(resultSet.getInt(COLUMN_DURATION));
            if (resultSet.getInt(COLUMN_RESPONSIBLE_EMPL_ID) != 0) {
                job.setResponsibleEmployee(employeeDAO.getById(resultSet.getInt(COLUMN_RESPONSIBLE_EMPL_ID)));
            }
            return job;
        } catch (SQLException e) {
            throw new ApplicationException("Error while building job! " + e, ResponseStatus.BAD_REQUEST);
        }
    }

}
