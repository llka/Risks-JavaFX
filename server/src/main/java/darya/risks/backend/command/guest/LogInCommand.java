package darya.risks.backend.command.guest;


import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.command.CommandType;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.util.JsonUtil;
import darya.risks.entity.Contact;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class LogInCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(LogInCommand.class);

    private static final String EMAIL_PARAM = "email";
    private static final String PASSWORD_PARAM = "password";

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactService = new ContactService();

        String email = request.getParameter(EMAIL_PARAM);
        String password = request.getParameter(PASSWORD_PARAM);

        Contact contact = contactService.login(email, password);

        session.getVisitor().setContact(contact);
        session.getVisitor().setRole(contact.getRole());
        return new CommandResponse(CommandType.LOGIN.toString(), JsonUtil.serialize(contact), ResponseStatus.OK);

    }
}
