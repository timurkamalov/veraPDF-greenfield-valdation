package org.verapdf.model.impl.pd;

import org.verapdf.cos.COSArray;
import org.verapdf.model.baselayer.Object;
import org.verapdf.model.coslayer.CosBBox;
import org.verapdf.model.factory.colors.ColorSpaceFactory;
import org.verapdf.model.impl.containers.StaticContainers;
import org.verapdf.model.impl.cos.GFCosBBox;
import org.verapdf.model.impl.pd.util.PDResourcesHandler;
import org.verapdf.model.pdlayer.*;
import org.verapdf.pd.PDAnnotation;
import org.verapdf.pd.actions.PDPageAdditionalActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * the object representing PDF page.
 * @author Sergey Shemyakov
 */
public class GFPDPage extends GFPDObject implements PDPage {

    /** Type name for GFPDPage */
    public static final String PD_PAGE_TYPE = "PDPage";

    /** Link name for page annotations */
    private static final String ANNOTS = "annots";
    /** Link name for page additional actions */
    private static final String ACTION = "AA";
    /** Link name for page content stream */
    private static final String CONTENT_STREAM = "contentStream";
    /** Link name for page transparency group */
    private static final String GROUP = "Group";
    /** Link name for page media box */
    private static final String MEDIA_BOX = "MediaBox";
    /** Link name for page crop box */
    private static final String CROP_BOX = "CropBox";
    /** Link name for page bleed box */
    private static final String BLEED_BOX = "BleedBox";
    /** Link name for trim media box */
    private static final String TRIM_BOX = "TrimBox";
    /** Link name for page art box */
    private static final String ART_BOX = "ArtBox";
    /** Link name for page presentation steps */
    private static final String PRESENTATION_STEPS = "PresSteps";
    /** Link name for page group colorspace */
    private static final String GROUP_CS = "groupCS";

    public static final int MAX_NUMBER_OF_ACTIONS = 2;

    private boolean containsTransparency = false;
    private List<PDContentStream> contentStreams = null;
    private List<PDAnnot> annotations = null;

    /**
     * Default constructor
     * @param pdPage is greenfield parser PDPage.
     */
    public GFPDPage(org.verapdf.pd.PDPage pdPage) {
        super(pdPage, PD_PAGE_TYPE);
    }

    @Override
    public List<? extends Object> getLinkedObjects(String link) {
        switch (link) {
            case GROUP:
                return this.getGroup();
            case ANNOTS:
                return this.getAnnotations();
            case ACTION:
                return this.getActions();
            case CONTENT_STREAM:
                return this.getContentStream();
            case MEDIA_BOX:
                return this.getMediaBox();
            case CROP_BOX:
                return this.getCropBox();
            case BLEED_BOX:
                return this.getBleedBox();
            case TRIM_BOX:
                return this.getTrimBox();
            case ART_BOX:
                return this.getArtBox();
            case GROUP_CS:
                return this.getGroupCS();
            default:
                return super.getLinkedObjects(link);
        }
    }

    private List<PDGroup> getGroup() {
        org.verapdf.pd.PDGroup group = ((org.verapdf.pd.PDPage) simplePDObject).getGroup();
        if (group != null) {
            List<PDGroup> res = new ArrayList<>(MAX_NUMBER_OF_ELEMENTS);
            res.add(new GFPDGroup(group));
            return Collections.unmodifiableList(res);
        }
        return Collections.emptyList();
    }

    private List<PDAnnot> getAnnotations() {
        if (this.annotations == null) {
            this.annotations = parseAnnotataions();
        }

        return this.annotations;
    }

    private List<PDAnnot> parseAnnotataions() {
        List<PDAnnotation> annots = ((org.verapdf.pd.PDPage) simplePDObject).getAnnotations();
        if (annots.size() > 0) {
            StaticContainers.transparencyCheckedSet.clear();
            String id = getID();
            if (id != null) {
                StaticContainers.transparencyCheckedSet.add(id);
            }
            List<PDAnnot> res = new ArrayList<>(annots.size());
            for (PDAnnotation annot : annots) {
                org.verapdf.pd.PDPage page = (org.verapdf.pd.PDPage) this.simplePDObject;
                PDResourcesHandler resourcesHandler = PDResourcesHandler.getInstance(page.getResources(), page.isInheritedResources());
                GFPDAnnot annotation = new GFPDAnnot(annot, resourcesHandler);
                String annotID = annotation.getID();
                if (annotID == null || !StaticContainers.transparencyCheckedSet.contains(annotID)) {
                    this.containsTransparency |= annotation.isContainsTransparency();
                }
                if (annotID != null) {
                    StaticContainers.transparencyCheckedSet.add(annotID);
                }
                res.add(annotation);
            }
            StaticContainers.transparencyCheckedSet.clear();
            return Collections.unmodifiableList(res);
        }
        return Collections.emptyList();
    }

