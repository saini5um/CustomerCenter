package com.maxis.panel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.event.HyperlinkEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csgsystems.domain.arbor.businessobject.AccountBalances;
import com.csgsystems.domain.arbor.businessobject.OrderableObject;
import com.csgsystems.domain.arbor.businessobject.Service;
import com.csgsystems.domain.arbor.order.OrderManager;
import com.csgsystems.domain.framework.PersistentObjectFactory;
import com.csgsystems.domain.framework.businessobject.IPersistentCollection;
import com.csgsystems.domain.framework.businessobject.IPersistentObject;
import com.csgsystems.domain.framework.context.IContext;
import com.csgsystems.domain.framework.lookup.LookupDomain;
import com.csgsystems.igpa.controls.CSGVelocityHTMLEP;
import com.csgsystems.igpa.utils.ContextFinder;


/**
 * The AbstractHTMLPanel allows the specification of links that allows the
 * programmer to specify objects to pass into the next action. The next section
 * explains the various parameters that are currently (2-28-03) allowed on the
 * url. This is not a comprehensive list but rather a list of the most common
 * parameters.
 * <ul>
 * <li><b>action</b>:  Name of the panel or dialog action defined in the
 * application configurator</li>
 * <li><b>object</b>: Class name of the business object to retrieve for the
 * action; if the object being looked for is a collection, the name that
 * should be passed in is the collection class name minus "List" because
 * "List" is appended to the class name when the getObject is called to
 * retrieve the collection.</li>
 * <li><b>index</b>: If the business object needed is in a collection, the
 * index is the location in the collection of the object being retrieved.
 * </li>
 * <li><b>subtype</b>: The subtype of the object being retrieved.</li>
 * <li><b>objecttree</b>: A dot notation parameter specifying the path to
 * the needed object. This parameter only allows for traversal of object -
 * collection[object] - collection[object].</li>
 * <li><b>Example</b>: <code>http://someurl?action=some-
 * action&object=ServiceZone&index=1&objecttree=Account.ServiceList.0
 * </code> where 0 is the index of the Service required to get the correct
 * collection of ServiceZones</li>
 * </ul>
 * 
 */
public class AbstractHTMLPanel extends javax.swing.JPanel {

    
    protected static Log log = null;
    protected static Hashtable roleNames = new Hashtable();
    static {
        try {
            log = LogFactory.getLog(AbstractHTMLPanel.class);
        } catch (Exception ex) {}
    }
            
    /**
     *  Instance of the ContextFinder utility class, which can recursively
     *  search up the component hierarchy for an ICSGContextForm, and then
     *  retrieve its context (cached for later use).
     */
    protected ContextFinder ctxFinder = new ContextFinder(this);
    
    public AbstractHTMLPanel() {

    }

