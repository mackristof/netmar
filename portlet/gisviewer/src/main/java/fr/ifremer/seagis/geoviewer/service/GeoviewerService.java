package fr.ifremer.seagis.geoviewer.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletSession;
import javax.xml.bind.JAXBException;

import org.mapfaces.model.Context;
import org.mapfaces.model.Session;

import fr.ifremer.seagis.common.service.CommonService;

/**
 * Service interface for all the data process of the Geoviewer portlet.
 * All the portlet's process prototypes has to be here.
 * @author leopratlong
 */
public interface GeoviewerService extends CommonService {
	
    /**
     * Returns the OWS Context as file.
     * 
     * @param context FacesContext
     * @param contextId Id of the OWS Context.
     * @return File The OWS Context as file.
     * @throws UnsupportedEncodingException
     * @throws JAXBException
     * @throws IOException
     */
	File getOWSContextFile(final FacesContext context, final String contextId) throws UnsupportedEncodingException, JAXBException, IOException;

	/**
	 * Retrieve the MapFaces Context for the specified ID.
	 * 
	 * @param context FacesContext
	 * @param contextId Id of the MFContext.
	 * @return Context
	 */
	Context getMFContext(final FacesContext context, final String contextId);
	
	/**
	 * Find the new selected layer then add it to the current basket.
	 * 
	 * @param context FacesContext
	 * @param modelId Id of the MFContext.
	 */
	void findLayerAndAddToBasket(final FacesContext context, final String modelId);
	
	/**
	 * Transforms a String which represents a list of predefined zooms into a list of 
	 * SelectItem (according to a pattern).
	 * 
	 * @param zoomList String
	 * @param selectItems
	 */
    void addZoomsToList(final String zoomList, final List<SelectItem> selectItems);	
    
    /**
     * Share the basket through the public PortletSession with portal and other portlets.
     * 
     * @param context FacesContext
     */
    void shareSession(final FacesContext context);
    
    /**
     * Returns current user MFSession.
     * 
     * @param context FacesContext
     * @param psession PortletSession
     * @return Session
     */
    Session getSession(final FacesContext context, final PortletSession psession);
    
    /**
     * Returns the PortletSession.
     * 
     * @param context FacesContext
     * @return PortletSession
     */
    PortletSession getPortletSession(final FacesContext context);
    
    /**
     * @param context
     * @return
     */
    int getNbLayer(final FacesContext context);

    /**
     * @param WMSList
     * @return
     */
    List<SelectItem> addUrlsToList(final String WMSList);
    
    /**
     * @param context
     */
    void removeLayersFromCatalogue(final FacesContext context);
}