    private List<PDAction> getActions() {
        PDPageAdditionalActions additionalActions =
                ((org.verapdf.pd.PDPage) this.simplePDObject).getAdditionalActions();
        if (additionalActions != null) {
            List<PDAction> actions = new ArrayList<>(MAX_NUMBER_OF_ACTIONS);

            org.verapdf.pd.actions.PDAction raw;

            raw = additionalActions.getC();
            this.addAction(actions, raw);

            raw = additionalActions.getO();
            this.addAction(actions, raw);

            return Collections.unmodifiableList(actions);
        }
        return Collections.emptyList();
    }

    private List<PDContentStream> getContentStream() {
        if (this.contentStreams == null) {
            parseContentStream();
        }
        return this.contentStreams;
    }

    private void parseContentStream() {
        StaticContainers.transparencyCheckedSet.clear();
        String id = getID();
        if (id != null) {
            StaticContainers.transparencyCheckedSet.add(id);
        }
        this.contentStreams = new ArrayList<>(MAX_NUMBER_OF_ELEMENTS);
        org.verapdf.pd.PDPage page = (org.verapdf.pd.PDPage) this.simplePDObject;
        if (page.getContent() != null) {
            PDResourcesHandler resourcesHandler = PDResourcesHandler.getInstance(page.getResources(), page.isInheritedResources());
            GFPDContentStream contentStream = new GFPDContentStream(page.getContent(), resourcesHandler);
            String contentStreamID = contentStream.getID();
            if (contentStreamID == null || !StaticContainers.transparencyCheckedSet.contains(contentStreamID)) {
                this.containsTransparency |= contentStream.isContainsTransparency();
            }
            if (contentStreamID != null) {
                StaticContainers.transparencyCheckedSet.add(contentStreamID);
            }
            contentStreams.add(contentStream);
        }
        StaticContainers.transparencyCheckedSet.clear();
    }

    private List<CosBBox> getMediaBox() {
        return getBBox(((org.verapdf.pd.PDPage) simplePDObject).getCOSMediaBox());
    }

    private List<CosBBox> getCropBox() {
        return getBBox(((org.verapdf.pd.PDPage) simplePDObject).getCOSCropBox());
    }

    private List<CosBBox> getBleedBox() {
        return getBBox(((org.verapdf.pd.PDPage) simplePDObject).getCOSBleedBox());
    }

    private List<CosBBox> getTrimBox() {
        return getBBox(((org.verapdf.pd.PDPage) simplePDObject).getCOSTrimBox());
    }

    private List<CosBBox> getArtBox() {
        return getBBox(((org.verapdf.pd.PDPage) simplePDObject).getCOSArtBox());
    }

    private List<CosBBox> getBBox(COSArray array) {
        if (array != null) {
            List<CosBBox> res = new ArrayList<>(MAX_NUMBER_OF_ELEMENTS);
            res.add(new GFCosBBox(array));
            return Collections.unmodifiableList(res);
        }
        return Collections.emptyList();
    }

    private List<PDColorSpace> getGroupCS() {
        org.verapdf.pd.PDGroup group = ((org.verapdf.pd.PDPage) simplePDObject).getGroup();
        if (group != null) {
            org.verapdf.pd.colors.PDColorSpace colorSpace = group.getColorSpace();
            if (colorSpace != null) {
                List<PDColorSpace> res = new ArrayList<>(MAX_NUMBER_OF_ELEMENTS);
                // TODO: check this. Have we add resources here?
                res.add(ColorSpaceFactory.getColorSpace(colorSpace));
                return Collections.unmodifiableList(res);
            }
        }
        return Collections.emptyList();
    }

    /**
     * @return true if the page contains presentation steps
     * (/PresSteps in the page dictionary).
     */
    @Override
    public Boolean getcontainsPresSteps() {
        return Boolean.valueOf(((org.verapdf.pd.PDPage) simplePDObject).getCOSPresSteps() != null);
    }

    /**
     * @return true if the page contains transparency.
     */
    @Override
    public Boolean getcontainsTransparency() {
        if (this.contentStreams == null) {
            parseContentStream();
        }
        if (this.annotations == null) {
            this.annotations = parseAnnotataions();
        }
        return this.containsTransparency;
    }
}
