# Rapport de logique — Socle commun (Phase 0)

Ce document explique **ce qui a été codé**, **comment les morceaux interagissent** et **comment faire tourner le tout**. Il s'adresse aux 3 membres de l'équipe pour qu'ils puissent construire leur partie par-dessus ce socle.

---

## 1. À quoi sert la Phase 0

Les 3 parties du projet (Site Web, Services Web SOAP/REST, Application Client) ont toutes besoin des **mêmes briques de base** :

- les **objets métier** (un article, une catégorie, un utilisateur, un jeton) ;
- la **base de données** qui stocke ces objets ;
- le **code qui lit/écrit dans la base** (les DAO).

Si chaque membre réinventait ça de son côté, rien ne s'emboîterait. La Phase 0 fournit donc ce socle **une seule fois, pour tout le monde**.

---

## 2. Les couches et le sens de circulation

Le projet suit une architecture **en couches**. L'information circule toujours dans le même sens :

```
  Navigateur / Client SOAP / Client REST
                  │
                  ▼
        Controller / Service web        (reçoit la demande)
                  │
                  ▼
            Service (métier)            (décide quoi faire)
                  │
                  ▼
              DAO (accès BD)            (lit / écrit en base)
                  │
                  ▼
          Base de données MySQL
```

**Règle d'or :** une couche ne parle qu'à la couche **juste en dessous**. Un contrôleur ne touche JAMAIS la base directement ; il passe par un Service, qui passe par un DAO.

