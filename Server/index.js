/*
RESTFul Service
AUTHOR: Yara Mazareeb, Ward Zidani
Update 28/08/2019
*/

var express = require('express'); //server framework
var crypto = require('crypto'); //encrypts password
var mysql = require('mysql'); //database 
var bodyParser = require('body-parser');//body translator

var app=express();//declare server framework object
app.use(bodyParser.json()); //accept json params
app.use(bodyParser.urlencoded({extended: true})); //accept url encoded params

//to prevent memory leakage, max listeners is set to 0, which means infinite
require('events').EventEmitter.defaultMaxListeners = 0;

//connect to mysql
 var con = mysql.createConnection({
    host: 'EXAMPLE IP', //host ip
    user: 'EXAMPLE DB USER',
    password:'EXAMPLE DB PASS',
    database:'EXAMPLE DB SCHEMA NAME'
 });

 //password util
 var genRandomString = function(length){
     //generate a random string to be used for password hashing
     return crypto.randomBytes(Math.ceil(length/2))
     .toString('hex')//convert to hexa format
     .slice(0,length);//return required number of characters
 };

 var sha512 = function(password,salt){
    //use the random string we made to hash password
    var hash = crypto.createHmac('sha512',salt); //use SHA512
    hash.update(password);//hash password
    var value = hash.digest('hex');
    return{//return final result
       salt:salt,
       passwordHash: value
    };
};

function saltHashPassword(userPassword){
    //function for ease of access
    var salt = genRandomString(16); //gen random string with 16 characters to salt
    var passwordData = sha512(userPassword,salt);
    return passwordData;
};

function checkHashPassword(userPassword,salt){
    //hash entered password with the user's saved salt to check if the proper output is recieved
    var passwordData = sha512(userPassword,salt);
    return passwordData;
};

 //check if the connection works
 app.get('/connection/',(req,res) => {
    console.log(getDateTime()+": Connection Attempt");
    res.end("true");
});

//current date and time used for clear printing in console
function getDateTime() {

    var date = new Date();

    var hour = date.getHours();
    hour = (hour < 10 ? "0" : "") + hour;

    var min  = date.getMinutes();
    min = (min < 10 ? "0" : "") + min;

    var sec  = date.getSeconds();
    sec = (sec < 10 ? "0" : "") + sec;

    var year = date.getFullYear();

    var month = date.getMonth() + 1;
    month = (month < 10 ? "0" : "") + month;

    var day  = date.getDate();
    day = (day < 10 ? "0" : "") + day;

    return day + "/" + month + "/" + year + "--" + hour + ":" + min + ":" + sec;

};

//function recieves email,password,and name to create a new user in th database
 app.post('/register/',(req,res) => {
    var post_data = req.body; //get POST params

    var plain_password = post_data.password; //get password from post params
    var hash_data = saltHashPassword(plain_password);
    var password = hash_data.passwordHash;//get hash value
    var salt = hash_data.salt; // get salt

    var name = post_data.name;
    var email = post_data.email;

    con.query("SELECT * FROM users WHERE email=?",[email],function(err,result,fields){
        con.on('error',function(err){
            console.log(getDateTime()+": [MySQL ERROR]",err);
        });
        if(result && result.length) {
            res.json('User already exists!');
        }
        else
        {
            con.query("INSERT INTO `users` (`name`,`email`,`encrypted_password`,`salt`,`created_at`,`updated_at`) VALUES (?,?,?,?,NOW(),NOW())",[name,email,password,salt], function(err,result,fields){
                con.on('error',function(err){
                    console.log('[MySQL ERROR]',err);
                    res.json('Register Error: ',err);
                });
                res.json('Register Successful');
                console.log(getDateTime()+": New User Registered: "+ name);
            });
        }
    });
});

