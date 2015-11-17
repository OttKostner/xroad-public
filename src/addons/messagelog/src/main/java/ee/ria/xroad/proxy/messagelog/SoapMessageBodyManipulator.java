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
package ee.ria.xroad.proxy.messagelog;

import com.google.common.collect.Iterables;
import ee.ria.xroad.common.identifier.ClientId;
import ee.ria.xroad.common.message.SoapBuilder;
import ee.ria.xroad.common.message.SoapHeader;
import ee.ria.xroad.common.message.SoapMessageImpl;
import ee.ria.xroad.common.message.SoapUtils;
import ee.ria.xroad.common.messagelog.MessageLogProperties;
import lombok.Setter;

import java.util.Collection;
import java.util.Objects;

/**
 * Utility class for processing SoapMessages and removing altered message with <soap:body>
 * section removed.
 */
public class SoapMessageBodyManipulator {

    /**
     * Extract configuration reading for better testability
     */
    public class Configurator {
        public Collection<ClientId> getLocalProducerOverrides() {
            return MessageLogProperties.getSoapBodyLoggingLocalProducerOverrides();
        }
        public Collection<ClientId> getRemoteProducerOverrides() {
            return MessageLogProperties.getSoapBodyLoggingRemoteProducerOverrides();
        }
        public boolean isSoapBodyLoggingEnabled() {
            return MessageLogProperties.isSoapBodyLoggingEnabled();
        }
    }

    @Setter
    private Configurator configurator = new Configurator();

    /**
     * Returns the string that should be logged. This will either be the original soap message
     * (when soap:body logging is used for this message) or manipulated soap message with
     * soap:body element cleared.
     * @param message
     * @return
     */
    public String getLoggableMessageText(SoapMessageImpl message, boolean clientSide) throws Exception {
        if (isSoapBodyLogged(message, clientSide)) {
            return message.getXml();
        } else {
            return buildBodyRemovedMessage(message);
        }
    }

    private String buildBodyRemovedMessage(SoapMessageImpl message) throws Exception {
        // build a new empty message with SoapBuilder and
        // set old SoapHeader to it
        SoapHeader oldHeader = message.getHeader();
        SoapBuilder builder = new SoapBuilder();
        builder.setHeader(oldHeader);
        builder.setRpcEncoded(false);
        SoapMessageImpl blankedMessage = builder.build();
        if (message.isResponse()) {
            // need to convert this to response message (changes body element name)
            // otherwise asicverifier gets confused
            blankedMessage = SoapUtils.toResponse(blankedMessage);
        }
        return blankedMessage.getXml();
    }

    /**
     * Tells whether SOAP body should be logged for this message.
     * @param message
     * @param clientSide whether we are calling external service (true) or someone
     *                   else is calling our service (false)
     * @return
     */
    public boolean isSoapBodyLogged(SoapMessageImpl message, boolean clientSide) {

        Collection<ClientId> overrides;
        if (clientSide) {
            overrides = configurator.getRemoteProducerOverrides();
        } else {
            overrides = configurator.getLocalProducerOverrides();
        }

        boolean producerSubsystemIsOverridden = isClientInCollection(
                message.getService().getClientId(),
                overrides);

        if (configurator.isSoapBodyLoggingEnabled()) {
            return !producerSubsystemIsOverridden;
        } else {
            return producerSubsystemIsOverridden;
        }
    }

    /**
     * Takes one ClientId object, and searches whether it is in searched group of ClientIds
     * @param searchParam
     * @param searched
     * @return
     */
    public boolean isClientInCollection(ClientId searchParam, Iterable<ClientId> searched) {
        ClientId searchResult = Iterables.find(searched,
                input -> (input.memberEquals(searchParam) &&
                        Objects.equals(input.getSubsystemCode(), searchParam.getSubsystemCode())),
                null);
        return (searchResult != null);
    }

}
