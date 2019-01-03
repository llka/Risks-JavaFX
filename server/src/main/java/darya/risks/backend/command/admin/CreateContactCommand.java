package darya.risks.backend.command.admin;


import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.command.CommandType;
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


public class CreateContactCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(CreateContactCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactService = new ContactService();

        Contact contact = JsonUtil.deserialize(request.getBody(), Contact.class);
        try {
            contactService.getByEmail(contact.getEmail());
            return new CommandResponse(CommandType.CREATE_CONTACT.toString(), "Contact with the same email " + contact.getEmail() + " already exists!", ResponseStatus.PARTIAL_CONTENT);
        } catch (ApplicationException e) {
            contact = contactService.register(contact);
            return new CommandResponse(CommandType.CREATE_CONTACT.toString(), JsonUtil.serialize(contact), ResponseStatus.OK);
        }
    }
}
