/*
 * DefaulterList.java
 *
 * Created on October 13, 2005, 5:28 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.maxis.businessobject;

import com.csgsystems.domain.framework.attribute.*;
import com.csgsystems.fxcommon.attribute.*;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.businessobject.*;
import com.csgsystems.localization.ResourceManager;

import java.net.PasswordAuthentication;
import javax.swing.JOptionPane;

import java.net.URL;
import java.net.URLConnection;
import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import java.net.Authenticator;
import java.io.IOException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.Attributes;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * List of defaulters obtained from DDMF query
 * @author Pankaj Saini
 */
public class DefaulterList extends DomainCollection {
    
    protected String externalId = null;
    protected int externalIdType;
    private String urlString = "http://telco.emr.com.my/xml/xml_enq.cgi"; 
            // "http://202.146.67.154/xml/xml_enq.cgi";
    protected StringAttribute ddmfUrl = null;
    protected StringAttribute ddmfUserid = null;
    protected StringAttribute ddmfPassword = null;
    protected StringAttribute ddmfProtocol = null;
    
    /** Creates a new instance of DefaulterList */
    public DefaulterList() {
        super(Defaulter.class);
        ddmfUrl = new StringAttribute("URL", this, false);
        ddmfUserid = new StringAttribute("Username", this, false);
        ddmfPassword = new StringAttribute("Password", this, false);
        ddmfProtocol = new StringAttribute("Protocol", this, false);
    }
    
    protected boolean localAddAssociation(IPersistentObject relObject) {
        boolean success = true;
        externalId = relObject.getAttributeDataAsString("Value");
        externalIdType = relObject.getAttributeDataAsInteger("Type");
        return success;
    }
    
    protected int listPageFault(int nStart) {
        // test code
/*        setAttributeDataAsString("Username", "maxisxml");
        setAttributeDataAsString("Password", "xml9maxis");
        setAttributeDataAsString("URL", "telco.emr.com.my/xml/xml_enq.cgi");
        setAttributeDataAsString("Protocol", "http");
        externalId = "560524085064";
        externalIdType = 9;
*/        // test code end
        Authenticator.setDefault( new DDMFAuthenticator(getAttributeDataAsString("Username"), 
                getAttributeDataAsString("Password")) ); // set authentication
        
        // get the ic number from genericDomain
        String request = formatRequest();
        // do HTTP post request and get response
        String response = sendRequest(request);
        
        if ( response == null || response.length() == 0 ) {
            JOptionPane.showMessageDialog(null, ResourceManager.getString("DDMF.down.text.Warning"), 
                    ResourceManager.getString("DDMF.down.title.Warning"), JOptionPane.WARNING_MESSAGE);            
            return 0;
        }
        
        // parse XML response and create defaulter IPersistentObjects
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            XMLReader parser = parserFactory.newSAXParser().getXMLReader();
            
            parser.setContentHandler(new DDMFContentHandler());
            InputSource source = new InputSource();
            source.setCharacterStream(new java.io.CharArrayReader(response.toCharArray()));
            parser.parse(source);
        } catch (IOException e) {
            System.out.println("Error reading response: " + e.getMessage());
        } catch (SAXException e) {
            System.out.println("Error parsing response: " + e.getMessage());
        } catch (ParserConfigurationException e) {
            System.out.println("Error parsing response: " + e.getMessage());            
        }
        
