package darya.risks.backend.command.admin;

import darya.risks.backend.command.ActionCommand;
import darya.risks.backend.dao.JobDAO;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.backend.util.JsonUtil;
import darya.risks.dto.JobListDTO;
import darya.risks.entity.enums.JobType;
import darya.risks.entity.enums.ResponseStatus;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.stream.Collectors;

public class GetAllRisksCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(GetAllRisksCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {

        JobDAO jobDAO = new JobDAO();

        JobListDTO jobListDTO = new JobListDTO(jobDAO.getAll().stream().filter(job -> JobType.RISK.equals(job.getJobType())).collect(Collectors.toList()));

        logger.debug("jobListDTO");
        jobListDTO.getJobList().forEach(logger::debug);

        return new CommandResponse(JsonUtil.serialize(jobListDTO), ResponseStatus.OK);
    }
}