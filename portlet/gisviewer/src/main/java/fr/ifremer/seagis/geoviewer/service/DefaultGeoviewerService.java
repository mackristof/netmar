package fr.ifremer.seagis.geoviewer.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletSession;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBException;

import org.mapfaces.model.Context;
import org.mapfaces.model.DefaultContext;
import org.mapfaces.model.Layer;
import org.mapfaces.model.Session;
import org.mapfaces.model.DefaultSession;
import org.mapfaces.utils.FacesPortletUtils;
import org.mapfaces.utils.FacesUtils;
import org.mapfaces.utils.SessionStorage;
import org.mapfaces.utils.XMLContextUtilities;

import fr.ifremer.seagis.common.service.DefaultCommonService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.util.logging.Logging;

/**
 * Implementation of the Geoviewer services.
 * @author leopratlong
 */
public class DefaultGeoviewerService extends DefaultCommonService implements GeoviewerService {
    
    private static final String ADDTOBASKETKEY = "ADD_TO_BASKET";
    private static final Logger LOGGER = Logging.getLogger(DefaultGeoviewerService.class.getName());
    private static final String CATALOGUE_IPC = "LIFERAY_SHARED_GEOVIEWER";
    
	@Override
	public File getOWSContextFile(final FacesContext context, final String contextId) throws UnsupportedEncodingException, JAXBException, IOException {
	    
        
        final Context model = getMFContext(context, contextId);
	    if (model != null) {
    		final ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
    		final String path = servletContext.getRealPath("/WEB-INF");
    		final File f = new File(path + "/temp.tmp");
    		XMLContextUtilities.writeContext(model, f);
    		return f;
	    } else {
            LOGGER.log(Level.SEVERE, "No OWS-Context file to save !!!");
	        return null;
	    }
	}

    @Override
    public Context getMFContext(final FacesContext context, final String contextId) {
        if (context != null) {
            
            final Object contextObj = FacesUtils.getSessionMapValue(context, contextId);
            if (contextObj instanceof DefaultContext) {
                return (Context) contextObj;
            } else {                
                LOGGER.log(Level.SEVERE, "No MapFaces context model in session map !!!");
            }
        }
        return null;
	}
    
    @Override
    public void findLayerAndAddToBasket(final FacesContext context, final String modelId) {
        final String layerId = (String) FacesUtils.getRequestParameterValue(context, "addToBasket");
        if (layerId != null) {
            final Context model = getMFContext(context, modelId);
            if (model != null) {
                final Layer layer = model.getLayerFromId(layerId);
                if (layer == null) {
                    return;
                }
                final String mdUrl = layer.getMetadataUrl();
                /**
                 * The MD Name will be the layer name until we find a better solution.
                 */
                final String name = layer.getName();
                
                if ((mdUrl != null) && (name != null)) {                
                    final PortletSession psession = getPortletSession(context);
                    Session session = getSession(context, psession);
                    if (session == null) {
                        session = new DefaultSession(psession.getId());
                        SessionStorage.getInstance().put(psession.getId(), session);
                    }
                    final Map<String, Object> userValues = session.getUserValues();
                    Map<String, List<String>> items = (Map<String, List<String>>) userValues.get(PANIERKEY);
                    
                    if (items == null) {
                        items = new HashMap<String, List<String>>();
                        userValues.put(PANIERKEY, items);
                    }
                    if (!items.containsKey(mdUrl)) {
                        final List<String> names = new ArrayList<String>();
                        names.add(name);
                        items.put(mdUrl, names);
                    } else {
                        final List<String> names = items.get(mdUrl);
                        if (!names.contains(name)) {
                            names.add(name);
                        }
                    }
                    userValues.put(ADDTOBASKETKEY, true);
                   /* 
                    if (basket == null) {
                        basket = new ArrayList<String>();
                    }
                    if (!basket.contains(mdUrl)) {
                        basket.add(mdUrl);
                        userValues.put(PANIERKEY, basket);
                        userValues.put(ADDTOBASKETKEY, true);
                    } */
                }
            }
        }
    }
    
