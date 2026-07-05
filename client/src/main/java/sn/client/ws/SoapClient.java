package sn.client.ws;

import sn.client.model.UtilisateurDTO;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Client des services SOAP exposés par le serveur (soap/AuthSoapService et
 * soap/UtilisateurSoapService, Membre 2).
 *
 * Contrat SOAP attendu (à faire valider/adapter avec Membre 2 lors de
 * l'intégration — aucun WSDL n'existait encore côté serveur au moment de
 * l'écriture de ce client) :
 *
 * <pre>
 * POST {baseUrl}/auth           (non protégé par jeton)
 *   Requête  : &lt;authentifierRequest&gt;&lt;login/&gt;&lt;motDePasse/&gt;&lt;/authentifierRequest&gt;
 *   Réponse  : &lt;authentifierResponse&gt;&lt;succes/&gt;&lt;role/&gt;&lt;message/&gt;&lt;/authentifierResponse&gt;
 *
 * POST {baseUrl}/utilisateurs   (protégé par jeton, en-tête HTTP "X-Jeton", voir util/JetonFilter)
 *   lister    : &lt;listerUtilisateursRequest/&gt;
 *             -&gt; &lt;listerUtilisateursResponse&gt;&lt;utilisateur&gt;&lt;id/&gt;&lt;login/&gt;&lt;role/&gt;&lt;/utilisateur&gt;*&lt;/listerUtilisateursResponse&gt;
 *   créer     : &lt;creerUtilisateurRequest&gt;&lt;login/&gt;&lt;motDePasse/&gt;&lt;role/&gt;&lt;/creerUtilisateurRequest&gt;
 *             -&gt; &lt;creerUtilisateurResponse&gt;&lt;id/&gt;&lt;/creerUtilisateurResponse&gt;
 *   modifier  : &lt;modifierUtilisateurRequest&gt;&lt;id/&gt;&lt;login/&gt;&lt;motDePasse/&gt;&lt;role/&gt;&lt;/modifierUtilisateurRequest&gt;
 *   supprimer : &lt;supprimerUtilisateurRequest&gt;&lt;id/&gt;&lt;/supprimerUtilisateurRequest&gt;
 * </pre>
 *
 * Toutes les requêtes sont envoyées comme un corps SOAP 1.1 minimal, via
 * {@link HttpClient} (JDK standard, aucune dépendance JAX-WS/SAAJ requise
 * côté client).
 */
public class SoapClient {

    private static final String NS = "http://actualite.sn/ws";

    private final String baseUrl;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public SoapClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /** Résultat de l'authentification (login/mot de passe) auprès de AuthSoapService. */
    public record ResultatAuth(boolean succes, String role, String message) {
    }

    public ResultatAuth authentifier(String login, String motDePasse) throws SoapClientException {
        String corps = "<authentifierRequest xmlns=\"" + NS + "\">"
                + "<login>" + echapper(login) + "</login>"
                + "<motDePasse>" + echapper(motDePasse) + "</motDePasse>"
                + "</authentifierRequest>";

        Element reponse = envoyer(baseUrl + "/auth", corps, null);
        boolean succes = Boolean.parseBoolean(texte(reponse, "succes"));
        String role = texte(reponse, "role");
        String message = texte(reponse, "message");
        return new ResultatAuth(succes, role, message);
    }

    public List<UtilisateurDTO> listerUtilisateurs(String jeton) throws SoapClientException {
        String corps = "<listerUtilisateursRequest xmlns=\"" + NS + "\"/>";
        Element reponse = envoyer(baseUrl + "/utilisateurs", corps, jeton);

        List<UtilisateurDTO> utilisateurs = new ArrayList<>();
        NodeList noeuds = reponse.getElementsByTagNameNS(NS, "utilisateur");
        for (int i = 0; i < noeuds.getLength(); i++) {
            Element u = (Element) noeuds.item(i);
            UtilisateurDTO dto = new UtilisateurDTO();
            dto.setId(Integer.parseInt(texte(u, "id")));
            dto.setLogin(texte(u, "login"));
            dto.setRole(texte(u, "role"));
            utilisateurs.add(dto);
        }
        return utilisateurs;
    }

    public int creerUtilisateur(String jeton, UtilisateurDTO utilisateur) throws SoapClientException {
        String corps = "<creerUtilisateurRequest xmlns=\"" + NS + "\">"
                + "<login>" + echapper(utilisateur.getLogin()) + "</login>"
                + "<motDePasse>" + echapper(utilisateur.getMotDePasse()) + "</motDePasse>"
                + "<role>" + echapper(utilisateur.getRole()) + "</role>"
                + "</creerUtilisateurRequest>";

        Element reponse = envoyer(baseUrl + "/utilisateurs", corps, jeton);
        return Integer.parseInt(texte(reponse, "id"));
    }

    public void modifierUtilisateur(String jeton, UtilisateurDTO utilisateur) throws SoapClientException {
        StringBuilder corps = new StringBuilder("<modifierUtilisateurRequest xmlns=\"" + NS + "\">")
                .append("<id>").append(utilisateur.getId()).append("</id>")
                .append("<login>").append(echapper(utilisateur.getLogin())).append("</login>");
        if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().isBlank()) {
            corps.append("<motDePasse>").append(echapper(utilisateur.getMotDePasse())).append("</motDePasse>");
        }
        corps.append("<role>").append(echapper(utilisateur.getRole())).append("</role>")
                .append("</modifierUtilisateurRequest>");

        envoyer(baseUrl + "/utilisateurs", corps.toString(), jeton);
    }

    public void supprimerUtilisateur(String jeton, int id) throws SoapClientException {
        String corps = "<supprimerUtilisateurRequest xmlns=\"" + NS + "\">"
                + "<id>" + id + "</id>"
                + "</supprimerUtilisateurRequest>";
        envoyer(baseUrl + "/utilisateurs", corps, jeton);
    }

    /** Envoie une requête SOAP 1.1 et retourne le premier élément du Body de la réponse. */
    private Element envoyer(String url, String corpsXml, String jeton) throws SoapClientException {
        String enveloppe = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soap:Body>" + corpsXml + "</soap:Body>"
                + "</soap:Envelope>";

        HttpRequest.Builder requete = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "text/xml; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(enveloppe, StandardCharsets.UTF_8));

        if (jeton != null) {
            requete.header("X-Jeton", jeton);
        }

        try {
            HttpResponse<String> reponse = httpClient.send(requete.build(), HttpResponse.BodyHandlers.ofString());

            if (reponse.statusCode() == 401) {
                throw new SoapClientException("Jeton manquant, invalide ou révoqué.");
            }
            if (reponse.statusCode() >= 400) {
                throw new SoapClientException("Le serveur a renvoyé une erreur HTTP " + reponse.statusCode());
            }

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(reponse.body().getBytes(StandardCharsets.UTF_8)));

            NodeList fautes = document.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Fault");
            if (fautes.getLength() > 0) {
                throw new SoapClientException("Erreur SOAP : " + fautes.item(0).getTextContent());
            }

            NodeList corps = document.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body");
            Node premier = corps.item(0).getFirstChild();
            while (premier != null && premier.getNodeType() != Node.ELEMENT_NODE) {
                premier = premier.getNextSibling();
            }
            if (premier == null) {
                throw new SoapClientException("Réponse SOAP vide ou invalide.");
            }
            return (Element) premier;
        } catch (SoapClientException e) {
            throw e;
        } catch (Exception e) {
            throw new SoapClientException("Impossible de contacter le service web (" + url + ") : " + e.getMessage(), e);
        }
    }

    private String texte(Element parent, String nomBalise) {
        NodeList noeuds = parent.getElementsByTagNameNS(NS, nomBalise);
        return noeuds.getLength() == 0 ? null : noeuds.item(0).getTextContent();
    }

    private String echapper(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
