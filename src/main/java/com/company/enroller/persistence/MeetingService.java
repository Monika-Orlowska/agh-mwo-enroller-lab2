package com.company.enroller.persistence;

import com.company.enroller.model.Meeting;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;
	Session session;

	public MeetingService() {
		session = DatabaseConnector.getInstance().getSession();
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = this.session.createQuery(hql);
		return query.list();
	}

	public Meeting findById(long id) {
		return connector.getSession().get(Meeting.class, id);
	}

	public Meeting add(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(meeting);
		transaction.commit();
		return meeting;
	}

	public void update(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().merge(meeting);
		transaction.commit();
	}

	public void delete(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(meeting);
		transaction.commit();
	}

//	public List findByTitle(String title, String key) {
//		String hql = "FROM Meeting WHERE lower(title) LIKE :key ";
//		Query query = connector.getSession().createQuery(hql);
//		query.setParameter("key", "%" + key.toLowerCase() + "%");
//		return query.list();
//	}

	public Meeting findByTitle(String title)
	{return connector.getSession().get(Meeting.class, title);
	}
}
