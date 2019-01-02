package darya.risks.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Project implements DatabaseEntity {
    private int id;
    @NotNull
    private Employer employer;
    @NotBlank
    private String title;

    private List<Job> jobs;

    private Date startDate;
    private Date endDate;

    public Project() {
    }

    public Project(@NotNull Employer employer, @NotBlank String title, List<Job> jobs, Date startDate, Date endDate) {
        this.employer = employer;
        this.title = title;
        this.jobs = jobs;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id == project.id &&
                Objects.equals(employer, project.employer) &&
                Objects.equals(title, project.title) &&
                Objects.equals(startDate, project.startDate) &&
                Objects.equals(endDate, project.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employer, title, startDate, endDate);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", employer=" + employer +
                ", title='" + title + '\'' +
                ", jobs=" + jobs +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
