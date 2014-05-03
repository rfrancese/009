<?php
 /****************************************************
 * Gestione della connessione al Database con MySQLi *
 ****************************************************/

    $db = mysqli_connect("localhost", "root", "neo", "stazioni");
        
    if (mysqli_connect_errno()) {
        printf("Connect failed: %s\n", mysqli_connect_error());
        exit();
    }

?>