package ozosync.web.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class JsonDecimalSerializer extends JsonSerializer<BigDecimal> {

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(decimalFormat.format(value));
    }
}
