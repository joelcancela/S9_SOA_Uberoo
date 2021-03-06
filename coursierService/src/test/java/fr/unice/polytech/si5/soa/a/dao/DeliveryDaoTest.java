package fr.unice.polytech.si5.soa.a.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;

import fr.unice.polytech.si5.soa.a.entities.Coursier;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.SQLGrammarException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.unice.polytech.si5.soa.a.configuration.TestConfiguration;
import fr.unice.polytech.si5.soa.a.entities.Delivery;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class DeliveryDaoTest {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private IDeliveryDao deliveryDao;

    private Delivery deliveryToDo;
    private Delivery deliveryDone;

    private Coursier coursier;
    private Coursier secondCoursier;

    private List<Delivery> deliveries;

    @BeforeEach
    public void setUp() {

        this.coursier = new Coursier();
        coursier.setName("jean");
        coursier.setAccountNumber("FR XXX XXXX");
        coursier.setId(9);

        this.secondCoursier = new Coursier();
        secondCoursier.setName("Paul");
        secondCoursier.setAccountNumber("DE XXX XXXX");
        secondCoursier.setId(18);

        deliveries = new ArrayList<>();
        deliveryToDo = new Delivery();
        deliveryToDo.setDeliveryAddress("140 sentier des hautes breguières");
        deliveryToDo.setCoursier(coursier);

        deliveryDone = new Delivery();
        deliveryDone.setDeliveryAddress("5 rue de l'hôpital");
        deliveryDone.state = true;
        deliveryDone.setCoursier(secondCoursier);

        deliveries.add(deliveryToDo);
        deliveries.add(deliveryDone);

        Session session = sessionFactory.openSession();
        try {
            session.save(coursier);
            session.save(secondCoursier);
            session.save(deliveryToDo);
            session.save(deliveryDone);
            session.beginTransaction().commit();
        } catch (SQLGrammarException e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    @AfterEach
    public void cleanUp() {
        Session session = sessionFactory.openSession();
        Transaction transaction;
        try {
            transaction = session.beginTransaction();
            session.delete(coursier);
            session.delete(secondCoursier);
            deliveries.forEach(session::delete);
            session.flush();
            transaction.commit();
            coursier = null;
            secondCoursier = null;
            deliveryToDo = null;
            deliveryDone = null;
        } catch (SQLGrammarException e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    @Test
    public void getDeliveriesToDo() {
        List<Delivery> deliveryList = deliveryDao.getDeliveriesToDo();
        assertTrue(deliveryList.size() == 1);
        assertEquals(deliveryList.get(0), deliveryToDo);
    }

    @Test
    public void updateDelivery() {
        deliveryToDo.state = true;
        deliveryDao.updateDelivery(deliveryToDo);
        assertTrue(deliveryDao.getDeliveriesToDo().stream().allMatch(delivery -> delivery.state));
    }

    @Test
    public void addDelivery() {
        assertTrue(deliveryDao.getDeliveriesToDo().size() == 1);
        Delivery newDelivery = new Delivery();
        newDelivery.setDeliveryAddress("22 rue des tests unitaires");

        deliveryDao.addDelivery(newDelivery);
        List<Delivery> deliveriesToDo = deliveryDao.getDeliveriesToDo();
        assertTrue(deliveriesToDo.size() == 2);
        assertEquals(deliveriesToDo.get(1), newDelivery);
        deliveries.add(newDelivery);
    }

    @Test
    public void getDeliveriesDoneBy() {
        assertEquals(this.deliveryDao.getDeliveriesDoneBy(coursier.getId()), Collections.emptyList());
        List<Delivery> deliveries = this.deliveryDao.getDeliveriesDoneBy(secondCoursier.getId());
        assertTrue(deliveries.size() == 1);
        assertEquals(deliveries.get(0), deliveryDone);
    }

    @Test
    public void findDeliveryById() throws Exception {
        Delivery newDelivery = new Delivery();
        newDelivery.setLongitude(0.8);
        Session session = sessionFactory.openSession();
        try {
            session.save(newDelivery);
            session.beginTransaction().commit();
        } catch (SQLGrammarException e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        Optional<Delivery> deliveryWrapped = this.deliveryDao.findDeliveryById(newDelivery.getId());
        if (deliveryWrapped.isPresent()) {
            Delivery deliveryFound = deliveryWrapped.get();
            assertEquals(deliveryFound, newDelivery);
            deliveries.add(newDelivery);
        } else {
            throw new Exception("Not found");
        }
    }
}
