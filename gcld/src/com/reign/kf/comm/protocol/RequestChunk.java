package com.reign.kf.comm.protocol;

import org.codehaus.jackson.map.annotate.*;
import java.io.*;
import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;
import java.util.*;

@JsonSerialize(using = RequestChunkJsonSerializer.class)
@JsonDeserialize(using = RequestChunkJsonDeserializer.class)
public class RequestChunk
{
    private String machineId;
    private List<Request> requestList;
    
    public void setRequestList(final List<Request> requestList) {
        this.requestList = requestList;
    }
    
    public List<Request> getRequestList() {
        return this.requestList;
    }
    
    public static void main(final String... args) throws JsonGenerationException, JsonMappingException, IOException {
    }
    
    public String getMachineId() {
        return this.machineId;
    }
    
    public void setMachineId(final String machineId) {
        this.machineId = machineId;
    }
    
    static /* synthetic */ void access$2(final RequestChunk requestChunk, final String machineId) {
        requestChunk.machineId = machineId;
    }
    
    private static class RequestChunkJsonDeserializer extends JsonDeserializer<RequestChunk>
    {
        @Override
		public RequestChunk deserialize(final JsonParser jsonparser, final DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
            final RequestChunk requestChunk = new RequestChunk();
            JsonToken token = jsonparser.getCurrentToken();
            if (token != JsonToken.START_OBJECT) {
                throw new JsonParseException("except: " + JsonToken.START_OBJECT.asString(), jsonparser.getCurrentLocation());
            }
            token = jsonparser.nextToken();
            if (token != JsonToken.FIELD_NAME) {
                throw new JsonParseException("except: " + JsonToken.FIELD_NAME.asString(), jsonparser.getCurrentLocation());
            }
            String fieldName = jsonparser.getText();
            if (!"machineId".equals(fieldName)) {
                throw new JsonParseException("except filedName: gameServer", jsonparser.getCurrentLocation());
            }
            token = jsonparser.nextToken();
            if (token != JsonToken.VALUE_STRING) {
                throw new JsonParseException("except: " + JsonToken.VALUE_STRING.asString(), jsonparser.getCurrentLocation());
            }
            RequestChunk.access$2(requestChunk, jsonparser.getText());
            token = jsonparser.nextToken();
            if (token != JsonToken.FIELD_NAME) {
                throw new JsonParseException("except: " + JsonToken.FIELD_NAME.asString(), jsonparser.getCurrentLocation());
            }
            fieldName = jsonparser.getText();
            if (!"requestList".equals(fieldName)) {
                throw new JsonParseException("except filedName: requestList", jsonparser.getCurrentLocation());
            }
            token = jsonparser.nextToken();
            if (token == JsonToken.VALUE_NULL) {
                requestChunk.setRequestList(null);
            }
            else {
                if (token != JsonToken.START_ARRAY) {
                    throw new JsonParseException("except: " + JsonToken.START_ARRAY.asString(), jsonparser.getCurrentLocation());
                }
                final ArrayList<Request> requestList = new ArrayList<Request>();
                for (token = jsonparser.nextToken(); token != JsonToken.END_ARRAY; token = jsonparser.nextToken()) {
                    final Request request = (Request)Types.objectReader(Request.class).readValue(jsonparser);
                    requestList.add(request);
                }
                requestChunk.setRequestList(requestList);
            }
            token = jsonparser.nextToken();
            if (token != JsonToken.END_OBJECT) {
                throw new JsonParseException("except: " + JsonToken.END_OBJECT.asString(), jsonparser.getCurrentLocation());
            }
            return requestChunk;
        }
    }
    
    private static class RequestChunkJsonSerializer extends JsonSerializer<RequestChunk>
    {
        @Override
		public void serialize(final RequestChunk requestChunk, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartObject();
            jgen.writeObjectField("machineId", requestChunk.machineId);
            jgen.writeFieldName("requestList");
            if (requestChunk.requestList != null) {
                jgen.writeStartArray();
                for (final Request request : requestChunk.requestList) {
                    jgen.writeObject(request);
                }
                jgen.writeEndArray();
            }
            else {
                jgen.writeNull();
            }
            jgen.writeEndObject();
        }
    }
}
