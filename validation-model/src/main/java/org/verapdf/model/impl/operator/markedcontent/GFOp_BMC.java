package org.verapdf.model.impl.operator.markedcontent;


import org.verapdf.cos.COSBase;
import org.verapdf.cos.COSName;
import org.verapdf.cos.COSObjType;
import org.verapdf.model.baselayer.Object;
import org.verapdf.model.coslayer.CosName;
import org.verapdf.model.impl.cos.GFCosName;
import org.verapdf.model.operator.Op_BMC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Timur Kamalov
 */
public class GFOp_BMC extends GFOpMarkedContent implements Op_BMC {

	/** Type name for {@code GFOp_BMC} */
    public static final String OP_BMC_TYPE = "Op_BMC";

    public GFOp_BMC(List<COSBase> arguments) {
        super(arguments, OP_BMC_TYPE);
    }

    @Override
    public List<? extends Object> getLinkedObjects(String link) {
        switch (link) {
            case TAG:
                return this.getTag();
            case PROPERTIES:
                return this.getPropertiesDict();
            default:
                return super.getLinkedObjects(link);
        }
    }

	@Override
	protected List<CosName> getTag() {
		if (!this.arguments.isEmpty()) {
			COSBase name = this.arguments.get(this.arguments.size() - 1);
			if (name.getType() == COSObjType.COS_NAME) {
				List<CosName> list = new ArrayList<>(MAX_NUMBER_OF_ELEMENTS);
				list.add(new GFCosName((COSName) name));
				return Collections.unmodifiableList(list);
			}
		}
		return Collections.emptyList();
	}

}
