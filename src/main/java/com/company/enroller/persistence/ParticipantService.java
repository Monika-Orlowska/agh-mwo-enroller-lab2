package com.company.enroller.persistence;

import com.company.enroller.model.Participant;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("participantService")
public class ParticipantService {

    private DatabaseConnector connector;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ParticipantService() {
        connector = DatabaseConnector.getInstance();
    }

    public Collection<Participant> getAll() {
        String hql = "FROM Participant";
        Query query = connector.getSession().createQuery(hql);
        return query.list();
    }

    public Participant findByLogin(String login) {
        return connector.getSession().get(Participant.class, login);
    }

    public Participant add(Participant participant) {
        String hashedPassword = passwordEncoder.encode(participant.getPassword());
        participant.setPassword(hashedPassword);

        Transaction transaction = connector.getSession().beginTransaction();
        connector.getSession().save(participant);
        transaction.commit();
        return participant;
    }

    public void update(Participant participant) {
        Transaction transaction = connector.getSession().beginTransaction();
        connector.getSession().merge(participant);
        transaction.commit();
    }

    public void delete(Participant participant) {
        Transaction transaction = connector.getSession().beginTransaction();
        connector.getSession().delete(participant);
        transaction.commit();
    }

    public Collection<Participant> getAllSorted(String sortBy, String sortOrder) {
        String hql = "FROM Participant ORDER BY " + sortBy + " " + sortOrder;
        Query query = connector.getSession().createQuery(hql);
        return query.list();
    }

    public Collection<Participant> getFilteredAndSorted(String key, String sortBy, String sortOrder) {
        String hql = "FROM Participant WHERE lower(login) LIKE :key ORDER BY " + sortBy + " " + sortOrder;
        Query query = connector.getSession().createQuery(hql);
        query.setParameter("key", "%" + key.toLowerCase() + "%");
        return query.list();
    }
}
