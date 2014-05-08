<?php 
    header("Content-Type : application/json");

    mysql_connect("localhost", "fanteam@localhost", "") or die("connessione fallita") ;
    mysql_select_db("my_fanteam") or die("connessione al database fallita") ;

    $query = "SELECT * FROM coordinate_colonnine_elettriche";
    $result = mysql_query($query) or die("query fallita");

    while($response = mysql_fetch_assoc($result))
        $output[] = $response;
        
    print(json_encode($output));
 
    mysql_close();
?>