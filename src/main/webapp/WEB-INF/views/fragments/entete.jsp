<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    Fragment d'en-tête réutilisable : logo, navigation par catégorie, bouton connexion,
    bandeau "EN DIRECT". Attend en request scope :
      - categories        : List<Categorie>   (toujours fourni par chaque servlet publique)
      - categorieActive    : Integer ou null   (id de la catégorie affichée, null = "Toutes")
      - articlesTicker     : List<Article>     (facultatif, pour le bandeau EN DIRECT)
--%>
<header class="entete">
    <div class="barre-nav">
        <a class="logo" href="${pageContext.request.contextPath}/accueil">Actu<span>SN</span></a>

        <nav class="nav-categories">
            <a class="pilule ${empty categorieActive ? 'pilule-active' : ''}"
               href="${pageContext.request.contextPath}/accueil">Toutes</a>
            <c:forEach var="cat" items="${categories}">
                <a class="pilule ${categorieActive == cat.id ? 'pilule-active' : ''}"
                   href="${pageContext.request.contextPath}/categorie?id=${cat.id}"><c:out value="${cat.nom}"/></a>
            </c:forEach>
        </nav>

        <div class="actions-nav">
            <c:choose>
                <c:when test="${not empty sessionScope.utilisateur}">
                    <a class="bouton-pilule" href="${pageContext.request.contextPath}/admin/articles">Gérer</a>
                    <span class="utilisateur-connecte"><c:out value="${sessionScope.utilisateur.login}"/></span>
                    <a class="bouton-pilule" href="${pageContext.request.contextPath}/deconnexion">Déconnexion</a>
                </c:when>
                <c:otherwise>
                    <a class="bouton-pilule" href="${pageContext.request.contextPath}/login">Connexion</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <c:if test="${not empty articlesTicker}">
        <div class="bandeau-direct">
            <span class="etiquette-direct">EN DIRECT</span>
            <div class="defilement-direct">
                <c:forEach var="a" items="${articlesTicker}" varStatus="statut">
                    <a href="${pageContext.request.contextPath}/article?id=${a.id}"><c:out value="${a.titre}"/></a>
                    <c:if test="${!statut.last}"><span class="puce-direct">&bull;</span></c:if>
                </c:forEach>
            </div>
        </div>
    </c:if>
</header>
