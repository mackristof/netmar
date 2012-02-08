package fr.ifremer.seagis.geoviewer.listener;

import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;
import org.geotoolkit.util.logging.Logging;

import org.mapfaces.model.DefaultSession;
import org.mapfaces.model.Session;
import org.mapfaces.utils.SessionStorage;

public class SessionListener implements PhaseListener {
    
    // Logger
    private static final Logger LOGGER = Logging.getLogger(SessionListener.class.getName());
    
    @Override
    public void afterPhase(PhaseEvent event) {
        final FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx != null) {           
            HttpSession httpSession = null;
            final ExternalContext extContext = ctx.getExternalContext(); 
            final Object obj = extContext.getSession(false);
            
            if (obj instanceof HttpSession)
                httpSession = (HttpSession) obj;
            else
                LOGGER.fine("The current session is not an HTTP session, can't store the session id.");
            
            if (httpSession == null) {
                return;
            }
            final SessionStorage sessionStorage = SessionStorage.getInstance();
            final String sessionId = httpSession.getId();
            final Session session = sessionStorage.get(sessionId);
            if (session == null) {
                sessionStorage.put(sessionId, new DefaultSession(sessionId));
            }
            SessionStorage.getInstance().refresh(sessionId);
        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {}

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
