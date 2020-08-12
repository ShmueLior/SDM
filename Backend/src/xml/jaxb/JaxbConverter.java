package xml.jaxb;

import models.Item;
import models.Store;
import models.StoreItem;
import myLocation.Location;
import myLocation.LocationException;
import myLocation.LocationManager;
import utils.PurchaseForm;
import xml.jaxb.schema.generated.SDMItem;
import xml.jaxb.schema.generated.SDMSell;
import xml.jaxb.schema.generated.SDMStore;
import xml.jaxb.schema.generated.SuperDuperMarketDescriptor;

import java.util.*;
import java.util.stream.Collectors;

public class JaxbConverter {
    IJaxbDataLoader m_IJaxbLoader;
    SuperDuperMarketDescriptor m_SuperDuperMarketDescriptor;
    Map<Integer,Item> m_Items;
    Collection<Store> m_Stores;

    public JaxbConverter(IJaxbDataLoader i_IJaxbLoader)
    {
        m_IJaxbLoader=i_IJaxbLoader;
    }
    public Collection<Store> getStores()
    {
        if(m_SuperDuperMarketDescriptor==null || m_Items==null)
        {
            return null;
        }

        if(m_Stores==null)
        {
            m_Stores = m_SuperDuperMarketDescriptor.getSDMStores().getSDMStore().stream().map(this::convertToStore).collect(Collectors.toList());
        }
        return m_Stores;
    }

    public Collection<Item> getItems() {
        if (m_SuperDuperMarketDescriptor == null) {
            return null;
        }

        if (m_Items == null) {
            m_Items = new HashMap<>();
            for (SDMItem sdmItem : m_SuperDuperMarketDescriptor.getSDMItems().getSDMItem()
            ) {
                m_Items.put(sdmItem.getId(), convertToItem(sdmItem));
            }
        }

        return m_Items.values();
    }

    public void loadJaxbData(String i_PathToFile) throws Exception {
        m_SuperDuperMarketDescriptor=m_IJaxbLoader.load(i_PathToFile);
    }


    private Store convertToStore(SDMStore i_JaxbStore) {
        List<StoreItem> storeItems = new ArrayList<>(i_JaxbStore.getSDMPrices().getSDMSell().size());
        for (SDMSell sdmSell : i_JaxbStore.getSDMPrices().getSDMSell()
        ) {
            storeItems.add(convertToStoreItem(sdmSell));
        }
        LocationManager.addLocation(i_JaxbStore.getLocation().getX(),i_JaxbStore.getLocation().getY());
        return new Store(i_JaxbStore.getId(), i_JaxbStore.getName(), new Location(i_JaxbStore.getLocation().getX(),i_JaxbStore.getLocation().getY()),i_JaxbStore.getDeliveryPpk(),storeItems);
    }

    private StoreItem convertToStoreItem(SDMSell i_JaxbStoreItem) {
        return new StoreItem(m_Items.get(i_JaxbStoreItem.getItemId()),i_JaxbStoreItem.getPrice());
    }

    private Item convertToItem(SDMItem i_JaxbItem)
    {
        return new Item(i_JaxbItem.getId(),i_JaxbItem.getName(), PurchaseForm.valueOf(i_JaxbItem.getPurchaseCategory().toUpperCase()));
    }
}
