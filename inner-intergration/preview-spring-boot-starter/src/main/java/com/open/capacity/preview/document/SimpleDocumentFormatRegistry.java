//
// JODConverter - Java OpenDocument Converter
// Copyright 2004-2012 Mirko Nasato and contributors
//
// JODConverter is Open Source software, you can redistribute it and/or
// modify it under either (at your option) of the following licenses
//
// 1. The GNU Lesser General Public License v3 (or later)
//    -> http://www.gnu.org/licenses/lgpl-3.0.txt
// 2. The Apache License, Version 2.0
//    -> http://www.apache.org/licenses/LICENSE-2.0.txt
//
package com.open.capacity.preview.document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleDocumentFormatRegistry implements DocumentFormatRegistry {

    private final List<DocumentFormat> documentFormats = new ArrayList<>();

    public void addFormat(DocumentFormat documentFormat) {
        documentFormats.add(documentFormat);
    }

    @Override
    public DocumentFormat getFormatByExtension(String extension) {
        if (extension == null) {
            return null;
        }
        String lowerExtension = extension.toLowerCase();
        for (DocumentFormat format : documentFormats) {
            if (format.getExtension().equals(lowerExtension)) {
                return format;
            }
        }
        return null;
    }

    @Override
    public DocumentFormat getFormatByMediaType(String mediaType) {
        if (mediaType == null) {
            return null;
        }
        for (DocumentFormat format : documentFormats) {
            if (format.getMediaType().equals(mediaType)) {
                return format;
            }
        }
        return null;
    }

    @Override
    public Set<DocumentFormat> getOutputFormats(DocumentFamily family) {
        Set<DocumentFormat> formats = new HashSet<>();
        for (DocumentFormat format : documentFormats) {
            if (format.getStoreProperties(family) != null) {
                formats.add(format);
            }
        }
        return formats;
    }

}
