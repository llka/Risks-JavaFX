package darya.risks.dto;

import darya.risks.entity.Project;

import java.util.List;

public class ProjectListDTO {
    private List<Project> projectList;

    public ProjectListDTO() {
    }

    public ProjectListDTO(List<Project> projectList) {
        this.projectList = projectList;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    @Override
    public String toString() {
        return "ProjectListDTO{" +
                "projectList=" + projectList +
                '}';
    }
}
