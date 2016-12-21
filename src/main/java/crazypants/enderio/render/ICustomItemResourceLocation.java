package crazypants.enderio.render;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;

/**
 * This interface is for items that get registered by the SmartModelLoader but do not have their own blockstate file, using another block's one instead. The
 * glass blocks use this to have one blockstate file for all of them.
 *
 */
public interface ICustomItemResourceLocation {

  @Nonnull
  ResourceLocation getRegistryNameForCustomModelResourceLocation();

}
