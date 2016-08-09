package org.verapdf.model.impl.pd;


import org.apache.log4j.Logger;
import org.verapdf.cos.COSObjType;
import org.verapdf.cos.COSObject;
import org.verapdf.cos.COSStream;
import org.verapdf.model.factory.operators.OperatorFactory;
import org.verapdf.model.impl.pd.util.PDResourcesHandler;
import org.verapdf.model.operator.Operator;
import org.verapdf.model.pdlayer.PDContentStream;
import org.verapdf.parser.PDFStreamParser;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Timur Kamalov
 */
public class GFPDContentStream extends GFPDObject implements PDContentStream {

	private static final Logger LOGGER = Logger.getLogger(GFPDContentStream.class);

	public static final String CONTENT_STREAM_TYPE = "PDContentStream";

	public static final String OPERATORS = "operators";

	private PDResourcesHandler resourcesHandler;

	private List<Operator> operators = null;

	public GFPDContentStream(org.verapdf.pd.PDContentStream contentStream, PDResourcesHandler resourcesHandler) {
		super(contentStream, CONTENT_STREAM_TYPE);
		this.resourcesHandler = resourcesHandler;
	}

	@Override
	public List<? extends org.verapdf.model.baselayer.Object> getLinkedObjects(String link) {
		if (OPERATORS.equals(link)) {
			return this.getOperators();
		}
		return super.getLinkedObjects(link);
	}

	private List<Operator> getOperators() {
		if (this.operators == null) {
			parseOperators();
		}
		return this.operators;
	}

	private void parseOperators() {
		try {
			COSObject contentStream = simplePDObject.getObject();
			if (!contentStream.empty() && contentStream.get().getType() == COSObjType.COS_STREAM) {
				PDFStreamParser streamParser = new PDFStreamParser((COSStream) contentStream.get());
				streamParser.parseTokens();
				OperatorFactory operatorFactory = new OperatorFactory();
				List<Operator> result = operatorFactory.operatorsFromTokens(streamParser.getTokens(), resourcesHandler);

				this.operators = Collections.unmodifiableList(result);
			} else {
				this.operators = Collections.emptyList();
			}
		} catch (IOException e) {
			LOGGER.debug("Error while parsing content stream. " + e.getMessage(), e);
			this.operators = Collections.emptyList();
		}
	}

	public boolean isContainsTransparency() {
		// TODO: implement me
		return false;
	}
}
