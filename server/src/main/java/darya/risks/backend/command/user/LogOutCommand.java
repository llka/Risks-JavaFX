package darya.risks.backend.command.user;


import darya.risks.backend.command.ActionCommand;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.enums.RoleEnum;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import darya.risks.entity.technical.Visitor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class LogOutCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(LogOutCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) {

        session.setVisitor(new Visitor(RoleEnum.GUEST));
        session.getVisitor().setContact(null);

        return new CommandResponse(ResponseStatus.OK);
    }
}
