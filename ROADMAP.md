# Feuille de route de Farmland

En l'état, les dates indiqués font office de deadline (à une semaine près maximum) sauf pour les dernières semaines où les commits étaient concentrés sur le rendu final.

### Semaine 1 (jusqu'au 12 février)

Majoritairement une mise en place du moteur, pour ensuite pouvoir développer le jeu plus sereinement.

- Système d'entrée (LE CORRE Léo) **[FAIT]**
	- Support des boutons de la souris/du clavier (appuyé, relaché, etc.)
	- Support du défilement de la souris

- Système de rendu graphique (KINDEL Hugo) **[FAIT]**
	- Afficher des textures à l'écran
	- Afficher des interfaces de déboguage (ImGui)
	- Déplacer une caméra
		- Déplacement horizontal/vertical
		- Zoom

- Système de scène (possibilité de changer la scène du jeu, etc.) (LE CORRE Léo) **[FAIT]**

- Système d'entité (KINDEL Hugo) **[FAIT]**
	- Créer/tuer des entités
	- Affecter des donnés aux entités (= des composants)
	- Intéragir avec les entités et leurs données (= des systèmes)

- Système d'événement (subscriber/listener) (JAUROYON Maxime) **[FAIT]**

- Support du format JSON (PAULAS VICTOR Francis)
	- Lecture/écrite
	- Sérialization/désérialization

- Système de lecture d'options de ligne de commande (ex: --exemple) (KINDEL Hugo) **[FAIT]**

- Mise en place de l'intégration continue (JAUROYON Maxime) **[FAIT]**

- Amélioration du README du projet (PAULAS VICTOR Francis) **[FAIT]**

### Semaine 2 (jusqu'au 19 février)

Amélioration du moteur et début du développement du jeu.

- Création/recherche de quelques textures (JAUROYON Maxime et PAULAS VICTOR Francis) **[FAIT]**

- Système de rendu graphique (KINDEL Hugo) **[FAIT]**
	- Afficher des textes à l'écran
	- Afficher des formes (carré, rond, ...) à l'écran

- Système de niveau (JAUROYON Maxime) **[FAIT]**
	- Mise en place d'un niveau composé d'un certains nombres de carré qui compose le terrain de jeu
	- Possibilité de placer des entités relatifs au jeu sur la carte
		- Ressources (ex: carotte, patate, mouton)
		- Propriétés (ex: parcelle, ferme)
		- Décoration (ex: arbre, montagne)

- Création de scène pour naviguer entre les différents menus (pour le moment réaliser avec ImGui) (PAULAS VICTOR Francis) **[FAIT]**
	- Menu principal
	- Menu option
	- Menu solo
	- Menu multijoueur
	- Scène en jeu (niveau visible)

- Création d'un système de joueur (possède un nom, nom de ferme) (LE CORRE Léo) **[FAIT]**
	- Possibilité de changer les informations dans un menu
	- Afficher les informations à l'écran lors de la scène de jeu

- Amélioration du système d'entrée (PAULAS VICTOR Francis) **[FAIT]**
	- Refactorisation d'une partie du code
	- Ajout de constante pour chaque touche pour rendre la vérification d'entrée plus lisible
	
- Amélioration du système de caméra (KINDEL Hugo) **[FAIT]**
	- Refactorisation d'une partie du code
	- Possibilité d'obtenir une position du monde à partir de la position du curseur sur l'écran (World Coord -> Screen Coord)

### Semaine 3 (jusqu'au 26 février)

Continuation du développement du jeu pour faire apparaître une base de gameplay (+ améliorations du moteur)

- Système audio (LE CORRE Léo) **[FAIT]**
	- Lire des musiques/effets sonores

- Création d'un système simple d'interface graphique (KINDEL Hugo) **[FAIT]**
  	- Texte
	- Bouton
	- Curseur

- Création d'un début de système économique (LE CORRE Léo et PAULAS VICTOR Francis) **[FAIT]**

- Mise en place d'un système de tour (PAULAS VICTOR Francis et JAUROYON Maxime) **[FAIT]**

- Continuation du système de niveau (JAUROYON Maxime) **[FAIT]**
	- Refactorisation du code pour une base plus saine et mieux lié au reste des systèmes
	
- Mettre en place un système de sauvegarde pour enregistrer les cartes de jeux et les objets placés (KINDEL Hugo) **[FAIT]**

### Semaine 4 (jusqu'au 5 mars)

Apparition du gameplay et du jeu en multijoueur.

- Refactorisation de certaine partie du moteur pour rendre le code plus lisible et mieux documenté (KINDEL Hugo et LE CORRE Léo) **[FAIT]**

