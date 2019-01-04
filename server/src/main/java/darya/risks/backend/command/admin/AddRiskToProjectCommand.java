package darya.risks.backend.command.admin;

import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.service.ProjectService;
import darya.risks.backend.util.JsonUtil;
import darya.risks.entity.Job;
import darya.risks.entity.Project;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class AddRiskToProjectCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(AddRiskToProjectCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {

        ProjectService projectService = new ProjectService();

        int projectId = Integer.parseInt(request.getParameter("projectId"));

        Project project = projectService.addRiskToProject(projectId, JsonUtil.deserialize(request.getBody(), Job.class));

        return new CommandResponse(JsonUtil.serialize(project), ResponseStatus.OK);
    }
}
