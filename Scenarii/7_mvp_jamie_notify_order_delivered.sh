#FIXME: works but error on restaurant and bank doesn't show up -- Hostnames ---
orderService="localhost:9555"
restaurant="localhost:9777"
coursierservice="localhost:9888"
# -- Context ---
echo "As Jamie, I want to notify that the order has been delivered, so that my account can be credited and the restaurant can be informed."
echo "--- Creating context... ---"
# Creating user bob
curl -X POST --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "{\"id\":-1,\"lastName\":\"Lenon\",\"firstName\":\"Bob\"}" "http://$orderService/users" > temp/7/0_bobId.txt
bob_id=$(grep -Po '"id": *\K[^,]*' temp/7/0_bobId.txt | head -1)
# Create a restaurant POST /restaurants
curl -X POST --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "{\"id\":-1,\"name\":\"Asiakeo\",\"restaurantAddress\":\"407 ch. de l'oued\",\"meals\":[]}" "http://$restaurant/restaurants" > temp/7/1_restaurantCreated.txt
restaurant_id=$(grep -Po '"id": *\K[^,]*' temp/7/1_restaurantCreated.txt | head -1)
# Create a ramen meal POST /restaurants/{restaurantId}/meals
curl -X POST --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "{\"id\":-1,\"name\":\"Ramen\",\"price\":3,\"ingredients\":[\"pork\"],\"tags\":[\"Asian\"]}" "http://$restaurant/restaurants/$restaurant_id/meals" > temp/7/2_mealCreated.txt
sleep 2
# Create a coursier POST /coursiers
curl -X POST --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "{\"id\":-1,\"name\":\"Jamie\",\"accountNumber\":\"FR89 3704 0044 0532 0130 00\",\"latitude\":0.0, \"longitude\":0.0}" "http://$coursierservice/coursiers" > temp/7/22_coursierCreated.txt
sleep 2
# ************** Scenario **************
echo "*******1- As Bob, a hungry student, I browse the food catalogue offered by Uberoo"
echo "Press any key to continue..."
read
# Récupère les plats du catalogue de la catégorie "Asian" :
curl -X GET --silent "http://$orderService/meals?tag=Asian" > temp/7/3_resultOfCatalog.txt
restaurant_id_for_orderService=$(grep -Po '"id": *\K[^,]*' temp/7/3_resultOfCatalog.txt | head -1)
echo "Result (temp/7/3_resultOfCatalog.txt):"
cat temp/7/3_resultOfCatalog.txt
echo "Press any key to continue..."
read

echo "*******2- I decide to go for an asian meal, ordering a ramen soup"
# Information complémentaire (identifiant de l'utilisateur) non nécessaire au scénario mais nécessaire pour le système :

# Envoi de la commande au système et récupération de l'ETA
curl -X POST --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "{ \"id\": -1, \"meals\": [ {\"name\":\"Ramen\",\"tags\":[\"Asian\"],\"restaurant\":{\"id\":$restaurant_id_for_orderService,\"name\":\"Asiakeo\",\"restaurantAddress\":\"690 Route de Grasse, 06600 Antibes\"}} ], \"transmitter\": { \"id\": \"$bob_id\", \"firstName\": \"Bob\", \"lastName\": \"\" }, \"deliveryAddress\": \"930 Route des Colles, 06410 Biot\", \"eta\": null, \"state\": \"WAITING\", \"restaurant\":{\"id\":$restaurant_id_for_orderService,\"name\":\"Asiakeo\",\"restaurantAddress\":\"690 Route de Grasse, 06600 Antibes\"} }" "http://$orderService/orders" > temp/7/4_orderWithETA.txt
echo "Result (temp/7/4_orderWithETA.txt):"
cat temp/7/4_orderWithETA.txt
echo "\nPress any key to continue..."
read

echo "*******3- The system estimates the ETA (e.g., 45 mins) for the food, and I decide to accept it"
# Acception de l'ETA :
sed -i 's/WAITING/VALIDATED/g' temp/7/4_orderWithETA.txt
order_id=$(grep -Po '"id": *\K[^,]*' temp/7/4_orderWithETA.txt | head -1)
# Envoi au système, le système poste un message dans le bus pour le restaurant :
curl -X PUT --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "$(tail -1 temp/7/4_orderWithETA.txt)" "http://$orderService/orders/$order_id" > temp/7/5_validatedOrder.txt
echo "Press any key to continue..."
read
sleep 2
restau_id=$(grep -Po '"id": *\K[^,]*' temp/7/5_validatedOrder.txt | tail -1)
echo "*******4- The restaurant can consult the list of meals to prepare, and start the cooking process"
curl -X GET --silent "http://$restaurant/restaurants/$restau_id/orders/" > temp/7/6_pendingOrders.txt
echo "Press any key to continue..."
read
echo "*******5- A coursier is assigned to my order, and deliver it on the campus"
curl -X GET --silent "http://$coursierservice/deliveries" > temp/7/7_pendingDeliveries.txt
echo "Pending deliveries:\n"
cat temp/7/7_pendingDeliveries.txt
echo "Press any key to continue..."
read
echo "*******6- Later, the coursier just delivered the order"
cp temp/7/7_pendingDeliveries.txt temp/7/8_delivered_delivery.txt
sed -i 's/"state":false/"state":true/g' temp/7/8_delivered_delivery.txt
json=$(cat temp/7/8_delivered_delivery.txt)
json=${json:1: -1}
curl -X PUT --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "$json" "http://$coursierservice/deliveries/" > temp/7/9_delivered_delivery_response.txt
echo "Press any key to continue..."
read
echo "*******7- The order is not to do anymore"
curl -X GET --silent "http://$coursierservice/deliveries" > temp/7/10_pendingDeliveries.txt
cat temp/7/10_pendingDeliveries.txt
echo "Press any key to finish..."
read
# **********************************************************************