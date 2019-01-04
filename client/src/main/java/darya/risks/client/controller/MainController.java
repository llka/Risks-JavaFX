package darya.risks.client.controller;

import darya.risks.client.Main;
import darya.risks.client.client.Client;
import darya.risks.client.client.ContextHolder;
import darya.risks.client.exception.ClientException;
import darya.risks.client.listener.ServerResponseListener;
import darya.risks.client.util.JsonUtil;
import darya.risks.entity.Contact;
import darya.risks.entity.enums.RoleEnum;
import darya.risks.entity.technical.CommandRequest;
import darya.risks.entity.technical.CommandResponse;
import darya.risks.entity.technical.Session;
import darya.risks.entity.technical.Visitor;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static darya.risks.client.util.AlertUtil.alert;
import static darya.risks.client.util.AlertUtil.alertError;

public class MainController {
    private static final Logger logger = LogManager.getLogger(MainController.class);

    private Main main;

    public void setMain(Main main) {
        this.main = main;
    }

    @FXML
    private MenuItem menuMyProfile;
    @FXML
    private MenuItem menuManageUsersProfiles;
    @FXML
    private MenuItem menuServerConnect;
    @FXML
    private MenuItem menuRegister;
    @FXML
    private MenuItem menuLogOut;
    @FXML
    private MenuItem menuServerDisconnect;
    @FXML
    private MenuItem menuLogIn;

    @FXML
    private MenuItem menuMyProjects;
    @FXML
    private MenuItem menuEditProjects;
    @FXML
    private MenuItem menuAllProjects;
    @FXML
    private MenuItem menuNewProject;


    @FXML
    void connectToServer(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("1996");
        dialog.setTitle("Connect Dialog");
        dialog.setHeaderText("Enter server's port to connect with.");
        dialog.setContentText("Port number:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            int portNumber = 0;
            try {
                portNumber = Integer.parseInt(result.get());
                logger.info("port number: " + portNumber);
            } catch (NumberFormatException e) {
                logger.error("Wrong input format!");
                alert(Alert.AlertType.WARNING, "Wrong input format!", "Use only numbers to define server port!");
            }

            try {
                ContextHolder.setClient(new Client(portNumber));
                ContextHolder.getClient().connect();
                ContextHolder.setServer(new Thread(new ServerResponseListener(ContextHolder.getClient().getSocketInput())));
                ContextHolder.getServer().start();
                Session session = new Session();
                session.setVisitor(new Visitor(RoleEnum.GUEST));
                ContextHolder.setSession(session);

                refreshMenuItemsAccordingToVisitorRole();
                alert("Successfully connected to the server!");
            } catch (ClientException e) {
                alertError("Can not connect to the server!");
                logger.error("Can not connect to the server!" + e);
            }
        } else {
            logger.info("cancelled connection dialog");
        }
    }

    @FXML
    void disconnectFromServer(ActionEvent event) {
        try {
            ContextHolder.getClient().disconnect();
            ContextHolder.setSession(null);

            refreshMenuItemsAccordingToVisitorRole();
            main.showGuestMainView();
            alert("Successfully disconnected from the server!");
        } catch (ClientException e) {
            alertError("Can not disconnect from the server!");
            logger.error("Can not disconnect from the server!" + e);
        }
    }

    @FXML
    void login(ActionEvent event) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Login Dialog");

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField email = new TextField();
        email.setPromptText("Email");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Email:"), 0, 0);
        grid.add(email, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        email.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> email.requestFocus());

