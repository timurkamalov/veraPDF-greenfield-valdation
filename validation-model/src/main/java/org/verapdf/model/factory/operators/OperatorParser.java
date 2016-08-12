/**
 *
 */
package org.verapdf.model.factory.operators;

import org.apache.log4j.Logger;
import org.verapdf.as.ASAtom;
import org.verapdf.cos.COSBase;
import org.verapdf.cos.COSInteger;
import org.verapdf.cos.COSName;
import org.verapdf.model.impl.operator.color.GFOpColor;
import org.verapdf.model.impl.operator.generalgs.*;
import org.verapdf.model.impl.operator.markedcontent.*;
import org.verapdf.model.impl.operator.opclip.GFOp_WStar;
import org.verapdf.model.impl.operator.opclip.GFOp_W_clip;
import org.verapdf.model.impl.operator.opcompability.GFOp_BX;
import org.verapdf.model.impl.operator.opcompability.GFOp_EX;
import org.verapdf.model.impl.operator.opcompability.GFOp_Undefined;
import org.verapdf.model.impl.operator.pathconstruction.*;
import org.verapdf.model.impl.operator.pathpaint.*;
import org.verapdf.model.impl.operator.shading.GFOp_sh;
import org.verapdf.model.impl.operator.specialgs.GFOp_Q_grestore;
import org.verapdf.model.impl.operator.specialgs.GFOp_cm;
import org.verapdf.model.impl.operator.specialgs.GFOp_q_gsave;
import org.verapdf.model.impl.operator.textobject.GFOpTextObject;
import org.verapdf.model.impl.operator.textposition.GFOpTextPosition;
import org.verapdf.model.impl.operator.textposition.GFOp_TD_Big;
import org.verapdf.model.impl.operator.textposition.GFOp_Td;
import org.verapdf.model.impl.operator.textposition.GFOp_Tm;
import org.verapdf.model.impl.operator.textshow.GFOp_DoubleQuote;
import org.verapdf.model.impl.operator.textshow.GFOp_Quote;
import org.verapdf.model.impl.operator.textshow.GFOp_TJ_Big;
import org.verapdf.model.impl.operator.textshow.GFOp_Tj;
import org.verapdf.model.impl.operator.textstate.*;
import org.verapdf.model.impl.operator.type3font.GFOp_d0;
import org.verapdf.model.impl.operator.type3font.GFOp_d1;
import org.verapdf.model.impl.operator.xobject.GFOp_Do;
import org.verapdf.model.impl.pd.util.PDResourcesHandler;
import org.verapdf.model.tools.constants.Operators;
import org.verapdf.operator.Operator;
import org.verapdf.pd.PDExtGState;
import org.verapdf.pd.colors.PDColorSpace;
import org.verapdf.pd.colors.PDDeviceCMYK;
import org.verapdf.pd.colors.PDDeviceGray;
import org.verapdf.pd.colors.PDDeviceRGB;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * @author Timur Kamalov
 */
class OperatorParser {

	private static final Logger LOGGER = Logger.getLogger(OperatorParser.class);
	private static final String MSG_PROBLEM_OBTAINING_RESOURCE = "Problem encountered while obtaining resources for ";

	private final Deque<GraphicState> graphicStateStack = new ArrayDeque<>();
	private GraphicState graphicState = new GraphicState();

	OperatorParser() {
	}

	void parseOperator(List<org.verapdf.model.operator.Operator> operators,
					   Operator rawOperator, List<COSBase> arguments) {

	}

