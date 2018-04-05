package vat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.*;

import org.w3c.dom.NodeList;

public class ViesValidation {
	public static boolean ValidNumber(String[] args, String countryCode, String vatNumber) {
		String soapEndpointUrl = "http://ec.europa.eu/taxation_customs/vies/services/checkVatService";
		
		return callSoapWebService(soapEndpointUrl, countryCode, vatNumber);
		
	}
	
	public static boolean ValidFormat( String[] args, String vatNumber ) {
		/* Need to define this in case country code isn't included */
		Map<String, String> CountryMap = new HashMap<String, String>();
		CountryMap.put("Austria", "AT");
		CountryMap.put("Belgium", "BE");
		CountryMap.put("Bulgaria", "BG");
		CountryMap.put("Cyprus", "CY");
		CountryMap.put("Czech Republic", "CZ");
		CountryMap.put("Germany", "DE");
		CountryMap.put("Denmark", "DK");
		CountryMap.put("Estonia", "EE");
		CountryMap.put("Greece", "EL");
		CountryMap.put("Spain", "ES");
		CountryMap.put("Finland", "FI");
		CountryMap.put("France", "FR");
		CountryMap.put("United Kingdom", "GB");
		CountryMap.put("Hungary", "HU");
		CountryMap.put("Ireland", "IE");
		CountryMap.put("Italy", "IT");
		CountryMap.put("Lithuania", "LT");
		CountryMap.put("Luxembourg", "LU");
		CountryMap.put("Latvia", "LV");
		CountryMap.put("Malta", "MT");
		CountryMap.put("Netherlands", "NL");
		CountryMap.put("Poland", "PL");
		CountryMap.put("Portugal", "PT");
		CountryMap.put("Romania", "RO");
		CountryMap.put("Sweden", "SE");
		CountryMap.put("Slovenia", "SI");
		CountryMap.put("Slovakia", "SK");
		
		
		Pattern pattern = Pattern.compile(vatFormat);
		Matcher matcher = pattern.matcher(vatNumber);
		return matcher.matches();
    }
    
	//Regular expression for verifying that provided VAT number is in the correct format.
    private static String vatFormat = ".*(AT)?U[0-9]{8}.*|"
    									+ ".*(BE)?[0-9]{10}.*|"
    									+ ".*(BG)?[0-9]{9,10}.*|"
    									+ ".*(CY)?[0-9]{8}L.*|"
    									+ ".*(CZ)?[0-9]{8,10}.*|"
    									+ ".*(DE)?[0-9]{9}.*|"
    									+ ".*(DK)?[0-9]{8}.*|"
    									+ ".*(EE)?[0-9]{9}.*|"
    									+ ".*(EL|GR)?[0-9]{9}.*|"
    									+ ".*(ES)?[0-9A-Z][0-9]{7}[0-9A-Z].*|"
    									+ ".*(FI)?[0-9]{8}.*|"
    									+ ".*(FR)?[0-9A-Z]{2}[0-9]{9}.*|"
    									+ ".*(GB)?([0-9]{9}([0-9]{3})?|[A-Z]{2}[0-9]{3}).*|"
    									+ ".*(HU)?[0-9]{8}.*|"
    									+ ".*(IE)?[0-9]S[0-9]{5}L.*|"
    									+ ".*(IT)?[0-9]{11}.*|"
    									+ ".*(LT)?([0-9]{9}|[0-9]{12}).*|"
    									+ ".*(LU)?[0-9]{8}.*|"
    									+ ".*(LV)?[0-9]{11}.*|"
    									+ ".*(MT)?[0-9]{8}.*|"
    									+ ".*(NL)?[0-9]{9}B[0-9]{2}.*|"
    									+ ".*(PL)?[0-9]{10}.*|"
    									+ ".*(PT)?[0-9]{9}.*|"
    									+ ".*(RO)?[0-9]{2,10}.*|"
    									+ ".*(SE)?[0-9]{12}.*|"
    									+ ".*(SI)?[0-9]{8}.*|"
    									+ ".*(SK)?[0-9]{10}.*";

	
	private static void createSoapEnvelope(SOAPMessage soapMessage, String countryCode, String vatNumber) throws SOAPException {
		SOAPPart soapPart = soapMessage.getSOAPPart();
		
		String myNamespace = "urn";
		String myNamespaceURI = "urn:ec.europa.eu:taxud:vies:services:checkVat:types";
		
		//SOAP Envelope
		
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
		
		
		
		 /*
        Constructed SOAP Request Message:
        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:myNamespace="http://www.webserviceX.NET">
            <SOAP-ENV:Header/>
            <SOAP-ENV:Body>
                <myNamespace:GetInfoByCity>
                    <myNamespace:USCity>New York</myNamespace:USCity>
                </myNamespace:GetInfoByCity>
            </SOAP-ENV:Body>
        </SOAP-ENV:Envelope>
        */
		
		SOAPBody soapBody = envelope.getBody();
		
		SOAPElement soapBodyElem = soapBody.addChildElement("checkVat", myNamespace);
		SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("countryCode", myNamespace);
		SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("vatNumber", myNamespace);
		soapBodyElem1.addTextNode(countryCode);
		soapBodyElem2.addTextNode(vatNumber);
	}
	
	private static boolean callSoapWebService(String soapEndpointUrl, String countryCode, String vatNumber) {
		try {
			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			// Send SOAP Message to SOAP Server
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(countryCode, vatNumber), soapEndpointUrl);
			//SOAPBody testResponse = soapResponse.getSOAPBody();
			//System.out.println(testResponse);
			/* Error message when VIES database is down for a specific country.
			 * <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    			<soap:Body>
        			<soap:Fault>
            			<faultcode>soap:Server</faultcode>
            			<faultstring>MS_UNAVAILABLE</faultstring>
        			</soap:Fault>
    			</soap:Body>
			   </soap:Envelope>
			 */
			
			soapConnection.close();
			try {
				SOAPBody soapBody = soapResponse.getSOAPBody();
				//In case you want to write the soap response 
				/*soapResponse.writeTo(System.out);*/
				NodeList returnList = soapBody.getElementsByTagName("valid");
				if (returnList.item(0).getTextContent().equals("true")) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				System.err.println("\nError retrieving soap response body\n");
				e.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			System.err.println("\nError occurred while sending request to soap server\nMake sure you have the correct endpoint URL and soap action\n");
			e.printStackTrace();
			return false;
		} 
	}
	
	private static SOAPMessage createSOAPRequest(String countryCode, String vatNumber) throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		
		createSoapEnvelope(soapMessage, countryCode, vatNumber);
		
		
		soapMessage.saveChanges();
		
		return soapMessage;
	}

}
