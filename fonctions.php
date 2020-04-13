<?php
	function connexionPDO(){
		$login = "rapace";
		$mdp = "rapace";
		$bd = "rapace";
		$serveur = "localhost";

		try{
			$connexion = new PDO("mysql:host=$serveur;dbname=$bd", $login, $mdp);
			return $connexion;
		}catch(PDOException $e){
			print "Erreur de connexion PDO";
			die();
		}
	}
?>