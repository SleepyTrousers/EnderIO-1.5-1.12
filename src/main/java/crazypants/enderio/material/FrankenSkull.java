package crazypants.enderio.material;

public enum FrankenSkull {

    ZOMBIE_ELECTRODE("skullZombieElectrode", false),
    ZOMBIE_CONTROLLER("skullZombieController", false),
    FRANKEN_ZOMBIE("skullZombieFrankenstien", "enderio:skullZombieController", true),
    ENDER_RESONATOR("skullEnderResonator", "enderio:skullEnderResonator", false),
    SENTIENT_ENDER("skullSentientEnder", "enderio:skullEnderResonator", true),
    SKELETAL_CONTRACTOR("skullSkeletalContractor", "enderio:skullSkeletalContractor", false),
    GUARDIAN_DIODE("skullGuardianDiode", "enderio:skullGuardianDiode", false);

    public final String unlocalisedName;
    public final String iconKey;
    public final boolean isAnimated;

    private FrankenSkull(String unlocalisedName, String iconKey, boolean isAnimated) {
        this.unlocalisedName = unlocalisedName;
        this.iconKey = iconKey;
        this.isAnimated = isAnimated;
    }

    private FrankenSkull(String unlocalisedName, boolean isAnimated) {
        this(unlocalisedName, "enderio:" + unlocalisedName, isAnimated);
    }

    public String getUnlocalisedName() {
        return unlocalisedName;
    }

    public String getIconKey() {
        return iconKey;
    }
}
