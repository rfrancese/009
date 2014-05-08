<?php

// Create DOM from URL or file

set_time_limit (1000); //funzione che mi denota i secondi massimi di attesa 30 sec default

include('simple_html_dom.php');

include('Connessione_Database.php');

$html = file_get_html('http://www.ecomotori.net/distributori/gpl/italy');

$div=$html->getElementById('listadistributori');

$regioni=array();
$nomiRegioni=array();

$es = $div->find('table td[width=180]'); //dovrebbero esserci le 20 regioni escluso san marino

for($i=0;$i<count($es)-1;$i++){

$link=$es[$i]->find('a');

$stringa=substr($link[0],9,38);

$regioni[$i]="http://www.ecomotori.net".$stringa;  //qui ci sono tutti i link relativi alle regioni

$nomiRegioni[$i]=$link[0]->plaintext; //qui ci sono i nomi delle regioni

echo $regioni[$i]." --> ".$nomiRegioni[$i]."<br>";

   }




echo "Ho finito di analizzare le regioni<br>";

echo "ora analizzo le province di <br>";


$provincia=array(); //mi creo un array dove mettero' i link delle province per ogni regione

for($n=0;$n<count($nomiRegioni);$n++){  //scorro i miei nomi delle regioni

$nome=$nomiRegioni[$n]; //mi salvo la regione corrente

$regione=file_get_html($regioni[$n]); //mi prendo il link della regione corrente


$nomeProvincia=array(); //mi creo un array dove mettero' i nomi delle province per ogni regione

$div2=$regione->getElementById('listadistributori');

$es2=$div2->find('table td[width=180]');

echo"<br>Ora stampo i link delle province<br>";


for($j=0;$j<count($es2);$j++){
    
    $linkProvincia=$es2[$j]->find('a');
    $stringaProvincia=substr($linkProvincia[0],9,54);
    $nomeProvincia[$j]=$linkProvincia[0]->plaintext;
    $l="http://www.ecomotori.net".$stringaProvincia;
    
    echo $nome."->".$nomeProvincia[$j]."<br>";
    
  /*  $str="insert into province_gpl (regione,provincia,link) values ('".addslashes($nome)."','".addslashes($nomeProvincia[$j])."','".addslashes($l)."')";

    $ris=mysqli_query($db,$str); */

  }

}



//fino a qui ho inserito regione province e i corrispondenti LINK TUTTO OKOKOKOK


//ora devo scorrere i nomi delle province, devo accedere ai loro link e trovare le info necessarie

$nomeProvincia=array();
$i=0;


$str="select provincia from province_gpl";

$ris=mysqli_query($db,$str); 


while($r=mysqli_fetch_row($ris)) {

$nomeProvincia[$i]=$r[0];
$i++;

}

//ora in nomeProvincia ci sono tt le province non mi resta che scorrerle prendermi i link e le info


for($k=0;$k<count($nomeProvincia);$k++){
    
    
    $primaProvincia=$nomeProvincia[$k];
    
    if($primaProvincia!=""){ //se la mia provincia esiste
        
        
        $str="select link from province_gpl where provincia='".$primaProvincia."'";
        
        $ris=mysqli_query($db,$str); 
        
        while($r=mysqli_fetch_row($ris))
        
        $link=$r[0]; //ho il mio link
        
        
        //accedo al mio link
        $info=file_get_html($link);
        $div3=$info->getElementById('listadistributori');
        $es3=$div3->find('table td[width=180]');
        
           //scorro i miei div trovati che si riferiscono alle varie stazioni
           
           for($y=0;$y<count($es3);$y++){
    
                $linkIndirizzo=$es3[$y]->find('a');
                $indirizzo=$linkIndirizzo[1]->plaintext; //ho l'indirizzo rimane il prezzo
                $p=$div3->find('table tr[valign=top]');
                $pr=$p[$y]->find('td');
                $prezzo=$pr[1]->plaintext;
                $prezzoTagliato=substr($prezzo,0,4);
                
                //mi prendo la lat e la long
                $sezione=$linkIndirizzo[1]->outertext;
                
               //mi faccio i controlli sulla formattazione della latitudine
               
                if(substr($sezione,199,1)=="(")
                        $stringaLatitudine=substr($sezione,201,7);
                
                else if(substr($sezione,198,1)=="k")
                $stringaLatitudine=substr($sezione,202,7);
                
                else 
                $stringaLatitudine=substr($sezione,198,7);
                
                //controlli sulle latitudini
               if(substr($stringaLatitudine,0,1)=="'")
                        $stringaLatitudine=substr($stringaLatitudine,1);
                        
                
                 //mi faccio i controlli sulla formattazione della longitudine       
                
                
                if(substr($sezione,211,1)==",")
                        $stringaLongitudine=substr($sezione,211,7);
                        
                else if(substr($sezione,209,1)==",")
                        $stringaLongitudine=substr($sezione,210,7);
                        
                else if(substr($sezione,214,1)=="'")
                        $stringaLongitudine=substr($sezione,215,7);
                        
                else if(substr($sezione,211,1)=="'")
                        $stringaLongitudine=substr($sezione,212,7); 
                
                else
                        $stringaLongitudine=substr($sezione,209,7);
                        
                        
                //controlli sulle longitudini
                if(substr($stringaLongitudine,0,1)=="'")
                        $stringaLongitudine=substr($stringaLongitudine,1);
                
                if(substr($stringaLongitudine,0,1)==",")
                        $stringaLongitudine=substr($stringaLongitudine,2); 
                
                
                $str="insert into coordinate_gpl (province,indirizzo,prezzo,latitudine,longitudine) values ('".addslashes($primaProvincia)."','".addslashes($indirizzo)."','".addslashes($prezzoTagliato)."','".addslashes($stringaLatitudine)."','".addslashes($stringaLongitudine)."')";
                $ris=mysqli_query($db,$str);
    
       }
        
        
    }
    
    
    
} //for principale 



?>  