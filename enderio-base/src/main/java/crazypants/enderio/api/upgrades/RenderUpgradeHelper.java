package crazypants.enderio.api.upgrades;

import javax.annotation.Nonnull;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RenderUpgradeHelper {

  /**
   * An {@link IRenderUpgrade} that does nothing. Can be used when implementing {@link IHasPlayerRenderer} to only conditionally render.
   */
  @SideOnly(Side.CLIENT)
  public static final @Nonnull IRenderUpgrade NULL_RENDERER = new IRenderUpgrade() {
    @Override
    public void doRenderLayer(@Nonnull RenderPlayer renderPlayer, EntityEquipmentSlot equipmentSlot, @Nonnull ItemStack piece,
        @Nonnull AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_,
        float p_177141_7_, float scale) {
    }
  };

}
