package darya.risks.backend.service;

import darya.risks.backend.dao.EmployeeDAO;
import darya.risks.backend.dao.JobDAO;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.entity.Employee;
import darya.risks.entity.Job;
import darya.risks.entity.enums.JobType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class EmployeeService {
    private static final Logger logger = LogManager.getLogger(EmployeeService.class);

    private static final int NORMAL_COEFF = 1;
    private static final int BEST_COEFF = 3;

    private EmployeeDAO employeeDAO;
    private JobDAO jobDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
        this.jobDAO = new JobDAO();
    }

    public List<Employee> findBestEmployeesForRisk(Job risk) throws ApplicationException {
        if (risk.getId() == 0) {
            risk.setJobType(JobType.RISK);
            jobDAO.save(risk);
        }
        HashMap<Integer, Integer> employeeAndSolvedRisks = new HashMap<>();
        List<Job> similarRisks = new ArrayList<>();

        for (Job job : jobDAO.getAll()) {
            if (JobType.RISK.equals(job.getJobType())) {
                similarRisks.add(job);
                int employeeId = job.getResponsibleEmployee().getId();
                if (employeeAndSolvedRisks.containsKey(employeeId)) {
                    employeeAndSolvedRisks.put(employeeId, employeeAndSolvedRisks.get(employeeId) + NORMAL_COEFF);
                } else {
                    employeeAndSolvedRisks.put(employeeId, NORMAL_COEFF);
                }
            }
        }

        logger.debug(" map 1:");
        employeeAndSolvedRisks.entrySet().forEach(logger::debug);

        for (Job job : similarRisks) {
            if (risk.getTitle().equalsIgnoreCase(job.getTitle()) ||
                    risk.getDescription().equalsIgnoreCase(job.getDescription())) {
                int employeeId = job.getResponsibleEmployee().getId();
                if (employeeAndSolvedRisks.containsKey(employeeId)) {
                    employeeAndSolvedRisks.put(employeeId, employeeAndSolvedRisks.get(employeeId) + BEST_COEFF);
                } else {
                    employeeAndSolvedRisks.put(employeeId, BEST_COEFF);
                }
            }
        }

        logger.debug(" map 2:");
        employeeAndSolvedRisks.entrySet().forEach(logger::debug);

            /*Stream<Map.Entry<Integer,Integer>> sorted = employeeAndSolvedRisks.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(value -> value)));*/

        Stream<Map.Entry<Integer, Integer>> sorted = employeeAndSolvedRisks.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((val1, val2) -> val2 - val1));

        logger.debug("sorted map:");
        sorted.forEach(logger::debug);

        List<Employee> bestEmployees = new ArrayList<>();
        sorted.forEach((entry) -> {
            try {
                bestEmployees.add(employeeDAO.getById(entry.getKey()));
            } catch (ApplicationException e) {
                logger.error(e);
                e.printStackTrace();
            }
        });

        logger.debug("best employees: ");
        bestEmployees.forEach(logger::debug);

        if (bestEmployees.isEmpty()) {
            logger.info("best employees not found!");
            return employeeDAO.getAll();
        }
        return bestEmployees;
    }
}
