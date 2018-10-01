package fr.unice.polytech.si5.soa.a.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.SQLGrammarException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import fr.unice.polytech.si5.soa.a.configuration.TestConfiguration;
import fr.unice.polytech.si5.soa.a.entities.Order;
import fr.unice.polytech.si5.soa.a.entities.Meal;
import fr.unice.polytech.si5.soa.a.entities.User;

/**
 * Class name	OrderTakerDaoTest
 * Date			29/09/2018
 * @author		PierreRainero
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class })
public class OrderTakerDaoTest {
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private IOrderTakerDao orderDao;
	
	private Meal ramen;
	private Order bobCommand;
	private User bob;
	
	@BeforeEach
	public void setUp() throws Exception {
		ramen = new Meal();
		ramen.setName("Ramen soup");
		
		bob = new User();
		bob.setFirstName("Bob");
		bob.setLastName("Harington");
		
		bobCommand = new Order();
		bobCommand.addMeal(ramen);
		bobCommand.setDeliveryAddress("930 Route des Colles, 06410 Biot");
		bobCommand.setTransmitter(bob);
		
		Session session = sessionFactory.openSession();
		try {
			session.save(ramen);
			session.save(bob);
			session.beginTransaction().commit();
		} catch (SQLGrammarException e) {
			session.getTransaction().rollback();
		} finally {
			session.close();
		}
	}
	
	@AfterEach
	public void cleanUp() throws Exception {
		Session session = sessionFactory.openSession();
	    Transaction transaction = null;
	    try {
	    	transaction = session.beginTransaction();
	    	
	    	if(bobCommand.getId() != 0) {
	    		session.delete(bobCommand);
	    	}
	    	
			session.delete(ramen);
			session.delete(bob);
			
			session.flush();
			transaction.commit();
			
			bobCommand = null;
			bob = null;
			ramen = null;
		} catch (SQLGrammarException e) {
			session.getTransaction().rollback();
		} finally {
			session.close();
		}
	}
	
	@Test
	public void addANewOrder() {
		Order order = orderDao.addOrder(bobCommand);
		
		assertNotNull(order);
		assertNotEquals(0, order.getId());
		assertEquals(1, order.getMeals().size());
	}
}