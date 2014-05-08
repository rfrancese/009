<?php
    require_once('geocoding.php');
    require_once('database-connection.php');
    
    $handle = fopen('addresses.txt', 'r');
    if ($handle) {
        ini_set('max_execution_time', 300);
        $success = 0;
        $failed  = 0;
        $geocoding_error = 0;
        
        echo "<b>Apertura del file addresses.txt effettuata con successo.</b>\n<br>";
        echo "<b>Lettura dei record in corso...</b>\n</p>";
        while(!feof($handle)) {
            $buffer = fgets($handle);
            if ($buffer != '') {
                $array          = explode('&', $buffer);
                $address        = urlencode($array[0]);
                $provider       = $array[1];
                $jacks          = $array[2];
                $numStations    = $array[3];
                $description    = trim($array[4]);
                
                if ($description == '')
                    $description = 'NULL';
                else
                    $description = "'".$description."'";

                $location = geocoder::getLocation($address);
                if ($location != false && $location['formatted-address'] != '') {
                    $latitude = $location['latitude'];
                    $longitude = $location['longitude'];
                    $formatted_address = htmlentities($location['formatted-address'], ENT_QUOTES, 'UTF-8');
                
                    echo $address."\n<br>".$formatted_address."\n<br>".$provider."\n<br>".$jacks."\n<br>".$latitude."\n<br>".$longitude."\n<br>".$numStations."\n<br>".$description."\n<br>";
                
                    $sql = "INSERT INTO coordinate_colonnine_elettriche VALUES ('".$formatted_address."', '".$provider."', '".$jacks."', '".$latitude."', '".$longitude."', '".$numStations."', ".$description.")";
                
                    if ($result = mysqli_query($db, $sql)) {
                        echo "\n<b>Caricamento completato con successo.</b>\n</p>";
                        $success++;
                    } else {
                        echo "\n<b>Caricamento fallito.</b>\n</br>";
                        $failed++;
                        
                        $handleLogger = fopen('logger.txt', 'a');
                        if($handleLogger) {
                            $string = "[Caricamento Fallito]\t";
                            $string .= $formatted_address.' '.$provider.' '.$jacks.' '.$latitude.' '.$longitude.' '.$numStations.' '.$description;
                            
                            if(fwrite($handleLogger, $string) != false) {
                                echo "\n<b>Record salvato nel file logger.txt</b>\n</p>";
                            } else {
                                echo "\n<b>Salvataggio fallito</b>\n</p>";
                            }
                        }
                    
                        fclose($handleLogger);
                    }
                } else {
                    echo "\n<b>Geocoding fallito per l\'indirizzo: ".$address.".</b>\n<br>";
                    $geocoding_error++;
                    
                    $handleLogger = fopen('logger.txt', 'a');
                    if($handleLogger) {
                        $string = "[Geocoding Fallito]\t\t";
                        $string .= implode('&', $array);
                        
                        if(fwrite($handleLogger, $string) != false) {
                            echo "\n<b>Record salvato nel file logger.txt</b>\n</p>";
                        } else {
                            echo "\n<b>Salvataggio fallito</b>\n</p>";
                        }
                    }
                    
                    fclose($handleLogger);
                }
            }
        }
        echo "\n</p>\n<b>Operazione Completata.</b>\n<br>";
        echo "\nRecord caricati con successo: ".$success."\n<br>";
        echo "\nCaricamenti falliti: ".$failed."\n<br>";
        echo "\nGeocoding falliti: ".$geocoding_error."\n<br>";
        fclose($handle);
    } else {
        echo "<b>Apertura del file addresses.txt fallita.</b>";
    }
    
    mysqli_close($db);
?>