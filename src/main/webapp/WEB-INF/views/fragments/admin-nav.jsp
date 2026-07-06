<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    Sous-navigation du back-office : à inclure dans chaque vue admin/*.jsp,
    juste après fragments/entete.jsp. Utilisateurs/Jetons ne sont affichés
    qu'aux administrateurs (ils sont de toute façon bloqués par
    util/AuthentificationFilter pour un éditeur).
--%>
<div class="admin-nav-bande">
    <nav class="admin-nav">
        <a class="admin-nav-lien ${pageContext.request.requestURI.endsWith('/admin/articles') ? 'admin-nav-actif' : ''}"
           href="${pageContext.request.contextPath}/admin/articles">Articles</a>
        <a class="admin-nav-lien ${pageContext.request.requestURI.endsWith('/admin/categories') ? 'admin-nav-actif' : ''}"
           href="${pageContext.request.contextPath}/admin/categories">Catégories</a>
        <c:if test="${sessionScope.utilisateur.role == 'ADMINISTRATEUR'}">
            <a class="admin-nav-lien ${pageContext.request.requestURI.endsWith('/admin/utilisateurs') ? 'admin-nav-actif' : ''}"
               href="${pageContext.request.contextPath}/admin/utilisateurs">Utilisateurs</a>
            <a class="admin-nav-lien ${pageContext.request.requestURI.endsWith('/admin/jetons') ? 'admin-nav-actif' : ''}"
               href="${pageContext.request.contextPath}/admin/jetons">Jetons</a>
        </c:if>
    </nav>
</div>
