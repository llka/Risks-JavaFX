package darya.risks.client.controller;

import darya.risks.client.Main;
import darya.risks.client.client.ContextHolder;
import darya.risks.client.exception.ClientException;
import darya.risks.client.util.JsonUtil;
import darya.risks.dto.EmployerListDTO;
import darya.risks.entity.Employer;
import darya.risks.entity.Job;
import darya.risks.entity.Project;
import darya.risks.entity.enums.EmployerPersonType;
import darya.risks.entity.enums.JobType;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static darya.risks.client.util.AlertUtil.alert;
import static darya.risks.client.util.AlertUtil.alertError;

public class NewProjectController {
    private static final Logger logger = LogManager.getLogger(NewProjectController.class);

    private static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static boolean firstOpened = true;

    private Project project;

    private static Main main;

    @FXML
    private Button addNewJobBtn;
    @FXML
    private Button okBtn;
    @FXML
    private Button newEmployerBtn;

    @FXML
    private TextField projectTitleTextField;
    @FXML
    private ComboBox<String> employerComboBox;
    @FXML
    private DatePicker startDateDatePicker;

    @FXML
    private TableView jobsTable;
    @FXML
    private TableColumn<Job, String> jobTitleColumn;
    @FXML
    private TableColumn<Job, Integer> durationColumn;
    @FXML
    private TableColumn<Job, Integer> idColumn;
    @FXML
    private TableColumn<Job, String> descriptionColumn;

