# Feuille de route de Farmland

En l'état, les dates indiqués font office de deadline (à une semaine près maximum).

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

- Amélioration du système d'entrée (PAULAS VICTOR Francis)
	- Refactorisation d'une partie du code
	- Ajout de constante pour chaque touche pour rendre la vérification d'entrée plus lisible
	
- Amélioration du système de caméra (KINDEL Hugo)
	- Refactorisation d'une partie du code
	- Possibilité d'obtenir une position du monde à partir de la position du curseur sur l'écran (World Coord -> Screen Coord)
	

### Semaine 3 (jusqu'au 26 février)

Continuation du développement du jeu pour faire apparaître une base de gameplay (+ améliorations du moteur).

- Système audio (LE CORRE Léo) **[FAIT]**
	- Lire des musiques/effets sonores

- Création d'un système simple d'interface graphique (KINDEL Hugo) **[WIP]**
	- Bouton
	- Curseur

- Création d'un système économique (LE CORRE Léo et PAULAS VICTOR Francis) **[WIP]**

- Mise en place d'un système de tour (PAULAS VICTOR Francis et JAUROYON Maxime) **[WIP]**

- Continuation du système de niveau (JAUROYON Maxime) **[WIP]**
	- Possibilité d'ajouter des éléments (ressources, ...) sur la carte en cliquant avec la souris.
	- Refactorisation du code pour une base plus saine et mieux lié au reste des systèmes.
	


### Semaine 4 (jusqu'au 4 mars)

- Refactorisation de certaine partie du moteur pour rendre le code plus lisible et mieux documenté (KINDEL Hugo)

- Mise en place du marché

- Mise en place du tour d'un robot