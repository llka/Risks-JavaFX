package darya.risks.backend.command.user;

import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.service.ContactService;
import darya.risks.backend.util.ExcelUtil;
import darya.risks.entity.Contact;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.enums.RoleEnum;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class GenerateProjectsReport implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(GenerateProjectsReport.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactService = new ContactService();
        List<Contact> contacts = new ArrayList<>();
        if (RoleEnum.USER.equals(session.getVisitor().getRole())) {
            contacts.add(session.getVisitor().getContact());
        } else if (RoleEnum.ADMIN.equals(session.getVisitor().getRole())) {
            contacts.addAll(contactService.getAll());
        }
        ExcelUtil.generateExcelProjectsReport(contactService.getAll(), "Projects report", request.getParameter("path"));
        return new CommandResponse(ResponseStatus.OK);
    }
}
