package com.poimobile.mobile.rest.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import oracle.adfmf.dc.ws.rest.RestServiceAdapter;
import oracle.adfmf.framework.api.Model;
import oracle.adfmf.java.beans.ProviderChangeListener;
import oracle.adfmf.java.beans.ProviderChangeSupport;
import oracle.adfmf.util.Utility;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * POIREST client
 */
public class POIRESTClient {

    public static final String POIS_HOST_CONNECTION = "POIREST";
    private final ProviderChangeSupport providerChangeSupport = new ProviderChangeSupport(this);
    private List<POI> _registeredPOIs;

    public POIRESTClient() {
        initConnection();
    }

    /**
     * Init connection
     */
    private void initConnection() {

        List<POI> pois = invokeRestService("/list", RestServiceAdapter.REQUEST_TYPE_GET, "", "application/json");

        if (pois != null) {
            this.setRegisteredPOI(pois);
        }
    }

    /**
     * Find nearest action listener
     *
     * @param _x
     * @param _y
     * @param _maxDistance
     */
    public void nearestPOIs(final int _x, final int _y, final int _maxDistance) {

        List<POI> pois =
            invokeRestService("/nearest/" + _x + "/" + _y + "/" + _maxDistance, 
                              RestServiceAdapter.REQUEST_TYPE_GET,
                              "",
                              "application/json");

        if (pois != null) {
            this.setRegisteredPOI(pois);
        }
    }
    
    /**
     * Create POI
     *
     * @param _x
     * @param _y
     * @param _name
     */
    @SuppressWarnings("unchecked")
    public void createPOI(final int _x, final int _y, final String _name) {
        
        invokeRestService("/create?name="+_name+"&x="+_x+"&y="+_y, 
                          RestServiceAdapter.REQUEST_TYPE_POST, "", "application/x-www-form-urlencoded");
        System.out.println(" >>>>>>>>>>> After invoke");
        initConnection();
        System.out.println(" >>>>>>>>>>>>>>> After init conn");
    }

    public void cleanRegisteredPOIs() {
        setRegisteredPOI(new ArrayList<POI>());
    }

    public void setRegisteredPOI(List<POI> _pois) {
        this._registeredPOIs = _pois;
        providerChangeSupport.fireProviderRefresh("registeredPOIs");
    }

    public void addProviderChangeListener(ProviderChangeListener l) {
        providerChangeSupport.addProviderChangeListener(l);
    }

    public void removePropertyChangeListener(ProviderChangeListener l) {
        providerChangeSupport.removeProviderChangeListener(l);
    }

    public List<POI> getRegisteredPOIs() {
        return _registeredPOIs;
    }

    /**
     * refresh action Listener method
     */
    public void refresh() {
        initConnection();
    }

    /**
     * Invoke REST service client
     *
     * @param uri
     * @param httpMethod
     * @param payload
     * @return List<POI> pois
     */
    private List<POI> invokeRestService(final String uri, final String httpMethod, final String payload, final String contentType) {

        RestServiceAdapter restServiceAdapter = Model.createRestServiceAdapter();
        restServiceAdapter.clearRequestProperties();
        restServiceAdapter.setConnectionName(POIS_HOST_CONNECTION);
        restServiceAdapter.setRequestType(httpMethod);
        restServiceAdapter.addRequestProperty("Content-Type", contentType);
//        restServiceAdapter.addRequestProperty("Accept", "application/json; charset=UTF-8");
        restServiceAdapter.setRetryLimit(0);
        restServiceAdapter.setRequestURI(uri);

        List<POI> pois = new ArrayList<POI>();

        try {
            String jsonResponse = restServiceAdapter.send(payload);
            System.out.println(">>>>>>>>> Uri: " + uri);
            System.out.println(">>>>>>>>> HTTP method: " + httpMethod);
            System.out.println(">>>>>>>>> Payload: " + payload);
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                JSONParser jsonParser = new JSONParser();
                JSONArray jsonArray = (JSONArray) jsonParser.parse(jsonResponse);

                if (jsonArray != null) {
                    for (Object o : jsonArray) {
                        JSONObject jo = (JSONObject) o;
                        pois.add(new POI(String.valueOf(jo.get("name")), Integer.parseInt(String.valueOf(jo.get("x"))),
                                         Integer.parseInt(String.valueOf(jo.get("y")))));
                    }
                }
            }

        } catch (Exception e) {
            Utility.FrameworkLogger.logp(Level.SEVERE, POIRESTClient.class.getName(), "initFromConnection",
                                         "Error parsing JSON object " + e.getMessage());
        }

        return pois;
    }

}
