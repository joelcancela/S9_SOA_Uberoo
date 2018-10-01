package fr.unice.polytech.si5.soa.a.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import fr.unice.polytech.si5.soa.a.communication.OrderDTO;
import fr.unice.polytech.si5.soa.a.configuration.TestConfiguration;
import fr.unice.polytech.si5.soa.a.configuration.WebApplicationConfiguration;
import fr.unice.polytech.si5.soa.a.entities.Order;
import fr.unice.polytech.si5.soa.a.entities.Meal;
import fr.unice.polytech.si5.soa.a.entities.User;
import fr.unice.polytech.si5.soa.a.exceptions.EmptyDeliveryAddressException;
import fr.unice.polytech.si5.soa.a.exceptions.UnknowMealException;
import fr.unice.polytech.si5.soa.a.exceptions.UnknowUserException;
import fr.unice.polytech.si5.soa.a.services.IOrderTakerService;
import fr.unice.polytech.si5.soa.a.util.TestUtil;

/**
 * Class name	OrderTakerControllerTest
 * Date			30/09/2018
 * @author		PierreRainero
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class, WebApplicationConfiguration.class})
@WebAppConfiguration
public class OrderTakerControllerTest {
	private final static String BASE_URI = "/orders/";
	private final static String ERROR_UNKNOW_USER = "Can't find user with id = -1";
	private final static String ERROR_EMPTY_ADDRESS = "Delivery address cannot be empty";
	private final static String ERROR_UNKNOW_MEAL = "Can't find meal nammed \"superfétatoire\"";
	private MockMvc mockMvc;
	
	@Qualifier("mock")
	@Autowired
	@Mock
    private IOrderTakerService orderServiceMock;
    
    @Autowired
    @InjectMocks
    private OrderTakerController orderTakerController;
    
	private OrderDTO bobCommand;
    
    @BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		Mockito.reset(orderServiceMock);
		mockMvc = MockMvcBuilders.standaloneSetup(orderTakerController).build();
		
		Meal ramen = new Meal();
		ramen.setName("Ramen soup");
		
		User bob = new User();
		bob.setFirstName("Bob");
		bob.setLastName("Harington");
		
		Order command  = new Order();
		command.addMeal(ramen);
		command.setDeliveryAddress("930 Route des Colles, 06410 Biot");
		command.setTransmitter(bob);
		
		bobCommand = command.toDTO();
	}
    
    @Test
    public void addCommandUsingHTTPPost() throws Exception {
		when(orderServiceMock.addOrder(any(OrderDTO.class))).thenReturn(bobCommand);
		
		mockMvc.perform(post(BASE_URI)
               .contentType(TestUtil.APPLICATION_JSON_UTF8)
               .content(TestUtil.convertObjectToJsonBytes(bobCommand))
        ).andExpect(status().isOk())
         .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));
        
        ArgumentCaptor<OrderDTO> captor = ArgumentCaptor.forClass(OrderDTO.class);
        verify(orderServiceMock, times(1)).addOrder(captor.capture());
        verifyNoMoreInteractions(orderServiceMock);
        
        OrderDTO commandAdded = captor.getValue();
        assertNotNull(commandAdded);
        assertEquals(1, commandAdded.getMeals().size());
	}
    
    @Test
    public void addCommandWithoutTransmitterUsingHTTPPost() throws Exception {
		when(orderServiceMock.addOrder(any(OrderDTO.class))).thenThrow(new UnknowUserException(ERROR_UNKNOW_USER));
		
		mockMvc.perform(post(BASE_URI)
               .contentType(TestUtil.APPLICATION_JSON_UTF8)
               .content(TestUtil.convertObjectToJsonBytes(bobCommand))
        ).andExpect(status().isNotFound()).andExpect(content().string(ERROR_UNKNOW_USER));
        
        ArgumentCaptor<OrderDTO> captor = ArgumentCaptor.forClass(OrderDTO.class);
        verify(orderServiceMock, times(1)).addOrder(captor.capture());
        verifyNoMoreInteractions(orderServiceMock);
	}
    
    @Test
    public void addCommandWithoutDeliveryAddressUsingHTTPPost() throws Exception {
		when(orderServiceMock.addOrder(any(OrderDTO.class))).thenThrow(new EmptyDeliveryAddressException(ERROR_EMPTY_ADDRESS));
		
		mockMvc.perform(post(BASE_URI)
               .contentType(TestUtil.APPLICATION_JSON_UTF8)
               .content(TestUtil.convertObjectToJsonBytes(bobCommand))
        ).andExpect(status().isBadRequest()).andExpect(content().string(ERROR_EMPTY_ADDRESS));
        
        ArgumentCaptor<OrderDTO> captor = ArgumentCaptor.forClass(OrderDTO.class);
        verify(orderServiceMock, times(1)).addOrder(captor.capture());
        verifyNoMoreInteractions(orderServiceMock);
	}
    
    @Test
    public void addCommandWithUnknowMealUsingHTTPPost() throws Exception {
		when(orderServiceMock.addOrder(any(OrderDTO.class))).thenThrow(new UnknowMealException(ERROR_UNKNOW_MEAL));
		
		mockMvc.perform(post(BASE_URI)
               .contentType(TestUtil.APPLICATION_JSON_UTF8)
               .content(TestUtil.convertObjectToJsonBytes(bobCommand))
        ).andExpect(status().isNotFound()).andExpect(content().string(ERROR_UNKNOW_MEAL));
        
        ArgumentCaptor<OrderDTO> captor = ArgumentCaptor.forClass(OrderDTO.class);
        verify(orderServiceMock, times(1)).addOrder(captor.capture());
        verifyNoMoreInteractions(orderServiceMock);
	}
}