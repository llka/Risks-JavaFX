package darya.risks.backend.command;


import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;

public interface ActionCommand {
    CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException;
}