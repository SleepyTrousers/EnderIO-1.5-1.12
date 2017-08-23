package mods.immibis.core.api.multipart;

import net.minecraft.client.renderer.RenderBlocks;

/**
 * If a Block class implements this, {@link IMultipartSystem#renderMultiparts(net.minecraft.world.IBlockAccess, int, int, int, net.minecraft.client.renderer.RenderBlocks)}
 * will automatically be called for that block type (after the normal block renderer).
 * This is implemented by a hooking {@link RenderBlocks#renderBlockByRenderType(net.minecraft.block.Block, int, int, int)}.
 * 
 * (Instead of this, there used to be a transformer that would modify your getRenderType and use a wrapping ISBRH.
 * That was unreliable, and could have affected unrelated things like mob AI, and didn't work with Railcraft, so
 * it was dropped)
 */
public interface IMultipartRenderingBlockMarker {
}
