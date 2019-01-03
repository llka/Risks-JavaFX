package darya.risks.backend.command;


import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class EmptyCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(EmptyCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) {
        logger.debug("Welcome to empty command");

        return new CommandResponse(CommandType.EMPTY.toString(), ResponseStatus.NOT_IMPLEMENTED);
    }
}
