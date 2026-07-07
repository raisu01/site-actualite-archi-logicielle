CREATE DATABASE IF NOT EXISTS actualite
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE actualite;

DROP TABLE IF EXISTS jeton;
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS utilisateur;
DROP TABLE IF EXISTS categorie;

CREATE TABLE categorie (
    id  INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE utilisateur (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    login        VARCHAR(100) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role         VARCHAR(20)  NOT NULL
);

CREATE TABLE article (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    titre            VARCHAR(200) NOT NULL,
    contenu          TEXT         NOT NULL,
    date_publication DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    categorie_id     INT          NOT NULL,
    auteur_id        INT          NOT NULL,
    CONSTRAINT fk_article_categorie FOREIGN KEY (categorie_id) REFERENCES categorie(id),
    CONSTRAINT fk_article_auteur    FOREIGN KEY (auteur_id)    REFERENCES utilisateur(id)
);

CREATE TABLE jeton (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    valeur         VARCHAR(255) NOT NULL UNIQUE,
    utilisateur_id INT          NOT NULL,
    date_creation  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actif          BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_jeton_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id)
);

INSERT INTO categorie (nom) VALUES
    ('Politique'),
    ('Sport'),
    ('Technologie');

INSERT INTO utilisateur (login, mot_de_passe, role) VALUES
    ('admin',   '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',   'ADMINISTRATEUR'),
    ('editeur', '78064a810c0dbacfae9e7ac33e7e84f3f032ab2c0dd157744ecab22d839a021e', 'EDITEUR');

INSERT INTO article (titre, contenu, categorie_id, auteur_id) VALUES
    ('Bienvenue sur le site', 'Premier article de demonstration.', 3, 1),
    ('Resultats du match',     'Le club local gagne 2 a 0.',        2, 2),
    ('Nouvelle loi votee',     'Le parlement adopte le texte.',     1, 1);
