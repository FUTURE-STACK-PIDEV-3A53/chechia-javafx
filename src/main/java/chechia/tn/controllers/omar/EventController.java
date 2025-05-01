package chechia.tn.controllers.omar;
import java.util.List;

import chechia.tn.entities.Event;
import chechia.tn.service.omar.EventDAO;

public class EventController {
    private final EventDAO dao;

    public EventController() {
        dao = new EventDAO();
    }

    public void createEvent(Event e) {
        dao.ajouterEvent(e);
    }

    public List<Event> getAllEvents() {
        return dao.afficherEvents();
    }

    public void updateEvent(Event e) {
        dao.modifierEvent(e);
    }

    public void deleteEvent(int id) {
        dao.supprimerEvent(id);
    }
}