    protected void NavigationHyperlinkFilter(javax.swing.event.HyperlinkEvent evt, CSGVelocityHTMLEP control){
        String url = evt.getURL().toString();
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){        
            LeftHyperlinkClick(url, (Object)control);
        }
    }
    
    protected void DetailHyperlinkFilter(javax.swing.event.HyperlinkEvent evt,CSGVelocityHTMLEP control) {    
        String url = evt.getURL().toString();
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
            RightHyperlinkClick(url, (Object)control);
        }
    }
    protected void DetailHyperlinkFilter(javax.swing.event.HyperlinkEvent evt,CSGVelocityHTMLEP control, String url) {    
        if (evt != null){
            url = evt.getURL().toString();
        }
        RightHyperlinkClick(url, (Object)control);
    }    
    
    protected void LeftHyperlinkClick(String url, Object htmlControl){
        System.out.println("left hyperlink click entered");
            // create a hashmap containing the url params
            HashMap params = parseURLtoMap(url);

            String action = (String) params.get("action");            
            String template = (String) params.get("template");
            String object = (String) params.get("object");     
            String subtype = (String) params.get("subtype");  
            String sessionPutName = (String) params.get("sessionPutName");     
            String sessionPutValue = (String) params.get("sessionPutValue"); 

            // index of selected object within collection 
            String ind = (String) params.get("index");
            int index = -1;
            if (ind != null) {
                index = Integer.parseInt(ind);
            }

            String type = "normal";
            if (params.get("type")!=null){
                type = (String) params.get("type");
            }     

            IPersistentObject obj = null;
                
            // get this panel's context
            IContext ctx = ctxFinder.findContext();
            if (ctx != null && object != null) {        
                
                // single object/subtype
                if (index >= 0) {
                    // we need to get the object out of a collection using the index

                    // Create collection to peek inside and get selected object knowing the index
                    IPersistentCollection coll = (IPersistentCollection) ctx.getObject(object + "List",subtype);

                    if (coll !=null && coll.getCount() > 0){
                        obj=coll.getAt(index);
                    }  
                }else {
                    // pass back object if it is already in context or entire collection
                    obj = ctx.getObject(object,subtype);                                    
                }     
                
                // Swap out Lookup Object for Real Object
                if (obj != null){
                    if (obj instanceof LookupDomain){
                        // convert to real object
                        IPersistentObject realObj = obj.getObject(object,null);
                        if (realObj == null){
                            realObj = obj.getObject(object,subtype);
                        }                        
                        obj = realObj;                    
                    }
                }
                                    
                if (action == null){ 
                    // put object into context
                    ctxFinder.addTopic(obj);  
          
                }else{
                    // pass object to action

                    // display panel
                    ctxFinder.fireActionForObject(action, obj);
                }
            }
            
            if (sessionPutName != null) {
                // put the page selected into the session velocity context object                
                    ((CSGVelocityHTMLEP)htmlControl).getSession().putValue(sessionPutName,sessionPutValue);                
            }
            
                
            if (type.equals("search")){
                // reset paging to first page
                ((CSGVelocityHTMLEP)htmlControl).getSession().putValue("page",new Integer(1));  
                IPersistentCollection coll = (IPersistentCollection)obj;
                coll.reset();           
            }              
           
            boolean refresh = true;
            if (params.get("refresh") != null){
                refresh = false;
            }

            if (template != null) {
                ((CSGVelocityHTMLEP)htmlControl).setVelocityTemplateUri("template/" + template + ".vm");
                // Reset back to page 1, if appropriate
                if (((CSGVelocityHTMLEP)htmlControl).getSession().get("page") != null) {
                    ((CSGVelocityHTMLEP)htmlControl).getSession().putValue("page",new Integer(1));
                }
            }
                      
            // refresh control
            if (refresh) {
                ((CSGVelocityHTMLEP)htmlControl).initializeControl();                 
            }            
    }        
    
    
    /**
     * Utility method to process the parameters passed in the URL and do specific
     * behaviours based on each of their values. (e.g. refresh, postProcessing, etc.)
     * 
     * @param evt    The event to be passed on to the HyperlinkFilter
     * @param htmlControl   HTML control that will be passed the new template name to load
     */
    protected void RightHyperlinkClick(String url,Object htmlControl) {

        // create a hashmap containing the url params
        HashMap params = parseURLtoMap(url);

        // get object name from map, i.e. Payment, InvoiceDetail, BilledUsage, ...
        String action = (String) params.get("action");      
        String object = (String) params.get("object");     
        String subtype = (String) params.get("subtype");  
        
        String baseObject = (String) params.get("baseObject");  
        String baseCollection = (String) params.get("baseCollection");  
        String baseCollectionSubtype = (String) params.get("baseCollectionSubtype");  
        String baseCollectionIndex = (String) params.get("baseCollectionIndex");  
        String resetCollection = (String) params.get("resetCollection");
        
        String sessionPutName = (String) params.get("sessionPutName");     
        String sessionPutValue = (String) params.get("sessionPutValue");  
        
        String postProcessing = (String) params.get("postProcessing");
        String refresh = (String) params.get("refresh");
        
        String strReadOnly = (String) params.get("setReadOnly");
        boolean bSetReadOnly = false;
        if( strReadOnly != null ) {
            bSetReadOnly = Boolean.valueOf( strReadOnly ).booleanValue();
        }

        if (sessionPutName != null) {
            // put the page selected into the session velocity context object
            ((CSGVelocityHTMLEP)htmlControl).getSession().putValue(sessionPutName,sessionPutValue);             
        }           

        int baseIndex = -1;
        if (baseCollectionIndex != null) {
            baseIndex = Integer.parseInt(baseCollectionIndex);
        }

        String type = "normal";
        if (params.get("type")!=null){
            type = (String) params.get("type");
        }     
        String template = (String) params.get("template");

        if (type.equals("paging")) {
            doPagination(params, htmlControl);
        } else if (object != null){
            if (type.equals("search")){
                // reset paging to first page
                ((CSGVelocityHTMLEP)htmlControl).getSession().putValue("page",new Integer(1));                
            }
            
            // index of selected object within collection 
            String ind = (String) params.get("index");
            int index = -1;
            if (ind != null) {
                index = Integer.parseInt(ind);
            }

            // get this panel's context
            IContext ctx = ctxFinder.findContext();
            if (ctx != null) {
                IPersistentObject obj = null;
                if (baseObject != null && baseIndex >=0 ){
                    // special case when object is buried in tree with multiple branches

                    // i.e. get ServiceList from Account
                    IPersistentObject po = null;
                    if (baseObject.equals("currentOrder")) {
                        po = OrderManager.getInstance().getCurrentOrder();
                        baseCollectionSubtype = "Order";
                    } else {
                        po = ctx.getObject(baseObject,null);
                    }
                    if (po != null){
                        IPersistentCollection pcoll = (IPersistentCollection) po.getObject(baseCollection,baseCollectionSubtype);                  
                        IPersistentObject pobj = null;
                        if (pcoll.getCount() > 0){
                            // i.e. get Service from ServiceList
                            pobj=pcoll.getAt(baseIndex);
                            if (pobj !=null && index >= 0) {
                                // i.e. get ProductList from Service
                                IPersistentCollection c = (IPersistentCollection) pobj.getObject(object + "List",subtype);    

                                if (c != null && c.getCount() > 0){
                                    // i.e. get Product from ProductList
                                    obj=c.getAt(index);
                                }

                            } else {
                                // pass back object if it is already in context or entire collection
                                obj = ctx.getObject(object,subtype);                                    
                            }                               
                        }   
                    }
                
                }else{
                    // single object/subtype
                    if (index >= 0) {
                        // we need to get the object out of a collection using the index

                        // Create collection to peek inside and get selected object knowing the index
                        IPersistentCollection coll = (IPersistentCollection) ctx.getObject(object + "List",subtype);

                        if (coll !=null && coll.getCount() > 0){
                            obj=coll.getAt(index);
                        }

                        /*
                         * HACK: Special case for objects related to service.  If the Parent service is readonly
                         * then the related objects must be set to readonly.  Service is readonly if it has a
                         * pending service order.
                         */
                        if (obj != null && subtype != null && subtype.equals("Service") ){
                            IPersistentObject parent = coll.getId().getParent();
                            if (parent instanceof Service && parent.isReadOnly()){
                                // we only want to set Product, NRC, CustomerContract, and CustomerIdEquipMap to readonly NOT zones, corridor plans, etc
                                if ( obj instanceof OrderableObject ){
                                    log.debug("AbstractHTMLPanel is setting child object: " + obj.getClass() + " readonly because Service was detected as readonly");
                                    obj.setReadOnly(true);
                                }
                            }
                        }    
                        
                        // Now check the reset collection parameter; this is to solve a problem where
                        // changing an object in the collection could have consequences across the
                        // collection.
                        if (resetCollection != null && resetCollection.equals("true")) {
                            coll.reset();
                        }                                            
                    } else {
                        
                        // special case for current order object
                        if (object.equals("currentOrder")){
                            obj = (IPersistentObject) OrderManager.getInstance().getCurrentOrder();
                        }else {
                            // pass back object if it is already in context or entire collection
                            obj = ctx.getObject(object,subtype);                                    
                        }
                    }
                }

                // Swap out Lookup Object for Real Object
                if (obj != null){
                    if (obj instanceof LookupDomain){
                        // convert to real object
                        IPersistentObject realObj = obj.getObject(object,null);
                        if (realObj == null){
                            realObj = obj.getObject(object,subtype);
                        }
                        obj = realObj;                        
                    }
                }
                
                if (action == null){ 
                    // put object into context
                    ctxFinder.addTopic(obj);  
          
                }else{
                    // setReadOnly was requested so save the old read-only state prior to setting read-only
                    //   we do this so that we can return the object to its original state
                    boolean originalReadOnlyState = false;
                    if( obj != null ) {
                        originalReadOnlyState = obj.isReadOnly();
                        if( bSetReadOnly ) {
                            obj.setReadOnly( true );
                        }
                    }

                    // Fire the requested action for the selected object                    
                    ctxFinder.fireActionForObject(action, obj);
                    
                    // setReadOnly was requested so we must have set the object read-only.  Since we have
                    //   already fired the action it is time to undo the set read-only.
                    if( obj!= null && bSetReadOnly ) {
                        obj.setReadOnly( originalReadOnlyState );
                    }
                }
            }        
        } else {
            if (type.equals("search")){
                // reset paging to first page
                ((CSGVelocityHTMLEP)htmlControl).getSession().putValue("page",new Integer(1));                
            }
            
            // get this panel's context
            IContext ctx = ctxFinder.findContext();
            if (ctx != null) {
                IPersistentObject obj = null;
                if (baseObject != null && baseIndex >=0 ){
                    // special case when object is buried in tree with multiple branches

                    // i.e. get ServiceList from Account
                    IPersistentObject po = null;
                    if (baseObject.equals("currentOrder")) {
                        po = OrderManager.getInstance().getCurrentOrder();
                        baseCollectionSubtype = "Order";
                    } else {
                        po = ctx.getObject(baseObject,null);
                    }

                    if (po != null){
                        IPersistentCollection pcoll = (IPersistentCollection) po.getObject(baseCollection,baseCollectionSubtype);                  
                        IPersistentObject pobj = null;
                        if (pcoll.getCount() > 0){
                            // i.e. get Service from ServiceList
                            pobj=pcoll.getAt(baseIndex);
                            if (pobj != null && object == null) {
                                // Return the collected object from the list
                                obj = pobj;
                            }
                        }   
                    }
                }
                
                if (action == null){ 
                    // put object into context
                    ctxFinder.addTopic(obj);  
          
                } else {
                    // setReadOnly was requested so save the old read-only state prior to setting read-only
                    //   we do this so that we can return the object to its original state
                    boolean originalReadOnlyState = false;
                    if( obj != null ) {
                        originalReadOnlyState = obj.isReadOnly();
                        if( bSetReadOnly ) {
                            obj.setReadOnly( true );
                        }
                    }

                    // Fire the requested action for the selected object                    
                    ctxFinder.fireActionForObject(action, obj);
                    
                    // setReadOnly was requested so we must have set the object read-only.  Since we have
                    //   already fired the action it is time to undo the set read-only.
                    if( obj != null && bSetReadOnly ) {
                        obj.setReadOnly( originalReadOnlyState );
                    }
                }
            }
        }
        
        
        if (template != null) {
            ((CSGVelocityHTMLEP)htmlControl).setVelocityTemplateUri("template/" + template + ".vm");                                               
        }
        
        if (postProcessing != null) {
            doPostProcessing(postProcessing);
        }
                  
        if (refresh == null || refresh.equals("true")) {
            ((CSGVelocityHTMLEP)htmlControl).initializeControl();            
        }    
    }        
    
    protected void DetailHyperlinkFilter(javax.swing.event.HyperlinkEvent evt,CSGVelocityHTMLEP htmlDetailControl, CSGVelocityHTMLEP htmlNavigationControl) {
        // get url from hyperlink click
        String url = evt.getURL().toString();
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){   
            RightHyperlinkClick(url,(Object)htmlDetailControl);
            RightHyperlinkClick(url,(Object)htmlDetailControl, (Object)htmlNavigationControl);
        }
    } 
    
    protected void RightHyperlinkClick(String url, Object htmlDetailControl, Object htmlNavigationControl) {

        // create a hashmap containing the url params
        HashMap params = parseURLtoMap(url);
        
        // url params
        String refresh = (String) params.get("refresh-navigation");      
        String type = "normal";
        if (params.get("type")!=null){
            type = (String) params.get("type");
        }  
        
        // paging
        if (type.equals("paging")) {
            // screen pagination
            String page = params.get("page").toString();

            // put the page selected into the session velocity context object   
            ((CSGVelocityHTMLEP)htmlNavigationControl).getSession().putValue("page",Integer.valueOf(page));     
        }
        
        if (refresh != null && refresh.equals("true")) {
            ((CSGVelocityHTMLEP)htmlNavigationControl).initializeControl();       
        }
    }    
        
    /**
     * Parse a URL and return a hashmap of the name/value pairs.<br><br> 
     * Current parameters for the URL.  This is not a comprehensive list but
     * rather a list of the most common parameters.
     * <ul>
     * <li><b>action</b>:  Name of the panel or dialog action defined in the
     * application configurator</li>
     * <li><b>object</b>: Class name of the business object to retrieve for the
     * action; if the object being looked for is a collection, the name that
     * should be passed in is the collection class name minus "List" because
     * "List" is appended to the class name when the getObject is called to
     * retrieve the collection.</li>
     * <li><b>index</b>: If the business object needed is in a collection, the
     * index is the location in the collection of the object being retrieved.
     * </li>
     * <li><b>subtype</b>: The subtype of the object being retrieved.</li>
     * <li><b>objecttree</b>: A dot notation parameter specifying the path to
     * the needed object. This parameter only allows for traversal of object -
     * collection[object] - collection[object].</li>
     * <li><b>Example</b>: <code>http://someurl?action=some-
     * action&object=ServiceZone &index=1&objecttree=Account.ServiceList.0
     * </code> where 0 is the index of the Service required to get the correct
     * collection of ServiceZones</li>
     * </ul>
     * 
     */
    protected HashMap parseURLtoMap(String url){
        
        HashMap params = new HashMap();

        // parse url and put params into hash map
        if (url != null) {
            
            int pos = url.lastIndexOf("?");
            String queryString = url.substring(pos +1);
            StringTokenizer stParams = new StringTokenizer(queryString, "&");
            
            while (stParams.hasMoreTokens()) {
                String token = stParams.nextToken();

                StringTokenizer stParam = new StringTokenizer(token, "=");
                String field = stParam.nextToken();
                String val = null;
                try {
                    val = URLDecoder.decode(stParam.nextToken(), "UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    log.error(e.getMessage());
                    break;
                }

                if (field.equals("objecttree")) {
                    // parse base object into 
                    //  object.collection.index  (object = subtype) ,i.e. Account.ServiceList.1
                    // in html   &objecttree=Account.ServiceList.1
                    StringTokenizer stTree = new StringTokenizer(val, ".");
                    String treeToken = stTree.nextToken();
                    params.put("baseObject",treeToken);
                    params.put("baseCollectionSubtype",treeToken);
                    treeToken = stTree.nextToken();
                    params.put("baseCollection",treeToken);
                    treeToken = stTree.nextToken();
                    params.put("baseCollectionIndex",treeToken);                    
                }else{
                    params.put(field,val);
                }
                
            }
        }
        
        return params;
    }

    
    protected void doPagination(Map params, Object htmlControl) {
        // screen pagination
        String page = params.get("page").toString();

        // put the page selected into the session velocity context object
        ((CSGVelocityHTMLEP)htmlControl).getSession().putValue("page",Integer.valueOf(page));   
    }
    
    protected void doPagination(Map params, CSGVelocityHTMLEP htmlControl) {
        // screen pagination
        String page = params.get("page").toString();

        // put the page selected into the session velocity context object
        htmlControl.getSession().putValue("page",Integer.valueOf(page)); 
    }
    
    protected void doPostProcessing(String sParamValue) {
    }
    
    protected void setSelectedRow(javax.swing.event.HyperlinkEvent evt, CSGVelocityHTMLEP htmlSourceList, CSGVelocityHTMLEP htmlTargetDetailList){
        // only for when the link is clicked not hover
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED){    
            // reset/deselect row in lower left control
            if (htmlTargetDetailList!=null){
                htmlTargetDetailList.getSession().putValue("selectedObject","none");   
            }
            // get url from hyperlink click
            String url = evt.getURL().toString();
            HashMap params = parseURLtoMap(url);   
            String object = (String) params.get("object");     
            String subtype = (String) params.get("subtype");  
            String index = (String) params.get("index");      
     
            // row highlighting
            String selectedObject = null; 
            if (object!=null){
                selectedObject = object;
            }
            if (subtype!=null){
                selectedObject += "." + subtype;
            }
            if (index!=null){
                selectedObject += "." + index;
            }              
            if (selectedObject != null){
                htmlSourceList.getSession().putValue("selectedObject",selectedObject); 
                htmlSourceList.initializeControl();                    
            }            
        }            
    }           
    
    /**
     * Handles the remove logic when the user clicks on a Remove link in the New Customer Acquisition or
     *  Service Add wizards.  This function contains logic that is rather specific to the New Cust Acq
     *  wizard and the Service Add wizard and as such should not be used blindly in other wizards.
     * 
     * @param evt The HyperlinEvent
     */
    public void NewCustAcq_ServiceAdd_RemoveHyperlinkFilter(javax.swing.event.HyperlinkEvent evt, CSGVelocityHTMLEP control ) {
        // create a hashmap containing the url params
        HashMap params = parseURLtoMap( evt.getURL().toString() );

        // Get the URL parameters
        String object = (String) params.get( "object" );     
        String subtype = (String) params.get( "subtype" );
        String sIndex = (String) params.get( "index" );  
        
        String baseObject = (String) params.get( "baseObject" );  
        String baseCollection = (String) params.get( "baseCollection" );  
        String baseCollectionSubtype = (String) params.get( "baseCollectionSubtype" );  
        String baseCollectionIndex = (String) params.get( "baseCollectionIndex" );  

        // Parse the index's if they are not null
        int index = -1;
        int baseIndex = -1;
        if( sIndex != null ) {
            index = Integer.parseInt( sIndex );
        }
        
        if( baseCollectionIndex != null ) {
            baseIndex = Integer.parseInt( baseCollectionIndex );
        }
        
        // Ensure that the 'object' parameter has been set, if not abort.
        if( object == null ) {
            logError( "The parameter 'object' was not set in the URL. Aborting remove action.", null );
        }

        // All the removable things are found somewhere on or in the account object 
        //    so we may as well get it.  If the account object can't be found then we abort.
        IContext ctx = ctxFinder.findContext();
        IPersistentObject accountObject = null;
        if( ctx != null ) {
            accountObject = ctx.getObject( "Account", null );
            if( accountObject == null ) {
                // Shouldn't ever happen but paranoia is a good thing. 
                logError( "Couldn't get the Account object. Aborting remove action.", null );
                return;
            }
        }
        
        // Find the necessary objects and do the remove
        if( baseObject != null && baseObject.equals( "Account" ) ) {
            // The object/collection to be removed is inside another collection on the Account Object.
            if( baseCollection != null ) {
                IPersistentCollection baseColl = accountObject.getCollection( baseCollection, baseCollectionSubtype );
                if( baseIndex > -1 && baseColl != null && baseColl.getCount() > baseIndex ) {
                    IPersistentObject serviceObj = baseColl.getAt( baseIndex );
                    
                    if( index > -1 ) {
                        // We are looking for a collection
                        IPersistentCollection coll = serviceObj.getCollection( object + "List", subtype );
                        if( coll != null ) {
                            IPersistentObject objToRemove = coll.getAt( index );
                            if( objToRemove != null ) {
                                serviceObj.removeAssociation( objToRemove );
                            }
                        } else {
                            logDebug( "The collection that was suppose to contain the object we are to remove was null." );
                        }
                    } else {
                        // Single object
                        IPersistentObject objToRemove = serviceObj.getObject( object, subtype );
                        serviceObj.removeAssociation( objToRemove );
                    }
                } else {
                    logDebug( "Couldn't get the base collection, or the baseCollectionIndex value was not set." );
                }
            }
        } else {
            // The collection to remove the object from is on the Account object.
            if( index > -1 ) {
                IPersistentCollection coll = null;
                if( object.equals( "Prepayment" )  && ctx != null ) {
                    coll = ctx.getCollection( object + "List", subtype );
                } else {
                    coll = accountObject.getCollection( object + "List", subtype );
                }
                
                if( coll != null ) {
                    IPersistentObject objToRemove = coll.getAt( index );
                    if( objToRemove != null ) {
                        if( objToRemove instanceof AccountBalances ) {
                            handleAccountBalancesRemove( accountObject, objToRemove );
                        } else if( object.equals( "Prepayment" ) ) {
                            // Must be removed from the account as well as the
                            //  collection sitting on the context.
                            accountObject.removeAssociation( objToRemove );
                            coll.remove( objToRemove );
                        } else {
                            accountObject.removeAssociation( objToRemove );
                        }
                    }
                } else {
                    logDebug( "Couldn't get the collection to remove the object from." );
                }
            } else {
                IPersistentObject objToRemove = accountObject.getObject( object, subtype );
                if( objToRemove != null ) {
                    accountObject.removeAssociation( objToRemove );
                } else {
                    logDebug( "Couldn't get the object to remove." );
                }
            }
        }

        control.initializeControl();
    }

    /**
     * Contains the logic required to ensure that the Account Balance can be removed.  If it can not
     *  be removed then a dialog will be shown to the user containing the list of stuff that must
     *  be removed first.
     * 
     * @param accountObject The Account object that the AccountBalance is on.
     * @param acctBalObj The Account Balance object.
     */
    protected void handleAccountBalancesRemove( IPersistentObject accountObject, IPersistentObject acctBalObj ) {
        // Create two collections to hold the list of items that still refernce the AccountBalance
        //   set the collections faulted as we will be the ones putting stuff in them.
        IPersistentCollection acctChargeRuleList = (IPersistentCollection)PersistentObjectFactory.getFactory().create( "OpenItemIdMapList", "Account" );
        IPersistentCollection servicesChargeRuleList = (IPersistentCollection)PersistentObjectFactory.getFactory().create( "OpenItemIdMapList", "Service" );
        IPersistentCollection paymentList = (IPersistentCollection)PersistentObjectFactory.getFactory().create( "PaymentList", null );
        acctChargeRuleList.setFaulted( true );
        servicesChargeRuleList.setFaulted( true );
        paymentList.setFaulted( true );
        
        IPersistentCollection coll = null;

        // The values to compare to 
        int abOpenItemId = acctBalObj.getAttributeDataAsInteger( "OpenItemId" );
        String acctExtId = accountObject.getAttributeDataAsString( "AccountExternalId" );
        String acctIntId = accountObject.getAttributeDataAsString( "AccountInternalId" );
        
        // First up, loop through the payment list and see if any of them reference the AccountBalance. 
        coll = accountObject.getCollection( "PaymentList", "Account" );
        if( coll != null ) {
            for( int i = 0; i < coll.getCount(); i++ ) {
                IPersistentObject obj = coll.getAt( i );
                if( obj.getAttributeDataAsInteger( "OpenItemId") == abOpenItemId &&
                    obj.getAttributeDataAsString( "AccountExternalId" ).equals( acctExtId ) ) {
                    
                    // This payment references the AccountBalances object
                    paymentList.add( obj );
                }
            }
        }

        /* Now the fun begins...  
         * 
         * 1) Loop through all the Charge Rules on the account to see if they reference the AccountBalances Object
         * 2) Loop through all the Charge Rules on every Service to see if they reference the AccountBalances object
         */
        coll = accountObject.getCollection( "OpenItemIdMapList", "Account" );
        if( coll != null ) {
            for( int i = 0; i < coll.getCount(); i++ ) {
                IPersistentObject obj = coll.getAt( i );
                if( obj != null &&
                    obj.getAttributeDataAsInteger( "OpenItemId") == abOpenItemId &&
                    obj.getAttributeDataAsString( "BalanceAccountInternalId" ).equals( acctIntId ) ) {
                    
                    // This Charge Rule references the AccountBalances object the user wants to delete.
                    acctChargeRuleList.add( obj );
                }
            }
        }
        
        IPersistentCollection serviceList = accountObject.getCollection( "ServiceList", "Account" );
        if( serviceList != null ) {
            for( int i = 0; i < serviceList.getCount(); i++ ) {
                IPersistentObject serviceObj = serviceList.getAt( i );
                coll = serviceObj.getCollection( "OpenItemIdMapList", "Service" );
                if( coll != null ) {
                    for( int x = 0; x < coll.getCount(); x++ ) {
                        IPersistentObject obj = coll.getAt( x );
                        if( obj != null &&
                            obj.getAttributeDataAsInteger( "OpenItemId") == abOpenItemId &&
                            obj.getAttributeDataAsString( "BalanceAccountInternalId" ).equals( acctIntId ) ) {
                        
                            servicesChargeRuleList.add( obj );
                        }
                    }
                }
            }
        }

        // If any of the lists have objects in them, then 1 or more objects reference
        //   the AccountBalances object.  If this is the case then pop a dialog that
        //   display's a list of all the items still referencing the AccountBalances object.
        if( paymentList.getCount() > 0 ||
            acctChargeRuleList.getCount() > 0 ||
            servicesChargeRuleList.getCount() > 0 ) {
                
            HashMap params = new HashMap();
            params.put( "AccountPaymentList", paymentList );
            params.put( "AccountChargeRuleList", acctChargeRuleList );
            params.put( "ServiceChargeRuleList", servicesChargeRuleList );    

            // Launch the dialog that displays what the user needs to remove first.
            ctxFinder.fireActionForObject( "account-balances-remove-requirements-list", params );
        } else {
            accountObject.removeAssociation( acctBalObj );
        }
    }

    /////////////////////////////////////////////////////////
    // Utility functions used by the remove handler 
    /////////////////////////////////////////////////////////
    protected void logDebug( String msg ) {
        if( log != null ) {
            log.debug( msg );
        }
    }

    protected void logWarn( String msg, Throwable ex ) {
        if( log != null ) {
            log.warn( msg, ex );
        }
    }

    protected void logError( String msg, Throwable ex ) {
        if( log != null ) {
            log.error( msg, ex );
        }
    }

}