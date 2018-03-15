package crazypants.enderio.api.upgrades;

import javax.annotation.Nonnull;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * See {@link IHasPlayerRenderer}.
 * 
 * @author Henry Loenwind
 *
 */
public interface IRenderUpgrade {

  /**
   * Called when an item that implements this interface is in one of the equipment slots or in a Baubles slot. Also called for {@link IDarkSteelUpgrade}s in
   * equipment slots (but not Baubles slots).
   * 
   * @param renderPlayer
   *          The player renderer this is called for.
   * @param equipmentSlot
   *          The equipment slot the item is in. Null for Baubles slots.
   * @param piece
   *          The item that should render
   * @param entitylivingbaseIn
   *          see {@link LayerRenderer}
   * @param limbSwing
   *          see {@link LayerRenderer}
   * @param limbSwingAmount
   *          see {@link LayerRenderer}
   * @param partialTicks
   *          see {@link LayerRenderer}
   * @param ageInTicks
   *          see {@link LayerRenderer}
   * @param netHeadYaw
   *          see {@link LayerRenderer}
   * @param headPitch
   *          see {@link LayerRenderer}
   * @param scale
   *          see {@link LayerRenderer}
   */
  void doRenderLayer(@Nonnull RenderPlayer renderPlayer, EntityEquipmentSlot equipmentSlot, @Nonnull ItemStack piece,
      @Nonnull AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
      float headPitch, float scale);
}
