package darya.risks.backend.command.guest;

import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.command.CommandType;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.service.ContactService;
import darya.risks.backend.util.JsonUtil;
import darya.risks.entity.Contact;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.enums.RoleEnum;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class RegisterCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(RegisterCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactService = new ContactService();

        Contact contact = JsonUtil.deserialize(request.getBody(), Contact.class);
        if (contact.getFirstName() == null || contact.getFirstName().isEmpty()) {
            throw new ApplicationException("Invalid First name!", ResponseStatus.BAD_REQUEST);
        }
        if (contact.getLastName() == null || contact.getLastName().isEmpty()) {
            throw new ApplicationException("Invalid Last name!", ResponseStatus.BAD_REQUEST);
        }
        if (contact.getEmail() == null || contact.getEmail().isEmpty()) {
            throw new ApplicationException("Invalid Email!", ResponseStatus.BAD_REQUEST);
        }
        if (contact.getPassword() == null || contact.getPassword().isEmpty()) {
            throw new ApplicationException("Invalid Password!", ResponseStatus.BAD_REQUEST);
        }

        contact.setRole(RoleEnum.USER);
        contact = contactService.register(contact);

        session.getVisitor().setContact(contact);
        session.getVisitor().setRole(contact.getRole());
        return new CommandResponse(CommandType.REGISTER.toString(), JsonUtil.serialize(contact), ResponseStatus.OK);

    }
}