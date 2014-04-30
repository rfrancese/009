<?php
    require_once('geocoding.php');
    require_once('database-connection.php');
    
    $handle = fopen('addresses.txt', 'r');
    if ($handle) {
        ini_set('max_execution_time', 300);
        while(!feof($handle)) {
            $buffer = fgets($handle);
            if ($buffer != '') {
                $array          = explode('&', $buffer);
                $address        = urlencode($array[0]);
                $provider       = $array[1];
                $jacks          = $array[2];
                $description    = trim($array[3]);
                
                if ($description == '')
                    $description = 'NULL';
                else
                    $description = "'".$description."'";
                    
                $location = geocoder::getLocation($address);
                $latitude = $location['latitude'];
                $longitude = $location['longitude'];
                $formatted_address = $location['formatted-address'];
                
                echo $address.'<br>'.$formatted_address.'<br>'.$provider.'<br>'.$latitude.'<br>'.$longitude.'<br>'.$description.'<br>';
                
                $sql = "INSERT INTO coordinate_colonnine_elettriche VALUES ('".$formatted_address."', '".$provider."', '".$latitude."', '".$longitude."', ".$description.")";
                
                if ($result = mysqli_query($db, $sql)) {
                    echo "Caricamento completato con successo!</p>";
                } else {
                    echo "Caricamento fallito!</p>";
                }
            }
        }
    }
    
    mysqli_close($db);
?>