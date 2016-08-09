package org.verapdf.model.impl.pd.patterns;

import org.verapdf.model.baselayer.Object;
import org.verapdf.model.impl.pd.GFPDContentStream;
import org.verapdf.model.impl.pd.util.PDResourcesHandler;
import org.verapdf.model.pdlayer.PDContentStream;
import org.verapdf.model.pdlayer.PDTilingPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maksim Bezrukov
 */
public class GFPDTilingPattern extends GFPDPattern implements PDTilingPattern {

	public static final String TILING_PATTERN_TYPE = "PDTilingPattern";

	public static final String CONTENT_STREAM = "contentStream";

	private final PDResourcesHandler resourcesHandler;

	private List<PDContentStream> contentStreams = null;
	private boolean containsTransparency = false;

	public GFPDTilingPattern(
			org.verapdf.pd.patterns.PDTilingPattern simplePDObject, PDResourcesHandler resourcesHandler) {
		super(simplePDObject, TILING_PATTERN_TYPE);
		this.resourcesHandler = resourcesHandler;
	}

	@Override
	public List<? extends Object> getLinkedObjects(String link) {

		if (CONTENT_STREAM.equals(link)) {
			return this.getContentStream();
		}
		return super.getLinkedObjects(link);
	}

	private List<PDContentStream> getContentStream() {
		if (this.contentStreams == null) {
			parseContentStream();
		}
		return this.contentStreams;
	}

	/**
	 * @return true if content stream of the pattern contains transparency
	 */
	public boolean isContainsTransparency() {
		if (this.contentStreams == null) {
			parseContentStream();
		}
		return this.containsTransparency;
	}

	private void parseContentStream() {
		List<PDContentStream> contentStreams = new ArrayList<>(MAX_NUMBER_OF_ELEMENTS);
		GFPDContentStream contentStream = new GFPDContentStream((org.verapdf.pd.PDContentStream) this.simplePDObject, this.resourcesHandler);
		this.containsTransparency |= contentStream.isContainsTransparency();
		contentStreams.add(contentStream);
		this.contentStreams = contentStreams;
	}

}
