<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion - ActuSN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="fragments/entete.jsp"/>

<main class="conteneur conteneur-etroit">

    <div class="carte-connexion">
        <h1>Connexion</h1>
        <p class="sous-titre-connexion">Réservé aux éditeurs et administrateurs.</p>

        <c:if test="${not empty erreur}">
            <p class="message-erreur"><c:out value="${erreur}"/></p>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login" class="formulaire-connexion">
            <label for="login">Login</label>
            <input type="text" id="login" name="login" value="<c:out value="${loginSaisi}"/>" required autofocus>

            <label for="motDePasse">Mot de passe</label>
            <input type="password" id="motDePasse" name="motDePasse" required>

            <button type="submit" class="bouton-principal">Se connecter</button>
        </form>
    </div>

</main>

<footer class="pied-page">
    <p>&copy; Projet Architecture Logicielle - Site d'actualité</p>
</footer>

</body>
</html>
