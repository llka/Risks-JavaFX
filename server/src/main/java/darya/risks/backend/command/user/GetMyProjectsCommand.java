package darya.risks.backend.command.user;

import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.dao.ProjectDAO;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.service.ContactService;
import darya.risks.backend.util.JsonUtil;
import darya.risks.dto.ProjectListDTO;
import darya.risks.entity.Contact;
import darya.risks.entity.Project;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.List;

public class GetMyProjectsCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(GetMyProjectsCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactService = new ContactService();

        Contact currentContact = contactService.getById(session.getVisitor().getContact().getId());

        ProjectDAO projectDAO = new ProjectDAO();
        List<Project> projects = projectDAO.getContactProjects(currentContact.getId());

        projects.sort((Comparator.comparing(Project::getEndDate)));

        ProjectListDTO projectListDTO = new ProjectListDTO(projects);
        return new CommandResponse(JsonUtil.serialize(projectListDTO), ResponseStatus.OK);
    }
}