La Phase 0 livre les **deux couches du bas** : `model` (les objets) et `dao` (l'accès BD). Les couches `service`, `controller`, `rest`, `soap` seront remplies par les membres 1, 2 et 3.

---

## 3. Les objets métier (package `model`)

Ce sont de simples « boîtes à données » (un identifiant, des champs, des getters/setters). Aucun calcul, aucune logique.

| Classe | Représente | Champs principaux |
|--------|-----------|-------------------|
| `Role` | Le profil d'un utilisateur | `VISITEUR`, `EDITEUR`, `ADMINISTRATEUR` (enum) |
| `Categorie` | Une rubrique | `id`, `nom` |
| `Utilisateur` | Un compte (éditeur/admin) | `id`, `login`, `motDePasse`, `role` |
| `Article` | Un article | `id`, `titre`, `contenu`, `datePublication`, `categorie`, `auteur` |
| `Jeton` | Un jeton d'authentification | `id`, `valeur`, `utilisateur`, `dateCreation`, `actif` |

**Points importants à comprendre :**

- Un `Article` **contient** un objet `Categorie` complet et un objet `Utilisateur` (l'auteur), pas juste des numéros. Quand on lit un article en base, le DAO va chercher en même temps le nom de la catégorie et l'auteur (via une jointure SQL). Résultat : pour afficher « Sport — par editeur », pas besoin de refaire des requêtes.
- Un `Jeton` **contient** l'`Utilisateur` à qui il appartient. Idem : quand on présente un jeton, on sait immédiatement de qui il s'agit.
- `Role` est un **enum** : impossible d'écrire un rôle qui n'existe pas, le compilateur l'interdit.

---

## 4. La base de données (`schema.sql`)

Quatre tables qui reflètent les objets ci-dessus :

```
categorie (id, nom)
utilisateur (id, login, mot_de_passe, role)
article (id, titre, contenu, date_publication, categorie_id → categorie, auteur_id → utilisateur)
jeton (id, valeur, utilisateur_id → utilisateur, date_creation, actif)
```

Les flèches `→` sont des **clés étrangères** : un article pointe vers sa catégorie et son auteur ; un jeton pointe vers son utilisateur. La base **refuse** un article sans catégorie valide, ou un jeton sans utilisateur : elle garantit la cohérence.

Le fichier crée aussi des **données d'exemple** pour tester tout de suite :

- 3 catégories : Politique, Sport, Technologie ;
- 2 comptes : `admin` / `admin123` (administrateur) et `editeur` / `editeur123` (éditeur) ;
- 3 articles.

> Note : les mots de passe sont en clair, choix assumé pour simplifier le projet scolaire.

---

## 5. La connexion à la base (`dao/ConnexionBD`)

Une seule classe, une seule méthode : `ConnexionBD.obtenir()` renvoie une connexion JDBC vers MySQL. Tous les DAO l'appellent. Si un jour on change d'adresse, de login ou de mot de passe de la base, **on ne modifie qu'ici** (les constantes `URL`, `UTILISATEUR`, `MOT_DE_PASSE`).

---

## 6. Les DAO (package `dao`)

DAO = *Data Access Object*. Chaque DAO sait lire/écrire **un type d'objet** en base. C'est le **seul** endroit du projet où l'on écrit du SQL.

| DAO | Méthodes fournies |
|-----|-------------------|
| `CategorieDao` | `listerToutes`, `trouverParId`, `creer`, `modifier`, `supprimer` |
| `UtilisateurDao` | `listerTous`, `trouverParId`, `trouverParLogin`, `creer`, `modifier`, `supprimer` |
| `ArticleDao` | `lister(limite, decalage)`, `listerTous`, `listerParCategorie`, `trouverParId`, `compter`, `creer`, `modifier`, `supprimer` |
| `JetonDao` | `listerTous`, `trouverParValeur`, `creer`, `definirActif`, `supprimer` |

**Comment marche un DAO, concrètement :**

1. il prend une connexion (`ConnexionBD.obtenir()`),
2. il prépare une requête SQL avec des `?` (paramètres),
3. il remplit les `?` avec les valeurs (protège contre les injections SQL),
4. il exécute, et pour les lectures il **transforme chaque ligne du résultat en objet Java** (méthode privée `construire(...)`).

Tout est dans des blocs `try (...)` : la connexion se **ferme automatiquement** à la fin, même en cas d'erreur. Pas de fuite de connexion.

**Détails utiles pour les autres membres :**

- `ArticleDao.lister(limite, decalage)` sert à la **pagination** de la page d'accueil (Membre 1). Combiné à `compter()`, il permet de calculer « page suivante / précédente ».
- `ArticleDao.listerParCategorie(...)` sert à la page « articles d'une catégorie » (Membre 1) **et** au service REST (Membre 2).
- `UtilisateurDao.trouverParLogin(...)` sert à l'authentification (vérifier login + mot de passe) — utile aux Membres 2 et 3.
- `JetonDao.trouverParValeur(...)` est la clé de la **sécurité par jeton** : le filtre de sécurité (Membre 3) appellera cette méthode pour vérifier qu'un jeton existe et est `actif`.

---

## 7. Exemple de bout en bout (ce que feront les autres membres)

Imaginons un visiteur qui ouvre la page d'accueil :

1. Le navigateur appelle `AccueilServlet` (Membre 1).
2. Le servlet demande au `ArticleService` les articles de la page 1.
3. Le service appelle `articleDao.lister(10, 0)` et `articleDao.compter()`.
4. Le DAO interroge MySQL, récupère 10 articles (avec leur catégorie + auteur déjà remplis).
5. Le servlet place la liste dans la requête et affiche `accueil.jsp`.
6. La JSP boucle sur les articles et affiche titre, catégorie, date.

**Tout ce qui est en gras dans les étapes 3-4 (DAO + objets) est déjà fait par la Phase 0.** Les membres n'ont plus qu'à brancher les couches du dessus.

---

## 8. Comment installer et tester

**Prérequis :** Java 17+, Maven, MySQL 8, Tomcat 10+ (obligatoirement 10+ car le projet utilise `jakarta.*`, pas `javax.*`).

1. **Créer la base** : exécuter `src/main/resources/schema.sql` dans MySQL
   (ex. `mysql -u root -p < src/main/resources/schema.sql`).
2. **Vérifier la connexion** : adapter si besoin `URL`, `UTILISATEUR`, `MOT_DE_PASSE` dans `dao/ConnexionBD.java`.
3. **Compiler** : `mvn clean package` → produit `target/actualite.war`.
4. **Déployer** : copier `actualite.war` dans le dossier `webapps/` de Tomcat.

> ⚠️ Maven n'était pas installé sur la machine au moment de l'écriture de ce rapport. La compilation des couches `model` + `dao` a tout de même été vérifiée avec `javac` (succès) : ces deux couches ne dépendent que du JDK standard. La compilation complète via Maven reste à lancer une fois Maven installé.

---

## 9. Ce qui est livré et ce qui reste

**Livré (Phase 0) :**
- `model/` : Role, Categorie, Utilisateur, Article, Jeton
- `dao/` : ConnexionBD + CategorieDao, UtilisateurDao, ArticleDao, JetonDao
- `schema.sql` (tables + données d'exemple)
- `web.xml` (Jakarta EE 10) et `pom.xml` complété (driver MySQL en `runtime`, JSTL ajouté)

**À faire ensuite :**
- **Membre 1** : couche `service` (articles/catégories) + `controller` (servlets) + vues JSP.
- **Membre 2** : services `soap` (utilisateurs + auth) et `rest` (articles XML/JSON).
- **Membre 3** : `AuthService`, gestion des jetons, `JetonFilter` et l'application `client/`.

Tous s'appuient sur les DAO et les objets de cette Phase 0.
