package darya.risks.entity;

import darya.risks.entity.enums.JobType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.Objects;

public class Job implements DatabaseEntity {
    private int id;
    private JobType jobType;
    @NotBlank
    private String title;
    private String description;
    @Positive
    private int durationInDays;

    public Job() {
    }

    public Job(@NotBlank String title, String description, @Positive int durationInDays) {
        this.title = title;
        this.description = description;
        this.durationInDays = durationInDays;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return id == job.id &&
                durationInDays == job.durationInDays &&
                jobType == job.jobType &&
                Objects.equals(title, job.title) &&
                Objects.equals(description, job.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jobType, title, description, durationInDays);
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", jobType=" + jobType +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", durationInDays=" + durationInDays +
                '}';
    }
}
