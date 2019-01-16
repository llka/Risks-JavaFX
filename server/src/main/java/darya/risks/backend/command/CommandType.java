package darya.risks.backend.command;


import darya.risks.backend.command.admin.*;
import darya.risks.backend.command.guest.LogInCommand;
import darya.risks.backend.command.guest.RegisterCommand;
import darya.risks.backend.command.user.*;
import darya.risks.entity.enums.RoleEnum;

import java.util.EnumSet;
import java.util.Set;


public enum CommandType {
    LOGIN {
        {
            this.command = new LogInCommand();
            this.role = EnumSet.of(RoleEnum.GUEST);
        }
    },
    REGISTER {
        {
            this.command = new RegisterCommand();
            this.role = EnumSet.of(RoleEnum.GUEST);
        }
    },
    LOGOUT {
        {
            this.command = new LogOutCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN, RoleEnum.USER);
        }
    },


    GET_CONTACT {
        {
            this.command = new GetContactCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN, RoleEnum.USER);
        }
    },
    GET_CONTACTS {
        {
            this.command = new GetContactsCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN, RoleEnum.USER);
        }
    },
    UPDATE_CONTACT {
        {
            this.command = new UpdateContactCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN, RoleEnum.USER);
        }
    },
    CREATE_CONTACT {
        {
            this.command = new CreateContactCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },
    DELETE_CONTACT {
        {
            this.command = new DeleteContactCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },

    CREATE_PROJECT {
        {
            this.command = new CreateProjectCommand();
            this.role = EnumSet.of(RoleEnum.USER, RoleEnum.ADMIN);
        }
    },
    UPDATE_PROJECT {
        {
            this.command = new UpdateProjectCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },
    CREATE_JOB {
        {
            this.command = new CreateJobCommand();
            this.role = EnumSet.of(RoleEnum.USER, RoleEnum.ADMIN);
        }
    },
    GET_MY_PROJECTS {
        {
            this.command = new GetMyProjectsCommand();
            this.role = EnumSet.of(RoleEnum.USER, RoleEnum.ADMIN);
        }
    },
    GET_ALL_PROJECTS {
        {
            this.command = new GetAllProjectsCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },

    GET_ALL_EMPLOYERS {
        {
            this.command = new GetAllEmployersCommand();
            this.role = EnumSet.of(RoleEnum.USER, RoleEnum.ADMIN);
        }
    },
    GET_ALL_EMPLOYEES {
        {
            this.command = new GetAllEmployeesCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },
    GET_BEST_EMPLOYEES_FOR_RISK {
        {
            this.command = new GetBestEmployeesForRiskCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },
    GET_ALL_RISKS {
        {
            this.command = new GetAllRisksCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },
    ADD_RISK_TO_PROJECT {
        {
            this.command = new AddRiskToProjectCommand();
            this.role = EnumSet.of(RoleEnum.ADMIN);
        }
    },

    GENERATE_EXCEL_PROJECTS_REPORT {
        {
            this.command = new GenerateProjectsReport();
            this.role = EnumSet.of(RoleEnum.USER, RoleEnum.ADMIN);
        }
    },


    EMPTY {
        {
            this.command = new EmptyCommand();
            this.role = EnumSet.of(RoleEnum.GUEST, RoleEnum.USER, RoleEnum.ADMIN);
        }
    };

    public ActionCommand command;
    public Set<darya.risks.entity.enums.RoleEnum> role;

    public ActionCommand getCommand() {
        return command;
    }
}
