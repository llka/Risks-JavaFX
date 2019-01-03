package darya.risks.backend.command.admin;


import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class DeleteContactCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(DeleteContactCommand.class);

    private static final String EMAIL_PARAM = "email";
    private static final String ID_PARAM = "id";

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactService = new ContactService();
        contactService.delete(request.getParameter(ID_PARAM), request.getParameter(EMAIL_PARAM));

        return new CommandResponse(ResponseStatus.OK);
    }
}
