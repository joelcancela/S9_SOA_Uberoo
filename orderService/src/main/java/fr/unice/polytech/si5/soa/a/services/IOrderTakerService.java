package fr.unice.polytech.si5.soa.a.services;

import fr.unice.polytech.si5.soa.a.dto.OrderDTO;
import fr.unice.polytech.si5.soa.a.exceptions.EmptyDeliveryAddressException;
import fr.unice.polytech.si5.soa.a.exceptions.UnknowMealException;
import fr.unice.polytech.si5.soa.a.exceptions.UnknowOrderException;
import fr.unice.polytech.si5.soa.a.exceptions.UnknowUserException;

/**
 * Class name	IOrderTakerService
 * Date			30/09/2018
 * @author		PierreRainero
 *
 */
public interface IOrderTakerService {
	/**
	 * Add a {@link OrderDTO} to the system
	 * @param orderToAdd command to add
	 * @return the traited command
	 * @throws UnknowUserException if the transmitter doesn't exist
	 * @throws EmptyDeliveryAddressException if the delivery address is empty
	 * @throws UnknowMealException if a meal doesn't exist
	 */
	OrderDTO addOrder(OrderDTO orderToAdd) throws UnknowUserException, EmptyDeliveryAddressException, UnknowMealException;
	
	/**
	 * Change the state of a {@link OrderDTO}
	 * @param orderToUpdate order to update
	 * @return updated order
	 * @throws UnknowOrderException if the order doesn't exist
	 */
	OrderDTO updateOrderState(OrderDTO orderToUpdate) throws UnknowOrderException;
}
