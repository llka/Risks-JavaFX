package darya.risks.backend.command.admin;

import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.service.EmployeeService;
import darya.risks.backend.util.JsonUtil;
import darya.risks.dto.EmployeeListDTO;
import darya.risks.entity.Job;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GetBestEmployeesForRiskCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(GetBestEmployeesForRiskCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {

        EmployeeService employeeService = new EmployeeService();

        Job risk = JsonUtil.deserialize(request.getBody(), Job.class);
        EmployeeListDTO employeeListDTO = new EmployeeListDTO(employeeService.findBestEmployeesForRisk(risk));

        return new CommandResponse(JsonUtil.serialize(employeeListDTO), ResponseStatus.OK);
    }
}
