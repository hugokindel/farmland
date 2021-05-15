# MOTEUR DE JEU

# FARMLAND

## Gameplay

### Actions du Joueur

#### Description

- Durant le tour du joueur ont a le choix entre plusieurs actions :
    - finir le tour : met fin au tour actuel
    - menu principal : retour au menu principal et sauvegarde la partie
    - inventaire : inventaire des graines et bébés animaux
    - marché : acheter des graines/bébés aniamux OU vendre les ressources qui ont pousser/atteint la maturité
    - caravanes : envoie des caravanes (limité a la moitié des ressources possedé) OU verifier l'avancé des caravanes envoyés
    - recherches : améliorer la recherche d'Eleveur (pour les animaux) ou de Fermier (pour les plantes) afin d'augmenter les gains des caravanes
    - banque :  prendre un prêt (limité a 1 prêt a la fois) OU rembourser une partie manuellement (remboursement d'une partie systematiquement a chaque tour)

#### Implémentation



### Système de robot (intelligence artificielle)

#### Description

- Les robots peuvent faire les mêmes actions que les joueurs c'est-à-dire : 
    - acheter un terrain, acheter des items, vendre son inventaire, envoyer des caravanes, améliorer les recherches et prendre des prêts
- leurs actions pendant les tours dependent de leurs difficultés :
    - facile : achete 1 terrain ou 1 item (de manière aléatoire) puis vend son inventaire
    - normal : achete 1 terrain ou autant d'items different que de terrains vide (si il y en existe) puis vend son inventaire
    - difficile : achete 1 terrain ou ameliore les recherches (de manière aléatoire) ou autant d'items identiques que de terrains vide (si il y en existe) puis envoie des caravanes (si il peut)  puis vend son inventaire
    - impossible : exactement les mêmes actions que la difficulté "difficile" mais à chaque tour si il n'a pas de prêt alors il y a 1 chance sur 2 qu'il en prenne 1


#### Implémentation

- amélioration des recherches : améliore la recherche qui benefie au premier item des terrains du bot

### Bonus 

- Le cadre du joueur evolue avec l'avancé des recherches par palier (palier 0 : niveau 1 / palier 1 : niveau 3 / palier 2 : niveau 5)