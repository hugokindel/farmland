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

### Inventaire

#### Description

#### Implémentation


### Marché

#### Description

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


### Caravanes

#### Description

- Le systeme des caravanes consiste en :
    - envoyer la moitié de ses ressources en caravanes afin de gagner plus d'argent qu'au marché local
         - calcul des gains des caravanes : ( ( Prix du Marché locale + Avantage des Recherches ) x 1,5 ) x Quantité envoyé
    - payer 10 pieces pour le cout de la caravane
    - attendre 4 tours avant de récuperer les gains
        
#### Implémentation

- Chaque joueur contient une liste de leurs caravanes qui s'actualise a chaque tour complet 
    - une caravanes contient les gains, le nombre tours effectues, le nombre de tours avant d'arriver, le produit transporter

### Recherches

#### Description

- Le systeme de recherches consiste en :
    - une augmentation des gains obtenu via les caravanes 
    - un niveau refletant l'augmentation 
    - un prix pour augmenter de niveaux

#### Implémentation

- Chaque joueur contient une liste de leurs recherches initialiser a 2 recherches (Eleveur et Fermier) qu'ils peuvent ameliorer :
     - les recherches sont initialisé au niveau 1 avec un prix de 10 pieces pour l'amelioration et une augmentation de 0 piece
     - lorsque l'on augmente une recherche elle gagne 1 niveau, le prix pour la prochaine amelioration augmente de 10 pieces et l'augmentation qu'elle apporte aux caravanes augmente de 1 piece


### Banque

#### Description

#### Implémentation


### Bonus 

- Le cadre du joueur evolue avec l'avancé des recherches par palier (palier 0 : niveau 1 / palier 1 : niveau 3 / palier 2 : niveau 5)
- Le podium affiche les joueurs dans la partie dans l'ordre de leur argent (le classement est realise par un tri par insertion)
- Il y a un compteur de temps restant pour le tour (1 minute et 30 secondes) et aussi un temps total pour la durée de la partie