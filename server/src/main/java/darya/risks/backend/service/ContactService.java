package darya.risks.backend.service;


import darya.risks.backend.dao.ContactDAO;
import darya.risks.backend.dao.ProjectDAO;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.entity.Contact;
import darya.risks.entity.Project;
import darya.risks.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ContactService {
    private static final Logger logger = LogManager.getLogger(ContactService.class);

    private ContactDAO contactDAO;
    private ProjectDAO projectDAO;

    public ContactService() {
        this.contactDAO = new ContactDAO();
        this.projectDAO = new ProjectDAO();
    }

    public Contact login(String email, String password) throws ApplicationException {
        if (contactDAO.login(email, password)) {
            return fetchContactsTickets(contactDAO.getByEmail(email));
        } else {
            throw new ApplicationException("Wrong email or password!", ResponseStatus.BAD_REQUEST);
        }
    }

    public Contact register(Contact contact) throws ApplicationException {
        contactDAO.save(contact);
        return fetchContactsTickets(contactDAO.getByEmail(contact.getEmail()));
    }

    public Contact getById(int contactId) throws ApplicationException {
        return fetchContactsTickets(contactDAO.getById(contactId));
    }

    public Contact getByEmail(String email) throws ApplicationException {
        return fetchContactsTickets((contactDAO.getByEmail(email)));
    }

    public List<Contact> getAll() throws ApplicationException {
        List<Contact> contacts = contactDAO.getAll();
        for (Contact contact : contacts) {
            fetchContactsTickets(contact);
        }
        return contacts;
    }

    public Contact update(Contact contact) throws ApplicationException {
        try {
            contactDAO.getByEmail(contact.getEmail());
        } catch (ApplicationException e) {
            throw new ApplicationException("You can not change email!", ResponseStatus.BAD_REQUEST);
        }

        contactDAO.update(contact);
        return fetchContactsTickets(contactDAO.getByEmail(contact.getEmail()));
    }

    public void delete(Contact contact) throws ApplicationException {
        contactDAO.deleteById(contact.getId());
    }

    public void delete(String contactIdString, String email) throws ApplicationException {
        int id = -1;
        if (contactIdString != null && !contactIdString.isEmpty()) {
            id = Integer.parseInt(contactIdString);
            contactDAO.getById(id);
            contactDAO.deleteById(id);
        } else if (email != null && !email.isEmpty()) {
            Contact contact = contactDAO.getByEmail(email);
            contactDAO.deleteById(contact.getId());
        } else {
            throw new ApplicationException("No contact id or email!", ResponseStatus.BAD_REQUEST);
        }
    }

    private Contact fetchContactsProjects(Contact contact) throws ApplicationException {
        if (contact != null) {
            List<Project> projects = ticketDAO.getTicketsForContact(contact.getId());
            if (tickets == null) {
                tickets = new ArrayList<>();
            }
            contact.setTickets(tickets);
            return contact;
        } else {
            throw new ApplicationException("Contact is null!", ResponseStatus.BAD_REQUEST);
        }
    }
}
