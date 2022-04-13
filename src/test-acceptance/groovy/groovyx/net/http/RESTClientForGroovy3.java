package groovyx.net.http;

import org.codehaus.groovy.runtime.IOGroovyMethods;

import java.io.*;
import java.net.URISyntaxException;

//temporal solution to fix incompatibility issues in HTTPBuilder 0.7.1 and Groovy 3
public class RESTClientForGroovy3 extends RESTClient {

    public RESTClientForGroovy3(Object defaultURI) throws URISyntaxException {
        super(defaultURI);
    }

    @Override
    protected HttpResponseDecorator defaultSuccessHandler(HttpResponseDecorator resp, Object data)
            throws ResponseParseException {
        try {
            //If response is streaming, buffer it in a byte array:
            if (data instanceof InputStream) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                // we've updated the below line
                IOGroovyMethods.leftShift(buffer, (InputStream) data);
                resp.setData(new ByteArrayInputStream(buffer.toByteArray()));
                return resp;
            }
            if (data instanceof Reader) {
                StringWriter buffer = new StringWriter();
                // we've updated the below line
                IOGroovyMethods.leftShift(buffer, data);
                resp.setData(new StringReader(buffer.toString()));
                return resp;
            }
            return super.defaultSuccessHandler(resp, data);
        } catch (IOException ex) {
            throw new ResponseParseException(resp, ex);
        }
    }
}
