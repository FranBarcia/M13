/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package provawebservice;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author montse.maqueda
 */
public class WebServiceCalls {
    
    //private final String SERVER_GIOC = "http://gamificacioc.com:80/ServerGIOC/";
    private final String SERVER_GIOC = "http://localhost:8080/ServerGIOC/";
    //private final String GIOC_WEB_SERVICES = "http://gamificacioc.com:80/ServerGIOC/webservices";
    private final String GIOC_WEB_SERVICES = "http://localhost:8080/ServerGIOC/webservices";
    private final String MY_NAMESPACE = "ws";
    private final String MY_NAMESPACE_URI = "http://webservices.gamificacioc.com/";
    
    //String soapEndpointUrl = "http://gamificacioc.com:80/ServerGIOC/webservices";
    
    
    /**
     * Fa l'autenticació d'usuari a partir de nom d'usuari i contrasenya i retorna un codi
     * únic de sessió
     * @param usuari
     * @param contrasenya
     * @return codi generat per a la sessió 
     * (si usuari-contrasenya no existents retorna "", si es dona error retorna "ERROR")
     */
    public String comprovarLogin(String usuari, String contrasenya){
        final String accio = "comprovarLogin";
        String ret = null;
        try{
            //1. Fem la petició SOAP
            //1.1 Instanciem el missatge de petició de base
            SOAPMessage soapPeticio = initiateSOAPRequest();
            //1.2 Obtenin el cos del missatge i afegim com a etiquetes filles els arguments de l'acció
            SOAPBody soapBody = getSOAPBodyRequest(soapPeticio);
            SOAPElement elementAccio = soapBody.addChildElement(accio, MY_NAMESPACE);
            elementAccio.addChildElement("usuari").addTextNode(usuari);
            elementAccio.addChildElement("contrasenya").addTextNode(contrasenya);
            //1.3 Acabem d'afegir altres elements necessaris al missatge de petició
            finishSOAPRequest(soapPeticio,accio);
            //2. Obtenim el missatge de resposta del nostre webservice
            SOAPMessage resposta = callSoapWebService(soapPeticio,GIOC_WEB_SERVICES);
            //3. Fem el tractament del XML rebut per extreure el resultat a retornar
            ret = getUniqueResult(resposta);
        }
        catch(Exception ex){
            ex.printStackTrace();
            ret = "ERROR";
        }
        return ret;
    }
    
    
    public String obtenirTipusUsuari(String usuari, String contrasenya){
        //TODO
        
        String ret = "";
        return ret;
    }
       
    private SOAPMessage callSoapWebService(SOAPMessage soapRequest, String soapEndpointUrl) 
            throws SOAPException {
        SOAPMessage soapResponse = null;
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            // Send SOAP Message to SOAP Server
            soapResponse = soapConnection.call(soapRequest, soapEndpointUrl);
            soapConnection.close();
        } catch (SOAPException ex) {
           System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            throw ex;
        }
        return soapResponse;
    }
    
    

    private SOAPMessage initiateSOAPRequest() throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        return messageFactory.createMessage();
    }
        
    private void finishSOAPRequest(SOAPMessage peticio, String soapAction) throws SOAPException{
        MimeHeaders headers = peticio.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);
        peticio.saveChanges();
    }
     
    private SOAPBody getSOAPBodyRequest(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(MY_NAMESPACE, MY_NAMESPACE_URI);
        return envelope.getBody();
 
    }
    
    /**
     * Permet extreure el resultat obtingut d'una resposta SOAP amb un únic resultat
     * @param resposta missatge SOAP obtingut com a resposta
     * @return resultat (si es troba més d'una etiqueta de resultat es retorna "ERROR")
     * @throws SOAPException 
     */
    private String getUniqueResult(SOAPMessage resposta) throws SOAPException {
        
        Node tagParent= (Node)resposta.getSOAPBody().getChildElements().next();
        NodeList llistaNodes = tagParent.getChildNodes();
        if (llistaNodes.getLength()==1){
            return llistaNodes.item(0).getChildNodes().item(0).getTextContent();
        }
        else return "ERROR";
        /*
        Iterator itr=resposta.getSOAPBody().getChildElements();
        while (itr.hasNext()) {
            Node node=(Node)itr.next();
            if (node.getNodeType()==Node.ELEMENT_NODE) {
                NodeList llistaNodes = node.getChildNodes();
                if (llistaNodes.getLength()==1){
                    return llistaNodes.item(0).getChildNodes().item(0).getTextContent();
                }          
            }
        }
        */
    }
    
    /*
    public SOAPMessage callSoapWebService(String soapEndpointUrl, String soapAction) {
        SOAPMessage soapResponse = null;
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            soapResponse = soapConnection.call(createSOAPRequest(soapAction), soapEndpointUrl);
           
            
            
            // Print the SOAP Response
            System.out.println("Response SOAP Message:");
            System.out.println("RESPUESTAKA: "+ soapResponse.getProperty(usuari));
            soapResponse.writeTo(System.out);
            System.out.println();

            soapConnection.close();
        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
        }
        
        return soapResponse;
    }
    */
    
    /*
    public SOAPMessage createSOAPRequest(String soapAction) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        createSOAPEnvelope(soapMessage);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }
    */
     
    /*
    private SOAPElement startSOAPEnvelope(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String myNamespace = "ws";
        String myNamespaceURI = "http://webservices.gamificacioc.com/";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
        SOAPBody soapBody = envelope.getBody();
        return soapBody.addChildElement("comprovarLogin", myNamespace);

            
            Constructed SOAP Request Message:
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:myNamespace="http://www.webserviceX.NET">
                <SOAP-ENV:Header/>
                <SOAP-ENV:Body>
                    <myNamespace:GetInfoByCity>
                        <myNamespace:USCity>New York</myNamespace:USCity>
                    </myNamespace:GetInfoByCity>
                </SOAP-ENV:Body>
            </SOAP-ENV:Envelope>
            

        // SOAP Body
        
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("comprovarLogin", myNamespace);
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("usuari");
        soapBodyElem1.addTextNode(usuari);
        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("contrasenya");
        soapBodyElem2.addTextNode(contrasenya);
        

        
    }
*/

    
 
}