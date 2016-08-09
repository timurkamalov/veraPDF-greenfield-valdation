package org.verapdf.model.impl.pd;

import org.apache.log4j.Logger;
import org.verapdf.model.baselayer.Object;
import org.verapdf.model.impl.containers.StaticContainers;
import org.verapdf.model.pdlayer.PDDocument;
import org.verapdf.model.pdlayer.PDMetadata;
import org.verapdf.model.pdlayer.PDPage;
import org.verapdf.model.pdlayer.PDStructTreeRoot;
import org.verapdf.pd.PDCatalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Timur Kamalov
 */
public class GFPDDocument extends GFPDObject implements PDDocument {

    private static final Logger LOGGER = Logger.getLogger(GFPDDocument.class);

    public static final String PD_DOCUMENT_TYPE = "PDDocument";

    /**
     * Link name for pages
     */
    public static final String PAGES = "pages";
    /**
     * Link name for main metadata of document
     */
    public static final String METADATA = "metadata";
    /**
     * Link name for annotations structure tree root of document
     */
    public static final String STRUCTURE_TREE_ROOT = "StructTreeRoot";

    private final PDCatalog catalog;

    public GFPDDocument(org.verapdf.pd.PDDocument document) {
        super(document, PD_DOCUMENT_TYPE);
        PDCatalog catalog = null;
        try {
            catalog = document.getCatalog();
        } catch (IOException e) {
            LOGGER.debug("Can't obtain document catalog");
        }
        this.catalog = catalog;
    }

    @Override
    public List<? extends Object> getLinkedObjects(String link) {
        switch (link) {
            case PAGES:
                return this.getPages();
            case METADATA:
                return this.getMetadata();
            case STRUCTURE_TREE_ROOT:
                return this.getStructureTreeRoot();
            default:
                return super.getLinkedObjects(link);
        }
    }

    private List<PDPage> getPages() {
        try {
            List<PDPage> result = new ArrayList<>();
            List<org.verapdf.pd.PDPage> rawPages = StaticContainers.getDocument().getPages();
            for (org.verapdf.pd.PDPage rawPage : rawPages) {
                result.add(new GFPDPage(rawPage));
            }
            return result;
        } catch (Exception e) {
            LOGGER.debug("Error while processing pages.");
        }
        return Collections.emptyList();
    }

    @Override
    public Boolean getcontainsAlternatePresentations() {
        return Boolean.FALSE;
    }

    private List<PDStructTreeRoot> getStructureTreeRoot() {
        try {
            org.verapdf.pd.PDStructTreeRoot root = document.getStructTreeRoot();
            if (root != null) {
                List<PDStructTreeRoot> res = new ArrayList<>(MAX_NUMBER_OF_ELEMENTS);
                res.add(new GFPDStructTreeRoot(root));
                return Collections.unmodifiableList(res);
            }
        } catch (IOException e) {
            LOGGER.debug("Exception during obtaining Structure tree root", e);
        }
        return Collections.emptyList();
    }

    private List<PDMetadata> getMetadata() {
        if (this.catalog != null) {
            org.verapdf.pd.PDMetadata meta = this.catalog.getMetadata();
            if (meta != null) {
                List<PDMetadata> metadata = new ArrayList<>(MAX_NUMBER_OF_ELEMENTS);
                metadata.add(new GFPDMetadata(meta, Boolean.TRUE));
                return Collections.unmodifiableList(metadata);
            }
        }
        return Collections.emptyList();
    }
}
