package ozosync.job.shop.database;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import ozosync.domain.CurrencyInfo;
import ozosync.domain.ExchangeRateInfo;
import ozosync.domain.schema.CurrencySchema;
import ozosync.domain.schema.ExchangeRateSchema;

@Service
public class ExchangeRateRespository {

    public ExchangeRateInfo getAudExchangeRate(Session session) {
        CurrencyInfo currencyInfo = (CurrencyInfo) session.createCriteria(CurrencyInfo.class)
                .add(Restrictions.eq(CurrencySchema.code, "AUD"))
                .uniqueResult();

        if (currencyInfo == null) {
            return null;
        }

        return (ExchangeRateInfo) session.createCriteria(ExchangeRateInfo.class)
                .add(Restrictions.eq(ExchangeRateSchema.currencyId, currencyInfo.getId()))
                .uniqueResult();
    }
}
