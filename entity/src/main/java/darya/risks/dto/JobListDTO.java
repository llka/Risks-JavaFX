package darya.risks.dto;

import darya.risks.entity.Job;

import java.util.List;

public class JobListDTO {
    private List<Job> jobList;

    public JobListDTO() {
    }

    public JobListDTO(List<Job> jobList) {
        this.jobList = jobList;
    }

    public List<Job> getJobList() {
        return jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    @Override
    public String toString() {
        return "JobListDTO{" +
                "jobList=" + jobList +
                '}';
    }
}
