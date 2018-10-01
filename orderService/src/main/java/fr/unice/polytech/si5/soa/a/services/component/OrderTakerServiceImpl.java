package fr.unice.polytech.si5.soa.a.services.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import fr.unice.polytech.si5.soa.a.communication.MealDTO;
import fr.unice.polytech.si5.soa.a.communication.OrderDTO;
import fr.unice.polytech.si5.soa.a.dao.ICatalogDao;
import fr.unice.polytech.si5.soa.a.dao.IOrderTakerDao;
import fr.unice.polytech.si5.soa.a.dao.IUserDao;
import fr.unice.polytech.si5.soa.a.entities.Order;
import fr.unice.polytech.si5.soa.a.entities.User;
import fr.unice.polytech.si5.soa.a.entities.Meal;
import fr.unice.polytech.si5.soa.a.exceptions.EmptyDeliveryAddressException;
import fr.unice.polytech.si5.soa.a.exceptions.UnknowMealException;
import fr.unice.polytech.si5.soa.a.exceptions.UnknowUserException;
import fr.unice.polytech.si5.soa.a.services.IOrderTakerService;

/**
 * Class name	OrderTakerServiceImpl
 * @see 		IOrderTakerService
 * Date			30/09/2018
 * @author		PierreRainero
**/
@Primary
@Service("OrderTakerService")
public class OrderTakerServiceImpl implements IOrderTakerService {
	private static Logger logger = LogManager.getLogger(OrderTakerServiceImpl.class);
	
	@Autowired
	private IOrderTakerDao orderDao;
	
	@Autowired
	private IUserDao userDao;
	
	@Autowired
	private ICatalogDao catalogDao;

	@Override
	/**
     * {@inheritDoc}
     */
	public OrderDTO addOrder(OrderDTO orderToAdd) throws UnknowUserException, EmptyDeliveryAddressException, UnknowMealException {
		Order order = new Order(orderToAdd);
		
		if(order.getDeliveryAddress()==null || order.getDeliveryAddress().isEmpty()) {
			throw new EmptyDeliveryAddressException("Delivery address cannot be empty");
		}
		
		Optional<User> userWrapped = userDao.findUserById(orderToAdd.getTransmitter().getId());
		if(!userWrapped.isPresent()) {
			throw new UnknowUserException("Can't find user with id = "+orderToAdd.getTransmitter().getId());
		}
		order.setTransmitter(userWrapped.get());
		
		findMeals(order, orderToAdd.getMeals());

		return orderDao.addOrder(order).toDTO();
	}
	
	private void findMeals(Order order, List<MealDTO> mealsDTO) throws UnknowMealException{
		for(MealDTO mealDTO : mealsDTO) {
			Optional<Meal> tmp = catalogDao.findMealByName(mealDTO.getName());
			
			if(!tmp.isPresent()) {
				throw new UnknowMealException("Can't find meal nammed \""+mealDTO.getName()+"\"");
			}
			
			order.addMeal(tmp.get());
		}
	}
	
}