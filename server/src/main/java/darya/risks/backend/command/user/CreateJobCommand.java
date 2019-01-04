package darya.risks.backend.command.user;

import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.dao.JobDAO;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.util.JsonUtil;
import darya.risks.entity.Job;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class CreateJobCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(CreateJobCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {

        JobDAO jobDAO = new JobDAO();
        Job job = JsonUtil.deserialize(request.getBody(), Job.class);
        jobDAO.save(job);
        return new CommandResponse(ResponseStatus.OK);
    }
}
