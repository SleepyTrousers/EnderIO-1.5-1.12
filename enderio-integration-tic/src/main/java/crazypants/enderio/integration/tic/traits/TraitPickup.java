package crazypants.enderio.integration.tic.traits;

import crazypants.enderio.base.item.darksteel.upgrade.direct.DirectUpgrade;
import crazypants.enderio.base.machine.fakeplayer.FakePlayerEIO;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitPickup extends ModifierTrait {

  public static TraitPickup instance = new TraitPickup();

  public TraitPickup() {
    super("enderpickup", 0x8F2ccdb1, 1, 1);
  }

  @Override
  public void blockHarvestDrops(ItemStack tool, HarvestDropsEvent event) {
    if (ToolHelper.isToolEffective2(tool, event.getState())) {
      DirectUpgrade.doDirect(event);
    }
  }

  @Override
  public boolean canApplyTogether(IToolMod otherModifier) {
    return !(otherModifier instanceof TraitPickup);
  }

  @Override
  public int getPriority() {
    return 10;
  }

  @Override
  public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
    if (damage > 0 && !player.world.isRemote && !(player instanceof FakePlayerEIO)) {
      target.getEntityData().setUniqueId(DirectUpgrade.HIT_BY_DIRECT_FREE, player.getUniqueID());
    }
  }

}
