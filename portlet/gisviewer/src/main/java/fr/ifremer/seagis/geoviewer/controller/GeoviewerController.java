package fr.ifremer.seagis.geoviewer.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.ActionRequest;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.fileupload.FileItem;
import org.mapfaces.model.Context;
import org.mapfaces.model.DefaultDownloadedFile;
import org.mapfaces.model.DownloadedFile;
import org.mapfaces.model.FeaturesStore;
import org.mapfaces.utils.XMLContextUtilities;
import org.opengis.feature.type.PropertyDescriptor;

import com.liferay.util.bridges.jsf.common.JSFPortletUtil;

import fr.ifremer.seagis.geoviewer.service.DefaultGeoviewerService;
import fr.ifremer.seagis.geoviewer.service.GeoviewerService;
import fr.ifremer.seagis.model.SextantConfig;

/**
 * Controller for the Geoviewer portlet.
 * @author leopratlong
 */
public class GeoviewerController implements Serializable {

    private static Logger LOGGER = Logger.getLogger(GeoviewerController.class.getName());
    private static String KEY = "mainCtx";
    private Object ctxService = "/data/Netmar.xml";
    private String currentCommunity;
    private SextantConfig sextantConfig;
    
    // Url of the application used to format metadata
    private String mdViewerUrl;
    private boolean activateGraticuleTool;
    
    private static GeoviewerService GEOVIEWERSERVICE = new DefaultGeoviewerService();
    
    ////////////////////////////////////////////GET FEATURE INFO ////////////////////////////////////////////////
    /* GetFeatureInfo output format */
    private String getFeatureInfoOutputFormat = "application/vnd.ogc.gml";
    /*Results of GetFeatureInfo as stirng format */
    private List<String> getFeatureInfoResults = new ArrayList<String>();
    private List<FeaturesStore> featuresStores = new ArrayList<FeaturesStore>();
    
    //////////////////////////////////////////// ZoomList ////////////////////////////////////////////////
    private final List<SelectItem> selectItems = new ArrayList<SelectItem>();
    
    //////////////////////////////////////////// WMSList ////////////////////////////////////////////////
    private List<SelectItem> WMSList = null;
    
    //////////////////////////////////////////// WPSList ////////////////////////////////////////////////
    private List<SelectItem> WPSList = null;
    private Boolean WPSActive;
    
    private String maxExtent;
    private String maxExtentLocalisation;

    /**
     * Action called when user want to download the OWC file.
     *
     * @return DownloadedFile : MapFaces model for the downloaded File (name, type, resource...).
     */
    public DownloadedFile getOWCFile() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final DownloadedFile downloadedFile = new DefaultDownloadedFile();

