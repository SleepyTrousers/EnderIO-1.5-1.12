package info.loenwind.processor;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

@SupportedAnnotationTypes("info.loenwind.processor.RemoteCall")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Processor extends AbstractProcessor {

  // Java
  private static final ClassName STRING = ClassName.get(String.class);
  private final static ClassName CONSUMER = ClassName.get(Consumer.class);

  // Minecraft
  private final static ClassName MINECRAFT = ClassName.get("net.minecraft.client", "Minecraft");
  private final static String CONTAINER = "net.minecraft.inventory.Container";
  private final static ClassName ITEM_STACK = ClassName.get("net.minecraft.item", "ItemStack");
  private final static ClassName NBT_TAG_COMPOUND = ClassName.get("net.minecraft.nbt", "NBTTagCompound");
  private final static ClassName ENTITY_PLAYER_MP = ClassName.get("net.minecraft.entity.player", "EntityPlayerMP");

  // Minecraft libs
  private final static ClassName BYTE_BUF = ClassName.get("io.netty.buffer", "ByteBuf");

  // Forge
  private final static ClassName EVENT_BUS_SUBSCRIBER = ClassName.get("net.minecraftforge.fml.common.Mod", "EventBusSubscriber");
  private final static ClassName SUBSCRIBE_EVENT = ClassName.get("net.minecraftforge.fml.common.eventhandler", "SubscribeEvent");
  private final static ClassName I_FORGE_REGISTRY_ENTRY__IMPL = ClassName.get("net.minecraftforge.registries", "IForgeRegistryEntry", "Impl");
  private final static ClassName REGISTRY_EVENT__REGISTER = ClassName.get("net.minecraftforge.event.RegistryEvent", "Register");
  private final static ClassName BYTE_BUF_UTILS = ClassName.get("net.minecraftforge.fml.common.network", "ByteBufUtils");

  // Ender IO
  private final static ClassName I_REMOTE_EXEC = ClassName.get("crazypants.enderio.base.network.ExecPacket", "IServerExec");
  private final static ClassName EXEC_PACKET = ClassName.get("crazypants.enderio.base.network", "ExecPacket");
  private final static ClassName ENUM_READER = ClassName.get("crazypants.enderio.util", "EnumReader");

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Types typeUtils = processingEnv.getTypeUtils();
    TypeMirror containerType = processingEnv.getElementUtils().getTypeElement(CONTAINER).asType();
    for (TypeElement annotation : annotations) {
      for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
        if (element.getKind() == ElementKind.CLASS) {
          TypeElement typeElement = (TypeElement) element;
          TypeMirror typeMirror = typeElement.asType();
          if (typeUtils.isAssignable(typeMirror, containerType)) {
            try {
              generateContainerProxy(typeElement);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported type '" + typeMirror + "' for @RemoteCall", element);
          }
        }
      }
    }
    return false;
  }

  private void generateContainerProxy(TypeElement typeElement) throws IOException {
    Filer filer = processingEnv.getFiler();
    String modid = typeElement.getAnnotation(RemoteCall.class).modid();
    if (modid.isEmpty()) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Parameter 'modid' must be set on class level", typeElement);
      return;
    }
    String packageName = ((PackageElement) typeElement.getEnclosingElement()).getQualifiedName().toString();
    String sourceClassName = typeElement.getSimpleName().toString();
    String proxyInterfaceClassName = sourceClassName + "Proxy";

    TypeSpec.Builder proxyInterfaceBuilder = TypeSpec.interfaceBuilder(proxyInterfaceClassName);

    for (ExecutableElement method : processingEnv.getElementUtils().getAllMembers(typeElement).stream().filter(el -> el.getKind() == ElementKind.METHOD)
        .map(el -> (ExecutableElement) el).filter(el -> el.getAnnotation(RemoteCall.class) != null).collect(Collectors.toList())) {
      String methodName = method.getSimpleName().toString();
      String methodProxyClassName = proxyInterfaceClassName + "$" + methodName;

      if (!method.getAnnotation(RemoteCall.class).modid().isEmpty()) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Parameter 'modid' must NOT be set on method level", method);
        return;
      }

      //

      FieldSpec instanceField = FieldSpec.builder(I_REMOTE_EXEC, "INSTANCE", Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
          .initializer("new $1L().setRegistryName($2S, $3S)", methodProxyClassName, modid, (sourceClassName + "_" + methodName).toLowerCase(Locale.ENGLISH))
          .build();

      TypeSpec.Builder methodProxyClassBuilder = TypeSpec.classBuilder(methodProxyClassName).addModifiers(Modifier.PUBLIC, Modifier.FINAL)
          .addAnnotation(AnnotationSpec.builder(EVENT_BUS_SUBSCRIBER).addMember("modid", "$S", modid).build())
          .superclass(ParameterizedTypeName.get(I_FORGE_REGISTRY_ENTRY__IMPL, I_REMOTE_EXEC)).addSuperinterface(I_REMOTE_EXEC).addField(instanceField)
          .addMethod(MethodSpec //
              .methodBuilder("register") //
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC) //
              .returns(void.class).addAnnotation(SUBSCRIBE_EVENT) //
              .addParameter(ParameterizedTypeName.get(REGISTRY_EVENT__REGISTER, I_REMOTE_EXEC), "event") //
              .addStatement("event.getRegistry().register($N)", instanceField).build());

      MethodSpec.Builder proxyMethodBuilder = MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC, Modifier.DEFAULT).returns(void.class);

      //

      CodeBlock.Builder parameterListCode = null;

      MethodSpec.Builder writerBuilder = MethodSpec.methodBuilder("makeWriter").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
          .returns(ParameterizedTypeName.get(CONSUMER, BYTE_BUF)).beginControlFlow("return buf ->")
          .addStatement("buf.writeInt($T.getMinecraft().player.openContainer.windowId)", MINECRAFT);

      MethodSpec.Builder runnerBuilder = MethodSpec.methodBuilder("apply").addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
          .returns(ParameterizedTypeName.get(CONSUMER, ENTITY_PLAYER_MP)).addParameter(BYTE_BUF, "buf").addStatement("final int _windowId = buf.readInt()");

      Networkbuilder b = Networkbuilder.builder(writerBuilder, runnerBuilder);

      for (VariableElement parameter : method.getParameters()) {
        TypeMirror paramTypeMirror = parameter.asType();
        TypeName typeName = TypeName.get(paramTypeMirror);
        ParameterSpec parameterSpec = ParameterSpec_get(parameter);

        writerBuilder.addParameter(parameterSpec);
        proxyMethodBuilder.addParameter(parameterSpec);
        if (parameterListCode == null) {
          parameterListCode = CodeBlock.builder().add("$N", parameterSpec);
        } else {
          parameterListCode.add(", $N", parameterSpec);
        }

        if (typeName.isPrimitive()) {
          String byteBuffCall = ucfirst(typeName.toString());
          b.addWriterStatement("buf.write$2L($1N)", parameterSpec, byteBuffCall);
          b.addReaderStatement("final $2T $1N = buf.read$3L()", parameterSpec, typeName, byteBuffCall);
        } else {
          if (!isNonnull(parameter)) {
            b.beginNullable(parameterSpec);
          }
          if (typeName.isBoxedPrimitive()) {
            String byteBuffCall = ucfirst(typeName.unbox().toString());
            b.addWriterStatement("buf.write$1L($2N)", byteBuffCall, parameterSpec);
            b.addReaderStatement(typeName, parameterSpec, "buf.read$1L()", byteBuffCall);
          } else if (paramTypeMirror.getKind() == TypeKind.DECLARED
              && ((TypeElement) ((DeclaredType) paramTypeMirror).asElement()).getKind() == ElementKind.ENUM) {
            b.addWriterStatement("buf.writeInt($1T.put($2N))", ENUM_READER, parameterSpec);
            b.addReaderStatement(typeName, parameterSpec, "$1T.get($2T.class, buf.readInt())", ENUM_READER, typeName);
          } else if (typeName.equals(STRING)) {
            b.addWriterStatement("$T.writeUTF8String(buf, $N)", BYTE_BUF_UTILS, parameterSpec);
            b.addReaderStatement(typeName, parameterSpec, "$1T.readUTF8String(buf)", BYTE_BUF_UTILS);
          } else if (typeName.equals(ITEM_STACK)) {
            b.addWriterStatement("$T.writeItemStack(buf, $N)", BYTE_BUF_UTILS, parameterSpec);
            b.addReaderStatement(typeName, parameterSpec, "$1T.readItemStack(buf)", BYTE_BUF_UTILS);
          } else if (typeName.equals(NBT_TAG_COMPOUND)) {
            b.addWriterStatement("$T.writeTag(buf, $N)", BYTE_BUF_UTILS, parameterSpec);
            b.addReaderStatement(typeName, parameterSpec, "$1T.readTag(buf)", BYTE_BUF_UTILS);
          } else {
            // TODO: []/List<>/NNList<> of supported types.
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Cannot serialize '" + typeName + "' into a byte stream", parameter);
            return;
          }
          b.endNullable();
        }
      }

      b.build();

      writerBuilder.endControlFlow(""/* "" to add a ; */);

      MethodSpec makeWriterMethod = writerBuilder.build();
      methodProxyClassBuilder.addMethod(makeWriterMethod);

      runnerBuilder.beginControlFlow("return player ->")
          .beginControlFlow("if (player.openContainer instanceof $T && player.openContainer.windowId == _windowId)", ClassName.get(typeElement))
          .addCode("(($T) player.openContainer).$L(", ClassName.get(typeElement), methodName);
      if (parameterListCode != null) {
        runnerBuilder.addCode(parameterListCode.build());
      }
      runnerBuilder.addStatement(")").endControlFlow().endControlFlow(""/* "" to add a ; */);
      methodProxyClassBuilder.addMethod(runnerBuilder.build());

      //

      TypeSpec methodProxyClass = methodProxyClassBuilder.build();
      JavaFile.builder(packageName, methodProxyClass).build().writeTo(filer);

      //

      proxyMethodBuilder.addCode("$1T.send($3N.$2N, $3N.$4N(", EXEC_PACKET, instanceField, methodProxyClass, makeWriterMethod);
      if (parameterListCode != null) {
        proxyMethodBuilder.addCode(parameterListCode.build());
      }
      proxyMethodBuilder.addCode("));");

      proxyInterfaceBuilder.addMethod(proxyMethodBuilder.build());
    }

    JavaFile.builder(packageName, proxyInterfaceBuilder.build()).build().writeTo(filer);

  }

  private static String ucfirst(String str) {
    return (str == null || str.isEmpty()) ? str : str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  private static boolean isNonnull(Element el) {
    for (AnnotationMirror annotation : el.getAnnotationMirrors()) {
      Element element = annotation.getAnnotationType().asElement();
      // assert element.getKind().equals(ElementKind.ANNOTATION_TYPE);
      return ((TypeElement) element).getQualifiedName().contentEquals("javax.annotation.Nonnull");
    }
    return false;
  }

  // see https://github.com/square/javapoet/issues/482
  private static ParameterSpec ParameterSpec_get(VariableElement element) {
    return ParameterSpec.builder(TypeName.get(element.asType()), element.getSimpleName().toString()).addModifiers(element.getModifiers())
        .addAnnotations(element.getAnnotationMirrors().stream().map((mirror) -> AnnotationSpec.get(mirror)).collect(Collectors.toList())).build();
  }
}
