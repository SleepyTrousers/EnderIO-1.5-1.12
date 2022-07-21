package crazypants.enderio.conduit;

public class ItemConduitSubtype {

    public final String unlocalisedName;

    public final String iconKey;

    public ItemConduitSubtype(String unlocalisedName, String iconKey) {
        this.unlocalisedName = "enderio." + unlocalisedName;
        this.iconKey = iconKey;
    }
}
