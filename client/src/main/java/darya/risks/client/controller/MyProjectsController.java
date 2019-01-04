package darya.risks.client.controller;

import darya.risks.client.client.ContextHolder;
import darya.risks.client.exception.ClientException;
import darya.risks.client.util.JsonUtil;
import darya.risks.dto.ProjectListDTO;
import darya.risks.entity.Project;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;

import static darya.risks.client.util.AlertUtil.alert;

public class MyProjectsController {
    private static final Logger logger = LogManager.getLogger(NewProjectController.class);

    private static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

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

        projectsTable.setRowFactory(val -> {
            TableRow<Project> row = new TableRow<>();
            logger.debug("binding " + row.idProperty().toString());
//            BooleanBinding critical = row.itemProperty().
//            row.styleProperty().bind(Bindings.when(critical)
//                    .then("-fx-background-color: red ;")
//                    .otherwise(""));
            return row ;
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
            logger.debug("Response " + response);
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