//function that recieves eamil and password and validates that info
//if the info is valid, the users full info is returned from the database. (not the pass and salt of course)
app.post('/signin/',(req,res) => {
    var post_data = req.body;

    //Extract email and password
    var user_password = post_data.password;
    var email = post_data.email;


    con.query('SELECT *,DATE_FORMAT(created_at, "%M %D of %Y at %H:%i") as created_at FROM users WHERE email=?',[email],function(err,result,fields){
        con.on('error',function(err){
            console.log(getDateTime()+": [MySQL ERROR]",err);
        });
        if(result && result.length) 
        {
            var salt = result[0].salt; // get salt of result if account exists
            var encrypted_password = result[0].encrypted_password;
            //Hash Password from Login request with in database
            var hashed_password = checkHashPassword(user_password,salt).passwordHash;
            if(encrypted_password==hashed_password) {
                console.log(getDateTime()+": User logged in");
                res.end(JSON.stringify(result[0]));//if pass is true, return all info of user
            } 
            else res.end(JSON.stringify('Wrong Password'));
        }
        else
        {
            res.json('User does not exist!');
        }
    });
});

//returns all products in database when used
//without images
app.get('/products/',(req,res) => {
    con.query("call product_table_without_images();",function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        console.log(getDateTime()+": Products listed");
        res.end('{"products":'+JSON.stringify(result[0])+'}');
    });
});

//returns a singlular product to the app according to the product's id recieved
app.post('/product/',(req,res) => {
    //get specific product

    var post_data = req.body;
    var productId=post_data.product_id;
    con.query("select P.product_id,P.product_name,P.product_type,P.amount,P.weight,P.price,P.area,P.row,P.prep_time from products P where P.product_id = ?",[productId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        console.log(getDateTime()+": Product fetched:");
        res.end(JSON.stringify(result[0]));
    });
});

//returns a list of all the product's categories
app.get('/categories/',(req,res) => {
    //retrieve categories currently available
    con.query("select distinct product_type from products order by product_type asc",function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        
        console.log(getDateTime()+": Categories fetched:");
        res.end('{"categories":'+JSON.stringify(result)+'}');
    });
});

//returns a list of prodycts by referenced product type (category)
app.post('/products_by_type/',(req,res) => {
   //retrieve products of a specific category
    var post_data = req.body;
    var productType=post_data.product_type;
    con.query("select P.product_id,P.product_name,P.product_type,P.amount,P.weight,P.price,P.area,P.row,P.prep_time from products P where P.product_type = ?",[productType],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        console.log(getDateTime()+": Product fetched by categories:");
        res.end('{"products":'+JSON.stringify(result)+'}');
    });
});

//returns a list of all cart items (for a specific user) as cart items
//cart items meaning that only the id and the amount of the product in the cart are returned
app.post('/cart/',(req,res) => {
    var post_data=req.body;
    var userId=post_data.user_id;

    console.log(getDateTime()+": Cart Listed: User: "+userId);
    con.query('SELECT * FROM cart WHERE user_id=?',[userId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        console.log(getDateTime()+": Cart fetched!");
        //res.end(JSON.stringify(result));
        res.end('{"cart":'+JSON.stringify(result)+'}');
    });
});

//returns a list of products in a specific user's cart
app.post('/products_in_cart/',(req,res) => {
    //products in cart
     var post_data = req.body;
     var userId=post_data.user_id;
     con.query("call products_in_cart(?);",[userId],function(err,result,fields){
         con.on('error',function(err){
             console.log('[MySQL ERROR]',err);
         });
         console.log(getDateTime()+": products in cart fetched");
         res.end('{"products":'+JSON.stringify(result[0])+'}');
     });
 });

 //a new item is added to a specific user's cart along with the amount
