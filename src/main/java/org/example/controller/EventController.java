package org.example.controller;
import java.util.List;

import org.example.model.Event;
import org.example.model.EventDAO;

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
