package darya.risks.backend.factory;

import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.command.CommandType;
import darya.risks.backend.command.EmptyCommand;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.Visitor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ActionFactory {
    private static final Logger logger = LogManager.getLogger(ActionFactory.class);

    public ActionCommand defineCommand(CommandRequest request, Visitor visitor) throws ApplicationException {

        ActionCommand command = new EmptyCommand();

        if (request == null || request.getCommand() == null) {
            throw new ApplicationException("Request is null!", ResponseStatus.BAD_REQUEST);
        }

        CommandType commandType = CommandType.valueOf(request.getCommand());
        if (commandType == null) {
            return command;
        }

        if (!commandType.role.contains(visitor.getRole())) {
            throw new ApplicationException("You don't have enough permissions for this action!", ResponseStatus.UNAUTHORIZED);
        }

        command = commandType.getCommand();
        logger.debug("ActionFactory command = " + command.getClass().toString());
        return command;
    }
}
