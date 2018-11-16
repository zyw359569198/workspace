package com.reign.kf.comm.protocol;

import org.codehaus.jackson.map.annotate.*;
import java.util.concurrent.atomic.*;
import java.io.*;
import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;

@JsonSerialize(using = RequestJsonSerializer.class)
@JsonDeserialize(using = RequestJsonDeserializer.class)
public class Request
{
    private static final AtomicInteger reqId;
    private int requestId;
    private int command;
    private int type;
    private Object message;
    
    static {
        reqId = new AtomicInteger(1);
    }
    
    public Request() {
        this.requestId = Request.reqId.getAndIncrement();
    }
    
    public int getRequestId() {
        return this.requestId;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public void setMessage(final Object message) {
        this.message = message;
    }
    
    public Object getMessage() {
        return this.message;
    }
    
    public void setCommand(final int command) {
        this.command = command;
    }
    
    public int getCommand() {
        return this.command;
    }
    
    @Override
    public int hashCode() {
        return this.command << 24 ^ this.type << 16 ^ ((this.message == null) ? 0 : this.message.hashCode());
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Request)) {
            return false;
        }
        final Request o = (Request)obj;
        if (this.getCommand() != o.getCommand()) {
            return false;
        }
        final Object msg1 = this.getMessage();
        final Object msg2 = o.getMessage();
        return msg1 == msg2 || (msg2 != null && msg1 != null && msg1.equals(msg2));
    }
    
    static /* synthetic */ void access$3(final Request request, final int requestId) {
        request.requestId = requestId;
    }
    
    static /* synthetic */ void access$4(final Request request, final int command) {
        request.command = command;
    }
    
    static /* synthetic */ void access$5(final Request request, final int type) {
        request.type = type;
    }
    
    static /* synthetic */ void access$6(final Request request, final Object message) {
        request.message = message;
    }
    
    private static class RequestJsonDeserializer extends JsonDeserializer<Request>
    {
        @Override
		public Request deserialize(final JsonParser jsonparser, final DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
            final Request request = new Request();
            JsonToken token = jsonparser.getCurrentToken();
            if (token != JsonToken.START_OBJECT) {
                throw new JsonParseException("except: " + JsonToken.START_OBJECT.asString(), jsonparser.getCurrentLocation());
            }
            token = jsonparser.nextToken();
            if (token != JsonToken.FIELD_NAME) {
                throw new JsonParseException("except: " + JsonToken.FIELD_NAME.asString(), jsonparser.getCurrentLocation());
            }
            String fieldName = jsonparser.getText();
            if (!"requestId".equals(fieldName)) {
                throw new JsonParseException("except filedName: requestId", jsonparser.getCurrentLocation());
            }
            token = jsonparser.nextToken();
            if (token != JsonToken.VALUE_NUMBER_INT) {
                throw new JsonParseException("except: " + JsonToken.VALUE_NUMBER_INT.asString(), jsonparser.getCurrentLocation());
            }
            Request.access$3(request, Integer.valueOf(jsonparser.getText()));
            token = jsonparser.nextToken();
            if (token != JsonToken.FIELD_NAME) {
                throw new JsonParseException("except: " + JsonToken.FIELD_NAME.asString(), jsonparser.getCurrentLocation());
            }
            fieldName = jsonparser.getText();
            if (!"command".equals(fieldName)) {
                throw new JsonParseException("except filedName: command", jsonparser.getCurrentLocation());
            }
            token = jsonparser.nextToken();
            if (token != JsonToken.VALUE_NUMBER_INT) {
                throw new JsonParseException("except: " + JsonToken.VALUE_NUMBER_INT.asString(), jsonparser.getCurrentLocation());
            }
            Request.access$4(request, Integer.valueOf(jsonparser.getText()));
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
            Request.access$5(request, Integer.valueOf(jsonparser.getText()));
            token = jsonparser.nextToken();
            if (token != JsonToken.FIELD_NAME) {
                throw new JsonParseException("except: " + JsonToken.FIELD_NAME.asString(), jsonparser.getCurrentLocation());
            }
            fieldName = jsonparser.getText();
            if (!"message".equals(fieldName)) {
                throw new JsonParseException("except filedName: message", jsonparser.getCurrentLocation());
            }
            token = jsonparser.nextToken();
            if (request.type == 0) {
                if (token != JsonToken.VALUE_NULL) {
                    throw new JsonParseException("except: " + JsonToken.VALUE_NULL.asString(), jsonparser.getCurrentLocation());
                }
            }
            else {
                Request.access$6(request, Types.objectReader(request.type).readValue(jsonparser));
            }
            token = jsonparser.nextToken();
            if (token != JsonToken.END_OBJECT) {
                throw new JsonParseException("except: " + JsonToken.END_OBJECT.asString(), jsonparser.getCurrentLocation());
            }
            return request;
        }
    }
    
    private static class RequestJsonSerializer extends JsonSerializer<Request>
    {
        @Override
		public void serialize(final Request request, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartObject();
            jgen.writeObjectField("requestId", request.requestId);
            int type = request.type;
            final Object message = request.getMessage();
            if (type == 0 && message != null) {
                type = Types.id(message.getClass());
            }
            jgen.writeObjectField("command", request.command);
            jgen.writeObjectField("type", type);
            jgen.writeObjectField("message", message);
            jgen.writeEndObject();
        }
    }
}