        // Convert the result to a email-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(email.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(emailAndPasswordPair -> {
            Map<String, String> params = new HashMap<>();
            params.put("email", emailAndPasswordPair.getKey());
            params.put("password", emailAndPasswordPair.getValue());
            try {
                logger.debug("email=" + emailAndPasswordPair.getKey() + ", Password=" + emailAndPasswordPair.getValue());
                ContextHolder.getClient().sendRequest(new CommandRequest("LOGIN", null, params));

                logger.debug("Request sent");
                CommandResponse response = ContextHolder.getLastResponse();
                logger.debug("Response " + response);
                if (response.getStatus().is2xxSuccessful()) {
                    alert("Successfully logged in!");
                    Contact contact = JsonUtil.deserialize(response.getBody(), Contact.class);
                    ContextHolder.getSession().getVisitor().setContact(contact);
                    ContextHolder.getSession().getVisitor().setRole(contact.getRole());
                    logger.debug("session " + ContextHolder.getSession());
                    refreshMenuItemsAccordingToVisitorRole();
                } else {
                    alert(Alert.AlertType.ERROR, "Cannot login!", response.getBody());
                }
            } catch (ClientException e) {
                alert(Alert.AlertType.ERROR, "Cannot login!", e.getMessage());
            }
        });
    }

    @FXML
    void register(ActionEvent event) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Registration Dialog");

        ButtonType loginButtonType = new ButtonType("Sign up", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstName = new TextField();
        firstName.setPromptText("First Name");
        TextField lastName = new TextField();
        lastName.setPromptText("Last Name");
        TextField email = new TextField();
        email.setPromptText("Email");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstName, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastName, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(email, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(password, 1, 3);

        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        final String PAIR_DELIMETER = "__";
        email.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> firstName.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(firstName.getText().trim() + PAIR_DELIMETER + lastName.getText().trim(),
                        email.getText().trim() + PAIR_DELIMETER + password.getText().trim());
            }
            return null;
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            Map<String, String> params = new HashMap<>();
            Contact contact = new Contact();
            contact.setFirstName(pair.getKey().split(PAIR_DELIMETER)[0]);
            contact.setLastName(pair.getKey().split(PAIR_DELIMETER)[1]);
            contact.setEmail(pair.getValue().split(PAIR_DELIMETER)[0]);
            contact.setPassword(pair.getValue().split(PAIR_DELIMETER)[1]);

            try {
                logger.debug("new contact" + contact);
                ContextHolder.getClient().sendRequest(new CommandRequest("REGISTER", JsonUtil.serialize(contact), params));

                logger.debug("Request sent");
                CommandResponse response = ContextHolder.getLastResponse();
                logger.debug("Response " + response);
                if (response.getStatus().is2xxSuccessful()) {
                    alert("Successfully signed up!");
                    Contact savedContact = JsonUtil.deserialize(response.getBody(), Contact.class);
                    ContextHolder.getSession().getVisitor().setContact(savedContact);
                    ContextHolder.getSession().getVisitor().setRole(savedContact.getRole());
                    logger.debug("session " + ContextHolder.getSession());
                    refreshMenuItemsAccordingToVisitorRole();
                } else {
                    alert(Alert.AlertType.ERROR, "Cannot sign up!", response.getBody());
                }
            } catch (ClientException e) {
                alert(Alert.AlertType.ERROR, "Cannot sign up!", e.getMessage());
            }
        });
    }

    @FXML
    void logOut(ActionEvent event) {
        try {
            ContextHolder.getClient().sendRequest(new CommandRequest("LOGOUT"));
            logger.debug("Request sent");

            CommandResponse response = ContextHolder.getLastResponse();
            logger.debug("Response " + response);
            if (response.getStatus().is2xxSuccessful()) {
                ContextHolder.getSession().getVisitor().setContact(null);
                ContextHolder.getSession().getVisitor().setRole(RoleEnum.GUEST);
                refreshMenuItemsAccordingToVisitorRole();
                logger.debug("session " + ContextHolder.getSession());
                main.showGuestMainView();
                alert("Successfully logged out!");
            } else {
                alert(Alert.AlertType.ERROR, "Cannot logout!", response.getBody());
            }

        } catch (ClientException e) {
            alert(Alert.AlertType.ERROR, "Cannot logout!", e.getMessage());
        }
    }

    @FXML
    void openMyProfileView(ActionEvent event) {
        if (isAuthenticatedUser()) {
            MyProfileController.setFirst(true);
            main.showView("/view/myProfileView.fxml");
        } else {
            alert(Alert.AlertType.ERROR, "You are not authorized!", "You are not authorized!");
        }
    }

    @FXML
    void openManageUsersView(ActionEvent event) {
        if (isAuthenticatedAdmin()) {
            ManageProfilesController.setFirstOpened(true);
            main.showView("/view/manageProfilesView.fxml");
        } else {
            alert(Alert.AlertType.ERROR, "You are not authorized!", "Only Admin can manage contacts!");
        }
    }


    @FXML
    void openMyProjectsView(ActionEvent event) {
        if (isAuthenticatedUser()) {
            MyProjectsController.setFirstOpened(true);
            main.showView("/view/myProjectsView.fxml");
        } else {
            alert(Alert.AlertType.ERROR, "You are not authorized!", "You are not authorized!");
        }
    }

    @FXML
    void openNewProjectView(ActionEvent event) {
        if (isAuthenticatedUser()) {
            NewProjectController.setFirstOpened(true);
            NewProjectController.setMain(main);
            main.showView("/view/newProjectView.fxml");
        } else {
            alert(Alert.AlertType.ERROR, "You are not authorized!", "You are not authorized!");
        }
    }

    @FXML
    void openAllProjectsView(ActionEvent event) {
        if (isAuthenticatedAdmin()) {
            MyProfileController.setFirst(true);
            main.showView("/view/allProjectsView.fxml");
        } else {
            alert(Alert.AlertType.ERROR, "You are not authorized!", "Only Admin can see all projects!!");
        }
    }

    @FXML
    void openEditProjectsView(ActionEvent event) {
        if (isAuthenticatedAdmin()) {
            MyProfileController.setFirst(true);
            main.showView("/view/editProjectsView.fxml");
        } else {
            alert(Alert.AlertType.ERROR, "You are not authorized!", "Only Admin can edit projects!");
        }
    }

    private void refreshMenuItemsAccordingToVisitorRole() {
        Session session = ContextHolder.getSession();
        if (session == null) {
            menuServerConnect.setDisable(false);
            menuServerDisconnect.setDisable(true);

            menuLogIn.setDisable(true);
            menuLogOut.setDisable(true);
            menuRegister.setDisable(true);

            menuMyProfile.setDisable(true);
            menuMyProjects.setDisable(true);
            menuNewProject.setDisable(true);

            menuAllProjects.setDisable(true);
            menuEditProjects.setDisable(true);
            menuManageUsersProfiles.setDisable(true);
        } else {
            if (session.getVisitor() != null) {
                menuServerConnect.setDisable(true);
                menuServerDisconnect.setDisable(false);
                switch (session.getVisitor().getRole()) {
                    case GUEST:
                        menuLogIn.setDisable(false);
                        menuLogOut.setDisable(true);
                        menuRegister.setDisable(false);

                        menuMyProfile.setDisable(true);
                        menuMyProjects.setDisable(true);
                        menuNewProject.setDisable(true);

                        menuAllProjects.setDisable(true);
                        menuEditProjects.setDisable(true);
                        menuManageUsersProfiles.setDisable(true);

                        break;
                    case USER:
                        menuLogIn.setDisable(true);
                        menuLogOut.setDisable(false);
                        menuRegister.setDisable(true);

                        menuMyProfile.setDisable(false);
                        menuMyProjects.setDisable(false);
                        menuNewProject.setDisable(false);

                        menuAllProjects.setDisable(true);
                        menuEditProjects.setDisable(true);
                        menuManageUsersProfiles.setDisable(true);

                        break;
                    case ADMIN:
                        menuLogIn.setDisable(true);
                        menuLogOut.setDisable(false);
                        menuRegister.setDisable(true);

                        menuMyProfile.setDisable(false);
                        menuMyProjects.setDisable(false);
                        menuNewProject.setDisable(false);

                        menuAllProjects.setDisable(false);
                        menuEditProjects.setDisable(false);
                        menuManageUsersProfiles.setDisable(false);
                        break;
                    default:
                        logger.error("unknown role!");
                }
            }
        }
    }

    private boolean isAuthenticatedUser() {
        if (ContextHolder.getSession() != null &&
                ContextHolder.getSession().getVisitor() != null) {
            return RoleEnum.USER.equals(ContextHolder.getSession().getVisitor().getRole()) ||
                    RoleEnum.ADMIN.equals(ContextHolder.getSession().getVisitor().getRole());
        } else {
            return false;
        }
    }

    private boolean isAuthenticatedAdmin() {
        if (ContextHolder.getSession() != null &&
                ContextHolder.getSession().getVisitor() != null) {
            return RoleEnum.ADMIN.equals(ContextHolder.getSession().getVisitor().getRole());
        } else {
            return false;
        }
    }
}