app.post('/add_to_cart/',(req,res) => {
    var post_data=req.body;

    var userId=post_data.user_id;
    var productId=post_data.product_id;
    var amount =post_data.amount_to_add;

    console.log(getDateTime()+": Cart addition: User: "+userId+" Product: "+productId+" Amount: "+amount);

    con.query('SELECT * FROM cart WHERE user_id = ? AND product_id = ?',[userId,productId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        if(result && result.length) {
            //if the product already exists in the persons cart, the amounts are added
            con.query('UPDATE `cart` SET `amount_in_cart` = (`amount_in_cart` + ? ) WHERE `user_id` = ? AND `product_id` = ?',[amount,userId,productId], function(err,result,fields){
                con.on('error',function(err){
                    console.log('[MySQL ERROR]',err);
                    res.json('Cart Error: ',err);
                });
                res.end('Cart Updated!');
            });
        }else {
            //else a new item is inserted for this person with the amount
            con.query("INSERT INTO `cart` (`user_id`,`product_id`,`amount_in_cart`) VALUES (?,?,?)",[userId,productId,amount], function(err,result,fields){
                con.on('error',function(err){
                    console.log('[MySQL ERROR]',err);
                    res.end('Cart Error: ',err);
                });
                res.end('Cart Updated!');
            });
        }
    });
});

//change the amount of a an item in a cart
app.post('/alter_cart/',(req,res) => {
    var post_data=req.body;

    var userId=post_data.user_id;
    var productId=post_data.product_id;
    var amount =post_data.amount_to_set;

    console.log(getDateTime()+": Cart alteratioin: User: "+userId+" Product: "+productId+" Amount: "+amount);

    con.query('SELECT * FROM cart WHERE user_id = ? AND product_id = ?',[userId,productId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        if(result && result.length) {
            if(amount<=0) {
                //if the amount recieved for change is 0, the cart item is removed from this persons cart
                con.query('DELETE FROM cart WHERE `user_id` = ? AND `product_id` = ?',[userId,productId], function(err,result,fields){
                    con.on('error',function(err){
                        console.log('[MySQL ERROR]',err);
                        res.json('Cart Error: ',err);
                    });
                    res.end('Item Deleted!');
                });
            }else{
                //if the amount is over 0, meaning it is valid, the new amount is set
                con.query('UPDATE `cart` SET `amount_in_cart` =  ?  WHERE `user_id` = ? AND `product_id` = ?',[amount,userId,productId], function(err,result,fields){
                    con.on('error',function(err){
                        console.log('[MySQL ERROR]',err);
                        res.json('Cart Error: ',err);
                    });
                    res.end('Cart Updated!');
                });
            }
        }else {
            if(amount > 0){
                //if for some reason the crt item isnt existant in the database, but the user somehow updated it, the item is added
                //this is just a bug precaution, no reason this should happen
                con.query("INSERT INTO `cart` (`user_id`,`product_id`,`amount_in_cart`) VALUES (?,?,?)",[userId,productId,amount], function(err,result,fields){
                    con.on('error',function(err){
                        console.log('[MySQL ERROR]',err);
                        res.json('Cart Error: ',err);
                    });
                    res.end('Cart Updated!');
                });
            }else{
                res.end('Item Non-Existant');
            }
        }
    });
});

//remove an item from someones cart
app.post('/delete_from_cart/',(req,res) => {
    var post_data=req.body;

    var userId=post_data.user_id;
    var productId=post_data.product_id;


    con.query('SELECT * FROM cart WHERE user_id = ? AND product_id = ?',[userId,productId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        if(result && result.length) {
            con.query('DELETE FROM cart WHERE `user_id` = ? AND `product_id` = ?',[userId,productId], function(err,result,fields){
                con.on('error',function(err){
                    console.log('[MySQL ERROR]',err);
                    res.end('Cart Error: ',err);
                });
                res.end('Item Deleted!');
            });
        }else {
            res.end("Item Already Deleted");
        }
    });
});

