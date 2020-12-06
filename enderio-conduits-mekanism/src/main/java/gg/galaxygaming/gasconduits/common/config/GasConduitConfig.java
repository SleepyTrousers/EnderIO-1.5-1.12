package gg.galaxygaming.gasconduits.common.config;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;


public final class GasConduitConfig {

    private static final int MAX = 2_000_000_000; // 0x77359400, keep some headroom to MAX_INT
    private static final int MAX_IO = MAX / 2;

    public static final IValueFactory F = Config.F.section("gasconduit");

    public static final IValue<Integer> tier1_extractRate = F.make("tier1_extractRate", 64,
          "Millibuckets per tick extracted by a gas conduit's auto extracting.").setRange(1, MAX_IO).sync();
    public static final IValue<Integer> tier1_maxIO = F.make("tier1_maxIO", 256,
          "Millibuckets per tick that can pass through a single connection to a gas conduit.").setRange(1, MAX_IO).sync();
    public static final IValue<Integer> tier2_extractRate = F.make("tier2_extractRate", 512,
          "Millibuckets per tick extracted by an advanced gas conduit's auto extracting.").setRange(1, MAX_IO).sync();
    public static final IValue<Integer> tier2_maxIO = F.make("tier2_maxIO", 2048,
          "Millibuckets per tick that can pass through a single connection to an advanced gas conduit.").setRange(1, MAX_IO).sync();
    public static final IValue<Integer> tier3_extractRate = F.make("tier3_extractRate", 4096,
          "Millibuckets per tick extracted by an ender gas conduit's auto extracting.").setRange(1, MAX_IO).sync();
    public static final IValue<Integer> tier3_maxIO = F.make("tier3_maxIO", 16384,
          "Millibuckets per tick that can pass through a single connection to an ender gas conduit.").setRange(1, MAX_IO).sync();
}