	void parseOperator(List<org.verapdf.model.operator.Operator> processedOperators, Operator rawOperator,
					   PDResourcesHandler resourcesHandler, List<COSBase> arguments) throws IOException {
		String operatorName = rawOperator.getOperator();
		switch (operatorName) {
			// GENERAL GS
			case Operators.D_SET_DASH:
				processedOperators.add(new GFOp_d(arguments));
				break;
			case Operators.GS:
				processExtGState(processedOperators, arguments, resourcesHandler, this.graphicState);
				break;
			case Operators.I_SETFLAT:
				processedOperators.add(new GFOp_i(arguments));
				break;
			case Operators.J_LINE_CAP:
				processedOperators.add(new GFOp_J_line_cap(arguments));
				break;
			case Operators.J_LINE_JOIN:
				processedOperators.add(new GFOp_j_line_join(arguments));
				break;
			case Operators.M_MITER_LIMIT:
				processedOperators.add(new GFOp_M_miter_limit(arguments));
				break;
			case Operators.RI:
				processedOperators.add(new GFOp_ri(arguments));
				break;
			case Operators.W_LINE_WIDTH:
				processedOperators.add(new GFOp_w_line_width(arguments));
				break;

			// MARKED CONTENT
			case Operators.BMC:
				processedOperators.add(new GFOp_BMC(arguments));
				break;
			case Operators.BDC:
				processedOperators.add(new GFOp_BDC(arguments));
				break;
			case Operators.EMC:
				processedOperators.add(new GFOp_EMC(arguments));
				break;
			case Operators.MP:
				processedOperators.add(new GFOp_MP(arguments));
				break;
			case Operators.DP:
				processedOperators.add(new GFOp_DP(arguments));
				break;

			// CLIP
			case Operators.W_CLIP:
				processedOperators.add(new GFOp_W_clip(arguments));
				break;
			case Operators.W_STAR_EOCLIP:
				processedOperators.add(new GFOp_WStar(arguments));
				break;

			// COLOR
			case Operators.G_STROKE: {
				processColorSpace(this.graphicState, resourcesHandler, PDDeviceGray.INSTANCE,
						ASAtom.DEVICEGRAY, true);
				processedOperators.add(new GFOpColor(arguments));
				break;
			}
			case Operators.G_FILL: {
				processColorSpace(this.graphicState, resourcesHandler, PDDeviceGray.INSTANCE,
						ASAtom.DEVICEGRAY, false);
				processedOperators.add(new GFOpColor(arguments));
				break;
			}
			case Operators.RG_STROKE: {
				processColorSpace(this.graphicState, resourcesHandler, PDDeviceRGB.INSTANCE,
						ASAtom.DEVICERGB, true);
				processedOperators.add(new GFOpColor(arguments));
				break;
			}
			case Operators.RG_FILL: {
				processColorSpace(this.graphicState, resourcesHandler, PDDeviceRGB.INSTANCE,
						ASAtom.DEVICERGB, false);
				processedOperators.add(new GFOpColor(arguments));
				break;
			}
			case Operators.K_STROKE: {
				processColorSpace(this.graphicState, resourcesHandler, PDDeviceCMYK.INSTANCE,
						ASAtom.DEVICECMYK, true);
				processedOperators.add(new GFOpColor(arguments));
				break;
			}
			case Operators.K_FILL: {
				processColorSpace(this.graphicState, resourcesHandler, PDDeviceCMYK.INSTANCE,
						ASAtom.DEVICECMYK, true);
				processedOperators.add(new GFOpColor(arguments));
				break;
			}
			case Operators.CS_STROKE:
				this.graphicState.setStrokeColorSpace(resourcesHandler.getColorSpace(getLastCOSName(arguments)));
				processedOperators.add(new GFOpColor(arguments));
				break;
			case Operators.CS_FILL:
				this.graphicState.setFillColorSpace(resourcesHandler.getColorSpace(getLastCOSName(arguments)));
				processedOperators.add(new GFOpColor(arguments));
				break;
			case Operators.SCN_STROKE:
				processPatternColorSpace(arguments, this.graphicState, resourcesHandler,
										this.graphicState.getStrokeColorSpace(), true);
				processedOperators.add(new GFOpColor(arguments));
				break;
			case Operators.SCN_FILL:
				processPatternColorSpace(arguments, this.graphicState, resourcesHandler,
						this.graphicState.getFillColorSpace(), false);
				processedOperators.add(new GFOpColor(arguments));
				break;
			case Operators.SC_STROKE:
				processedOperators.add(new GFOpColor(arguments));
				break;
			case Operators.SC_FILL:
				processedOperators.add(new GFOpColor(arguments));
				break;

			// TEXT OBJECT
			case Operators.ET:
			case Operators.BT:
				processedOperators.add(new GFOpTextObject(arguments));
				break;

			// TEXT POSITION
			case Operators.TD_MOVE:
				processedOperators.add(new GFOp_Td(arguments));
				break;
			case Operators.TD_MOVE_SET_LEADING:
				processedOperators.add(new GFOp_TD_Big(arguments));
				break;
			case Operators.TM:
				processedOperators.add(new GFOp_Tm(arguments));
				break;
			case Operators.T_STAR:
				processedOperators.add(new GFOpTextPosition(arguments));
				break;

			// TEXT SHOW
			case Operators.TJ_SHOW:
				processedOperators.add(new GFOp_Tj(arguments, this.graphicState.clone(), resourcesHandler));
				break;
			case Operators.TJ_SHOW_POS:
				processedOperators.add(new GFOp_TJ_Big(arguments, this.graphicState.clone(), resourcesHandler));
				break;
			case Operators.QUOTE:
				processedOperators.add(new GFOp_Quote(arguments, this.graphicState.clone(), resourcesHandler));
				break;
			case Operators.DOUBLE_QUOTE:
				processedOperators.add(new GFOp_DoubleQuote(arguments, this.graphicState.clone(), resourcesHandler));
				break;

			// TEXT STATE
			case Operators.TZ:
				processedOperators.add(new GFOp_Tz(arguments));
				break;
			case Operators.TR:
				this.graphicState.setRenderingMode(getRenderingMode(arguments));
				processedOperators.add(new GFOp_Tr(arguments));
				break;
			case Operators.TF:
				this.graphicState.setFontName(getFirstCOSName(arguments));
				processedOperators.add(new GFOp_Tf(arguments));
				break;
			case Operators.TC:
				processedOperators.add(new GFOp_Tc(arguments));
				break;
			case Operators.TW:
				processedOperators.add(new GFOp_Tw(arguments));
				break;
			case Operators.TL:
				processedOperators.add(new GFOp_Tl(arguments));
				break;
			case Operators.TS:
				processedOperators.add(new GFOp_Ts(arguments));
				break;

			// TYPE 3 FONT
			case Operators.D0:
				processedOperators.add(new GFOp_d0(arguments));
				break;
			case Operators.D1:
				processedOperators.add(new GFOp_d1(arguments));
				break;

			// INLINE IMAGE
			case Operators.BI:
				//TODO : deal with inline images
				break;

			// COMPABILITY
			case Operators.BX:
				processedOperators.add(new GFOp_BX(arguments));
				break;
			case Operators.EX:
				processedOperators.add(new GFOp_EX(arguments));
				break;

			// PATH CONSTRUCTION
			case Operators.C_CURVE_TO:
				processedOperators.add(new GFOp_c(arguments));
				break;
			case Operators.H_CLOSEPATH:
				processedOperators.add(new GFOp_h(arguments));
				break;
			case Operators.L_LINE_TO:
				processedOperators.add(new GFOp_l(arguments));
				break;
			case Operators.M_MOVE_TO:
				processedOperators.add(new GFOp_m_moveto(arguments));
				break;
			case Operators.RE:
				processedOperators.add(new GFOp_re(arguments));
				break;
			case Operators.V:
				processedOperators.add(new GFOp_v(arguments));
				break;
			case Operators.Y:
				processedOperators.add(new GFOp_y(arguments));
				break;

			// PATH PAINT
			case Operators.B_CLOSEPATH_FILL_STROKE:
				processedOperators.add(new GFOp_b_closepath_fill_stroke(arguments, this.graphicState, resourcesHandler));
				break;
			case Operators.B_FILL_STROKE:
				processedOperators.add(new GFOp_B_fill_stroke(arguments, this.graphicState, resourcesHandler));
				break;
			case Operators.B_STAR_CLOSEPATH_EOFILL_STROKE:
				processedOperators.add(new GFOp_bstar_closepath_eofill_stroke(arguments, this.graphicState, resourcesHandler));
				break;
			case Operators.B_STAR_EOFILL_STROKE:
				processedOperators.add(new GFOp_BStar_eofill_stroke(arguments, this.graphicState, resourcesHandler));
				break;
			case Operators.F_FILL:
				processedOperators.add(new GFOp_f_fill(arguments, this.graphicState, resourcesHandler));
				break;
			case Operators.F_FILL_OBSOLETE:
				processedOperators.add(new GFOp_F_fill_obsolete(arguments, this.graphicState, resourcesHandler));
				break;
			case Operators.F_STAR_FILL:
				processedOperators.add(new GFOp_FStar(arguments, this.graphicState, resourcesHandler));
				break;
			case Operators.N:
				processedOperators.add(new GFOp_n(arguments));
				break;
			case Operators.S_CLOSE_STROKE:
				processedOperators.add( new GFOp_s_close_stroke(arguments, this.graphicState, resourcesHandler));
				break;
			case Operators.S_STROKE:
				processedOperators.add(new GFOp_S_stroke(arguments, this.graphicState, resourcesHandler));

			// SHADING
			case Operators.SH:
				processedOperators.add(new GFOp_sh(arguments, resourcesHandler.getShading(getLastCOSName(arguments))));
				break;

			// SPECIAL GS
			case Operators.CM_CONCAT:
				processedOperators.add(new GFOp_cm(arguments));
				break;
			case Operators.Q_GRESTORE:
				if (!graphicStateStack.isEmpty()) {
					this.graphicState.copyProperties(this.graphicStateStack.pop());
				}
				processedOperators.add(new GFOp_Q_grestore(arguments));
				break;
			case Operators.Q_GSAVE:
				this.graphicStateStack.push(this.graphicState.clone());
				processedOperators.add(new GFOp_q_gsave(arguments, this.graphicStateStack.size()));
				break;

			// XOBJECT
			case Operators.DO:
				processedOperators.add(new GFOp_Do(arguments, resourcesHandler.getXObject(getLastCOSName(arguments)),
													resourcesHandler));
				break;

			default:
				processedOperators.add(new GFOp_Undefined(arguments));
				break;
		}
	}

