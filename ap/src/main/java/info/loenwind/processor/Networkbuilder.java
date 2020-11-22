package info.loenwind.processor;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

public class Networkbuilder {

  private static final String NULLER = "_null";
  private static final String ERROR = "_error";

  private boolean hasNuller = false;
  private int nextNullerBit = 0x1;
  private CodeBlock isInNuller = null;

  private boolean hasErrorFlag = false;

  private CodeBlock.Builder writerPre = CodeBlock.builder();
  private CodeBlock.Builder writerPre2 = CodeBlock.builder();
  private CodeBlock.Builder writer = CodeBlock.builder();
  private CodeBlock.Builder writerPost = CodeBlock.builder();
  private CodeBlock.Builder readerPre = CodeBlock.builder();
  private CodeBlock.Builder reader = CodeBlock.builder();
  private CodeBlock.Builder readerPost = CodeBlock.builder();

  private MethodSpec.Builder writerBuilder, readerBuilder;

  public Networkbuilder(Builder writerBuilder, Builder readerBuilder) {
    this.writerBuilder = writerBuilder;
    this.readerBuilder = readerBuilder;
  }

  static Networkbuilder builder() {
    return new Networkbuilder(null, null);
  }

  static Networkbuilder builder(MethodSpec.Builder writerBuilder, MethodSpec.Builder readerBuilder) {
    return new Networkbuilder(writerBuilder, readerBuilder);
  }

  void build() {
    if (writerBuilder != null) {
      writerBuilder.addCode(buildWriter());
    }
    if (readerBuilder != null) {
      readerBuilder.addCode(buildReader());
    }
  }

  CodeBlock buildWriter() {
    return CodeBlock.builder().add(writerPre.build()).add(writerPre2.build()).add(writer.build()).add(writerPost.build()).build();
  }

  CodeBlock buildReader() {
    return CodeBlock.builder().add(readerPre.build()).add(reader.build()).add(readerPost.build()).build();
  }

  public static class TooManyNullableParametersException extends Exception {
    private static final long serialVersionUID = -5987343144369714864L;
  }

  Networkbuilder beginNullable(ParameterSpec parameter) throws TooManyNullableParametersException {
    setupNuller();
    if (nextNullerBit == 0) {
      throw new TooManyNullableParametersException();
    }
    writerPre.beginControlFlow("if ($N == null)", parameter);
    writerPre.addStatement("$L |= 0b$L", NULLER, Integer.toBinaryString(nextNullerBit));
    writerPre.endControlFlow();

    writer.beginControlFlow("if ($N != null)", parameter);
    isInNuller = CodeBlock.builder().add("(($L & 0b$L) != 0) ? null : ", NULLER, Integer.toBinaryString(nextNullerBit)).build();

    nextNullerBit <<= 1;
    return this;
  }

  private void setupNuller() {
    if (!hasNuller) {
      writerPre.addStatement("int $L = 0x00", NULLER);
      writerPre2.addStatement("buf.writeInt($L)", NULLER);
      readerPre.addStatement("final int $L = buf.readInt()", NULLER);
      hasNuller = true;
    }
  }

  Networkbuilder endNullable() {
    if (isInNuller != null) {
      writer.endControlFlow();
      isInNuller = null;
    }
    return this;
  }

  Networkbuilder setReaderError() {
    setupError();
    reader.addStatement("$L = true", ERROR);
    return this;
  }

  Networkbuilder setReaderError(String controlFlow, Object... args) {
    setupError();
    reader.beginControlFlow(controlFlow, args);
    reader.addStatement("$L = true", ERROR);
    reader.endControlFlow();
    return this;
  }

  private void setupError() {
    if (!hasErrorFlag) {
      readerPre.addStatement("boolean $L = false", ERROR);
      readerPost.beginControlFlow("if ($L)", ERROR);
      readerPost.addStatement("return");
      readerPost.endControlFlow();
      hasErrorFlag = true;
    }
  }

  Networkbuilder addWriterStatement(String format, Object... args) {
    writer.addStatement(format, args);
    return this;
  }

  Networkbuilder addReaderDeclaration(String format, Object... args) {
    readerPre.addStatement(format, args);
    return this;
  }

  Networkbuilder addReaderStatement(String format, Object... args) {
    reader.addStatement(format, args);
    return this;
  }

  Networkbuilder addReaderStatement(TypeName type, ParameterSpec name, String assignment, Object... args) {
    reader.add("final $T $N = ", type, name);
    if (isInNuller != null) {
      reader.add(isInNuller);
    }
    reader.addStatement(assignment, args);
    return this;
  }

  CodeBlock.Builder getWriter() {
    return writer;
  }

  CodeBlock.Builder getReader() {
    return reader;
  }

}
