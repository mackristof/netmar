package fr.ifremer.seagis.geoviewer.facesportlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.faces.GenericFacesPortlet;

import org.apache.commons.fileupload.FileUploadException;
import org.mapfaces.actionrequest.FileUploadActionRequestWrapper;
import org.mapfaces.utils.PortletFileUploadUtils;

/**
 * FacesPortlet class to wrap ActionRequest in a FileUploadActionRequestWrapper (to
 * manage the FileUpload).
 * @author leopratlong
 */
public class GeoviewerFacesPortlet extends GenericFacesPortlet {
	private static Logger LOGGER = Logger.getLogger(GeoviewerFacesPortlet.class.getName());
	
    public GeoviewerFacesPortlet() {
        super();
    }
		
    @Override
    public void processAction(ActionRequest request, ActionResponse response)
            throws IOException, PortletException {
    	
        ActionRequest myRequest = request;
		
        if (PortletFileUploadUtils.isMultipart(request)) {
            try {
                myRequest = new FileUploadActionRequestWrapper(request);
            } catch (FileUploadException e) {
            	LOGGER.log(Level.SEVERE, "File upload failed during the wrapping of the request.");
            }
        }	
        super.processAction(myRequest, response);
    }
}