//create a new order by following some steps
//step 1: create an order in the orders item
//step 2: copy into the product_in_order table all the referenced user's cart
//step 3: copy into the fridge table all the referenced user's cart
//step 4: update store's stock amounts
//step 5: empty the user's cart
//step 6: check the if the cart was cleaned properly
//step 7: fetch the order's id
//step 8: prepare order for step 9
//step 9: calculate the time needed to prepare each order ,then update the status to "WAITING", meaning it is ready to enter the order queue
app.post('/new_order/',(req,res) => {
    post_data=req.body;
    var userId = post_data.user_id;

    console.log(getDateTime()+": New order: User: "+userId);
    //step 1
    con.query("INSERT INTO `orders` (`user_id`,`added_at`,`order_status`) VALUES (?,NOW(),'JUST_ADDED')",[userId], function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
            res.json('Order Error: ',err);
        });
        //step 2
        con.query("INSERT INTO `products_in_order` (`order_id`,`product_id`,`amount_in_order`) SELECT O.order_id,C.product_id,C.amount_in_cart FROM orders O, cart C WHERE C.user_id = O.user_id AND C.user_id = ? AND O.order_status = 'JUST_ADDED';",[userId], function(err,result,fields){
            con.on('error',function(err){
                console.log('[MySQL ERROR]',err);
                res.json('cart transfer Error: ',err);
            });
            //step 3
            con.query("INSERT INTO `fridge` (`user_id`,`product_id`,`amount_in_fridge`,`added_at`) SELECT C.user_id,C.product_id,C.amount_in_cart,NOW() FROM cart C WHERE C.user_id = ?;",[userId], function(err,result,fields){
                con.on('error',function(err){
                    console.log('[MySQL ERROR]',err);
                    res.json('fridge transfer Error: ',err);
                });
            });
            //step 4
            con.query("UPDATE products P, cart C SET P.amount = P.amount - C.amount_in_cart WHERE P.product_id = C.product_id AND C.user_id = ?",[userId],function(err,result,fields){
                con.on('error',function(err){
                    console.log('[MySQL ERROR]',err);
                    res.end('cart transfer Error: ',err);
                });
                //step 5
                con.query("DELETE FROM cart WHERE user_id = ?;",[userId],function(err,result,fields){
                    con.on('error',function(err){
                        console.log('[MySQL ERROR]',err);
                        res.json('cart cleaning Error: ',err);
                    });
                    //step 6
                    con.query("SELECT COUNT(*) as cnt FROM cart WHERE user_id = ?",[userId], function(err,result,fields){
                        con.on('error',function(err){
                            console.log('[MySQL ERROR]',err);
                            res.end('CART Error: ',err);
                        });
                        var count = result[0].cnt;
                        //step 7
                        con.query("SELECT max(order_id) AS oid FROM orders",[userId],function(err,result,fields){
                            con.on('error',function(err){
                                console.log('[MySQL ERROR]',err);
                            });
                            var orderId = result[0].oid;
                            //if cleaning didn't work
                            if(count > 0) res.end("Cart cleaning Unseccessful!");
                            else{
                                //step 8
                                con.query("UPDATE orders SET order_status = 'READY_FOR_QUEUE' WHERE order_id = ?;",[orderId],function(err,result,fields){
                                    con.on('error',function(err){
                                        console.log('[MySQL ERROR]',err);
                                        res.json('Status updating Error: ',err);
                                    });
                                    //step 9
                                    con.query("UPDATE orders O SET O.total_time = (SELECT sum(temp.t) FROM (SELECT PO.product_id, (P.prep_time * PO.amount_in_order) AS t FROM products P,products_in_order PO, orders O WHERE PO.product_id = P.product_id AND PO.order_id = ? GROUP BY PO.product_id) temp), O.order_status='WAITING' WHERE O.order_id = ?",[orderId,orderId],function(err,result,fields){
                                        con.on('error',function(err){
                                            console.log('[MySQL ERROR]',err);
                                            res.json('Status updating Error: ',err);
                                        });
                                        res.end('Order Sent');
                                    });
                                });
                            }
                        });
                        
                    });
                });
            });
        });
    });
});

//return a list of orders and the datetime they were ordered at
app.post('/orders/',(req,res) => {
    var post_data=req.body;

    var userId=post_data.user_id;

    console.log(getDateTime()+": Orders Listed for user: "+userId);

    con.query('SELECT order_id,order_status,DATE_FORMAT(added_at, "%M %D of %Y at %H:%i") as date_of_order FROM orders WHERE user_id=? ORDER BY added_at desc',[userId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        res.end('{"orders":'+JSON.stringify(result)+'}');
    });
});

