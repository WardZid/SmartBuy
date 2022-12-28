# SmartBuy
## Supermarket System

- Built by: 
  - Ward Zidani
  - Yara Mazareeb

SmartBuy was made as a final project for our Software Practical Engineering degree (הנדסאים) at Ort Braude College.

### Algorithms:
SmartBuy boasts a fully working product suggestion algorithm in the customer Android app that works by matching customers with respective ranks which are calculated by comparing their buying habits. The equation looks like this: P=((|A∩B|)-|B|)/m >> where A is the number of similar items for person a, B is the same for person b, and m is the average difference of priority between both people (a and b).

SmartBuy also ranks order preperation priority using our own priority equation: P=(w+t)/t >> where (w) is the amount of time the order has been waiting and (t) is the exptected time needed to prepare the order. and that leaves P to be the final priority value of said order.

*For Hebrew explanation please see the Algoritms png*

#### This project is divided into 4 parts:
1. The Management Desktop Application
2. The Customer Android Application
3. The Server
4. The SQL Database

#### Technical Overview:
- Desktop App:
  - JAVA
  - JDBC
  - Swing
  - Jgoodies Forms
- Android App:
  - Android Java
  - XML
  - Maven
  - Retrofit
  - JSON
  - GSON
- Server:
  - NodeJS
  - Javascript
  - ExpressJS
  - MySQL
  - Crypto
- Database:
  - MySQL

##### The Desktop Management Console:

Allows two types of managers:
  - General Manager
    - Can employ new managers (of both kinds) and workers.
    - Can add, edit, delete products in the database
  - Shift Manager
    - Can insert workers into shifts.
    - Can recieve and manage orders made to the supermarket.

##### The Customer Android Application:

There is only one type of customer, a registered customer that can:
- Sign up/Log in.
- Browse products categorically or with typed search.
- Search for an item's physical location in a map overlay of the store using typed search.
- Add items to personal cart.
- Check Out.
- View items in personal fridge.

*To view the Android app's model and control classes, follow the path:*
***SmartBuy/Customer/SmartBuy01/app/src/main/java/com/example/smartbuy01/***
    
