package fr.unice.polytech.si5.soa.a.services;


import fr.unice.polytech.si5.soa.a.communication.DeliveryDTO;
import fr.unice.polytech.si5.soa.a.configuration.TestConfiguration;
import fr.unice.polytech.si5.soa.a.dao.IDeliveryDao;
import fr.unice.polytech.si5.soa.a.entities.Delivery;
import fr.unice.polytech.si5.soa.a.exceptions.UnknowDeliveryException;
import fr.unice.polytech.si5.soa.a.services.component.DeliveryServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class DeliveryServiceTest {
    @Autowired
    @Qualifier("mock")
    @Mock
    private IDeliveryDao iDeliveryDaoMock;

    @Autowired
    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    private Delivery deliveryTodo;
    private Delivery deliveryDone;

    private static final String ADDRESS = "Evariste Galois";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.reset(iDeliveryDaoMock);

        deliveryTodo = new Delivery();
        deliveryTodo.setDeliveryAddress(ADDRESS);
        deliveryDone = new Delivery();
        deliveryDone.setState(true);
        deliveryDone.setDeliveryAddress(ADDRESS);
    }

    @AfterEach
    public void cleanUp() {
        deliveryTodo = null;
        deliveryDone = null;
    }

    @Test
    public void getDeliveriesToDo() {
        List<Delivery> deliveries = Collections.singletonList(deliveryTodo);
        when(iDeliveryDaoMock.getDeliveriesToDo()).thenReturn(deliveries);
        List<DeliveryDTO> deliveriesReturned = deliveryService.getDeliveriesToDo();
        assertTrue(deliveriesReturned.size() == 1);
        assertEquals(deliveriesReturned.get(0), deliveryTodo.toDTO());
    }

    @Test
    public void addDelivery() {
        when(iDeliveryDaoMock.addDelivery(deliveryTodo)).thenReturn(deliveryTodo);
        DeliveryDTO returnedDelivery = deliveryService.addDelivery(deliveryTodo.toDTO());
        assertEquals(returnedDelivery, deliveryTodo.toDTO());
    }

    @Test
    public void updateDelivery() throws UnknowDeliveryException {
        when(iDeliveryDaoMock.updateDelivery(deliveryTodo)).thenReturn(deliveryDone);
        when(iDeliveryDaoMock.findDeliveryById(deliveryTodo.getId())).thenReturn(Optional.of(deliveryTodo));
        DeliveryDTO returnedDelivery = deliveryService.updateDelivery(deliveryTodo.toDTO());
        assertNotEquals(returnedDelivery,deliveryTodo.toDTO());
        assertEquals(returnedDelivery, deliveryDone.toDTO());
    }


}