package org.tron.core.db2.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.aspectj.org.eclipse.jdt.core.dom.PostfixExpression;
import org.tron.core.db.common.WrappedByteArray;

import java.awt.image.ImageConsumer;

@EqualsAndHashCode(exclude = "operator")
public class Value implements Encoder, Decoder<Value> {

  @Override
  public byte[] encode() {
    if (data.getBytes() == null) {
      return new byte[]{operator.getValue()};
    }

    byte[] r = new byte[1 + data.getBytes().length];
    r[0] = operator.getValue();
    System.arraycopy(data.getBytes(), 0, r, 1, data.getBytes().length);
    return r;
  }

  @Override
  public Value decode(byte[] bytes) {
    Operator operator = Operator.valueOf(bytes[0]);
    byte[] value = null;
    if (bytes.length > 1) {
      value = new byte[bytes.length - 1];
      System.arraycopy(bytes, 1, value, 0, bytes.length - 1);
    }
    return Value.of(operator, value);
  }

  public enum Operator {
    CREATE((byte) 0),
    MODIFY((byte) 1),
    DELETE((byte) 2);

    @Getter
    private byte value;

    Operator(byte value) {
      this.value = value;
    }

    static Operator valueOf(byte b) {
      switch (b) {
        case 0:
          return Operator.CREATE;
        case 1:
          return Operator.MODIFY;
        case 2:
          return Operator.DELETE;
        default:
          return null;
      }
    }
  }

  @Getter
  final private Operator operator;
  final private WrappedByteArray data;

  private Value(Operator operator, WrappedByteArray data) {
    this.operator = operator;
    this.data = data;
  }

  public static Value of(Operator operator, byte[] data) {
    return new Value(operator, WrappedByteArray.of(data));
  }

  public byte[] getBytes() {
    return data.getBytes();
  }
}
