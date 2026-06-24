# Plan de réalisation — Répartition des tâches (3 membres)

Projet d'Architecture Logicielle — Site d'actualité (MVC + services web SOAP/REST + application client).

## Principe

Le projet a **3 parties** qui correspondent naturellement à **3 membres**. Mais elles
reposent sur un **socle commun** (modèle de données + base de données + DAO) qui doit
être fait **en premier et ensemble**, sinon chacun est bloqué.

```
            ┌─────────────────────────────┐
            │  PHASE 0 — Socle commun     │  (les 3 membres ensemble)
            │  modèle, BDD, DAO, structure│
            └──────────────┬──────────────┘
                           │
        ┌──────────────────┼──────────────────┐
        ▼                  ▼                  ▼
   Membre 1            Membre 2            Membre 3
   Site Web MVC        Services Web        Application Client
                       (SOAP + REST)       + Sécurité/Jetons
```

---

## Phase 0 — Socle commun (les 3 membres, à faire d'abord)

| Tâche | Fichiers concernés |
|-------|--------------------|
| Modélisation des entités | `model/Article`, `Categorie`, `Utilisateur`, `Role`, `Jeton` |
| Schéma de la base de données | `src/main/resources/schema.sql` |
| Connexion JDBC | `dao/ConnexionBD` |
| Couche DAO (CRUD) | `dao/ArticleDao`, `CategorieDao`, `UtilisateurDao`, `JetonDao` |
| Mise en place Maven / Tomcat | `pom.xml`, `web.xml` |

> ⚠️ Tant que cette phase n'est pas finie, les 3 membres ne peuvent pas avancer
> sereinement. À faire en binôme/trinôme sur 2-3 jours.

---

## Membre 1 — Site Web (Partie 1 : MVC)

**Objectif :** toute l'interface web consultée depuis le navigateur.

| Tâche | Fichiers |
|-------|----------|
| Page d'accueil + pagination (suivant/précédent) | `controller/AccueilServlet`, `views/accueil.jsp` |
| Détail d'un article | `controller/ArticleServlet`, `views/article-detail.jsp` |
| Articles par catégorie | `controller/CategorieServlet`, `views/categorie.jsp` |
| Authentification (login éditeur/admin) | `controller/LoginServlet`, `views/login.jsp` |
| Espace admin : gestion articles | `controller/admin/GestionArticlesServlet`, `views/admin/articles.jsp` |
| Espace admin : gestion catégories | `controller/admin/GestionCategoriesServlet`, `views/admin/categories.jsp` |
| Espace admin : gestion utilisateurs | `controller/admin/GestionUtilisateursServlet`, `views/admin/utilisateurs.jsp` |
| Mise en forme (CSS) | `webapp/css/style.css` |
| Logique métier articles/catégories | `service/ArticleService`, `service/CategorieService` |

**Livrable :** un site fonctionnel avec les 3 profils (visiteur / éditeur / administrateur).

---

## Membre 2 — Services Web (Partie 2 : SOAP + REST)

**Objectif :** exposer les fonctionnalités métier à d'autres applications.

| Tâche | Fichiers |
|-------|----------|
| Service SOAP — gestion utilisateurs (CRUD, protégé par jeton) | `soap/UtilisateurSoapService` |
| Service SOAP — authentification (login/mot de passe) | `soap/AuthSoapService` |
| Service REST — liste de tous les articles (XML/JSON) | `rest/ArticleRestService`, `rest/RestApplication` |
| Service REST — articles groupés par catégorie | `rest/ArticleRestService` |
| Service REST — articles d'une catégorie donnée | `rest/ArticleRestService` |
| Sérialisation XML **et** JSON au choix | configuration JAX-RS |
| Tests des services (SoapUI / Postman) | `docs/` (captures, collection) |

**Livrable :** services SOAP et REST déployés et testables, REST renvoyant XML ou JSON au choix.

---

## Membre 3 — Application Client + Sécurité (Partie 3)

**Objectif :** application Java de gestion des utilisateurs + tout le système de jetons.

| Tâche | Fichiers |
|-------|----------|
| Logique d'authentification + droits | `service/AuthService` |
| Gestion des jetons (génération/révocation par admin) | `controller/admin/GestionJetonsServlet`, `views/admin/jetons.jsp` |
| Filtre de sécurité (vérification jeton) | `util/JetonFilter` |
| Logique métier utilisateurs | `service/UtilisateurService` |
| Application client — point d'entrée | `client/MainClient` |
| Client — écran de connexion (login/mot de passe) | `client/ui/LoginFrame` |
| Client — écran gestion utilisateurs (CRUD) | `client/ui/GestionUtilisateursFrame` |
| Client — appel du service SOAP | `client/ws/SoapClient` |
| Build du client | `client/pom.xml` |

**Livrable :** application Java qui s'authentifie via SOAP, vérifie les droits admin et gère les utilisateurs.

---

## Planning indicatif

| Semaine | Objectif |
|---------|----------|
| S1 | Phase 0 (socle commun) terminée |
| S2 | Chaque membre développe sa partie en parallèle |
| S3 | Intégration (le client appelle les services, le site utilise tout) + tests |
| S4 | Qualité du code, documentation, finalisation, envoi |

## Bonnes pratiques (la qualité du code est notée)

- Travailler avec des **branches Git** par membre (`feature/site-web`, `feature/services`, `feature/client`) puis fusionner via Pull Request.
- **Commits réguliers** et messages clairs.
- Respecter la séparation **MVC / couches** (un contrôleur n'accède jamais directement à la BDD : il passe par `service` → `dao`).
- Relire le code des autres avant fusion.