//return the product_id and amount pairs in a specific order
app.post('/products_in_order_summary/',(req,res) => {
    var post_data=req.body;
    var orderId=post_data.order_id;

    con.query('SELECT product_id,amount_in_order FROM products_in_order WHERE order_id = ?',[orderId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        console.log(getDateTime()+": Orders fetched!");
        res.end('{"products_in_order":'+JSON.stringify(result)+'}');
    });
});

//return the actual products in the order referenced
app.post('/products_in_order/',(req,res) => {
    //products in order without images
     var post_data = req.body;
     var orderId=post_data.order_id;

     con.query("call products_in_order(?);",[orderId],function(err,result,fields){
         con.on('error',function(err){
             console.log('[MySQL ERROR]',err);
         });
         console.log(getDateTime()+": products in order fetched");
         res.end('{"products":'+JSON.stringify(result[0])+'}');
     });
 });

//return the product id and amount pairs from the fridge fro the referenced user
app.post('/fridge/',(req,res) => {
    var post_data=req.body;

    var userId=post_data.user_id;

    con.query('SELECT product_id,amount_in_fridge,DATE_FORMAT(added_at, "%M %D of %Y at %H:%i") as added_at FROM fridge WHERE user_id = ?',[userId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        console.log(getDateTime()+": Fridge Fetched");
        res.end('{"fridge":'+JSON.stringify(result)+'}');
    });
});

//return a list of products int a the referenced user's fridge
app.post('/products_in_fridge/',(req,res) => {
    //products in fridge without images
     var post_data = req.body;
     var userId=post_data.user_id;
     con.query("call products_in_fridge(?);",[userId],function(err,result,fields){
         con.on('error',function(err){
             console.log('[MySQL ERROR]',err);
         });
         console.log(getDateTime()+": products in fridge fetched");
         res.end('{"products":'+JSON.stringify(result[0])+'}');
     });
 });

 //change the amount of an item in the referenced user fridge
 app.post('/alter_fridge/',(req,res) => {
    var post_data=req.body;

    var userId=post_data.user_id;
    var productId=post_data.product_id;
    var amount =post_data.amount_to_set;

    console.log(getDateTime()+": Fridge alteratioin: User: "+userId+" Product: "+productId+" Amount: "+amount);

    con.query('SELECT * FROM fridge WHERE user_id = ? AND product_id = ?',[userId,productId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        if(result && result.length) {
            if(amount<=0) {
                //if the amount recieved for change is 0, the item is deleted from thr fridge
                con.query('DELETE FROM fridge WHERE `user_id` = ? AND `product_id` = ?',[userId,productId], function(err,result,fields){
                    con.on('error',function(err){
                        console.log('[MySQL ERROR]',err);
                        res.json('Fridge Error: ',err);
                    });
                    res.end('Item Deleted!');
                });
            }else{
                //if the value is valid, the amount is changed
                con.query('UPDATE `fridge` SET `amount_in_fridge` =  ?  WHERE `user_id` = ? AND `product_id` = ?',[amount,userId,productId], function(err,result,fields){
                    con.on('error',function(err){
                        console.log('[MySQL ERROR]',err);
                        res.json('Cart Error: ',err);
                    });
                    res.end('Fridge Updated!');
                });
            }
        }else {
            if(amount > 0){
                //if for some reason the item isnt in the fridge but somehow the user updated it, the product is added to the fridge with the specified amount
                con.query("INSERT INTO `fridge` (`user_id`,`product_id`,`amount_in_cart`) VALUES (?,?,?)",[userId,productId,amount], function(err,result,fields){
                    con.on('error',function(err){
                        console.log('[MySQL ERROR]',err);
                        res.json('Fridge Error: ',err);
                    });
                    res.end('Fridge Updated!');
                });
            }else{
                res.end('Item Non-Existant');
            }
        }
    });
});

