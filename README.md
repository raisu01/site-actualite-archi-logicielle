# Projet d'Architecture Logicielle — Site d'actualité

Site d'actualité avec services web (SOAP + REST) et application client Java.

> Cahier des charges complet : [`docs/cahier-des-charges.pdf`](docs/cahier-des-charges.pdf)

## Contenu du projet

Le projet est découpé en **trois parties** :

### 1. Site Web (MVC — Servlets / JSP)
- Page d'accueil listant les derniers articles avec pagination (« suivant » / « précédent »).
- Consultation détaillée d'un article (clic sur le titre).
- Consultation des articles par catégorie.
- Trois profils utilisateurs :
  - **Visiteur** : consultation des articles (par catégorie, etc.).
  - **Éditeur** : après authentification, gère (CRUD) articles et catégories.
  - **Administrateur** : éditeur + gestion des utilisateurs et des jetons d'authentification.

### 2. Services Web
- **SOAP** (JAX-WS) : CRUD utilisateurs (accès par jeton généré par un admin) + authentification (login/mot de passe).
- **REST** (JAX-RS) : liste des articles, articles groupés par catégorie, articles d'une catégorie donnée — au format **XML ou JSON** au choix.

### 3. Application Client Java
Application desktop qui demande login/mot de passe, invoque le service SOAP d'authentification pour vérifier les droits admin, puis permet la gestion complète des utilisateurs via les services web.

## Architecture (MVC)

```
src/main/java/sn/diop/actualite/
├── model/        Entités métier (Article, Categorie, Utilisateur, Role, Jeton)
├── dao/          Accès aux données
├── service/      Logique métier
├── controller/   Servlets (couche contrôleur MVC)
├── rest/         Services REST (JAX-RS)
├── soap/         Services SOAP (JAX-WS)
└── util/         Utilitaires (filtres, sécurité)

src/main/webapp/  Vues JSP (couche vue MVC)
client/           Application client Java (partie 3)
```

## Stack technique
- Java (Servlet / JSP)
- JAX-WS (SOAP), JAX-RS (REST)
- Maven
- Serveur : Apache Tomcat

## Build

```bash
mvn clean package        # construit le .war du site web
cd client && mvn package # construit l'application client
```

## Équipe
- Groupe X — Classe : DIC2 / MASTER1 / DIT2 *(à compléter)*