    @FXML
    private void initialize() {
        logger.debug("initialize");

        employerComboBox.setItems(FXCollections.observableArrayList(getAllEmployers()));

        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        jobTitleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        durationColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDurationInDays()).asObject());

        if (firstOpened) {
            project = new Project();
            firstOpened = false;
        }

        projectTitleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            okBtn.setDisable(newValue.trim().isEmpty());
        });
    }

    private void addJobToTable(Job job) {
        ObservableList<Job> currentList = jobsTable.getItems();
        currentList.add(job);
        jobsTable.setItems(currentList);
    }

    @FXML
    void addNewJob(ActionEvent event) {
        Dialog<Job> dialog = new Dialog<>();
        dialog.setTitle("New Job Dialog");

        ButtonType okButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField title = new TextField();
        title.setPromptText("Title");
        TextField description = new TextField();
        description.setPromptText("Description");
        ComboBox<String> jobType = new ComboBox();
        jobType.setItems(FXCollections.observableArrayList(
                JobType.NORMAL_JOB.toString(),
                JobType.RISK.toString()
        ));
        TextField duration = new TextField();
        duration.setPromptText("1");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(title, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(description, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(jobType, 1, 2);
        grid.add(new Label("Duration (days):"), 0, 3);
        grid.add(duration, 1, 3);

        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);
        duration.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
            if (!newValue.trim().isEmpty()) {
                try {
                    Integer.parseInt(newValue.trim());
                } catch (NumberFormatException e) {
                    okButton.setDisable(true);
                }
            }

        });
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> title.requestFocus());

        dialog.setResultConverter(new Callback<ButtonType, Job>() {
                                      @Override
                                      public Job call(ButtonType buttonType) {
                                          if (buttonType == okButtonType) {
                                              Job job = new Job();
                                              job.setJobType(JobType.valueOf(jobType.getValue()));
                                              job.setDescription(description.getText());
                                              job.setTitle(title.getText());
                                              job.setDurationInDays(Integer.parseInt(duration.getText()));
                                              return job;
                                          }
                                          return null;
                                      }
                                  }

        );
        Optional<Job> result = dialog.showAndWait();
        if (result.isPresent()) {
            logger.debug("new job Result is present!");
            logger.debug(result.get());
            project.addJob(result.get());
            logger.debug("Current project:");
            logger.debug(project);

            addJobToTable(result.get());
        } else {
            alertError("Wrong input!");
        }
    }

    @FXML
    void createProject(ActionEvent event) {
        project.setTitle(projectTitleTextField.getText().trim());

        LocalDate startDate = startDateDatePicker.getValue();
        if (startDate != null) {
            Instant instant = Instant.from(startDate.atStartOfDay(ZoneId.systemDefault()));
            Date date = Date.from(instant);
            project.setStartDate(date);
        } else {
            logger.warn("Start date not provided, current date will be used!");
            project.setStartDate(new Date());
        }

        if (project.getEmployer() == null && employerComboBox.getValue() != null && !employerComboBox.getValue().isEmpty()) {
            try {
                int employerId = Integer.valueOf(employerComboBox.getValue().split(",")[0]);
                logger.debug(employerId);
                if (employerId != 0) {
                    try {
                        ContextHolder.getClient().sendRequest(new CommandRequest("GET_ALL_EMPLOYERS"));
                        CommandResponse response = ContextHolder.getLastResponse();
                        if (response.getStatus().is2xxSuccessful()) {
                            EmployerListDTO employerListDTO = JsonUtil.deserialize(response.getBody(), EmployerListDTO.class);
                            for (Employer empl : employerListDTO.getEmployerList()) {
                                if (employerId == empl.getId()) {
                                    project.setEmployer(empl);
                                }
                            }
                        } else {
                            alert(Alert.AlertType.ERROR, "Cannot get employer!", response.getBody());
                        }
                    } catch (ClientException e) {
                        logger.error(e);
                        alert(Alert.AlertType.ERROR, "Cannot get all employers!", e.getMessage());
                    }
                } else {
                    project.setEmployer(null);
                }
            } catch (Exception e) {
                logger.error(e);
                project.setEmployer(null);
            }

        }


        boolean valid = true;
        if (project.getJobs().isEmpty()) {
            alertError("Cannot create new project without any jobs!");
            valid = false;
        }
        if (project.getEmployer() == null) {
            alertError("Cannot create new project without an employer!");
            valid = false;
        }
        if (project.getStartDate() == null) {
            alertError("Cannot create new project without a start date!");
            valid = false;
        }

        if (valid) {
            logger.debug("valid project!");
            logger.debug(project);
            try {
                ContextHolder.getClient().sendRequest(new CommandRequest("CREATE_PROJECT", JsonUtil.serialize(project)));
                CommandResponse response = ContextHolder.getLastResponse();
                if (response.getStatus().is2xxSuccessful()) {
                    alert("Successfully created a new project, you can find it in 'My Projects' tab!");
                    openMainView();
                } else {
                    alert(Alert.AlertType.ERROR, "Cannot create project!", response.getBody());
                }
            } catch (ClientException e) {
                logger.error(e);
                alert(Alert.AlertType.ERROR, "Cannot create project!", e.getMessage());
            }
        }
    }

    @FXML
    void useNewEmployer(ActionEvent event) {
        Dialog<Employer> dialog = new Dialog<>();
        dialog.setTitle("New Employer Dialog");

        ButtonType okButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstName = new TextField();
        firstName.setPromptText("First Name");
        TextField lastName = new TextField();
        lastName.setPromptText("Last Name");
        ComboBox<String> personType = new ComboBox();
        personType.setItems(FXCollections.observableArrayList(
                EmployerPersonType.INDIVIDUAL.toString(),
                EmployerPersonType.LEGAL_PERSON.toString()
        ));
        TextField companyName = new TextField();
        companyName.setPromptText("Company Name");
        TextField telephone = new TextField();
        telephone.setPromptText("Telephone");
        TextField address = new TextField();
        address.setPromptText("Address");
        TextField scopeOfWork = new TextField();
        scopeOfWork.setPromptText("Business scope");
        TextField passportNumber = new TextField();
        passportNumber.setPromptText("Passport Number");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstName, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastName, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(personType, 1, 2);
        grid.add(new Label("Company Name:"), 0, 3);
        grid.add(companyName, 1, 3);
        grid.add(new Label("Telephone:"), 0, 4);
        grid.add(telephone, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(address, 1, 5);
        grid.add(new Label("Business scope:"), 0, 6);
        grid.add(scopeOfWork, 1, 6);
        grid.add(new Label("Passport Number:"), 0, 7);
        grid.add(passportNumber, 1, 7);

        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);
        passportNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> firstName.requestFocus());

        dialog.setResultConverter(new Callback<ButtonType, Employer>() {
                                      @Override
                                      public Employer call(ButtonType buttonType) {
                                          if (buttonType == okButtonType) {
                                              Employer employer = new Employer();
                                              employer.setFirstName(firstName.getText());
                                              employer.setLastName(lastName.getText());
                                              employer.setPersonType(EmployerPersonType.valueOf(personType.getValue()));
                                              employer.setCompanyName(companyName.getText());
                                              employer.setTelephone(telephone.getText());
                                              employer.setAddress(address.getText());
                                              employer.setScopeOfWork(scopeOfWork.getText());
                                              employer.setPassportNumber(passportNumber.getText());
                                              return employer;
                                          }
                                          return null;
                                      }
                                  }

        );
        Optional<Employer> result = dialog.showAndWait();
        if (result.isPresent()) {
            logger.debug("Result is present!");
            logger.debug(result.get());
            project.setEmployer(result.get());
            logger.debug("Current project:");
            logger.debug(project);
            employerComboBox.setValue(result.get().readableValueForComboBox());
        } else {
            alertError("Invalid input!");
        }
    }

    public static boolean isFirstOpened() {
        return firstOpened;
    }

    public static void setFirstOpened(boolean firstOpened) {
        NewProjectController.firstOpened = firstOpened;
    }

    public static void setMain(Main main) {
        NewProjectController.main = main;
    }

    private ArrayList<String> getAllEmployers() {
        try {
            ContextHolder.getClient().sendRequest(new CommandRequest("GET_ALL_EMPLOYERS"));
            CommandResponse response = ContextHolder.getLastResponse();
            if (response.getStatus().is2xxSuccessful()) {
                EmployerListDTO employerListDTO = JsonUtil.deserialize(response.getBody(), EmployerListDTO.class);
                ArrayList<String> employersForCheckBox = new ArrayList<>();
                employerListDTO.getEmployerList().forEach(employer -> employersForCheckBox.add(employer.readableValueForComboBox()));

                logger.debug(employersForCheckBox);
                return employersForCheckBox;
            } else {
                alert(Alert.AlertType.ERROR, "Cannot get all employers!", response.getBody());
            }
        } catch (ClientException e) {
            logger.error(e);
            alert(Alert.AlertType.ERROR, "Cannot get all employers!", e.getMessage());
        }
        return new ArrayList<>();
    }

    private void openMainView() {
        main.showGuestMainView();
    }

}
