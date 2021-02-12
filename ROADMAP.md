# Feuille de route de Farmland

En l'état, les dates indiqués font office de deadline (à une semaine près maximum).

### Semaine 1 (jusqu'au 12 février)

Majoritairement une mise en place du moteur, pour ensuite pouvoir développer le jeu plus sereinement.

- Système d'entrée (LE CORRE Léo)
	- Support des boutons de la souris/du clavier (appuyé, relaché, etc.)
	- Support du défilement de la souris

- Système de rendu graphique (KINDEL Hugo)
	- Afficher des textures à l'écran
	- Afficher des interfaces de déboguage (ImGui)
	- Déplacer une caméra
		- Déplacement horizontal/vertical
		- Zoom

- Système de scène (possibilité de changer la scène du jeu, etc.) (LE CORRE Léo)

- Système d'entité (KINDEL Hugo)
	- Créer/tuer des entités
	- Affecter des donnés aux entités (= des composants)
	- Intéragir avec les entités et leurs données (= des systèmes)

- Système d'événement (subscriber/listener) (JAUROYON Maxime)

- Support du format JSON (PAULAS VICTOR Francis)
	- Lecture/écrite
	- Sérialization/désérialization

- Système de lecture d'options de ligne de commande (ex: --exemple) (KINDEL Hugo)

- Mise en place de l'intégration continue (JAUROYON Maxime)

- Amélioration du README du projet (PAULAS VICTOR Francis)

### Semaine 2 (jusqu'au 19 février)

Amélioration du moteur et début du développement du jeu.

- Création/recherche de quelques textures (JAUROYON Maxime/PAULAS VICTOR Francis)

- Système de rendu graphique (KINDEL Hugo)
	- Afficher des textes à l'écran
	- Afficher des formes (carré, rond, ...) à l'écran

- Système de niveau (JAUROYON Maxime)
	- Mise en place d'un niveau composé d'un certains nombres de carré qui compose le terrain de jeu
	- Possibilité de placer des entités relatifs au jeu sur la carte
		- Ressources (ex: carotte, patate, mouton)
		- Propriétés (ex: parcelle, ferme)
		- Décoration (ex: arbre, montagne)

- Création de scène pour naviguer entre les différents menus (pour le moment réaliser avec ImGui) (PAULAS VICTOR Francis)
	- Menu principal
	- Menu option
	- Menu solo
	- Menu multijoueur
	- Scène en jeu (niveau visible)

- Création d'un système de joueur (possède un nom, nom de ferme) (LE CORRE Léo)
	- Possibilité de changer les informations dans un menu
	- Afficher les informations à l'écran lors de la scène de jeu

### Semaine 3 (jusqu'au 26 février)

- Système audio (KINDEL Hugo)
	- Lire des musiques/fichier audio

- Création d'un système économique

- Mise en place d'un système de tour

### Semaine 4 (jusqu'au 4 mars)

- Mise en place du marché