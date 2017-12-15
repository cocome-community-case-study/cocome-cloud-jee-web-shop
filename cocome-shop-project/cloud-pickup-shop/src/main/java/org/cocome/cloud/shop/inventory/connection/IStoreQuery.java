package org.cocome.cloud.shop.inventory.connection;

import org.cocome.cloud.logic.stub.NotInDatabaseException_Exception;
import org.cocome.cloud.logic.stub.ProductOutOfStockException_Exception;
import org.cocome.cloud.logic.stub.RecipeException_Exception;
import org.cocome.cloud.logic.stub.UpdateException_Exception;
import org.cocome.cloud.shop.inventory.store.ProductWrapper;
import org.cocome.cloud.shop.inventory.store.Store;
import org.cocome.cloud.shop.shoppingcart.IShoppingCart;

import java.util.List;

/**
 * Interface to retrieve stock items from a specific store and to account sales
 * at that store.
 *
 * @author Tobias Pöppke
 * @author Robert Heinrich
 */
public interface IStoreQuery {
    List<ProductWrapper> queryStockItems(Store store) throws NotInDatabaseException_Exception;

    ProductWrapper getStockItemByProductID(Store store, long productID);

    ProductWrapper getStockItemByBarcode(Store store, long barcode);

    boolean accountSale(IShoppingCart cart)
            throws NotInDatabaseException_Exception, ProductOutOfStockException_Exception, UpdateException_Exception;
}
