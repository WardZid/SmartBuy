# SmartBuy
## Supermarket System

- Built by: 
  - Ward Zidani
  - Yara Mazareeb

SmartBuy was made as a final project for our Software Practical Engineering degree (הנדסאים) at Ort Braude College.
SmartBuy boasts a fully working product suggestion in the customer Android app that works by matching customers with respective ranks which are calculated by comparing their buying habits.

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
    
