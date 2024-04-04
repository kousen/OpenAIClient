package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

import static com.kousenit.openaiclient.json.OpenAIRecords.*;

public class ComplexContentSerializer extends StdSerializer<ComplexContent> {
    public ComplexContentSerializer() {
        super(ComplexContent.class);
    }

    @Override
    public void serialize(ComplexContent value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeObject(value.contentObjects());
    }
}
