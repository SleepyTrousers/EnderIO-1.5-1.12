package crazypants.enderio.machine.painter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.Util;

import crazypants.enderio.config.Config;
import crazypants.enderio.machine.recipe.RecipeInput;

public class PaintSourceValidator {

    public static PaintSourceValidator instance = new PaintSourceValidator();

    private boolean listsPopulated = false;
    private final List<RecipeInput> whitelist = new ArrayList<RecipeInput>();
    private final List<RecipeInput> blacklist = new ArrayList<RecipeInput>();

    public boolean isValidSourceDefault(ItemStack paintSource) {
        if (paintSource == null) {
            return false;
        }
        Block block = Util.getBlockFromItemId(paintSource);
        if (block == null || block instanceof IPaintedBlock) {
            return false;
        }
        if (isBlacklisted(paintSource)) {
            return false;
        }
        if (isWhitelisted(paintSource)) {
            return true;
        }
        if (!Config.allowTileEntitiesAsPaintSource && block instanceof ITileEntityProvider) {
            return false;
        }

        return block.isOpaqueCube() || (block.getMaterial().isOpaque() && block.renderAsNormalBlock())
                || block == Blocks.glass;
    }

    public boolean isWhitelisted(ItemStack paintSource) {
        return isInList(paintSource, whitelist);
    }

    public boolean isBlacklisted(ItemStack paintSource) {
        return isInList(paintSource, blacklist);
    }

    public void addToWhitelist(ItemStack input) {
        addToWhitelist(new RecipeInput(input, true));
    }

    public void addToWhitelist(RecipeInput input) {
        whitelist.add(input);
    }

    public void addToBlacklist(ItemStack input) {
        addToBlacklist(new RecipeInput(input, true));
    }

    public void addToBlacklist(RecipeInput input) {
        blacklist.add(input);
    }

    public void removeFromWhitelist(RecipeInput input) {
        removeFromList(input, whitelist);
    }

    public void removeFromBlackList(RecipeInput input) {
        removeFromList(input, blacklist);
    }

    protected boolean isInList(ItemStack paintSource, List<RecipeInput> list) {
        if (paintSource == null) {
            return false;
        }
        for (RecipeInput ri : list) {
            if (ri != null && ri.isInput(paintSource)) {
                return true;
            }
        }
        return false;
    }

    protected void removeFromList(RecipeInput input, List<RecipeInput> list) {
        ItemStack inStack = input.getInput();
        if (inStack == null) {
            return;
        }
        RecipeInput toRemove = null;
        for (RecipeInput in : list) {
            if (ItemUtil.areStacksEqual(inStack, in.getInput())) {
                toRemove = in;
                break;
            }
        }
        if (toRemove != null) {
            list.remove(toRemove);
        }
    }

    public void loadConfig() {
        PaintSourceParser.loadConfig();
    }
}
