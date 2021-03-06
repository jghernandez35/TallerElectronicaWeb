package com.zeus.tallerelectronica.managedbean;

import com.zeus.tallerelectronica.entidades.Reparacion;
import com.zeus.tallerelectronica.entidades.Cliente;
import com.zeus.tallerelectronica.entidades.Articulo;
import com.zeus.tallerelectronica.managedbean.util.JsfUtil;
import com.zeus.tallerelectronica.managedbean.util.JsfUtil.PersistAction;
import com.zeus.tallerelectronica.sessionbean.ClienteFacade;
import com.zeus.tallerelectronica.sessionbean.ReparacionFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.event.FlowEvent;

@Named("reparacionController")
@SessionScoped
public class ReparacionController implements Serializable {

    @EJB
    private com.zeus.tallerelectronica.sessionbean.ReparacionFacade ejbFacade;
    private List<Reparacion> items = null;
    private Reparacion selected;
    //------------------------------Cliente-------------------------------------
    private com.zeus.tallerelectronica.sessionbean.ClienteFacade ejbFacadeCliente;
    private Cliente cliente;
    //------------------------------Articulo-------------------------------------
    private com.zeus.tallerelectronica.sessionbean.ArticuloFacade ejbFacadeArticulo;
    private Articulo articulo;    
    //----------------------------Wizard-----------------------------------
    private boolean skip;

    public ReparacionController() {
    }

    //----------------------------Inicio Metodos Cliente------------------------

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public ClienteFacade getEjbFacadeCliente() {
        return ejbFacadeCliente;
    }

    public void setEjbFacadeCliente(ClienteFacade ejbFacadeCliente) {
        this.ejbFacadeCliente = ejbFacadeCliente;
    }
    
    //-----------------------------Inicio Metodos Wizard------------------------

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }
    
    
    public String onFlowProcess(FlowEvent event) {
            if(skip) {
                    skip = false;	//reiniciar en caso de que el usuario vuelva-reset in case user goes back
                    return "confirm";
            }
            else {
                    return event.getNewStep();
            }
    }
    //---------------------------Inicio Metodos Articulo------------------------    

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }
    
    //---------------------------------------Fin Metodos------------------------   
    public Reparacion getSelected() {
        return selected;
    }

    public void setSelected(Reparacion selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ReparacionFacade getFacade() {
        return ejbFacade;
    }

    //carga Inicio
    public String inicio(){
        return "/admin/inicio/Inicio";
    }    
    
    //carga la lista Inicial
    public String cargarLista(){
        return "/admin/reparacion/List";
    }
    
    //crear nueva orden de servicio Junio 2021
    public String crearNuevaOrden(){
        cliente = new Cliente();
        articulo = new Articulo();
        return "/admin/reparacion/CrearReparacion";
    }    
    
    public Reparacion prepareCreate() {
        selected = new Reparacion();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle2").getString("ReparacionCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle2").getString("ReparacionUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle2").getString("ReparacionDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Reparacion> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle2").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle2").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Reparacion getReparacion(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Reparacion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Reparacion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Reparacion.class)
    public static class ReparacionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ReparacionController controller = (ReparacionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "reparacionController");
            return controller.getReparacion(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Reparacion) {
                Reparacion o = (Reparacion) object;
                return getStringKey(o.getRepaIdNumorden());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Reparacion.class.getName()});
                return null;
            }
        }

    }

}
