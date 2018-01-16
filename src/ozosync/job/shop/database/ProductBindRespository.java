package ozosync.job.shop.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import ozosync.domain.ProductBindInfo;
import ozosync.domain.ProductBindStatus;
import ozosync.domain.schema.ProductBindSchema;
import ozosync.domain.schema.StatusSchema;

public class ProductBindRespository {

    public boolean isProductBindSku(String productSku, Session session) {

        String statusIdPropertyName = Property.forName(ProductBindSchema.status)
                .getProperty(StatusSchema.id)
                .getPropertyName();

        Long count = (Long) session.createCriteria(ProductBindInfo.class)
                .add(Restrictions.eq(ProductBindSchema.code, productSku))
                .add(Restrictions.eq(statusIdPropertyName, ProductBindStatus.Normal))
                .setProjection(Projections.rowCount())
                .uniqueResult();

        return count != 0;
    }

    public Map<String, Integer> getProductBindSkuList(String productBindCode, Session session) {
        String statusIdPropertyName = Property.forName(ProductBindSchema.status)
                .getProperty(StatusSchema.id)
                .getPropertyName();

        ProductBindInfo productBind = (ProductBindInfo) session.createCriteria(ProductBindInfo.class)
                .add(Restrictions.eq(ProductBindSchema.code, productBindCode))
                .add(Restrictions.eq(statusIdPropertyName, ProductBindStatus.Normal))
                .uniqueResult();

        String[] productSkuWithNumberList = productBind.getBindList().split(",");

        Map<String, Integer> productBindSkuList = new HashMap<String, Integer>();
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

/*
type Product_bind_sku_t struct {
	Sku   string
	Count int64
}

// 解析绑定 sku， product_bind_code 就是绑定商品的虚拟 sku
func Get_product_bind_skus(product_bind_code string, db_session *xorm.Session) []Product_bind_sku_t {


    var product_bind kcgl_models.ProductBindInfo
        _,err:=db_session.Where("code = ? AND status_id = ?",product_bind_code,models.PRODUCT_BIND_STATUS_NORMAL).Get(&product_bind)
        core.HandleError(err)

        var product_bind_sku_list=make([]Product_bind_sku_t,0)

        product_sku_with_number_list:=strings.Split(product_bind.BindList,",")
        for _,product_sku_with_number:=range product_sku_with_number_list{
            product_sku_number_slice:=strings.Split(product_sku_with_number,"*")

            product_sku:=strings.TrimSpace(product_sku_number_slice[0]);
            product_number_str:=strings.TrimSpace(product_sku_number_slice[1]);
            product_number,_:=strconv.ParseInt(product_number_str,10,64)

            var product_bind_sku Product_bind_sku_t
            product_bind_sku.Sku=product_sku
            product_bind_sku.Count=product_number

            product_bind_sku_list=append(product_bind_sku_list,product_bind_sku)
        }

        return product_bind_sku_list
        }

        */