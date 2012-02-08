/*
 *    Mapfaces -
 *    http://www.mapfaces.org
 *
 *    (C) 2007 - 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.mapfaces.portal.utils;

import com.liferay.portal.util.PortalUtil;
import javax.portlet.PortletRequest;

/**
 * This class must be overrided in your portlet if you want all the MapFaces functionnalities,
 * all of these functions are portal specific and we don't want MapFaces depends on portal jars.
 * 
 * @author Olivier Terral (Geomatys)
 */
public final class PortalUtils {


    private PortalUtils() {
    }

    /**
     * Retrieve the original servlet request, usefull to retrieve URL GET parameters in JSF component renderers
     * like http://localhost:8080/web/guest/geoviewer?url=myurl
     * @param request
     * @return
     */
    public static Object getOriginalServletRequest(PortletRequest request) {
        //Never implement this function, override the class in your portlet.
        return PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
    }


}
