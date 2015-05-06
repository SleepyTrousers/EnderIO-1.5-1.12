package crazypants.enderio.item.darksteel.upgrade;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelController;
import crazypants.enderio.item.darksteel.DarkSteelItems;

import static org.lwjgl.opengl.GL11.glDepthMask;

public class GliderUpgrade extends AbstractUpgrade {

  private static String UPGRADE_NAME = "glide";
  
  public static final GliderUpgrade INSTANCE = new GliderUpgrade();
  
  private final Render render = new Render();
  
  public static GliderUpgrade loadFromItem(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(stack.stackTagCompound == null) {
      return null;
    }
    if(!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
      return null;
    }
    return new GliderUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
  }
  
  
  public GliderUpgrade(NBTTagCompound tag) {
    super(UPGRADE_NAME, tag);    
  }

  public GliderUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.glider", new ItemStack(DarkSteelItems.itemGliderWing,1,1), Config.darkSteelGliderCost);
  }  
  
  @Override
  public boolean canAddToItem(ItemStack stack) {
    if(stack == null || stack.getItem() != DarkSteelItems.itemDarkSteelChestplate) {
      return false;
    }
    GliderUpgrade up = loadFromItem(stack);
    if(up == null) {
      return true;
    }
    return false;
  }

  @Override
  public IRenderUpgrade getRender() {
    return render;
  }

  @Override
  public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
  }

  @SideOnly(Side.CLIENT)
  private class Render implements IRenderUpgrade {

    private EntityItem item = new EntityItem(Minecraft.getMinecraft().theWorld);
    private ItemStack glider = new ItemStack(DarkSteelItems.itemGliderWing, 1, 1);

    private Render() {
      item.setEntityItemStack(glider);
    }

    @Override
    public void render(RenderPlayerEvent event, ItemStack stack, boolean head) {
      if (!head && DarkSteelController.instance.isGlideActive(event.entityPlayer)) {
        RenderUtil.bindItemTexture();
        glDepthMask(true);
        item.hoverStart = 0;
        GL11.glTranslatef(-0, 1, 0.25f);
        GL11.glRotatef(180, 0, 0, 1);
        GL11.glScalef(3, 3, 3);
        RenderManager.instance.renderEntityWithPosYaw(item, 0, 0, 0, 0, 0);
      }
    }
  }
}
