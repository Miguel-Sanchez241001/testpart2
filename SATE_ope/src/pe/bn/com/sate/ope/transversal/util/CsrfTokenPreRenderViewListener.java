package pe.bn.com.sate.ope.transversal.util;

import java.util.UUID;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.*;
import org.apache.log4j.Logger;

public class CsrfTokenPreRenderViewListener implements SystemEventListener {

    private static final String CSRF_TOKEN_SESSION_ATTR = "csrf_token";
    private static final Logger logger = Logger.getLogger(CsrfTokenPreRenderViewListener.class);

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();

        // Obtener o generar el token CSRF
        String csrfToken = (String) externalContext.getSessionMap().get(CSRF_TOKEN_SESSION_ATTR);
        if (csrfToken == null) {
            csrfToken = UUID.randomUUID().toString();
            externalContext.getSessionMap().put(CSRF_TOKEN_SESSION_ATTR, csrfToken);
            logger.debug("CSRF token generado y almacenado en sesión: " + csrfToken);
        } else {
            logger.debug("CSRF token obtenido de sesión: " + csrfToken);
        }

        // Agregar el token CSRF a los formularios
        UIComponent root = context.getViewRoot();
        agregarTokenCsrfAFormularios(root, csrfToken);
    }

    @Override
    public boolean isListenerForSource(Object source) {
        return source instanceof UIViewRoot;
    }

    private void agregarTokenCsrfAFormularios(UIComponent componente, String csrfToken) {
        if (componente instanceof UIForm) {
            logger.debug("Agregando CSRF token al formulario con ID: " + componente.getClientId());

             String inputId = FacesContext.getCurrentInstance().getViewRoot().createUniqueId();
           
            HtmlInputHidden inputOculto = new HtmlInputHidden();
            inputOculto.setId(inputId);
            inputOculto.setValue(csrfToken);
            inputOculto.getAttributes().put("name", "csrf_token");

            componente.getChildren().add(inputOculto);
            logger.debug("CSRF token agregado al formulario: " + componente.getClientId() + ", input ID: " + inputId);
        }

        for (UIComponent hijo : componente.getChildren()) {
            agregarTokenCsrfAFormularios(hijo, csrfToken);
        }
    }
}
