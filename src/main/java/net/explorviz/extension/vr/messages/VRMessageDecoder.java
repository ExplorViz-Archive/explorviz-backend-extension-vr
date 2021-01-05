package net.explorviz.extension.vr.messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class VRMessageDecoder implements Decoder.TextStream<List<VRMessage>> {

    @Inject
    ObjectMapper objectMapper;
    
    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public List<VRMessage> decode(Reader reader) throws DecodeException, IOException {
        String jsonString = readAll(reader);
        JsonArray array = new JsonArray(jsonString);
        List<VRMessage> messages = new ArrayList<>();
        for (Object object : array) {
            if (object instanceof JsonObject) {
                messages.add(VRMessage.fromJson((JsonObject) object));
            } else {
                throw new DecodeException(jsonString, "All messages must be JSON objects.");
            }
        }
        return messages;
    }

    public String readAll(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

}
