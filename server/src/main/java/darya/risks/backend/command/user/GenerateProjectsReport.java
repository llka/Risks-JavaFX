package darya.risks.backend.command.user;

import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.service.ContactService;
import darya.risks.backend.util.ExcelUtil;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GenerateProjectsReport implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(GenerateProjectsReport.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactService = new ContactService();
        ExcelUtil.generateExcelProjectsReport(contactService.getAll(), "Projects report");
        return new CommandResponse(ResponseStatus.OK);
    }
}
