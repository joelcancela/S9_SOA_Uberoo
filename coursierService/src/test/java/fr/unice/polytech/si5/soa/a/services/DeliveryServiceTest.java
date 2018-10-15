package fr.unice.polytech.si5.soa.a.services;

import fr.unice.polytech.si5.soa.a.configuration.TestConfiguration;
import fr.unice.polytech.si5.soa.a.dao.IDeliveryDao;
import fr.unice.polytech.si5.soa.a.entities.Delivery;
import fr.unice.polytech.si5.soa.a.services.component.DeliveryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class })
public class DeliveryServiceTest {
    @Autowired
    @Qualifier("mock")
    @Mock
    private IDeliveryDao iDeliveryDaoMock;

    @Autowired
    @Qualifier("mock")
    @Mock
    private RestTemplate restTemplate;

    @Autowired
    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    private Delivery delivery;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        Mockito.reset(iDeliveryDaoMock);
        Mockito.reset(restTemplate);
        delivery = new Delivery();
    }
}
