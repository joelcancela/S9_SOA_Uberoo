package fr.unice.polytech.si5.soa.a.controllers;

import fr.unice.polytech.si5.soa.a.communication.DeliveryDTO;
import fr.unice.polytech.si5.soa.a.communication.NewOrder;
import fr.unice.polytech.si5.soa.a.configuration.TestConfiguration;
import fr.unice.polytech.si5.soa.a.configuration.WebApplicationConfiguration;
import fr.unice.polytech.si5.soa.a.entities.Delivery;
import fr.unice.polytech.si5.soa.a.exceptions.UnknowDeliveryException;
import fr.unice.polytech.si5.soa.a.services.IDeliveryService;
import fr.unice.polytech.si5.soa.a.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        TestConfiguration.class,
        WebApplicationConfiguration.class
})
@WebAppConfiguration
public class DeliveryControllerTest {
    private final static String BASE_URI = "/deliveries/";
    private MockMvc mockMvc;

    @Qualifier("mock")
    @Autowired
    @Mock
    private IDeliveryService deliveryServiceMock;

    @Autowired
    @InjectMocks
    private DeliveryController deliveryController;

    private Delivery delivery;
    private Delivery deliveryDone;
    private Delivery deliveryBelow10;
    private Delivery deliveryOver10;
    private NewOrder order;

    private static final String ADDRESS = "475 rue Evariste Galois";
    private static final String ERROR_MESSAGE = "Can't find delivery";


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.reset(deliveryServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(deliveryController).build();

        delivery = new Delivery();
        delivery.setDeliveryAddress(ADDRESS);
        delivery.setId(8);
        delivery.setState(false);

        deliveryDone = new Delivery();
        deliveryDone.setDeliveryAddress(ADDRESS);
        deliveryDone.setState(true);

        deliveryBelow10 = new Delivery();
        deliveryBelow10.setDeliveryAddress(ADDRESS);
        deliveryBelow10.setState(false);
        deliveryBelow10.setLatitude(0.0);
        deliveryBelow10.setLongitude(0.0);

        deliveryOver10 = new Delivery();
        deliveryOver10.setDeliveryAddress(ADDRESS);
        deliveryOver10.setState(false);
        deliveryOver10.setLatitude(1.0);
        deliveryOver10.setLongitude(1.0);

        order = new NewOrder();
        order.setId(8);
        order.setAddress(ADDRESS);
        order.setFood(Arrays.asList("Sushi", "Maki"));
        order.setType("Sushi");
    }

    @Test
    public void getDeliveriesToDoTest() throws Exception {
        List<DeliveryDTO> expectedMock = new ArrayList<>();
        expectedMock.add(delivery.toDTO());
        when(deliveryServiceMock.getDeliveriesToDo()).thenReturn(expectedMock);

        mockMvc.perform(get(BASE_URI)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(expectedMock)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));
    }

    @Test
    public void addDeliveryTest() throws Exception {
        when(deliveryServiceMock.addDelivery(any(DeliveryDTO.class))).thenReturn(delivery.toDTO());
        mockMvc.perform(post(BASE_URI)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(order)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));

        ArgumentCaptor<DeliveryDTO> captor = ArgumentCaptor.forClass(DeliveryDTO.class);
        verify(deliveryServiceMock, times(1)).addDelivery(captor.capture());
        verifyNoMoreInteractions(deliveryServiceMock);

        DeliveryDTO captorValue = captor.getValue();
        assertNotNull(captorValue);
        assertEquals(captorValue, delivery.toDTO());
    }

    @Test
    public void updateDeliveryStateCorrectly() throws Exception {
        when(deliveryServiceMock.updateDelivery(any(DeliveryDTO.class))).thenReturn(deliveryDone.toDTO());

        mockMvc.perform(put(BASE_URI + "/" + delivery.getId() + "/")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(deliveryDone.toDTO())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));

        ArgumentCaptor<DeliveryDTO> captor = ArgumentCaptor.forClass(DeliveryDTO.class);
        verify(deliveryServiceMock, times(1)).updateDelivery(captor.capture());
        verifyNoMoreInteractions(deliveryServiceMock);

        DeliveryDTO deliveryDTO = captor.getValue();
        assertNotNull(deliveryDTO);
        assertEquals(deliveryDTO, deliveryDone.toDTO());
    }

    @Test
    public void tryUpdateDeliveryStateCantFindDelivery() throws Exception {
        when(deliveryServiceMock.updateDelivery(any(DeliveryDTO.class))).thenThrow(new UnknowDeliveryException(ERROR_MESSAGE));

        mockMvc.perform(put(BASE_URI + "/2/")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(new Delivery().toDTO())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ERROR_MESSAGE));

        ArgumentCaptor<DeliveryDTO> captor = ArgumentCaptor.forClass(DeliveryDTO.class);
        verify(deliveryServiceMock, times(1)).updateDelivery(captor.capture());
        verifyNoMoreInteractions(deliveryServiceMock);
    }

    @Test
    public void getDeliveriesToDoWithPositionTest() throws Exception {
        List<DeliveryDTO> expectedMock = new ArrayList<>();
        expectedMock.add(deliveryBelow10.toDTO());
        when(deliveryServiceMock.getDeliveriesToDo()).thenReturn(expectedMock);

        mockMvc.perform(get(BASE_URI + "?latitude=0.05&longitude=0.5")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(expectedMock)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));
    }

}