//remove an item from the referrenced usre's fridge
app.post('/delete_from_fridge/',(req,res) => {
    var post_data=req.body;

    var userId=post_data.user_id;
    var productId=post_data.product_id;

    con.query('SELECT * FROM fridge WHERE user_id = ? AND product_id = ?',[userId,productId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        if(result && result.length) {
            con.query('DELETE FROM fridge WHERE `user_id` = ? AND `product_id` = ?',[userId,productId], function(err,result,fields){
                con.on('error',function(err){
                    console.log('[MySQL ERROR]',err);
                    res.json('Fridge Error: ',err);
                });
                res.json('Item Deleted!');
            });
        }else {
            res.json("Item Already Deleted");
        }
    });
});

app.post('/suggestions/',(req,res) => {
    var post_data=req.body;
    var userId=post_data.user_id;

    con.query('call sp_suggestions(?)',[userId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
            res.end("error");
        });
        if(result && result.length) {
            //send suggestions
            console.log(getDateTime()+": Suggestions fetched!");
            res.end('{"products":'+JSON.stringify(result[0])+'}');
        }
    });
});

//returns a list of the top selling items in the store.
//20 products are returned in a random order, but all of them are best sellers
app.get('/best_sellers/',(req,res) => {
    con.query("CALL sp_best_selling();",function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        console.log(getDateTime()+": Best Sellers Fetched!");
        res.end('{"products":'+JSON.stringify(result[0])+'}');
    });
});

//returns a list of products from the user's favourtes list
app.post('/favourites/',(req,res) => {
    var post_data=req.body;
    var userId=post_data.user_id;

    con.query("call sp_favourite_products(?)",[userId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        console.log(getDateTime()+": Favourites Fetched!");
        res.end('{"products":'+JSON.stringify(result[0])+'}');
    });
});

//add the referenced product to the referenced user's favourites list
app.post('/add_to_favourites/',(req,res) => {
    var post_data=req.body;
    var userId = post_data.user_id;
    var productId=post_data.product_id;

    con.query("select * from favourites where user_id = ? and product_id=?;",[userId,productId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        if(result && result.length) {
            res.end("Product Exists!");
        }else{
            con.query("insert into favourites (user_id,product_id) values (?,?);",[userId,productId],function(err,result,fields){
                con.on('error',function(err){
                    console.log('[MySQL ERROR]',err);
                });
                
                console.log(getDateTime()+": Added To Favourites");
                res.end("Product Added");
            });
        }
    });
});

//remove the referenced product from the referenced user's favourites list
app.post('/delete_from_favourites/',(req,res) => {
    var post_data=req.body;

    var userId=post_data.user_id;
    var productId=post_data.product_id;


    con.query('SELECT * FROM favourites WHERE user_id = ? AND product_id = ?',[userId,productId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        if(result && result.length) {
            con.query('DELETE FROM favourites WHERE `user_id` = ? AND `product_id` = ?',[userId,productId], function(err,result,fields){
                con.on('error',function(err){
                    console.log('[MySQL ERROR]',err);
                    res.json('Favourites Error: ',err);
                });
                res.end('Item Deleted!');
            });
        }else {
            res.end("Item Already Deleted");
        }
    });
});

//returns a byte array for an image blob
//the byte array is then converted to in image in the app
app.post('/blob_image/',(req,res) => {
    var post_data=req.body;
    var productId=post_data.product_id;

    con.query("select image from products where product_id = ?",[productId],function(err,result,fields){
        con.on('error',function(err){
            console.log('[MySQL ERROR]',err);
        });
        var row=JSON.parse(JSON.stringify(result[0]));
        console.log(getDateTime()+": blob image Fetched!");
        res.end('{"image":'+JSON.stringify(row.image.data)+'}');
    });
});

 //initiate the server to listen on port 3000
 app.listen(3000, () => {
     console.log('SmartBuy RESTFul running on port 3000');
 });