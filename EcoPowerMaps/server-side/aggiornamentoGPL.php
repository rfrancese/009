<?php

set_time_limit (5000); //funzione che mi denota i secondi massimi di attesa 30 sec default

include('simple_html_dom.php');

include('Connessione_Database.php');

$data=date('d/m/Y');

$str="select link from province_gpl";

$ris=mysqli_query($db,$str); 
        

while($r=mysqli_fetch_row($ris)){
    
     $link=$r[0]; //ho il mio link
     
     $selProv="select provincia from province_gpl where link='".addslashes($link)."'";

     $risProv=mysqli_query($db,$selProv);
     
     $risultato=mysqli_fetch_row($risProv);
     
     $prov=$risultato[0]; //ho la mia provincia
        
        //accedo al mio link
        $info=file_get_html($link);
        $div3=$info->getElementById('listadistributori');
        $es3=$div3->find('table td[width=180]');
        
        for($y=0;$y<count($es3);$y++){
    
                $linkIndirizzo=$es3[$y]->find('a');
                $indirizzo=$linkIndirizzo[1]->plaintext; //ho l'indirizzo rimane il prezzo
                $p=$div3->find('table tr[valign=top]');
                $pr=$p[$y]->find('td');
                $prezzo=$pr[1]->plaintext;
                $prezzoTagliato=substr($prezzo,0,4); //Ho il mio prezzo attuale
                
                $str2="select prezzo from coordinate_gpl where indirizzo='".addslashes($indirizzo)."'";
                $ris2=mysqli_query($db,$str2);
                
                while($r2=mysqli_fetch_row($ris2)){
                    
                   // echo $prov."=> ".$indirizzo.": ".$r2[0]."=>".$prezzoTagliato."<br>";
                
                if(strcmp($r2[0],$prezzoTagliato) !=0){
                    
                   // echo"Sto aggiornando<br>";
                    
                    $str3="update coordinate_gpl set prezzo='".$prezzoTagliato."' where indirizzo='".addslashes($indirizzo)."' and province='".addslashes($prov)."'";
                    $ris4=mysqli_query($db,$str3); 
                    
                 /*   $stringa="Ho aggiornato il ".$data.": ".$indirizzo." di ".$prov." da ".$r2[0]." a ".$prezzoTagliato;
                    
                    $str4="insert into aggiornamento (info) values ('".addslashes($stringa)."')";
                    $ris5=mysqli_query($db,$str4);  */
                    
                }
               }//fine while
               
        }
        
}

$stringa="Ho aggiornato il ".$data." il gpl con successo";
                    
$str4="insert into aggiornamento (info) values ('".addslashes($stringa)."')";
$ris5=mysqli_query($db,$str4);

//echo "Prezzi aggiornati con successo!";

?>