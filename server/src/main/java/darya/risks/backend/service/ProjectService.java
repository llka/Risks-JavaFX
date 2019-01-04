package darya.risks.backend.service;

import darya.risks.backend.dao.EmployerDAO;
import darya.risks.backend.dao.JobDAO;
import darya.risks.backend.dao.ProjectDAO;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.entity.Contact;
import darya.risks.entity.Employer;
import darya.risks.entity.Job;
import darya.risks.entity.Project;
import darya.risks.entity.enums.ResponseStatus;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectService {
    private static final Logger logger = LogManager.getLogger(ProjectService.class);


    private ProjectDAO projectDAO;
    private EmployerDAO employerDAO;
    private JobDAO jobDAO;

    public ProjectService() {
        this.projectDAO = new ProjectDAO();
        this.employerDAO = new EmployerDAO();
        this.jobDAO = new JobDAO();
    }

    public Project createProject(Project project, Contact contact) throws ApplicationException {
        if (project.getEmployer() != null) {
            if (project.getEmployer().getId() == 0) {
                Employer savedEmployer = employerDAO.save(project.getEmployer());
                project.setEmployer(savedEmployer);
            }
        } else {
            throw new ApplicationException("Cannot create a project without employer!", ResponseStatus.BAD_REQUEST);
        }

        if (project.getJobs() == null || project.getJobs().isEmpty()) {
            throw new ApplicationException("Cannot create a project without jobs!", ResponseStatus.BAD_REQUEST);
        }

        List<Job> savedJobs = new ArrayList<>();
        for (Job job : project.getJobs()) {
            if (job.getId() == 0) {
                savedJobs.add(jobDAO.save(job));
            } else {
                savedJobs.add(job);
            }
        }
        project.setJobs(savedJobs);

        calculateProjectEndDate(project);
        Project savedProject = projectDAO.save(project, contact);
        for (Job job : savedJobs) {
            jobDAO.saveProjectJob(job, savedProject.getId());
        }

        savedProject = projectDAO.getById(savedProject.getId());
        return savedProject;
    }

    public List<Project> getAll() throws ApplicationException {
        return projectDAO.getAll();
    }

    private void calculateProjectEndDate(Project project) {
        if (project.getStartDate() == null) {
            Date now = new Date();
            project.setStartDate(now);
        }

        int sumTime = project.getJobs().stream().mapToInt(Job::getDurationInDays).reduce(0, Integer::sum);
        logger.debug("sum time = " + sumTime);
        project.setEndDate(DateUtils.addDays(project.getStartDate(), sumTime));
        logger.debug("start - " + project.getStartDate());
        logger.debug("end - " + project.getEndDate());
    }
}
