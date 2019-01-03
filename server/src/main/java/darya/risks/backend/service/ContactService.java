package darya.risks.backend.service;


import darya.risks.backend.dao.ContactDAO;
import darya.risks.backend.exceprion.ApplicationException;
import darya.risks.entity.Contact;
import darya.risks.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class ContactService {
    private static final Logger logger = LogManager.getLogger(ContactService.class);

    private ContactDAO contactDAO;

    public ContactService() {
        this.contactDAO = new ContactDAO();
    }

    public Contact login(String email, String password) throws ApplicationException {
        if (contactDAO.login(email, password)) {
            return contactDAO.getByEmail(email);
        } else {
            throw new ApplicationException("Wrong email or password!", ResponseStatus.BAD_REQUEST);
        }
    }

    public Contact register(Contact contact) throws ApplicationException {
        contactDAO.save(contact);
        return contactDAO.getByEmail(contact.getEmail());
    }

    public Contact getById(int contactId) throws ApplicationException {
        return contactDAO.getById(contactId);
    }

    public Contact getByEmail(String email) throws ApplicationException {
        return contactDAO.getByEmail(email);
    }

    public List<Contact> getAll() throws ApplicationException {
        return contactDAO.getAll();
    }

    public Contact update(Contact contact) throws ApplicationException {
        try {
            contactDAO.getByEmail(contact.getEmail());
        } catch (ApplicationException e) {
            throw new ApplicationException("You can not change email!", ResponseStatus.BAD_REQUEST);
        }

        contactDAO.update(contact);
        return contactDAO.getByEmail(contact.getEmail());
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
}
