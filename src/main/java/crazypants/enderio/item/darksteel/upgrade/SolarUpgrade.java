package crazypants.enderio.item.darksteel.upgrade;

import static org.lwjgl.opengl.GL11.glDepthMask;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class SolarUpgrade extends AbstractUpgrade {

  private static final String KEY_LEVEL = "level";

  private static final String UPGRADE_NAME = "speedBoost";

  public static final SolarUpgrade SOLAR_ONE = new SolarUpgrade("enderio.darksteel.upgrade.solar_one", (byte) 1, Config.darkSteelSolarOneCost);
  public static final SolarUpgrade SOLAR_TWO = new SolarUpgrade("enderio.darksteel.upgrade.solar_two", (byte) 2, Config.darkSteelSolarTwoCost);
  public static final SolarUpgrade SOLAR_THREE = new SolarUpgrade("enderio.darksteel.upgrade.solar_three", (byte) 3, Config.darkSteelSolarThreeCost);

  private Render render;

  public static SolarUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.stackTagCompound == null) {
      return null;
    }
    if(!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new SolarUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }

  private static ItemStack createUpgradeItem(int level) {
	  return new ItemStack(EnderIO.blockSolarPanel,1,level-1);
  }

  private byte level;

  public SolarUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);
    level = tag.getByte(KEY_LEVEL);
  }

  public SolarUpgrade(String unlocName, byte level, int levelCost) {
    super(UPGRADE_NAME, unlocName, createUpgradeItem(level), levelCost);
    this.level = (byte) level;
  }

  @Override
  public boolean canAddToItem(ItemStack stack) {
      if(stack == null || !DarkSteelItems.isArmorPart(stack.getItem(), 0)|| !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)) {
        return false;
      }
      SolarUpgrade up = loadFromItem(stack);
      if(up == null) {
        return level == 1;
      }
      return up.level == level - 1;
  }

  @Override
  public boolean hasUpgrade(ItemStack stack) {
    if(!super.hasUpgrade(stack)) {
      return false;
    }
    SolarUpgrade up = loadFromItem(stack);
    if(up == null) {
      return false;
    }
    return up.unlocName.equals(unlocName);
  }

  @Override
  public ItemStack getUpgradeItem() {
    return new ItemStack(EnderIO.blockSolarPanel, 1, level - 1);
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
    upgradeRoot.setByte(KEY_LEVEL, level);
  }

  public int getRFPerSec() {
	  if(level==0) return Config.darkSteelSolarOneGen;
	  if(level==1) return Config.darkSteelSolarTwoGen;
	  else return Config.darkSteelSolarThreeGen;

  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderUpgrade getRender() {
    return render == null ? render = new Render() : render;
  }

  @SideOnly(Side.CLIENT)
  private class Render implements IRenderUpgrade {

    private EntityItem item = new EntityItem(Minecraft.getMinecraft().theWorld);

    @Override
    public void render(RenderPlayerEvent event, ItemStack stack, boolean head) {
      if (head) {
        RenderUtil.bindItemTexture();
        glDepthMask(true);
        item.hoverStart = 0;
        Helper.translateToHeadLevel(event.entityPlayer);
        GL11.glTranslated(0, -0.155, 0);
        GL11.glRotated(180, 1, 0, 0);
        GL11.glScalef(2.1f, 2.1f, 2.1f);
        byte level = loadFromItem(stack).level;
        item.setEntityItemStack(new ItemStack(EnderIO.blockSolarPanel, 1, level-1));
        RenderManager.instance.renderEntityWithPosYaw(item, 0, 0, 0, 0, 0);
      }
    }
  }
}
