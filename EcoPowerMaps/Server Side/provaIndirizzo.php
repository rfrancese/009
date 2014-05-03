<?php

set_time_limit (100); //funzione che mi denota i secondi massimi di attesa 30 sec default

include('simple_html_dom.php');

include('Connessione_Database.php');

$html = file_get_html('http://www.ecomotori.net/distributori/metano/italy/?&idregione=19&idprovincia=084');

$div=$html->getElementById('listadistributori');

$es = $div->find('table td[width=180]');

for($i=0;$i<count($es);$i++){

$link=$es[$i]->find('a');
$indirizzo=$link[1]->plaintext;

$p=$div->find('table tr[valign=top]');
$pr=$p[$i]->find('td');

$prezzo=$pr[1]->plaintext;
$prezzoTagliato=substr($prezzo,0,4);


echo $indirizzo."-->".$prezzoTagliato."<br>";

}


for($i=0;$i<count($es);$i++){

$link=$es[$i]->find('a');
$indirizzo=$link[1]->outertext;

$stringaLatitudine=substr($indirizzo,202,9);
$stringaLongitudine=substr($indirizzo,214,9);

echo htmlspecialchars($stringaLatitudine)."-->".htmlspecialchars($stringaLongitudine)."<br>";

}




?>