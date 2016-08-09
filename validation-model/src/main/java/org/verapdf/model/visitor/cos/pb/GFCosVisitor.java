package org.verapdf.model.visitor.cos.pb;

import org.verapdf.as.ASAtom;
import org.verapdf.cos.*;
import org.verapdf.cos.visitor.ICOSVisitor;
import org.verapdf.model.impl.cos.*;

/**
 * @author Timur Kamalov
 */
public class GFCosVisitor implements ICOSVisitor {

    private GFCosVisitor() {
    }

    public static GFCosVisitor getInstance() {
        return new GFCosVisitor();
    }

    /** {@inheritDoc} Create a GFCosArray for corresponding COSArray.
     * @return GFCosArray object
     * @see GFCosArray
     */
    @Override
    public Object visitFromArray(COSArray obj) {
        return new GFCosArray(obj);
    }

    /** {@inheritDoc} Create a GFCosBool for corresponding COSBoolean.
     * @return GFCosBool object
     * @see GFCosBool
     */
    @Override
    public Object visitFromBoolean(COSBoolean obj) {
        return GFCosBool.valueOf(obj);
    }

    /** {@inheritDoc} Create a GFCosFileSpecification COSDictionary if
     * value of type key of {@code obj} is file specification. Otherwise
     * create GFCosDict
     * @return GFCosFileSpecification or GFCosDict
     * @see GFCosDict
     * @see GFCosFileSpecification
     */
    @Override
    public Object visitFromDictionary(COSDictionary obj) {
        ASAtom type = obj.getNameKey(ASAtom.TYPE);
        boolean isFileSpec = type != null && ASAtom.FILESPEC.equals(type);
        return isFileSpec ? new GFCosFileSpecification(obj) : new GFCosDict(obj);
    }

    /** {@inheritDoc} Create a GFCosDocument for corresponding COSDocument.
     * @return GFCosDocument object
     * @see GFCosDocument
     */
    @Override
    public Object visitFromDocument(COSDocument obj) {
        return new GFCosDocument(obj);
    }

    /** {@inheritDoc} Create a GFCosReal for corresponding COSReal.
     * @return GFCosReal object
     * @see GFCosReal
     */
    @Override
    public Object visitFromReal(COSReal obj) {
        return new GFCosReal(obj);
    }

    /** {@inheritDoc} Create a GFCosInteger for corresponding COSInteger.
     * @return GFCosInteger object
     * @see GFCosInteger
     */
    @Override
    public Object visitFromInteger(COSInteger obj) {
        return new GFCosInteger(obj);
    }

    /** {@inheritDoc} Create a GFCosName for corresponding COSName.
     * @return GFCosName object
     * @see GFCosName
     */
    @Override
    public Object visitFromName(COSName obj) {
        return new GFCosName(obj);
    }

    /** {@inheritDoc} Create a GFCosNull for corresponding COSNull.
     * @return GFCosNull object
     * @see GFCosNull
     */
    @Override
    public Object visitFromNull(COSNull obj) {
        return GFCosNull.getInstance();
    }

    /** {@inheritDoc} Create a GFCosStream for corresponding COSStream.
     * @return GFCosStream object
     * @see GFCosStream
     */
    @Override
    public Object visitFromStream(COSStream obj) {
        return new GFCosStream(obj);
    }

    /** {@inheritDoc} Create a GFCosString for corresponding COSString.
     * @return GFCosString object
     * @see GFCosString
     */
    @Override
    public Object visitFromString(COSString obj) {
        return new GFCosString(obj);
    }

    /** Notification of visiting in indirect object. Create a GFCosIndirect for corresponding
     * COSIndirect.
     * @return {@link GFCosIndirect} object
     * @see GFCosIndirect
     */
    public static Object visitFromIndirect(COSIndirect obj) {
        return new GFCosIndirect(obj);
    }

}