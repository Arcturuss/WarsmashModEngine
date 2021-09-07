package com.etheller.interpreter.ast.value.visitor;

import com.etheller.interpreter.ast.expression.ArithmeticSign;
import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.JassValueVisitor;
import com.etheller.interpreter.ast.value.RealJassValue;
import com.etheller.interpreter.ast.value.StringJassValue;

public class ArithmeticJassValueVisitor implements JassValueVisitor<JassValue> {
	public static final ArithmeticJassValueVisitor INSTANCE = new ArithmeticJassValueVisitor();
	private JassValue rightHand;
	private ArithmeticSign sign;

	public ArithmeticJassValueVisitor reset(final JassValue rightHand, final ArithmeticSign sign) {
		this.rightHand = rightHand;
		this.sign = sign;
		return this;
	}

	@Override
	public JassValue accept(final BooleanJassValue value) {
		return this.rightHand.visit(ArithmeticLeftHandBooleanJassValueVisitor.INSTANCE.reset(value, this.sign));
	}

	@Override
	public JassValue accept(final IntegerJassValue value) {
		return this.rightHand.visit(ArithmeticLeftHandIntegerJassValueVisitor.INSTANCE.reset(value, this.sign));
	}

	@Override
	public JassValue accept(final RealJassValue value) {
		return this.rightHand.visit(ArithmeticLeftHandRealJassValueVisitor.INSTANCE.reset(value, this.sign));
	}

	@Override
	public JassValue accept(final StringJassValue value) {
		if (this.rightHand == null) {
			return this.sign.apply(value.getValue(), null);
		}
		return this.rightHand.visit(ArithmeticLeftHandStringJassValueVisitor.INSTANCE.reset(value, this.sign));
	}

	@Override
	public JassValue accept(final CodeJassValue value) {
		throw new UnsupportedOperationException("Cannot perform arithmetic on code");
	}

	@Override
	public JassValue accept(final ArrayJassValue value) {
		throw new UnsupportedOperationException("Cannot perform arithmetic on array");
	}

	@Override
	public JassValue accept(final HandleJassValue value) {
		final HandleJassType leftHandType = value.getType();
		if (!leftHandType.isNullable()) {
			throw new UnsupportedOperationException("Cannot operate on null for type: " + leftHandType.getName());
		}
		// TODO would be nice not to have to call getNullValue here...
		if (this.rightHand == null) {
			return this.sign.apply(value, leftHandType.getNullValue());
		}
		return this.rightHand.visit(ArithmeticLeftHandHandleJassValueVisitor.INSTANCE.reset(value, this.sign));
	}

}
