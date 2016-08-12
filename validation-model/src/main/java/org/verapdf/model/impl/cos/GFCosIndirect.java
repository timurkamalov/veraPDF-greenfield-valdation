package org.verapdf.model.impl.cos;

import org.verapdf.cos.COSIndirect;
import org.verapdf.cos.COSObject;
import org.verapdf.model.baselayer.Object;
import org.verapdf.model.coslayer.CosIndirect;
import org.verapdf.model.coslayer.CosObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Timur Kamalov
 */
public class GFCosIndirect extends GFCosObject implements CosIndirect {


    public static final String DIRECT_OBJECT = "directObject";
    /** Type name for GFCosBool */
    public static final String COS_INDIRECT_TYPE = "CosIndirect";

    private final boolean isSpacingPDFACompliant;

    private final String id;

    /**
     * Default constructor
     * @param indirectObject greenfield COSObject
     */
    public GFCosIndirect(COSIndirect indirectObject) {
        super(indirectObject, COS_INDIRECT_TYPE);
        //TODO : implement id generator
        //this.id = IDGenerator.generateID(indirectObject);
        this.isSpacingPDFACompliant = getspacingCompliesPDFA(indirectObject.getDirect());
        this.id = "";
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public List<? extends Object> getLinkedObjects(
            String link) {
        if (DIRECT_OBJECT.equals(link)) {
            return parseDirectObject();
        }
        return super.getLinkedObjects(link);

    }

    /**
     * Get the direct contents of the indirect object
     */
    private List<CosObject> parseDirectObject() {
        List<CosObject> list = new ArrayList<>();
        list.add(baseObject != null ? getFromValue(baseObject) : GFCosNull.getInstance());
        return Collections.unmodifiableList(list);
    }

    /**
     * true if the words 'obj' and 'endobj' are surrounded by the correct
     * spacings according to PDF/A standard
     */
    @Override
    public Boolean getspacingCompliesPDFA() {
        return Boolean.valueOf(this.isSpacingPDFACompliant);
    }

    /**
     * Get the direct contents of the indirect object
     */
    private static boolean getspacingCompliesPDFA(COSObject object) {
        return object.isEndOfObjectComplyPDFA()
                && object.isHeaderFormatComplyPDFA()
                && object.isHeaderOfObjectComplyPDFA();
    }

}
