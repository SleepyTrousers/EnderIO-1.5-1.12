package crazypants.enderio.conduits.render;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IExtractor;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.conduits.conduit.power.IPowerConduit;
import crazypants.enderio.conduits.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduits.geom.ConnectionModeGeometry;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ModelLoader.White;

public class ConduitInOutRenderer {

  // background
  public static final @Nonnull TextureSupplier ICON_BG = TextureRegistry.registerTexture("blocks/item_conduit_io_connector");
  // centered in arrow
  public static final @Nonnull TextureSupplier ICON_IN = TextureRegistry.registerTexture("blocks/item_conduit_input");
  // centered out arrow
  public static final @Nonnull TextureSupplier ICON_OUT = TextureRegistry.registerTexture("blocks/item_conduit_output");
  // both arrows
  // public static final @Nonnull TextureSupplier ICON_INOUT_IN_OUT = TextureRegistry.registerTexture("blocks/item_conduit_in_out");
  // offset in arrow
  public static final @Nonnull TextureSupplier ICON_INOUT_IN = TextureRegistry.registerTexture("blocks/item_conduit_in_out_in");
  // offset out arrow
  public static final @Nonnull TextureSupplier ICON_INOUT_OUT = TextureRegistry.registerTexture("blocks/item_conduit_in_out_out");

  public static void renderIO(@Nonnull IConduitBundle bundle, @Nonnull IClientConduit conduit, @Nonnull CollidableComponent component, BlockRenderLayer layer,
      @Nonnull List<BakedQuad> quads, @Nonnull DyeColor inChannel, @Nonnull DyeColor outChannel) {
    if (layer != BlockRenderLayer.CUTOUT || !component.isDirectional()) {
      return;
    }
    EnumFacing dir = component.getDirection();
    if (!conduit.getExternalConnections().contains(dir)) {
      return;
    }
    ConnectionMode mode = conduit.getConnectionMode(dir);
    if (!mode.acceptsInput() && !mode.acceptsOutput()) {
      return;
    }
    addColorBand(conduit, component, quads, dir, mode);

    if (conduit instanceof IRedstoneConduit) {
      // redstone conduits are backwards...
      if (mode == ConnectionMode.INPUT) {
        mode = ConnectionMode.OUTPUT;
      } else if (mode == ConnectionMode.OUTPUT) {
        mode = ConnectionMode.INPUT;
      }
    }

    Offset offset = bundle.getOffset(conduit.getBaseConduitType(), dir);
    ConnectionModeGeometry.addModeConnectorQuads(dir, offset, ICON_BG.get(TextureAtlasSprite.class), null, quads);
    if (mode.acceptsInput()) {
      if (mode.acceptsOutput()) {
        ConnectionModeGeometry.addModeConnectorQuads(dir, offset, ICON_INOUT_IN.get(TextureAtlasSprite.class), ColorUtil.toFloat4(inChannel.getColor()), quads);
        ConnectionModeGeometry.addModeConnectorQuads(dir, offset, ICON_INOUT_OUT.get(TextureAtlasSprite.class), ColorUtil.toFloat4(outChannel.getColor()),
            quads);
      } else {
        ConnectionModeGeometry.addModeConnectorQuads(dir, offset, ICON_IN.get(TextureAtlasSprite.class), ColorUtil.toFloat4(inChannel.getColor()), quads);
      }
    } else if (mode.acceptsOutput()) {
      ConnectionModeGeometry.addModeConnectorQuads(dir, offset, ICON_OUT.get(TextureAtlasSprite.class), ColorUtil.toFloat4(outChannel.getColor()), quads);
    }
  }

  private static void addColorBand(@Nonnull IClientConduit conduit, @Nonnull CollidableComponent component, @Nonnull List<BakedQuad> quads,
      @Nonnull EnumFacing dir, @Nonnull ConnectionMode mode) {
    if (IPowerConduit.COLOR_CONTROLLER_ID.equals(component.data) && (conduit instanceof IExtractor)) {
      IExtractor pc = (IExtractor) conduit;
      final RedstoneControlMode extractionRedstoneMode = pc.getExtractionRedstoneMode(dir);
      if (extractionRedstoneMode != RedstoneControlMode.IGNORE && extractionRedstoneMode != RedstoneControlMode.NEVER && mode.acceptsInput()) {
        int cInt = pc.getExtractionSignalColor(dir).getColor();
        BoundingBox bound = component.bound;
        if (mode != ConnectionMode.NOT_SET) {
          Vector3d trans = ForgeDirectionOffsets.offsetScaled(dir, -0.12);
          bound = trans != null ? bound.translate(trans) : bound;
        }
        for (EnumFacing face : EnumFacing.VALUES) {
          if (face != null && face != dir) {
            BakedQuadBuilder.addBakedQuadForFace(quads, bound, White.INSTANCE, face, ConduitTexture.FULL, false, false, ColorUtil.toFloat4(cInt));
          }
        }
      }
    }
  }

}
