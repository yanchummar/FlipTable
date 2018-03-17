<?php

	//Defining Constants
	define('HOST','localhost');
	define('USER','USERNAME_HERE');
	define('PASS','PASSWORD_HERE');
	define('DB','DB_NAME_HERE');

	//Connecting to Database
    $con = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Reach our Servers!');
    mysqli_set_charset($con, 'utf8');
