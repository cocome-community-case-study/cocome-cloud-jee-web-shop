package org.cocome.cloud.shop.inventory.store;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.cocome.cloud.shop.inventory.IInventory;

/**
 * Converts a product from the UI representation into a {@link ProductWrapper} 
 * object or vice versa. 
 * 
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
@Named
@RequestScoped
public class ProductConverter implements Converter {
	private static final Logger LOG = Logger.getLogger(ProductConverter.class);

	@Inject
	IInventory inventory;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		LOG.debug("Converting String: " + value);
		
		if (value == null || value.isEmpty()) {
            return null;
        }
		
        Long barcode = Long.valueOf(value);
        ProductWrapper product = inventory.getProduct(barcode); 
        
        if (product == null) {
        	throw new ConverterException("The value is not a valid Product Barcode: " + value);
        }
        
        return product;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		LOG.debug("Converting object: " + value);
		if (value == null) {
            return "";
        }

        if (value instanceof ProductWrapper) {
            Long barcode = ((ProductWrapper) value).getBarcode();
            return (barcode != null) ? String.valueOf(barcode) : null;
        } else {
            throw new ConverterException("The value is not a valid Product instance: " + value);
        }
	}

}
