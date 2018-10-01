# Order Service

## Description
Assure la prise de commande et la recherche dans le catalogue.

## Technologies
Java 1.8 (JEE) avec les frameworks Spring et Hibernate.

## API exposée (HTTP REST)
`/orders` :  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- POST (content-type : JSON, encoding : UTF-8), body : OrderDTO  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Permet d'ajouter une nouvelle commande