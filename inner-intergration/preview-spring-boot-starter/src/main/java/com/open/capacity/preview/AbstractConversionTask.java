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
package com.open.capacity.preview;

import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.io.IOException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XComponent;
import com.sun.star.task.ErrorCodeIOException;
import com.sun.star.util.CloseVetoException;
import com.sun.star.util.XCloseable;
import com.open.capacity.preview.office.OfficeContext;
import com.open.capacity.preview.office.OfficeException;
import com.open.capacity.preview.office.OfficeTask;
import com.open.capacity.preview.office.OfficeUtils;

import java.io.File;
import java.util.Map;

public abstract class AbstractConversionTask implements OfficeTask {

    private final File inputFile;
    private final File outputFile;

    public AbstractConversionTask(File inputFile, File outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    protected abstract Map<String, ?> getLoadProperties(File inputFile);

    protected abstract Map<String, ?> getStoreProperties(File outputFile, XComponent document);

    @Override
    public void execute(OfficeContext context) throws OfficeException {
        XComponent document = null;
        try {
            document = loadDocument(context, inputFile);
            modifyDocument(document);
            storeDocument(document, outputFile);
        } catch (OfficeException officeException) {
            throw officeException;
        } catch (Exception exception) {
            throw new OfficeException("conversion failed", exception);
        } finally {
            if (document != null) {
                XCloseable closeable = OfficeUtils.cast(XCloseable.class, document);
                if (closeable != null) {
                    try {
                        closeable.close(true);
                    } catch (CloseVetoException closeVetoException) {
                        // whoever raised the veto should close the document
                    }
                } else {
                    document.dispose();
                }
            }
        }
    }

    private XComponent loadDocument(OfficeContext context, File inputFile) throws OfficeException {
        if (!inputFile.exists()) {
            throw new OfficeException("input document not found");
        }
        XComponentLoader loader = OfficeUtils.cast(XComponentLoader.class, context.getService(OfficeUtils.SERVICE_DESKTOP));
        Map<String, ?> loadProperties = getLoadProperties(inputFile);
        XComponent document = null;
        try {
            document = loader.loadComponentFromURL(OfficeUtils.toUrl(inputFile), "_blank", 0, OfficeUtils.toUnoProperties(loadProperties));
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new OfficeException("could not load document: " + inputFile.getName(), illegalArgumentException);
        } catch (ErrorCodeIOException errorCodeIOException) {
            throw new OfficeException("could not load document: " + inputFile.getName() + "; errorCode: " + errorCodeIOException.ErrCode, errorCodeIOException);
        } catch (IOException ioException) {
            throw new OfficeException("could not load document: " + inputFile.getName(), ioException);
        }
        if (document == null) {
            throw new OfficeException("could not load document: " + inputFile.getName());
        }
        return document;
    }

    /**
     * Override to modify the document after it has been loaded and before it gets
     * saved in the new format.
     * <p>
     * Does nothing by default.
     *
     * @param document
     * @throws OfficeException
     */
    protected void modifyDocument(XComponent document) throws OfficeException {
        // noop
    }

    private void storeDocument(XComponent document, File outputFile) throws OfficeException {
        Map<String, ?> storeProperties = getStoreProperties(outputFile, document);
        if (storeProperties == null) {
            throw new OfficeException("unsupported conversion");
        }
        try {
            OfficeUtils.cast(XStorable.class, document).storeToURL(OfficeUtils.toUrl(outputFile), OfficeUtils.toUnoProperties(storeProperties));
        } catch (ErrorCodeIOException errorCodeIOException) {
            throw new OfficeException("could not store document: " + outputFile.getName() + "; errorCode: " + errorCodeIOException.ErrCode, errorCodeIOException);
        } catch (IOException ioException) {
            throw new OfficeException("could not store document: " + outputFile.getName(), ioException);
        }
    }

}
