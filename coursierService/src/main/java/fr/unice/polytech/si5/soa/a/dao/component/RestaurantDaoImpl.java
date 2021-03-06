package fr.unice.polytech.si5.soa.a.dao.component;

import fr.unice.polytech.si5.soa.a.dao.IRestaurantDao;
import fr.unice.polytech.si5.soa.a.entities.Delivery;
import fr.unice.polytech.si5.soa.a.entities.Restaurant;
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

import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Primary
@Repository
@Transactional
public class RestaurantDaoImpl implements IRestaurantDao {
    private static Logger logger = LogManager.getLogger(DeliveryDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Optional<Restaurant> findRestaurantById(Integer idRestaurant) {
        Session session = sessionFactory.getCurrentSession();
        Optional<Restaurant> result = Optional.empty();
        try {
            Restaurant restaurant = session.get(Restaurant.class, idRestaurant);
            if (restaurant != null) {
                result = Optional.of(restaurant);
            }
        } catch (SQLGrammarException e) {
            logger.error("Cannot execute query : findDeliveryById", e);
        }

        return result;
    }

	@Override
	public Restaurant addRestaurant(Restaurant restaurantToAdd) {
		Session session = sessionFactory.getCurrentSession();

        try {
            session.save(restaurantToAdd);
        } catch (SQLGrammarException e) {
            session.getTransaction().rollback();
            logger.error("Cannot execute query : addRestaurant", e);
        }

        return restaurantToAdd;
	}

	@Override
	public Optional<Restaurant> findRestaurant(String name, String address) {
		Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Restaurant> criteria = builder.createQuery(Restaurant.class);
        Root<Restaurant> root =  criteria.from(Restaurant.class);
        criteria.select(root).where(
        		builder.and(
        				builder.equal(root.get("name"), name), 
        				builder.equal(root.get("address"), address)
        				));
        Query<Restaurant> query = session.createQuery(criteria);

        try {
            return Optional.of(query.getSingleResult());
        }catch(Exception e) {
            return Optional.empty();
        }
	}
}
