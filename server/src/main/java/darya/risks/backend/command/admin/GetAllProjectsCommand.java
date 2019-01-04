package darya.risks.backend.command.admin;

import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.command.user.GetMyProjectsCommand;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.service.ProjectService;
import darya.risks.backend.util.JsonUtil;
import darya.risks.dto.ProjectListDTO;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GetAllProjectsCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(GetMyProjectsCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ProjectService projectService = new ProjectService();
        ProjectListDTO projectListDTO = new ProjectListDTO(projectService.getAll());
        return new CommandResponse(JsonUtil.serialize(projectListDTO), ResponseStatus.OK);
    }
}
