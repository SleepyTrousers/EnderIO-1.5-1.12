package mcp;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.meta.TypeQualifierDefault;

/**
 * This a wild hack because Eclipse started honoring @TypeQualifierDefault recently. Ideally fields in vanilla code would be properly annotated, but not a
 * single one of them is. And most of them are @Nonnull.
 * <p>
 * So to (a) make our code the same work with older and newer Eclipse versions and (b) avoid countless useless null checks (e.g. Blocks, Items, all
 * properties...) we chose to force all vanilla fields to @Nonnull with this.
 * <p>
 * Let's hope that future Minecraft versions come with better annotations...
 * 
 * @author Henry Loenwind
 *
 */

@Documented
@TypeQualifierDefault({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodsReturnNonnullByDefault {
}
