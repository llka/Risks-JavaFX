package darya.risks.client.controller;

import darya.risks.client.client.ContextHolder;
import darya.risks.client.exception.ClientException;
import darya.risks.client.util.JsonUtil;
import darya.risks.dto.ProjectListDTO;
import darya.risks.entity.Project;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static darya.risks.client.util.AlertUtil.alert;

public class MyProjectsController {
    private static final Logger logger = LogManager.getLogger(NewProjectController.class);

    private static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final int DAYS_BEFORE_PROJECT_END_YELLOW = 15;

    private static boolean firstOpened = true;

    @FXML
    private TableView projectsTable;
    @FXML
    private TableColumn<Project, String> endDateColumn;
    @FXML
    private TableColumn<Project, String> titleColumn;
    @FXML
    private TableColumn<Project, String> startDateColumn;
    @FXML
    private TableColumn<Project, String> employerColumn;
    @FXML
    private TableColumn<Project, Integer> idColumn;


    @FXML
    private void initialize() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        logger.debug("initialize");

        endDateColumn.setCellFactory(column -> new TableCell<Project, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText("");

                } else {
                    setText(item);

                    TableRow row = getTableRow();
                    Date now = new Date();
                    try {
                        Date endDate = dateFormat.parse(item);
                        if (DateUtils.addDays(now, DAYS_BEFORE_PROJECT_END_YELLOW).after(endDate) && endDate.after(now)) {
                            row.setStyle("-fx-background-color: #F7FB76;");
                        } else if (endDate.before(now)) {
                            row.setStyle("-fx-background-color: #FD5656;");
                        }
                    } catch (ParseException e) {
                        logger.error("Date parsing error! " + e);
                    }
                }
            }
        });


        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        employerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployer().readableValueForComboBox()));
        startDateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStartDate() != null) {
                return new SimpleStringProperty(dateFormat.format(cellData.getValue().getStartDate()));
            } else {
                return new SimpleStringProperty("");
            }
        });
        endDateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEndDate() != null) {
                return new SimpleStringProperty(dateFormat.format(cellData.getValue().getEndDate()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        if (firstOpened) {
            fillTable();
            firstOpened = false;
        }
    }

    private void fillTable() {
        try {
            ContextHolder.getClient().sendRequest(new CommandRequest("GET_MY_PROJECTS"));
            CommandResponse response = ContextHolder.getLastResponse();
            if (response.getStatus().is2xxSuccessful()) {
                ProjectListDTO projectListDTO = JsonUtil.deserialize(response.getBody(), ProjectListDTO.class);
                projectsTable.setItems(FXCollections.observableArrayList(projectListDTO.getProjectList()));
            } else {
                alert(Alert.AlertType.ERROR, "Cannot fill in table!", response.getBody());
            }
        } catch (ClientException e) {
            logger.error(e);
            alert(Alert.AlertType.ERROR, "Cannot get my projects!", e.getMessage());
        }
    }

    public static boolean isFirstOpened() {
        return firstOpened;
    }

    public static void setFirstOpened(boolean firstOpened) {
        MyProjectsController.firstOpened = firstOpened;
    }
}
