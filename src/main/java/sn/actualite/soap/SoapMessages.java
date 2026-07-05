package sn.actualite.soap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;

/**
 * Aide partagée par les services SOAP (AuthSoapService, UtilisateurSoapService)
 * pour lire une enveloppe SOAP 1.1 entrante et écrire une réponse conforme.
 *
 * Contrat de service web volontairement implémenté en HttpServlet plutôt
 * qu'avec le conteneur JAX-WS (Metro) : le client (client/ws/SoapClient,
 * Membre 3) a été écrit sans dépendance SAAJ/JAX-WS, en s'appuyant uniquement
 * sur ce contrat XML brut (enveloppe SOAP 1.1, namespace fixe, un élément
 * racine par opération). Rester au niveau servlet garantit que le service
 * répond exactement à ce contrat, sans risque de divergence liée au style de
 * liaison (RPC/Document, wrapped/bare) qu'un runtime JAX-WS introduirait.
 */
final class SoapMessages {

    static final String NS = "http://actualite.sn/ws";
    private static final String NS_ENVELOPPE = "http://schemas.xmlsoap.org/soap/envelope/";

    private SoapMessages() {
    }

    /** Retourne le premier élément du corps SOAP de la requête (l'opération demandée). */
    static Element extraireCorps(HttpServletRequest request) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(request.getInputStream());

        NodeList corps = document.getElementsByTagNameNS(NS_ENVELOPPE, "Body");
        if (corps.getLength() == 0) {
            throw new IllegalArgumentException("Enveloppe SOAP invalide : element Body absent.");
        }
        Node premier = corps.item(0).getFirstChild();
        while (premier != null && premier.getNodeType() != Node.ELEMENT_NODE) {
            premier = premier.getNextSibling();
        }
        if (premier == null) {
            throw new IllegalArgumentException("Enveloppe SOAP invalide : corps vide.");
        }
        return (Element) premier;
    }

    /** Texte du premier enfant portant ce nom local (namespace du service), ou null si absent. */
    static String texte(Element parent, String nomBalise) {
        NodeList noeuds = parent.getElementsByTagNameNS(NS, nomBalise);
        return noeuds.getLength() == 0 ? null : noeuds.item(0).getTextContent();
    }

    /** Écrit une réponse SOAP 1.1 réussie, {@code contenuXml} étant l'élément racine de la réponse. */
    static void ecrireReponse(HttpServletResponse response, String contenuXml) throws IOException {
        String enveloppe = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soap:Envelope xmlns:soap=\"" + NS_ENVELOPPE + "\">"
                + "<soap:Body>" + contenuXml + "</soap:Body>"
                + "</soap:Envelope>";
        response.setContentType("text/xml; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(enveloppe);
    }

    /**
     * Écrit une erreur métier (login déjà pris, ressource introuvable, etc.) sous
     * forme de soap:Fault. Le code HTTP reste 200 : le client (SoapClient)
     * considère tout code >= 400 comme une erreur de transport générique et
     * n'inspecte le corps que dans ce cas-là.
     */
    static void ecrireErreur(HttpServletResponse response, String message) throws IOException {
        String enveloppe = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soap:Envelope xmlns:soap=\"" + NS_ENVELOPPE + "\">"
                + "<soap:Body><soap:Fault>"
                + "<faultcode>soap:Server</faultcode>"
                + "<faultstring>" + echapper(message) + "</faultstring>"
                + "</soap:Fault></soap:Body>"
                + "</soap:Envelope>";
        response.setContentType("text/xml; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(enveloppe);
    }

    static String echapper(String valeur) {
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
