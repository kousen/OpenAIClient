package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

import static com.kousenit.openaiclient.json.OpenAIRecords.*;

public class SimpleTextContentSerializer extends StdSerializer<SimpleTextContent> {
    public SimpleTextContentSerializer() {
        super(SimpleTextContent.class);
    }

    @Override
    public void serialize(SimpleTextContent value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.text());
    }
}