	private static void processExtGState(List<org.verapdf.model.operator.Operator> processedOperators, List<COSBase> arguments,
										 PDResourcesHandler resourcesHandler, GraphicState graphicState) {
		PDExtGState extGState = resourcesHandler.getExtGState(getLastCOSName(arguments));
		graphicState.copyPropertiesFormExtGState(extGState);
		processedOperators.add(new GFOp_gs(arguments, extGState));
	}

	private static void processColorSpace(GraphicState graphicState, PDResourcesHandler resourcesHandler,
										  PDColorSpace defaultCS, ASAtom name, boolean stroke) {
		PDColorSpace colorSpace = resourcesHandler == null ?  defaultCS : resourcesHandler.getColorSpace(name);
		if (stroke) {
			graphicState.setStrokeColorSpace(colorSpace);
		} else {
			graphicState.setFillColorSpace(colorSpace);
		}
	}

	private static void processPatternColorSpace(List<COSBase> arguments, GraphicState graphicState,
												 PDResourcesHandler resourcesHandler, PDColorSpace colorSpace, boolean stroke) {
		if (colorSpace != null && ASAtom.PATTERN == colorSpace.getType()) {
			if (stroke) {
				graphicState.setStrokeColorSpace(resourcesHandler.getPattern(getLastCOSName(arguments)));
			} else {
				graphicState.setFillColorSpace(resourcesHandler.getPattern(getLastCOSName(arguments)));
			}
		}
	}

	private static RenderingMode getRenderingMode(List<COSBase> arguments) {
		if (!arguments.isEmpty()) {
			COSBase renderingMode = arguments.get(0);
			if (renderingMode instanceof COSInteger) {
				return RenderingMode.getRenderingMode(renderingMode.getInteger().intValue());
			}
		}
		return RenderingMode.FILL;
	}

	private static COSName getFirstCOSName(List<COSBase> arguments) {
		COSBase lastElement = arguments.isEmpty() ? null : arguments.get(0);
		if (lastElement instanceof COSName) {
			return (COSName) lastElement;
		}
		return null;
	}

	private static COSName getLastCOSName(List<COSBase> arguments) {
		COSBase lastElement = arguments.isEmpty() ? null : arguments.get(arguments.size() - 1);
		if (lastElement instanceof COSName) {
			return (COSName) lastElement;
		}
		return null;
	}

}
