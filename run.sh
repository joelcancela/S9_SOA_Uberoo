# -- Context ---
echo "--- Creating context... ---"
docker exec soa_orderService_database mysql -uroot -pteama -e "USE uberoo; INSERT INTO MEAL (name) VALUES ('Ramen'); INSERT INTO Meal_tags VALUES ((SELECT id FROM MEAL WHERE name = 'Ramen'), 'Asian'); INSERT INTO USER (firstName, lastName) VALUES ('Bob', '');"
docker exec soa_orderService_database mysql -uroot -pteama -e "USE uberoo; SELECT id FROM USER WHERE firstName='Bob';" > Temp/bobId.txt
curl -X POST --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "{\"route\":\"http://restaurantservice:8080/restaurants/orders/\"}" "http://localhost:5001/subscribe" # Subscription of the restaurant service to the bus
curl -X POST --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "{\"route\":\"http://coursierservice:8080/deliveries/\"}" "http://localhost:5001/subscribe" # Subscription of the coursier service to the bus
echo "--- Context created ---"

# ************** Scenario **************
echo "*******1- As Bob, a hungry student, I browse the food catalogue offered by Uberoo"
# Récupère les plats du catalogue de la catégorie "Asian" :
curl -X GET --silent "http://localhost:9555/meals?tag=Asian" > Temp/resultOfSearch.txt 
echo "Press any key to continue..."
read

echo "*******2- I decide to go for an asian meal, ordering a ramen soup"
# Information complémentaire (identifiant de l'utilisateur) non nécessaire au scénario mais nécessaire pour le système :
bob_id=$(tail -1 Temp/bobId.txt)
# Envoi de la commande au système et récupération de l'ETA
curl -X POST --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "{ \"id\": -1, \"meals\": [ { \"name\": \"Ramen\", \"tags\": [ \"Asian\" ] } ], \"transmitter\": { \"id\": \"$bob_id\", \"firstName\": \"Bob\", \"lastName\": \"\" }, \"deliveryAddress\": \"930 Route des Colles, 06410 Biot\", \"eta\": null, \"state\": \"WAITING\" }" "http://localhost:9555/orders" > Temp/orderWithETA.txt
echo "Press any key to continue..."
read

echo "*******3- The system estimates the ETA (e.g., 45 mins) for the food, and I decide to accept it"
# Acception de l'ETA :
sed -i 's/WAITING/VALIDATED/g' Temp/orderWithETA.txt
order_id=$(grep -Po '"id": *\K[^,]*' Temp/orderWithETA.txt | head -1)
# Envoi au système, le système poste un message dans le bus pour le restaurant :
curl -X PUT --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "$(tail -1 Temp/orderWithETA.txt)" "http://localhost:9555/orders/$order_id/" > Temp/validatedOrder.txt
curl -X GET --silent "http://localhost:5001/messages" > Temp/messagesInTheBus.txt
echo "Press any key to continue..."
read

sleep 3

echo "*******4- The restaurant can consult the list of meals to prepare, and start the cooking process"
curl -X GET --silent "http://localhost:9777/restaurants/orders/" > Temp/pendingOrders.txt
echo "Press any key to continue..."
read

echo "*******5- A coursier is assigned to my order, and deliver it on the campus"
curl -X GET --silent "http://localhost:9666/deliveries" > Temp/pendingDeliveries.txt
echo "Press any key to finish..."
read

# **********************************************************************
echo "--- Cleaning context... ---"
# -- Clean context --
docker exec soa_orderService_database mysql -uroot -pteama  -e "USE uberoo; DELETE FROM UBEROOORDER_MEAL; DELETE FROM UBEROOORDER; DELETE FROM Meal_tags; DELETE FROM MEAL; DELETE FROM USER;"
echo "--- Context cleaned ---"