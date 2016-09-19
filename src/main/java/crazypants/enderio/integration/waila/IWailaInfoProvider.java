package crazypants.enderio.integration.waila;

import java.util.List;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IWailaInfoProvider {
  
  public static final int BIT_BASIC = 0x1;
  public static final int BIT_COMMON = 0x2;
  public static final int BIT_DETAILED = 0x4;
  public static final int ALL_BITS = BIT_BASIC | BIT_COMMON | BIT_DETAILED;
  
  /**
   * Adds the block's info to the WAILA information
   * @param tooltip - current list of strings in the WAILA body
   * @param world
   * @param x
   * @param y
   * @param z
   */
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z);
  
  /**
   * A bitmask for the default tooltips to show, use static ints in {@link IWailaInfoProvider}
   * <p>
   * Unneded if your block doesn't implement {@link IAdvancedTooltipProvider}
   * 
   * @return Bits enabled corresponding to the tooltip sections you want displayed.
   * <p>
   * Should be returned in the format bit | otherbit (ex. <code> BIT_BASIC | BIT_COMMON</code>). Return <code>ALL_BITS</code> to show all, or 0 to show none.
   */
  public int getDefaultDisplayMask(World world, int x, int y, int z);
}
