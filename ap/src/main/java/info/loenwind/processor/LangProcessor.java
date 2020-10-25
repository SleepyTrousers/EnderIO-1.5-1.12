package info.loenwind.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

@SupportedAnnotationTypes("info.loenwind.processor.LangBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class LangProcessor extends AbstractProcessor {

  private File findResource(String path) throws IOException, URISyntaxException {
    Filer filer = processingEnv.getFiler();

    JavaFileObject dummySourceFile = filer.createSourceFile("dummy" + System.currentTimeMillis());
    String dummySourceFilePath = dummySourceFile.toUri().toString();

    if (dummySourceFilePath.startsWith("file:")) {
      if (!dummySourceFilePath.startsWith("file://")) {
        dummySourceFilePath = "file://" + dummySourceFilePath.substring("file:".length());
      }
    } else {
      dummySourceFilePath = "file://" + dummySourceFilePath;
    }

    File dummyFile = new File(new URI(dummySourceFilePath));

    while (!new File(dummyFile.getAbsolutePath() + "/src/main/resources/").exists()) {
      dummyFile = dummyFile.getParentFile();
      if (dummyFile == null || !dummyFile.isDirectory()) {
        return null;
      }
    }

    return new File(dummyFile.getAbsolutePath() + "/src/main/resources/" + path);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
        if (element.getKind() == ElementKind.CLASS) {
          try {
            generateLangFile((TypeElement) element);
          } catch (IOException | URISyntaxException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Internal error: " + e.getMessage(), element);
            e.printStackTrace();
          }
        }
      }
    }
    return false;
  }

  private void generateLangFile(TypeElement element) throws IOException, URISyntaxException {
    Filer filer = processingEnv.getFiler();

    // String packageName = ((PackageElement) element.getEnclosingElement()).getQualifiedName().toString();
    LangBuilder annotation = element.getAnnotation(LangBuilder.class);
    ClassName className = ClassName.get(ClassName.get(element).packageName(), annotation.classname());

    // we cannot call annotation.mod() as that would want to give us a Class object, which is not accessible at this point. So we need to dig a bit to get that
    // parameter value as a TypeMirror instead...
    TypeMirror modClass = findAnnotationClassParameter(element);
    if (modClass == null) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Couldn't find class given in parameter 'mod'", element);
      return;
    }
    String modid = getModid(processingEnv.getTypeUtils().asElement(modClass));

    String file = "assets/" + modid + "/lang/" + annotation.lang() + ".lang";
    File resource = findResource(file);
    if (resource == null) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Couldn't find source folder root", element);
      return;
    } else if (!resource.isFile()) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Resource '" + file + "' is not a file", element);
      return;
    } else if (!resource.exists()) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Resource '" + file + "' does not exist", element);
      return;
    } else if (!resource.canRead()) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Resource '" + file + "' is not readable", element);
      return;
    }

    TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(className);

    Set<String> multiLine = new HashSet<>();

    try (InputStream input = new FileInputStream(resource)) {
      Properties prop = new Properties();
      prop.load(input);

      prop.forEach((key, langstring) -> {
        String value = String.valueOf(key);
        if (!value.startsWith("tile.") && !value.startsWith("item.") && !value.startsWith("entity.")) {
          boolean isMultiLine = value.matches(".*\\.line\\d+$");
          int placeholders = -1;
          if (isMultiLine) {
            value = value.replaceFirst("\\.line\\d+$", "");
            if (multiLine.contains(value)) {
              return;
            }
            multiLine.add(value);
          } else {
            placeholders = String.valueOf(langstring).replaceAll("%%", "").replaceAll("[^%]", "").length();
          }

          String name = value.replaceFirst("^" + modid + "\\.", ".").replaceAll("[^a-zA-Z0-9]", "_").replaceAll("__+", "_").replaceFirst("^_", "")
              .toUpperCase(Locale.ENGLISH);
          enumBuilder.addEnumConstant(name, TypeSpec.anonymousClassBuilder("$S, $L, $L", value, placeholders, isMultiLine).build());
        }
      });
    }

    final TypeName nnString = ClassName.get(String.class).annotated(AnnotationSpec.builder(ClassName.get("javax.annotation", "Nonnull")).build());
    enumBuilder.addField(nnString, "key", Modifier.FINAL, Modifier.PRIVATE);
    enumBuilder.addField(TypeName.INT, "placeholders", Modifier.FINAL, Modifier.PRIVATE);
    enumBuilder.addField(TypeName.BOOLEAN, "multiline", Modifier.FINAL, Modifier.PRIVATE);

    enumBuilder.addMethod(MethodSpec.constructorBuilder() //
        .addModifiers(Modifier.PRIVATE) //
        .addParameter(nnString, "key", Modifier.FINAL) //
        .addParameter(TypeName.INT, "placeholders", Modifier.FINAL) //
        .addParameter(TypeName.BOOLEAN, "multiline", Modifier.FINAL) //
        .addStatement("this.key = key") //
        .addStatement("this.placeholders = placeholders") //
        .addStatement("this.multiline = multiline") //
        .build());

    enumBuilder.addSuperinterface(ClassName.get("crazypants.enderio.base.lang", "ILang"));

    enumBuilder.addMethod(MethodSpec.methodBuilder("getKey").addModifiers(Modifier.PUBLIC) //
        .addAnnotation(ClassName.get(Override.class)) //
        .returns(nnString) //
        .addStatement("return key") //
        .build());

    enumBuilder.addMethod(MethodSpec.methodBuilder("getLang").addModifiers(Modifier.PUBLIC) //
        .addAnnotation(ClassName.get(Override.class)) //
        .returns(ClassName.get("com.enderio.core.common", "Lang").annotated(AnnotationSpec.builder(ClassName.get("javax.annotation", "Nonnull")).build())) //
        .addStatement("return $T.$L", modClass, annotation.lang_field()) //
        .build());

    JavaFile.builder(className.packageName(), enumBuilder.build()).build().writeTo(filer);

    return;
  }

  /**
   * Returns {@link LangBuilder#mod()} which is annotated on the given element as a {@link TypeMirror}.
   * <p>
   * Is there any way to do this a little bit less convoluted?
   * 
   * @param element
   *          The annotated element
   * @return A TypeMirror that corresponds to the parameter or null if it couldn't be found.
   */
  private TypeMirror findAnnotationClassParameter(TypeElement element) {
    final TypeMirror LANG_BUILDER = processingEnv.getElementUtils().getTypeElement(LangBuilder.class.getCanonicalName()).asType();
    for (AnnotationMirror m : element.getAnnotationMirrors()) {
      if (processingEnv.getTypeUtils().isSameType(m.getAnnotationType().asElement().asType(), LANG_BUILDER)) {
        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> b : m.getElementValues().entrySet()) {
          if (b.getKey().getSimpleName().toString().equals("mod")) {
            Object x = b.getValue().getValue();
            if (x instanceof TypeMirror) {
              return (TypeMirror) x;
            } else {
              processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "How did you assign something that's not a Class to a field of type Class<?>? ",
                  element);
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * Returns the String value of the parameter 'modid' of the annotation 'net.minecraftforge.fml.common.Mod' that is on the given element.
   * <p>
   * This is a bit less convoluted, but not by much.
   * 
   * @param e
   *          The annotated element.
   * @return The String value or null if the annotation of parameter could not be found.
   */
  private String getModid(Element e) {
    final ClassName MOD = ClassName.get("net.minecraftforge.fml.common", "Mod");
    for (AnnotationMirror m : e.getAnnotationMirrors()) {
      if (MOD.equals(ClassName.get(m.getAnnotationType()))) {
        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> b : m.getElementValues().entrySet()) {
          if (b.getKey().getSimpleName().toString().equals("modid")) {
            return b.getValue().getValue().toString();
          }
        }
      }
    }
    return null;
  }

}
