package jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.bson.types.ObjectId;

public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {

  @Override
  public ObjectId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String id = p.getValueAsString();
    return id != null && !id.isEmpty() ? new ObjectId(id) : null;
  }
}
