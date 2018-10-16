package fr.unice.polytech.si5.soa.a.dao.component;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.unice.polytech.si5.soa.a.dao.IOrderDao;
import fr.unice.polytech.si5.soa.a.entities.OrderState;
import fr.unice.polytech.si5.soa.a.entities.RestaurantOrder;

/**
 * Class name	OrderDaoImpl
 * @see			IOrderDao
 * Date			08/10/2018
 * @author		PierreRainero
 */
@Primary
@Repository
@Transactional
public class OrderDaoImpl implements IOrderDao {
	private static Logger logger = LogManager.getLogger(OrderDaoImpl.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public RestaurantOrder addOrder(RestaurantOrder orderToAdd) {
		Session session = sessionFactory.getCurrentSession();

		try {
			session.save(orderToAdd);
		} catch (SQLGrammarException e) {
			session.getTransaction().rollback();
			logger.error("Cannot execute query : addOrder", e);
		}

		return orderToAdd;
	}

	@Override
	public RestaurantOrder updateOrder(RestaurantOrder orderToUpdate) {
		Session session = sessionFactory.getCurrentSession();

		RestaurantOrder result = null;
		try {
            result = (RestaurantOrder) session.merge(orderToUpdate);
		} catch (SQLGrammarException e) {
			session.getTransaction().rollback();
			logger.error("Cannot execute query : updateOrder", e);
		}

		return result;
	}

	@Override
	public Optional<RestaurantOrder> findOrderById(int id) {
		Session session = sessionFactory.getCurrentSession();

		Optional<RestaurantOrder> result = Optional.empty();
		try {
			RestaurantOrder order = (RestaurantOrder) session.get(RestaurantOrder.class, id);

			if(order!=null){
				result = Optional.of(order);
			}
		} catch (SQLGrammarException e) {
			logger.error("Cannot execute query : findOrderById", e);
		}

		return result;
	}

	@Override
	public List<RestaurantOrder> getOrdersToDo() {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<RestaurantOrder> criteria = builder.createQuery(RestaurantOrder.class);
		Root<RestaurantOrder> root =  criteria.from(RestaurantOrder.class);
		criteria.select(root).where(builder.equal(root.get("state"), OrderState.TO_PREPARE));
		Query<RestaurantOrder> query = session.createQuery(criteria);
		
		return query.getResultList();
	}

}