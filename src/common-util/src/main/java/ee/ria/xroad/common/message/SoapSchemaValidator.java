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

import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import ee.ria.xroad.common.ErrorCodes;
import ee.ria.xroad.common.util.SchemaValidator;

/**
 * Validates SOAP messages according to schema.
 */
public class SoapSchemaValidator extends SchemaValidator {

    private static final String FILE = "soap-schema.xsd";

    private static Schema schema;

    static {
        schema = createSchema(FILE);
    }

    /**
     * Validates the provided SOAP message source.
     * @param source source of the SOAP message to be validated
     * @throws Exception if validation is unsuccessful
     */
    public static void validate(Source source) throws Exception {
        validate(schema, source, ErrorCodes.X_MALFORMED_SOAP);
    }
}