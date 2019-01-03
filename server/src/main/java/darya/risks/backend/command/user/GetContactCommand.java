package darya.risks.backend.command.user;


import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.service.ContactService;
import darya.risks.backend.util.JsonUtil;
import darya.risks.entity.Contact;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GetContactCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(GetContactCommand.class);

    private static final String EMAIL_PARAM = "email";
    private static final String ID_PARAM = "id";

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactLogic = new ContactService();
        String email = request.getParameter(EMAIL_PARAM);
        int id = -1;
        if (request.getParameter(ID_PARAM) != null) {
            id = Integer.parseInt(request.getParameter(ID_PARAM));
        }
        Contact contact = null;
        if (email != null && !email.isEmpty()) {
            try {
                contact = contactLogic.getByEmail(email);
            } catch (ApplicationException e) {
                if (id > 0) {
                    contact = contactLogic.getById(id);
                }
            }
        } else if (id > 0) {
            try {
                contact = contactLogic.getById(id);
            } catch (ApplicationException e) {
                contact = contactLogic.getByEmail(email);
            }
        }
        if (contact != null) {
            //logger.debug(contact);
            return new CommandResponse(JsonUtil.serialize(contact), ResponseStatus.OK);
        } else {
            return new CommandResponse("Contact not found", ResponseStatus.NOT_FOUND);
        }
    }
}