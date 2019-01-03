package darya.risks.backend.command.user;


import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.command.CommandType;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.util.JsonUtil;
import darya.risks.entity.Contact;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;

public class UpdateContactCommand implements ActionCommand {
    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactService = new ContactService();

        Contact contact = JsonUtil.deserialize(request.getBody(), Contact.class);
        contactService.update(contact);

        if (session.getVisitor().getContact().getId() == contact.getId()) {
            session.getVisitor().setContact(contact);
            session.getVisitor().setRole(contact.getRole());
        }

        return new CommandResponse(CommandType.UPDATE_CONTACT.toString(), JsonUtil.serialize(contact), ResponseStatus.OK);
    }
}