- Mise en place du marché et amélioration du système économique (PAULAS VICTOR Francis et LE CORRE Léo) **[FAIT]**
	- Payer lorsque l'on achète des ressources

- Mise en place d'une base de donnée de ressources (JAUROYON Maxime) **[FAIT]**

- Amélioration des menus (toute l'équipe) **[FAIT]**

- Création des bases du système multijoueur (KINDEL Hugo) **[FAIT]**

### Semaine 5 (jusqu'au 12 mars)

- Attribuer des ressources sur les grilles de la carte (JAUROYON Maxime) **[FAIT]**

- IA basique de robot (Capable de finir un tour) (PAULAS VICTOR Francis) 

- Ecran de game over si l'argent du joueur passe en négatif (LE CORRE Léo) **[FAIT]**

- Ecran de victoire si l'argent du joueur dépasse une certaine somme (LE CORRE Léo) **[FAIT]**

### Semaine 6 (jusqu'au 19 mars)

Semaine de partiel (peu de travail effectué), amélioration du gameplay.

- Faire grandir les ressources à chaque tour (JAUROYON Maxime) **[FAIT]**

- Possibilité pour le bot de réaliser des actions (acheter du terrain, acheter et poser des ressources) (PAULAS VICTOR Francis) **[FAIT]**

- Amélioration de certaines parties de l'interface graphique (LE CORRE Léo) **[FAIT]**

- Tester le projet pour fixer des bugs et avancer le code réseau (KINDEL Hugo) **[FAIT]**

### Semaine 7 (jusqu'au 26 mars)

- Continuation du travail sur le système économique (LE CORRE Léo et PAULAS VICTOR Francis) **[FAIT]**

- Gagner de l'argent à chaque tour et payer pour chaque propriété possédé (JAUROYON Maxime) **[FAIT]**

- Synchronisation complète multijoueur entre les différents joueur en local (KINDEL Hugo) **[FAIT]**

### Semaine 8 (jusqu'au 2 avril)

- Synchronisation complète multijoueur entre les différents joueur en ligne (KINDEL Hugo)

- Amélioration de certaines parties de l'interface graphique (KINDEL Hugo)

- Optimisation des composants du gameplay (PAULAS VICTOR Francis et JAUROYON Maxime)

- Mise à niveau de la taille de certaines ressources (ex: les animaux) qui dépasse de leur case (PAULAS VICTOR Francis) **[FAIT]**

- Amélioration de l'affichage des composantes (JAUROYON Maxime) **[FAIT]**

- Possibilité de placer des décorations **[Facultatif]**

- Système de troupeau pour les animaux (peut-être ?) **[Facultatif]**

### Semaine 9 (jusqu'au 9 avril)

- Ajout d'un système de recherche, de caravane (JAUROYON Maxime) **[FAIT]**

- Création d'un avatar personnalisable (JAUROYON Maxime) **[FAIT]**

- Passage du protocole UDP à TCP pour le multijoueur (KINDEL Hugo) **[FAIT]**

- Finition du système économique (LE CORRE Léo) **[FAIT]**

### Semaine 10 (jusqu'au 16 avril)

- Choix de la langue utilisée (PAULAS VICTOR Francis) **[FAIT]**

- Ajout d'un système d'emprunt (LE CORRE Léo) **[FAIT]**

- Optimisation du code du gameplay (JAUROYON Maxime) **[FAIT]**

### Semaine 11-12-13 (jusqu'au 9 mai)

- Ajout de la difficulté des bots (JAUROYON Maxime) **[FAIT]**

- Amélioration du système de langue (PAULAS VICTOR Francis) **[FAIT]**

- Création d'un système de commande (LE CORRE Léo et Hugo Kindel) **[FAIT]**

- Ajout de la console pour communiquer (chat) et pour l'exécution des commandes (LE CORRE Léo et Hugo Kindel) **[FAIT]**

- Optimisation du code, correction de bug et optimisation du projet (KINDEL Hugo, JAUROYON Maxime, PAULAS VICTOR Francis et LE CORRE Léo) **[FAIT]**

### Semaine 14 (jusqu'au 16 mai)

- Optimisation du code, correction de bug et optimisation du projet (KINDEL Hugo, JAUROYON Maxime, PAULAS VICTOR Francis et LE CORRE Léo) **[FAIT]**

- Ajout des fichiers de rendu (KINDEL Hugo, JAUROYON Maxime, PAULAS VICTOR Francis et LE CORRE Léo) **[FAIT]**