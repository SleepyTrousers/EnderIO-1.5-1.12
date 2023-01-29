package crazypants.enderio.machine.soul;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.ForgeDirectionOffsets;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.ClientProxy;
import crazypants.enderio.EnderIO;

public class SoulBinderRenderer implements ISimpleBlockRenderingHandler {

    private float skullScale = 0.5f;
    private BoundingBox scaledBB = BoundingBox.UNIT_CUBE.scale(skullScale, skullScale, skullScale);
    private IIcon[] icons = new IIcon[6];
    private IIcon override = null;

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

        GL11.glDisable(GL11.GL_LIGHTING);
        Tessellator.instance.startDrawingQuads();
        renderWorldBlock(null, 0, 0, 0, block, 0, renderer);
        Tessellator.instance.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
            RenderBlocks renderer) {

        IIcon soulariumIcon = EnderIO.blockSoulFuser.getIcon(ForgeDirection.EAST.ordinal(), 0);
        override = renderer.overrideBlockTexture;

        // Horrible hack to get the MC lighting engine to set the correct values for me
        if (renderer != null && world != null) {
            renderer.setOverrideBlockTexture(IconUtil.blankTexture);
            renderer.renderStandardBlock(Blocks.stone, x, y, z);
            renderer.setOverrideBlockTexture(null);
        }
        BoundingBox bb;

        Tessellator.instance.addTranslation(x, y, z);

        bb = BoundingBox.UNIT_CUBE.scale(0.85, 0.85, 0.85);
        setIcons(soulariumIcon, soulariumIcon, ForgeDirection.NORTH);
        CubeRenderer.render(bb, icons, true);

        float slabWidth = 0.15f;
        bb = BoundingBox.UNIT_CUBE.scale(1, slabWidth, 1);
        bb = bb.translate(0, 0.5f - (slabWidth / 2), 0);
        setIcons(soulariumIcon, EnderIO.blockSoulFuser.getIcon(ForgeDirection.UP.ordinal(), 0), ForgeDirection.UP);
        CubeRenderer.render(bb, icons, true);

        bb = BoundingBox.UNIT_CUBE.scale(1, slabWidth, 1);
        bb = bb.translate(0, -0.5f + (slabWidth / 2), 0);
        setIcons(soulariumIcon, soulariumIcon, ForgeDirection.NORTH);

        CubeRenderer.render(bb, icons, true);

        IIcon endermanIcon;
        int facing = ForgeDirection.SOUTH.ordinal();

        if (world == null || !(world.getTileEntity(x, y, z) instanceof TileSoulBinder)) {
            endermanIcon = EnderIO.blockSoulFuser.endermanSkullIcon;
        } else {
            TileSoulBinder sb = (TileSoulBinder) world.getTileEntity(x, y, z);
            facing = sb.facing;
            endermanIcon = sb.isActive() ? EnderIO.blockSoulFuser.endermanSkullIconOn
                    : EnderIO.blockSoulFuser.endermanSkullIcon;
        }

        renderSkull(forFacing(ForgeDirection.SOUTH, facing), soulariumIcon, endermanIcon);
        renderSkull(forFacing(ForgeDirection.WEST, facing), soulariumIcon, EnderIO.blockSoulFuser.skeletonSkullIcon);
        renderSkull(forFacing(ForgeDirection.NORTH, facing), soulariumIcon, EnderIO.blockSoulFuser.zombieSkullIcon);
        renderSkull(forFacing(ForgeDirection.EAST, facing), soulariumIcon, EnderIO.blockSoulFuser.creeperSkullIcon);

        Tessellator.instance.addTranslation(-x, -y, -z);

        return true;
    }

    private ForgeDirection forFacing(ForgeDirection side, int facing) {
        return ForgeDirection.values()[ClientProxy.sideAndFacingToSpriteOffset[side.ordinal()][facing]];
    }

    private void renderSkull(ForgeDirection face, IIcon soulariumIcon, IIcon faceIcon) {
        BoundingBox bb;
        bb = scaledBB.translate(ForgeDirectionOffsets.offsetScaled(face, 0.5 - skullScale / 2));
        setIcons(soulariumIcon, faceIcon, face);
        CubeRenderer.render(bb, icons, true);
    }

    private void setIcons(IIcon defaultIcon, IIcon faceIcon, ForgeDirection faceSide) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            icons[dir.ordinal()] = override != null ? override : dir == faceSide ? faceIcon : defaultIcon;
        }
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return BlockSoulBinder.renderId;
    }
}