    @Override
    public void addZoomsToList(final String zoomList, final List<SelectItem> selectItems) {
        if ((zoomList != null) && (selectItems != null)) {
            selectItems.clear();
            final StringTokenizer tokens = new StringTokenizer(zoomList, ";");
            while (tokens.hasMoreTokens()) {
                final String token = tokens.nextToken();
                final int coordLimit = token.indexOf(":");
                if (coordLimit == -1) {
                    selectItems.add(new SelectItem(token));
                } else {
                    final String zoom = token.substring(0, coordLimit);
                    final String title = token.substring(coordLimit + 1);
                    selectItems.add(new SelectItem(zoom, title));
                }
            }
        }
    }
    
    @Override
    public List<SelectItem> addUrlsToList(final String urlList) {
        if (urlList != null) {
            final List<SelectItem> list = new ArrayList<SelectItem>();
            final String[] arrayTmp = urlList.split(";");
            
            for (int i = 0; i < arrayTmp.length; i++) {
                final String[] tmp = arrayTmp[i].split(",");   
                
                if (tmp != null && tmp.length == 2) {
                    final String urlLabel = tmp[0].trim();
                    final String url = tmp[1].trim();
                    
                    if (url != null && !url.isEmpty()) {                    
                        list.add(new SelectItem(url, (urlLabel == null || 
                                urlLabel.isEmpty()) ?  url : urlLabel)); 
                    }
                }
            }
            return (list.isEmpty() ? null : list);
        }
        return null;
    }
    
    @Override
    public PortletSession getPortletSession(final FacesContext context) {
        return (PortletSession) context.getExternalContext().getSession(false);
    }
    
    @Override
    public Session getSession(final FacesContext context, final PortletSession psession) {
        return SessionStorage.getInstance().get(psession.getId());
    }
    
    @Override
    public int getNbLayer(final FacesContext context) {
        int i = 0;
        final Session session = getSession(context, getPortletSession(context));
        if (session != null) {
            final Map<String, List<String>> basket = (Map<String, List<String>>) session.getUserValues().get(PANIERKEY);
            if (basket != null) {
                for (final List<String> names : basket.values()) {
                    i = i + names.size();
                }
            }
        }
        return i;
    }
    
    @Override
    public void shareSession(final FacesContext context) {
        try {
            final PortletSession psession = getPortletSession(context);
            if (psession != null) {
                final Session session = getSession(context, psession);
                if (session != null) {
                    final Map<String, Object> userValues = session.getUserValues();
                    if (userValues != null && !userValues.isEmpty()) {
                        final boolean addToBasket = (Boolean) userValues.get(ADDTOBASKETKEY);
                        if (addToBasket) {
                            final Map<String, List<String>> basket = (Map<String, List<String>>) userValues.get(PANIERKEY);
                            if (basket != null) {
                                Map<String, Map<String, List<String>>> globalBasket = (Map<String, Map<String, List<String>>>) FacesPortletUtils.getPublicPortletSessionAttribute(context, PANIERKEY);
                                if (globalBasket == null) {
                                    globalBasket = new HashMap<String, Map<String,List<String>>>();
                                    FacesPortletUtils.setPublicPortletSessionAttribute(context, PANIERKEY, globalBasket);
                                }
                                globalBasket.put(getCurrentCommunity(context), basket);
                            }
                            userValues.put(ADDTOBASKETKEY, false);
                        } else {
                            userValues.put(PANIERKEY, getItemsFromBasketForCommunity(context, getCurrentCommunity(context)));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            /**
             * Yes, it's not a good habit to catch Exception but since Liferay blocks our
             * throwed exception, then we need to print a log trace.
             */
            ex.printStackTrace();
        } catch (Error er) {
            er.printStackTrace();
        }
    }
    
    @Override
    public void removeLayersFromCatalogue(final FacesContext context) {
        final  PortletSession psession = getPortletSession(context);
        if (psession != null) {
            final Map<String, List<Map<String, String>>> geoviewerIpc = (Map<String, List<Map<String, String>>>) FacesPortletUtils.getPublicPortletSessionAttribute(context, CATALOGUE_IPC);
            if (geoviewerIpc != null) {
                final String community = getCurrentCommunity(context);
                if (community != null) {
                    final List<Map<String, String>> currentIpc = geoviewerIpc.get(community);
                    if (currentIpc != null) {
                        currentIpc.clear();
                    }
                }
            }
        }
    }
}
