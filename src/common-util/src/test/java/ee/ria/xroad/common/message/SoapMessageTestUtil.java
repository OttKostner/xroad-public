/**
 * The MIT License
 * Copyright (c) 2015 Estonian Information System Authority (RIA), Population Register Centre (VRK)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ee.ria.xroad.common.message;

import ee.ria.xroad.common.identifier.ClientId;
import ee.ria.xroad.common.identifier.ServiceId;
import ee.ria.xroad.common.message.*;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

public final class SoapMessageTestUtil {

    public static final String QUERY_DIR = "../proxy/src/test/queries/";

    private SoapMessageTestUtil() {
    }

    public static SoapMessageImpl build(ClientId sender,
            ServiceId receiver, String userId, String queryId)
                    throws Exception {
        return build(false, sender, receiver, userId, queryId, null);
    }

    public static SoapMessageImpl build(boolean isRpcEncoded, ClientId sender,
            ServiceId receiver, String userId, String queryId)
                    throws Exception {
        return build(isRpcEncoded, sender, receiver, userId, queryId, null);
    }

    public static SoapMessageImpl build(boolean isRpcEncoded, ClientId sender,
            ServiceId receiver, String userId, String queryId,
            SoapBuilder.SoapBodyCallback createBodyCallback)
                    throws Exception {
        SoapHeader header = new SoapHeader();
        header.setClient(sender);
        header.setService(receiver);
        header.setUserId(userId);
        header.setQueryId(queryId);

        SoapBuilder builder = new SoapBuilder();
        builder.setHeader(header);
        builder.setRpcEncoded(isRpcEncoded);
        builder.setCreateBodyCallback(createBodyCallback);

        return builder.build();
    }

    public static byte[] fileToBytes(String fileName) throws Exception {
        return IOUtils.toByteArray(newQueryInputStream(fileName));
    }

    public static byte[] messageToBytes(Soap soap) throws Exception {
        if (soap instanceof SoapMessage) {
            return ((SoapMessage)soap).getBytes();
        }

        return soap.getXml().getBytes();
    }

    public static Soap createSoapMessage(String fileName)
            throws Exception {
        return createSoapMessage(QUERY_DIR, fileName);
    }

    public static Soap createSoapMessage(String queryDir, String fileName)
            throws Exception {
        return new SoapParserImpl().parse(newQueryInputStream(queryDir, fileName));
    }

    public static Soap createSoapMessage(byte[] data)
            throws Exception {
        return new SoapParserImpl().parse(new ByteArrayInputStream(data));
    }

    public static SoapMessageImpl createRequest(String fileName)
            throws Exception {
        return createRequest(QUERY_DIR, fileName);
    }
    public static SoapMessageImpl createRequest(String queryDir, String fileName)
            throws Exception {
        Soap message = createSoapMessage(queryDir, fileName);
        if (!(message instanceof SoapMessageImpl)) {
            throw new RuntimeException(
                    "Got " + message.getClass() + " instead of SoapMessage");
        }

        if (((SoapMessageImpl) message).isResponse()) {
            throw new RuntimeException("Got response instead of request");
        }

        return (SoapMessageImpl) message;
    }

    public static SoapMessageImpl createResponse(String fileName)
            throws Exception {
        return createResponse(QUERY_DIR, fileName);
    }
    public static SoapMessageImpl createResponse(String queryDir, String fileName)
            throws Exception {
        Soap message = createSoapMessage(queryDir, fileName);
        if (!(message instanceof SoapMessageImpl)) {
            throw new RuntimeException(
                    "Got " + message.getClass() + " instead of SoapResponse");
        }

        if (((SoapMessageImpl) message).isRequest()) {
            throw new RuntimeException("Got request instead of response");
        }

        return (SoapMessageImpl) message;
    }

    public static FileInputStream newQueryInputStream(String fileName)
            throws Exception {
        return newQueryInputStream(QUERY_DIR, fileName);
    }
    public static FileInputStream newQueryInputStream(String queryDir, String fileName)
            throws Exception {
        return new FileInputStream(queryDir + fileName);
    }
}
