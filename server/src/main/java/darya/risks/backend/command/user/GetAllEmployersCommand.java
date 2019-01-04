package darya.risks.backend.command.user;

import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.dao.EmployerDAO;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.util.JsonUtil;
import darya.risks.dto.EmployerListDTO;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GetAllEmployersCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(CreateProjectCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {

        EmployerDAO employerDAO = new EmployerDAO();
        EmployerListDTO employerListDTO = new EmployerListDTO(employerDAO.getAll());
        return new CommandResponse(JsonUtil.serialize(employerListDTO), ResponseStatus.OK);
    }
}