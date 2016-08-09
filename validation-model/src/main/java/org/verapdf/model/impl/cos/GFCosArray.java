package org.verapdf.model.impl.cos;

import org.verapdf.cos.COSArray;
import org.verapdf.cos.COSObject;
import org.verapdf.model.baselayer.Object;
import org.verapdf.model.coslayer.CosArray;
import org.verapdf.model.coslayer.CosObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Timur Kamalov
 */
public class GFCosArray extends GFCosObject implements CosArray {

    /** Type name for GFCosArray */
    public static final String COS_ARRAY_TYPE = "CosArray";

    public static final String ELEMENTS = "elements";
    private final int size;

    /**
     * Default constructor
     * @param array greenfield COSArray
     */
    public GFCosArray(COSArray array) {
        this(array, COS_ARRAY_TYPE);
    }

    /**
     * Constructor used by child classes
     *
     * @param array greenfield COSArray
     * @param type type of object
     */
    public GFCosArray(COSArray array, String type) {
        super(array, type);
        this.size = array.size();
    }

    /**
     * Getter for array size.
     *
     * @return size of array
     */
    @Override
    public Long getsize() {
        return Long.valueOf(this.size);
    }

    @Override
    public List<? extends Object> getLinkedObjects(String link) {
        if (link.equals(ELEMENTS)) {
            return this.getElements();
        }
        return super.getLinkedObjects(link);
    }

    /**
     * Get all elements of array.
     *
     * @return elements of array
     */
    private List<CosObject> getElements() {
        List<CosObject> list = new ArrayList<>(this.getsize().intValue());
        Iterator iterator = ((COSArray) this.baseObject).iterator();
        while (iterator.hasNext()) {
            COSObject object = (COSObject) iterator.next();
            if (object != null && object.get() != null) {
                list.add(getFromValue(object.get()));
            }
        }
        return Collections.unmodifiableList(list);
    }

}
