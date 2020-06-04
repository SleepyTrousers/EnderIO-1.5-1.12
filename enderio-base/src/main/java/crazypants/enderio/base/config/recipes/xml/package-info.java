//@javax.annotation.ParametersAreNonnullByDefault // Not the right one, but eclipse knows only 3 null annotations anyway, so it's ok
//@mcp.MethodsReturnNonnullByDefault // and Eclipse now observes the TypeQualifierDefault annotation, so we need this one, too
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
