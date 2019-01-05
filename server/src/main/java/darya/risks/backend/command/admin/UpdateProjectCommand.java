package darya.risks.backend.command.admin;

import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.service.ProjectService;
import darya.risks.backend.util.JsonUtil;
import darya.risks.entity.Project;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class UpdateProjectCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(UpdateProjectCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ProjectService projectService = new ProjectService();

        Project project = JsonUtil.deserialize(request.getBody(), Project.class);
        Project updatedProject = projectService.updateProject(project);
        return new CommandResponse(JsonUtil.serialize(updatedProject), ResponseStatus.OK);
    }
}