        return 0;
    }
    
    private void addDefaulter(String ic_no, String new_ic, String brn, String name, String status, 
            String account, String company, String branch, String input_date, String user_id, 
            String last_enq_company, String last_enq_date) {
        IPersistentObject defaulter = PersistentObjectFactory.getFactory().createNew(Defaulter.class, null);
        defaulter.setAttributeDataAsString("Status", status);
        defaulter.setAttributeDataAsString("OldIC", ic_no);
        defaulter.setAttributeDataAsString("NewIC", new_ic);
        defaulter.setAttributeDataAsString("BRN", brn);
        defaulter.setAttributeDataAsString("Name", name);
        defaulter.setAttributeDataAsString("Account", account);
        defaulter.setAttributeDataAsString("SubmitBy", company);
        defaulter.setAttributeDataAsString("InputDate", input_date);
        defaulter.setAttributeDataAsString("LastEnqBy", last_enq_company);
        defaulter.setAttributeDataAsString("LastEnqOn", last_enq_date);
        
        add(defaulter);
    }
    
    private String formatRequest() {
        String request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        request = request + "<!DOCTYPE telco_xml_enq SYSTEM \"telco_xml_enq.dtd\">\n";
        request = request + "<telco_xml_enq>\n";
        request = request + "<header>\n";
        request = request + "<enq_company_code>MAXIS</enq_company_code>\n"; // users company code
        request = request + "<enq_branch_code>MAXISHQ</enq_branch_code>\n"; // users account number
        request = request + "<enq_user_id>" + ddmfUserid + "</enq_user_id>\n"; // users system id
        request = request + "<enq_batch_no>1</enq_batch_no>\n"; // generate unique sequence for company_code/branch_code
        request = request + "<output_format>0</output_format>\n"; // 1 = HTML, 0 = XML
        
        if ( externalIdType > 7 && externalIdType < 11 ) // IC or BRN
            request = request + "<record_total>1</record_total>\n";
        else request = request + "<record_total>2</record_total>\n";
        
        request = request + "</header>\n";
        request = request + "<body>\n";
        
        if ( externalIdType == 10 ) { // Old IC
            request = request + "<individual>\n";
            request = request + "<enq_ic_no>" + externalId + "</enq_ic_no>\n";
            request = request + "</individual>\n";            
        } else if ( externalIdType == 9 ) { // New IC
            request = request + "<individual>\n";
            request = request + "<enq_new_ic>" + externalId + "</enq_new_ic>\n";
            request = request + "</individual>\n";            
        } else if ( externalIdType == 8 ) { // BRN
            request = request + "<business>\n";
            request = request + "<enq_br_no>" + externalId + "</enq_br_no>\n";
            request = request + "</business>\n";            
        } else { // Passport and others
            request = request + "<individual>\n";
            request = request + "<enq_ic_no>" + externalId + "</enq_ic_no>\n";
            request = request + "</individual>\n";            
            request = request + "<individual>\n";
            request = request + "<enq_new_ic>" + externalId + "</enq_new_ic>\n";
            request = request + "</individual>\n";
        }
        request = request + "</body>\n";
        request = request + "</telco_xml_enq>\n";
        
        return request;
    }
    
    private void setUpSSL() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
            System.out.println("Warning: URL Host: "+urlHostName+" vs. "+session.getPeerHost());
            return true;
            }
        };
 
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
    
    private String sendRequest(String request) {
        String response = "";
        String resp = "";
        URL url;
        URLConnection urlConn;
        OutputStreamWriter outStream;
        DataInputStream input;
        
        if ( "https".equals(ddmfProtocol.getDataAsString()) ) setUpSSL();
        
        try {
            // URL of CGI-Bin script.
            url = new URL(getAttributeDataAsString("Protocol").toLowerCase() + "://" + getAttributeDataAsString("URL"));
            // URL connection channel.
            urlConn = url.openConnection();
            // Let the run-time system (RTS) know that we want input.
            urlConn.setDoInput(true);
            // Let the RTS know that we want to do output.
            urlConn.setDoOutput(true);
            // No caching, we want the real thing.
            urlConn.setUseCaches(false);
            // Specify the content type.
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Send POST output.
            outStream = new OutputStreamWriter(urlConn.getOutputStream());
            String content = "XML_STRING=" + URLEncoder.encode(request);
            outStream.write(content);
            outStream.flush();
            outStream.close();

            // Get response data.
            input = new DataInputStream (urlConn.getInputStream ());
            while( null != ((resp = input.readLine())) )
                response = response + resp;
            
            input.close ();        
        } catch (MalformedURLException e) {
            logDebug("Unable to parse DDMF URL! Please make sure the URL is correct");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logDebug(request);
        logDebug(response);
        return response;
    }
    
    class DDMFAuthenticator extends Authenticator {
        private String userid = null;
        private String password = null;
        
        public DDMFAuthenticator(String id, String pass) {
            userid = id;
            password = pass;
        }
        
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userid, password.toCharArray());
        }
    }
    
    class DDMFContentHandler extends XMLFilterImpl {
        private String ic_no = null;
        private String new_ic = null;
        private String brn = null;
        private String name = null;
        private String status = null;
        private String company = null;
        private String branch = null;
        private String user_id = null;
        private String input_date = null;
        private String account = null;
        private String last_enq_company = null;
        private String last_enq_date = null;

        public final int NONE = 0;
        public final int IC = 1;
        public final int NEWIC = 2;
        public final int BRN = 3;
        public final int NAME = 4;
        public final int COMPANY = 5;
        public final int ACCOUNT = 6;
        public final int BRANCH = 7;
        public final int INPUT_DATE = 8;
        public final int SUBMIT_BY = 9;
        public final int LAST_ENQ_COM = 10;
                
        private int readingNow = NONE;
        
        public void characters(char[] ch, int start, int length) {
            if ( readingNow == IC ) ic_no = ic_no + new String(ch, start, length);
            else if ( readingNow == NEWIC ) new_ic = new_ic + new String(ch, start, length);
            else if ( readingNow == BRN ) brn = brn + new String(ch, start, length);
            else if ( readingNow == NAME ) name = name + new String(ch, start, length);
            else if ( readingNow == COMPANY ) company = company + new String(ch, start, length);
            else if ( readingNow == ACCOUNT ) account = account + new String(ch, start, length);
            else if ( readingNow == BRANCH ) branch = branch + new String(ch, start, length);
            else if ( readingNow == INPUT_DATE ) input_date = input_date + new String(ch, start, length);
            else if ( readingNow == SUBMIT_BY ) user_id = user_id + new String(ch, start, length);
            else if ( readingNow == LAST_ENQ_COM ) last_enq_company = last_enq_company + new String(ch, start, length);
        }
        
        public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) {            
            if ( qName.equals("df_ic_no") ) readingNow = IC;
            else if ( qName.equals("df_new_ic") ) readingNow = NEWIC;
            else if ( qName.equals("df_br_no") ) readingNow = BRN;
            else if ( qName.equals("df_name") ) readingNow = NAME;
            else if ( qName.equals("df_account") ) readingNow = ACCOUNT;
            else if ( qName.equals("company") ) readingNow = COMPANY;
            else if ( qName.equals("branch") ) readingNow = BRANCH;
            else if ( qName.equals("entry_date") ) readingNow = INPUT_DATE;
            else if ( qName.equals("user_id") ) readingNow = SUBMIT_BY;
            else if ( qName.equals("last_enq_com") ) {
                readingNow = LAST_ENQ_COM;
                last_enq_date = attributes.getValue("date");
            }
            else if ( qName.equals("enq_status") ) status = attributes.getValue("code");
            else if ( qName.equals("result") ) clear();
        }
        
        public void endElement(String namespaceURI, String localName, String qName) {
            readingNow = NONE;
            if ( qName.equals("result") ) addDefaulter(ic_no, new_ic, brn, name, status, account, company, branch,
                    input_date, user_id, last_enq_company, last_enq_date);
        }
        
        private void clear() {
            readingNow = NONE;
            ic_no = ""; new_ic = ""; brn = ""; name = ""; company = ""; last_enq_date = "";
            account = ""; input_date = ""; branch = ""; user_id = ""; last_enq_company = "";
        }
    }
}
