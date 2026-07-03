<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>ActuSN - Accueil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="fragments/entete.jsp"/>

<main class="conteneur">

    <c:if test="${empty articleUne and empty articlesGrille}">
        <p class="message-vide">Aucun article pour le moment.</p>
    </c:if>

    <%-- Bloc "à la une" : uniquement sur la page 1 --%>
    <c:if test="${not empty articleUne}">
        <section class="bloc-une">
            <article class="carte-une">
                <span class="etiquette-une">À LA UNE</span>
                <h2><a href="${pageContext.request.contextPath}/article?id=${articleUne.id}"><c:out value="${articleUne.titre}"/></a></h2>
                <p class="resume-une"><c:out value="${articleUne.resume}"/></p>
                <p class="meta-article"><c:out value="${articleUne.auteur.login}"/> &middot; <c:out value="${articleUne.categorie.nom}"/> &middot; ${articleUne.datePublicationFormatee}</p>
            </article>

            <div class="colonne-en-avant">
                <c:forEach var="article" items="${articlesEnAvant}">
                    <article class="carte-en-avant">
                        <span class="badge badge-cat-${article.categorie.id % 4}"><c:out value="${article.categorie.nom}"/></span>
                        <h3><a href="${pageContext.request.contextPath}/article?id=${article.id}"><c:out value="${article.titre}"/></a></h3>
                        <p class="meta-article">${article.datePublicationFormatee} &middot; <c:out value="${article.auteur.login}"/></p>
                    </article>
                </c:forEach>
            </div>
        </section>
    </c:if>

    <%-- Grille "Derniers articles" --%>
    <c:if test="${not empty articlesGrille}">
        <section class="bloc-grille">
            <div class="entete-section">
                <h2>Derniers articles</h2>
            </div>

            <div class="grille-articles">
                <c:forEach var="article" items="${articlesGrille}">
                    <article class="carte-article">
                        <span class="badge badge-cat-${article.categorie.id % 4}"><c:out value="${article.categorie.nom}"/></span>
                        <h3><a href="${pageContext.request.contextPath}/article?id=${article.id}"><c:out value="${article.titre}"/></a></h3>
                        <p class="resume-article"><c:out value="${article.resume}"/></p>
                        <p class="meta-article"><c:out value="${article.auteur.login}"/> &middot; ${article.datePublicationFormatee}</p>
                    </article>
                </c:forEach>
            </div>

            <nav class="pagination">
                <c:if test="${aPagePrecedente}">
                    <a class="bouton-page" href="${pageContext.request.contextPath}/accueil?page=${pageCourante - 1}">&lsaquo;</a>
                </c:if>

                <c:forEach var="numero" items="${numerosPages}">
                    <a class="bouton-page ${numero == pageCourante ? 'bouton-page-active' : ''}"
                       href="${pageContext.request.contextPath}/accueil?page=${numero}">${numero}</a>
                </c:forEach>

                <c:if test="${aPageSuivante}">
                    <a class="bouton-page" href="${pageContext.request.contextPath}/accueil?page=${pageCourante + 1}">&rsaquo;</a>
                </c:if>
            </nav>
        </section>
    </c:if>

</main>

<footer class="pied-page">
    <p>&copy; Projet Architecture Logicielle - Site d'actualité</p>
</footer>

</body>
</html>
