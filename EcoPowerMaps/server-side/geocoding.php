<?php
    //Geocoding Test
    
    class geocoder {
        static private $url = "http://maps.google.com/maps/api/geocode/json?sensor=false&address=";
   
        static public function getLocation($address) {
            $url = self::$url.urlencode($address);
        
            $jsonResponse = self::curl_file_get_contents($url);
            $response = json_decode($jsonResponse, true);

            if($response['status'] = 'OK') {
                //$address = explode(',', $response['results'][0]['formatted_address']);
                //print_r($address);
                $info = array(
                    'formatted-address' => $response['results'][0]['formatted_address'],
                    'latitude' => $response['results'][0]['geometry']['location']['lat'],
                    'longitude' => $response['results'][0]['geometry']['location']['lng']
                );
                return $info;
            } else {
                return false;
            }
        }

        static private function curl_file_get_contents($url){
            //Sessione CURL
            $curl = curl_init();
            curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
            curl_setopt($curl, CURLOPT_URL, $url);
            if (! $contents = curl_exec($curl)) {
                trigger_error(curl_error($curl));
                curl_close($curl);
                return false;
            } else {
                curl_close($curl);
                return $contents;
            }
        }
    }
    
    /********************* TEST ******************************************************************************************
    $address = urlencode("corso vittorio veneto 4 bari");
    $location = geocoder::getLocation($address);
    
    //echo '<br>';
    print_r($location);

    $latitude = $location['latitude'];
    $longitude = $location['longitude'];
    $formatted_address = $location['formatted-address'];
    
    echo '<br>Latitude='.$latitude.'<br>Longitude='.$longitude.'<br>Formatted Address='.htmlentities($formatted_address);
    **********************************************************************************************************************/
?>