package jogLog.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kish
 */
@Component
public class JsonDateSerializer extends JsonSerializer<Date> {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        String formattedDate = DATE_FORMAT.format(date);

        gen.writeString(formattedDate);
    }
}
