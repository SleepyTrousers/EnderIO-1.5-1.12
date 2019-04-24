package crazypants.enderio.base.block.painted;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.interfaces.IOverlayRenderAware;

import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.render.itemoverlay.MobNameOverlayRenderHelper;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockItemPaintedPressurePlate extends BlockItemPaintedBlock implements IOverlayRenderAware {

  public BlockItemPaintedPressurePlate(@Nonnull BlockPaintedPressurePlate block) {
    super(block);
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return EnumPressurePlateType.getTypeFromMeta(stack.getMetadata()).hasEffect();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    CapturedMob capturedMob = CapturedMob.create(stack);
    if (capturedMob != null) {
      tooltip.add(Lang.PRESSURE_PLATE_TUNED.get(capturedMob.getDisplayName()));
    }
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    MobNameOverlayRenderHelper.doItemOverlayIntoGUI(stack, xPosition, yPosition);
  }

}