/*
 * Copyright (c) 1997, 2010, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.message.stream.StreamHeader;
import com.sun.xml.internal.ws.message.stream.StreamHeader12;

import javax.xml.stream.XMLStreamReader;
import java.util.Collections;
import java.util.List;
import java.io.InputStream;
import java.io.IOException;

/**
 * {@link StreamSOAPCodec} for SOAP 1.2.
 *
 * @author Paul.Sandoz@Sun.Com
 */
final class StreamSOAP12Codec extends StreamSOAPCodec {
    public static final String SOAP12_MIME_TYPE = "application/soap+xml";
    public static final String SOAP12_CONTENT_TYPE = SOAP12_MIME_TYPE+"; charset=utf-8";

    private static final List<String> expectedContentTypes = Collections.singletonList(SOAP12_MIME_TYPE);

    /*package*/ StreamSOAP12Codec() {
        super(SOAPVersion.SOAP_12);
    }

    public String getMimeType() {
        return SOAP12_MIME_TYPE;
    }

    @Override
    protected final StreamHeader createHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
        return new StreamHeader12(reader, mark);
    }

    public static final ContentTypeImpl defaultContentType =
            new ContentTypeImpl(SOAP12_CONTENT_TYPE);

    @Override
    protected ContentType getContentType(String soapAction) {
        // TODO: set accept header
        if (soapAction == null) {
            return defaultContentType;
        } else {
            return new ContentTypeImpl(SOAP12_CONTENT_TYPE + ";action="+fixQuotesAroundSoapAction(soapAction));
        }
    }

    @Override
    public void decode(InputStream in, String contentType, Packet packet, AttachmentSet att ) throws IOException {
        com.sun.xml.internal.ws.encoding.ContentType ct = new com.sun.xml.internal.ws.encoding.ContentType(contentType);
        packet.soapAction = fixQuotesAroundSoapAction(ct.getParameter("action"));
        super.decode(in,contentType,packet,att);
    }

    private String fixQuotesAroundSoapAction(String soapAction) {
        if(soapAction != null && (!soapAction.startsWith("\"") || !soapAction.endsWith("\"")) ) {
            String fixedSoapAction = soapAction;
            if(!soapAction.startsWith("\""))
                fixedSoapAction = "\"" + fixedSoapAction;
            if(!soapAction.endsWith("\""))
                fixedSoapAction = fixedSoapAction + "\"";
            return fixedSoapAction;
        }
        return soapAction;
    }

    protected List<String> getExpectedContentTypes() {
        return expectedContentTypes;
    }
}