        try {
            final File f = GEOVIEWERSERVICE.getOWSContextFile(context, KEY);
            if (f != null) {
                FileInputStream fis;
                try {
                    fis = new FileInputStream(f);
                    downloadedFile.setResource(fis);
                    downloadedFile.setFileName("ows_context.xml");
                    downloadedFile.setMimeType("text/xml");
                } catch (FileNotFoundException e) {
                    LOGGER.log(Level.WARNING, "Error while trying to write the OWS file.");
                }
                f.delete();
            }
        } catch (UnsupportedEncodingException e1) {
            LOGGER.log(Level.WARNING, "Error while trying to write the OWS file.");
        } catch (JAXBException e1) {
            LOGGER.log(Level.WARNING, "Error while trying to write the OWS file.");
        } catch (IOException e1) {
            LOGGER.log(Level.WARNING, "Error while trying to write the OWS file.");
        }
        return downloadedFile;
    }
    
    /**
     * Called by geoviewer_ipc portlet.
     * 
     * @return
     */
    public String getShareSession() {
        final FacesContext context = FacesContext.getCurrentInstance();
        GEOVIEWERSERVICE.shareSession(context);
        GEOVIEWERSERVICE.removeLayersFromCatalogue(context);
        return null;
    }

    /**
     * Action called when user validate his OWC file upload.
     */
    public void uploadOWCFile() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final ActionRequest request = (ActionRequest) JSFPortletUtil.getPortletRequest(context);
        final FileItem item = (FileItem) request.getAttribute("resumeFile");
        
        try {
            final Context model = XMLContextUtilities.readContext(item.getInputStream());
            if (model != null) {
                model.setReloading(true);
                setCtxService(model);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, "Error while trying to read the OWS file.");
        } catch (MalformedURLException e) {
            LOGGER.log(Level.WARNING, "Error while trying to read the OWS file.");
        } catch (JAXBException e) {
            LOGGER.log(Level.WARNING, "Error while trying to read the OWS file.");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error while trying to read the OWS file.");
        }
    }

    /**
     * Add a layer to the user basket. Technically, we use IPC to send information to the "Panier" portlet.
     */
    public void addToBasket() {
        GEOVIEWERSERVICE.findLayerAndAddToBasket(FacesContext.getCurrentInstance(), KEY);
    }

    /**
     * This is an action event for the getFeatureInfo
     * @throws JAXBException
     * @throws IOException
     * @throws XMLStreamException
     */
    public void afterGetFeatureInfoRequests() throws JAXBException, IOException, XMLStreamException {
        System.out.println("Action afterGetFeatureInfoRequests");

        for (final FeaturesStore featuresStore : featuresStores) {
            System.out.println("FeatureType : " + featuresStore.getFeatureType().getName().getLocalPart());

            for (final org.opengis.feature.Feature feature : featuresStore.getFeatures()) {
                System.out.println("\t\t Feature : " + feature.getIdentifier().getID());

                for (final PropertyDescriptor prop : featuresStore.getFeatureType().getDescriptors()) {
                    System.out.println("\t\t\t\t Property : " + prop.getName().getLocalPart() + " = " + feature.getProperty(prop.getName()));
                }
            }
        }
    }

    /**
     * This method build the initial configuration for the current community.
     */
    private void makeConfig() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final String newCommunity = GEOVIEWERSERVICE.getCurrentCommunity(context);
        
        if ((newCommunity != null) && !newCommunity.equals(currentCommunity)) {
            sextantConfig = GEOVIEWERSERVICE.getConfiguration(context);  
            currentCommunity = newCommunity; 
            WMSList = GEOVIEWERSERVICE.addUrlsToList(sextantConfig.getGeoviewerWMSList());
            WPSList = GEOVIEWERSERVICE.addUrlsToList(sextantConfig.getGeoviewerWPSList());
            if ("yes".equals(sextantConfig.getGeoviewerWPSActive())) {
                WPSActive = true;
            } else {
                WPSActive = false;
            }
            GEOVIEWERSERVICE.addZoomsToList(sextantConfig.getGeoviewerZoomList(), selectItems);
            final String contextUrl = sextantConfig.getGeoviewerOWCUrl();               
            maxExtentLocalisation = GEOVIEWERSERVICE.getMaxExtentFromString(
                    sextantConfig.getGeoviewerLocalWest(), sextantConfig.getGeoviewerLocalSouth(),
                    sextantConfig.getGeoviewerLocalEast(), sextantConfig.getGeoviewerLocalNorth());
            maxExtent = GEOVIEWERSERVICE.getMaxExtentFromString(
                    sextantConfig.getGeoviewerWest(), sextantConfig.getGeoviewerSouth(),
                    sextantConfig.getGeoviewerEast(), sextantConfig.getGeoviewerNorth());
            
            mdViewerUrl = sextantConfig.getMdViewerUrl();
            activateGraticuleTool = "yes".equals(sextantConfig.getGeoviewerToolGraticule());
            
            //System.out.println("\n*************************************  contextUrl = " + contextUrl  + " *********************************\n");
            if (contextUrl != null) {
                setCtxService(contextUrl);
            }
        }
        context.getViewRoot().setLocale(GEOVIEWERSERVICE.getLocale(context));
    }

    public String getMdViewerUrl() {
        return mdViewerUrl;
    }
    
    /**
     * @return the featuresStores
     */
    public List<FeaturesStore> getFeaturesStores() {
        return featuresStores;
    }

    public void setCtxService(Object ctxService) {
        this.ctxService = ctxService;
    }

    public Object getCtxService() {
        return ctxService;
    }
    
    /**
     * @return the GetFeatureInfoOutputFormat
     */
    public String getGetFeatureInfoOutputFormat() {
        return getFeatureInfoOutputFormat;
    }

    /**
     * @return the GetFeatureInfoResults
     */
    public List<String> getGetFeatureInfoResults() {
        return getFeatureInfoResults;
    }

    /**
     * @param getFeatureInfoResults the GetFeatureInfoResults to set
     */
    public void setGetFeatureInfoResults(List<String> getFeatureInfoResults) {
        this.getFeatureInfoResults = getFeatureInfoResults;
    }
    
    /**
     * @param featuresStores the featuresStores to set
     */
    public void setFeaturesStores(List<FeaturesStore> featuresStores) {
        this.featuresStores = featuresStores;
    }

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }
    
    /**
     * Initializes the Geoviewer portlet state : it has been called by the geoviewer.xhtml through
     * an outputText. It will not render a text since we return a null value, but will initialize
     * all needed values for the Geoviewer.
     * 
     * @return
     */
    public String getInit() {
        //("\n*************************************  getInti() *********************************\n");
        try {
            makeConfig();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
        } catch (Error er) {
            LOGGER.log(Level.SEVERE, er.getMessage());
        }
        return null;
    }

    public String getMaxExtent() {
        return maxExtent;
    }

    public String getMaxExtentLocalisation() {
        if (maxExtentLocalisation == null) {
            maxExtentLocalisation = "-180,-90,180,90";
        }
        return maxExtentLocalisation;
    }
    
    public String getCurrentCommunity() {
        return GEOVIEWERSERVICE.getCurrentCommunity(FacesContext.getCurrentInstance());
    }
    
    /**
     * @return the nbLayers
     */
    public int getNbLayers() {
        return GEOVIEWERSERVICE.getNbLayer(FacesContext.getCurrentInstance());
    }

    /**
     * @return the WMSList
     */
    public List<SelectItem> getWMSList() {
        return WMSList;
    }
    
    /**
     * @return the WPSList
     */
    public List<SelectItem> getWPSList() {
        return WPSList;
    }

    public Boolean getWPSActive() {
        return WPSActive;
    }

    public boolean isActivateGraticuleTool() {
        return activateGraticuleTool;
    }
}
