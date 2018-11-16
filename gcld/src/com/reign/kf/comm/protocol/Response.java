package com.reign.kf.comm.protocol;

import org.codehaus.jackson.map.annotate.*;
import java.util.*;
import java.io.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.*;

@JsonSerialize(using = ReponseJsonSerializer.class)
@JsonDeserialize(using = ResponseJsonDeserializer.class)
public class Response
{
    private int responseId;
    private int type;
    private Object message;
    private int command;
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public void setResponseId(final int responseId) {
        this.responseId = responseId;
    }
    
    public int getResponseId() {
        return this.responseId;
    }
    
    public void setMessage(final Object message) {
        this.message = message;
    }
    
    public Object getMessage() {
        return this.message;
    }
    
    public int getCommand() {
        return this.command;
    }
    
    public void setCommand(final int command) {
        this.command = command;
    }
    
    public static void main(final String... args) throws JsonGenerationException, JsonMappingException, IOException {
        final Response response = new Response();
        response.setResponseId(3);
        final Request request = new Request();
        request.setCommand(1);
        request.setMessage("nihao");
        request.setType(0);
        response.setCommand(1);
        response.setMessage(request);
        final ArrayList<Response> resps = new ArrayList<Response>();
        resps.add(response);
        final String str = Types.OBJECT_MAPPER.writeValueAsString(resps);
        System.out.println(str);
        final List<Response> rs = (List<Response>)Types.objectReader(Types.JAVATYPE_RESPONSELIST).readValue(str);
        final Response r = rs.get(0);
        System.out.println(r.getResponseId());
        System.out.println(r.type);
        final Request ff = (Request)r.getMessage();
        System.out.println(ff.getType());
        System.out.println(r.command);
    }
    
    static /* synthetic */ void access$3(final Response response, final int responseId) {
        response.responseId = responseId;
    }
    
    static /* synthetic */ void access$4(final Response response, final int type) {
        response.type = type;
    }
    
    static /* synthetic */ void access$5(final Response response, final int command) {
        response.command = command;
    }
    
    static /* synthetic */ void access$6(final Response response, final Object message) {
        response.message = message;
    }
    
    private static class ReponseJsonSerializer extends JsonSerializer<Response>
    {
        @Override
		public void serialize(final Response response, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartObject();
            jgen.writeObjectField("responseId", response.responseId);
            int type = response.type;
            final Object message = response.getMessage();
            if (type == 0 && message != null) {
                type = Types.id(message.getClass());
            }
            jgen.writeObjectField("type", type);
            jgen.writeObjectField("command", response.command);
            jgen.writeObjectField("message", message);
            jgen.writeEndObject();
        }
    }
    
    private static class ResponseJsonDeserializer extends JsonDeserializer<Response>
    {
        @Override
		public Response deserialize(final JsonParser jsonparser, final DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
            JsonToken token = jsonparser.getCurrentToken();
            if (token != JsonToken.START_OBJECT) {
                throw new JsonParseException("except: " + JsonToken.START_OBJECT.asString(), jsonparser.getCurrentLocation());
            }
            token = jsonparser.nextToken();
            if (token != JsonToken.FIELD_NAME) {
                throw new JsonParseException("except: " + JsonToken.FIELD_NAME.asString(), jsonparser.getCurrentLocation());
            }
            String fieldName = jsonparser.getText();
            if (!"responseId".equals(fieldName)) {
                throw new JsonParseException("except filedName: responseId", jsonparser.getCurrentLocation());
            }
            token = jsonparser.nextToken();
            if (token != JsonToken.VALUE_NUMBER_INT) {
                throw new JsonParseException("except: " + JsonToken.VALUE_NUMBER_INT.asString(), jsonparser.getCurrentLocation());
            }
            final int responseId = Integer.valueOf(jsonparser.getText());
            final Response response = new Response();
            Response.access$3(response, responseId);
            token = jsonparser.nextToken();
            if (token != JsonToken.FIELD_NAME) {
                throw new JsonParseException("except: " + JsonToken.FIELD_NAME.asString(), jsonparser.getCurrentLocation());
            }
            fieldName = jsonparser.getText();
            if (!"type".equals(fieldName)) {
                throw new JsonParseException("except filedName: type", jsonparser.getCurrentLocation());
            }
            token = jsonparser.nextToken();
            if (token != JsonToken.VALUE_NUMBER_INT) {
                throw new JsonParseException("except: " + JsonToken.VALUE_NUMBER_INT.asString(), jsonparser.getCurrentLocation());
            }
            Response.access$4(response, Integer.valueOf(jsonparser.getText()));
            token = jsonparser.nextToken();
            if (token != JsonToken.FIELD_NAME) {
                throw new JsonParseException("except: " + JsonToken.FIELD_NAME.asString(), jsonparser.getCurrentLocation());
            }
            fieldName = jsonparser.getText();
            if (!"command".equals(fieldName)) {
                throw new JsonParseException("except filedName: type", jsonparser.getCurrentLocation());
            }
            token = jsonparser.nextToken();
            if (token != JsonToken.VALUE_NUMBER_INT) {
                throw new JsonParseException("except: " + JsonToken.VALUE_NUMBER_INT.asString(), jsonparser.getCurrentLocation());
            }
            Response.access$5(response, Integer.valueOf(jsonparser.getText()));
            token = jsonparser.nextToken();
            if (token != JsonToken.FIELD_NAME) {
                throw new JsonParseException("except: " + JsonToken.FIELD_NAME.asString(), jsonparser.getCurrentLocation());
            }
            fieldName = jsonparser.getText();
            if (!"message".equals(fieldName)) {
                throw new JsonParseException("except filedName: message", jsonparser.getCurrentLocation());
            }
            token = jsonparser.nextToken();
            if (token != JsonToken.VALUE_NULL) {
                Response.access$6(response, Types.objectReader(response.type).readValue(jsonparser));
            }
            token = jsonparser.nextToken();
            if (token != JsonToken.END_OBJECT) {
                throw new JsonParseException("except: " + JsonToken.END_OBJECT.asString(), jsonparser.getCurrentLocation());
            }
            return response;
        }
    }
}
