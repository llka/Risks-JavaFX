package darya.risks.backend.command.admin;

import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.dao.EmployeeDAO;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.util.JsonUtil;
import darya.risks.dto.EmployeeListDTO;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GetAllEmployeesCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(GetAllEmployeesCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeListDTO employeeListDTO = new EmployeeListDTO(employeeDAO.getAll());

        /*
        Stream<Map.Entry<K,V>> sorted = map.entrySet().stream().sorted(Map.Entry.comparingByValue(comparator));
         */

        return new CommandResponse(JsonUtil.serialize(employeeListDTO), ResponseStatus.OK);
    }
}
