package darya.risks.backend.command.user;


import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.command.CommandType;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.service.ContactService;
import darya.risks.backend.util.JsonUtil;
import darya.risks.dto.ContactListDTO;
import darya.risks.entity.Contact;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class GetContactsCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(GetContactsCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactLogic = new ContactService();

        List<Contact> contacts = contactLogic.getAll();
        ContactListDTO contactListDTO = new ContactListDTO(contacts);
        return new CommandResponse(CommandType.GET_CONTACTS.toString(), JsonUtil.serialize(contactListDTO), ResponseStatus.OK);
    }
}