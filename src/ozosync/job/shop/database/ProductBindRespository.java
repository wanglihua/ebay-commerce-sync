package ozosync.job.shop.database;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ozosync.domain.ProductBindInfo;
import ozosync.domain.ProductBindStatus;
import ozosync.domain.schema.ProductBindSchema;

public class ProductBindRespository {

    public boolean isProductBindSku(String productSku, Session session) {

        Long count = (Long) session.createCriteria(ProductBindInfo.class)
                .add(Restrictions.eq(ProductBindSchema.code, productSku))
                .add(Restrictions.eq(ProductBindSchema.statusId, ProductBindStatus.Normal))
                .setProjection(Projections.rowCount())
                .uniqueResult();

        return count != 0;
    }

    public Map<String, Integer> getProductBindSkuList(String productBindCode, Session session) {

        ProductBindInfo productBind = (ProductBindInfo) session.createCriteria(ProductBindInfo.class)
                .add(Restrictions.eq(ProductBindSchema.code, productBindCode))
                .add(Restrictions.eq(ProductBindSchema.statusId, ProductBindStatus.Normal))
                .uniqueResult();

        String[] productSkuWithNumberList = productBind.getBindList().split(",");

        Map<String, Integer> productBindSkuList = new HashMap<>();
        for (String productSkuWithNumber : productSkuWithNumberList) {
            String[] productSkuNumberArray = productSkuWithNumber.split("\\*");
            String productSku = productSkuNumberArray[0];
            String productNumberStr = productSkuNumberArray[1];
            Integer productNumber = Integer.parseInt(productNumberStr);

            productBindSkuList.put(productSku, productNumber);
        }

        return productBindSkuList;
    }
}
