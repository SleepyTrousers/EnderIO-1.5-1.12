package crazypants.enderio.base.scheduler;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import com.enderio.core.common.util.EntityUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.PersonalConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Celeb11 implements Event, Runnable {

  public static void create() {
    Calendar cal = Calendar.getInstance(Locale.getDefault());
    if ((cal.get(Calendar.MONTH) == Calendar.DECEMBER && cal.get(Calendar.DAY_OF_MONTH) > 15)
        || (cal.get(Calendar.MONTH) == Calendar.JANUARY && cal.get(Calendar.DAY_OF_MONTH) < 3)) {
      //@formatter:off
      Scheduler.instance.registerEvent(new Celeb11("UTC+14:00", "Samoa", "Christmas Island", "Kiritimati", "Apia", "Salelologa"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+13:45", "Chatham Islands"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+13:00", "New Zealand", "Auckland", "Suva", "Wellington", "Nukualofa", "Tokelau", "Fiji", "Tonga", "McMurdo", "Skott stations", "Phoenix Islands"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+12:00", "Anadyr", "Funafuti", "Yaren", "Tarawa", "Majuro", "Chukotka", "Kamchatka", " Nauru", "Tuvalu", "Marshall Isl.", "Wallis and Futuna", "Tarawa"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+11:30", "Norfolk Island"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+11:00", "Melbourne", "Sydney", "Canberra", "Honiara", "Hobart", "Solomon Isl.", "New Caledonia", "Pohnpei", "Vanuatu", "Bougainville", "Srednekolymsk"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+10:30", "Adelaide", "Broken Hill", "Ceduna"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+10:00", "Brisbane", "Port Moresby", "Hagåtña", "Vladivostok", "Khabarovsk", "Magadan", "Yuzhno-Sakhalinsk", "Yuzhno-Kurilsk", "Birobidzhan", "Komsomolsk-na-Amure", "Guam", "Papua New Guinea", "Northern Mariana Isl."));
      Scheduler.instance.registerEvent(new Celeb11("UTC+09:30", "Darwin", "Alice Springs", "Tennant Creek"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+09:00", "Tokyo", "Seoul", "Dili", "Melekeok", "Yakutsk", "Blagoveschensk", "Mirnyi", "Tiksi", "Palau", "Jayapura", "Ambon", "East Timor"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+08:45", "Eucla"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+08:30", "Pyongyang", "Hamhung", "Chongjin", "Namp’o"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+08:00", "Beijing", "Hong Kong", "Manila", "Singapore", "Shanghai", "Macau", "Taiwan", "Philippines", "Malaysia", "Bali", "Mongolia", "Brunei Darussalam", "Irkutsk", "Chita", "Bratsk", "Ulan-Ude", "Perth"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+07:00", "Jakarta", "Bangkok", "Hanoi", "Phnom Penh", "Vietnam", "Laos", "Cambodia", "Java", "Sumatra", "Christmas Isl.", "Krasnoyarsk", "Kemerovo", "Abakan", "Kyzyl"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+06:30", "Myanmar", "Cocos Islands,  Yangon", "Naypyidaw", "Mandalay", "Mawlamyine", "Keeling Islands"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+06:00", "Dhaka", "Almaty", "Bishkek", "Thimphu", "Astana", "Novosibirsk", "Omsk", "Tomsk", "Barnau", "Bhutan", "British Indian Ocean Territory", "Chagos", "Diego Garcia", "Kazakhstan", "Almaty", "Astana", "Kyrgystan"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+05:45", "Nepal,  Kathmandu", "Biratnagar", "Pokhara"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+05:30", "New Delhi", "Mumbai", "Kolkata", "Bengaluru", "Bangalore", "Sri Lanka"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+05:00", "Tashkent", "Islamabad", "Lahore", "Karachi", "Yekaterinburg ", "Chelyabinsk", "Perm", "Ufa", "Kurgan", "Orengurg", "Tyumen", "Uzbekistan", "Turkmenistan", "Tajikistan", "Kazakhstan", "Aqtau", "Aqtobe", "Maldives", "Kerguelen Isl.(Fr.)"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+04:30", "Kabul", "Kandahar", "Mazari Sharif", "Herat"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+04:00", "Dubai", "Abu Dhabi", "Muscat", "Port Louis", "Samara", "Izhevsk", "Armenia", "Azerbaijan", "Georgia", "Seychelles", "Reunion", "Oman", "Mauritius"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+03:30", "Tehran", "Rasht", "Esfahãn", "Mashhad", "Tabriz"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+03:00", "Moscow", "St.Petersburg", "Novgorod", "Volgograd", "Sevastopol", "Baghdad", "Minsk", "Saudi Arabia", "Bahrain", "Kuwait", "Yemen", "Qatar", "Madagascar", "Kenya", "Tanzania", "Somalia", "Sudan", "Ethiopia", "Comoros", "Uganda", "Khartoum", "Nairobi"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+02:00", "Cairo", "Ankara", "Athens", "Bucharest", "Kiev", "Kaliningrad", "Jordan", "Moldova", "Latvia", "Lithuania", "Estonia", "Bulgaria", "Romania", "Israel", "Gaza Strip", "Syria", "Lebanon", "Cyprus", "Turkey", "Libya", "South Africa", "Zimbabwe", "Zambia", "Malawi", "Rwanda", "Namibia", "Mozambique"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+01:00", "Hintertupfingen", "Brussels", "Madrid", "Paris", "Rome", "Algiers", "Serbia", "Netherlands", "Malta", "Monaco", "Helsingborg", "Zurich", "Bern", "Gibraltar", "Czech Republic", "Denmark", "Algeria", "Angola", "Austria", "Belgium", "Cameroon", "Chad", "Congo", "Croatia", "Hungary", "Liechtenstein", "Luxembourg", "Nigeria", "Norway", "Poland", "Slovakia", "Slovenia", "Tunisia", "Vatican City"));
      Scheduler.instance.registerEvent(new Celeb11("UTC+00:00", "London", "Casablanca", "Dublin", "Lisbon", "Accra", "Ireland", "Iceland", "Canary Isl.", "Cote d'Ivoire", "Sierra Leone", "Saint Helena", "Western Sahara", "Sao Tome and Principe", "Senegal", "Guinea", "Guinea-Bissau", "Gambia", "Ghana", "Liberia", "Mali", "Mauritania"));
      Scheduler.instance.registerEvent(new Celeb11("UTC-01:00", "Praia", "Ponta Delgada", "Ittoqqortoormiit", "Azores", "Cabo Verde"));
      Scheduler.instance.registerEvent(new Celeb11("UTC-02:00", "Rio de Janeiro", "São Paulo", "Brasilia", "Fernando de Noronha", "Uruguay", "South Georgia", "South Sandwich Islands  "));
      Scheduler.instance.registerEvent(new Celeb11("UTC-03:00", "Buenos Aires", "Santiago", "Asuncion", "Ushuaia", "Recife", "Paraguay", "Suriname", "French Guiana", "Nuuk", "Falkland Isl.  "));
      Scheduler.instance.registerEvent(new Celeb11("UTC-03:30", "St. John's", "Mary's Harbour"));
      Scheduler.instance.registerEvent(new Celeb11("UTC-04:00", "La Paz", "San Juan", "Santo Domingo", "Halifax", "New Brunswick", "Prince Edward Island", "Barbados", "Bermuda", "Netherlands Antilles", "Saint Lucia", "Grenada", "Dominica", "Dominican Republic", "Saint Kitts and Nevis", "Antigua and Barbuda", "Trinidad and Tobago", "Puerto Rico", "Manaus", "Bolivia "));
      Scheduler.instance.registerEvent(new Celeb11("UTC-04:30", "Caracas", "Barquisimeto", "Maracaibo"));
      Scheduler.instance.registerEvent(new Celeb11("UTC-05:00", "New York", "Washington D.C.", "Miami", "Boston", "Atlanta", "Detroit", "Indianapolis", "Baltimore", "Philadelphia", "Virginia Beach", "Toronto", "Montreal", "Ottawa", "Quebec", "Havana", "Peru", "Acre", "Colombia", "Bahamas", "Jamaica", "Ecuador", "Panama"));
      Scheduler.instance.registerEvent(new Celeb11("UTC-06:00", "Mexico City", "Chicago", "Guatemala", "Dallas", "New Orleans", "Minneapolis/St. Paul", "Oklahoma City", "Houston", "Dallas", "Milwaukee", "Winnipeg", "Regina", "Mexico City", "Acapulco", "Veracruz", "Guatemala", "Honduras", "El Salvador", "Nicaragua", "Belize", "Costa Rica"));
      Scheduler.instance.registerEvent(new Celeb11("UTC-07:00", "Denver", "Albuquerque", "El Paso", "Salt Lake City", "Santa Fe", "Phoenix", "Tucson", "Calgary", "Edmonton", "Aklavik", "Yellowknife", "Chihuahua", "Mazatlan", "Phoenix"));
      Scheduler.instance.registerEvent(new Celeb11("UTC-08:00", "Los Angeles", "San Francisco", "Las Vegas", "San Diego", "Seattle", "San Jose", "Portland", "Vancouver", "Whitehorse", "Tijuana", "Mexicali", "Pitcairn"));
      Scheduler.instance.registerEvent(new Celeb11("UTC-09:00", "Anchorage", "Fairbanks", "Juneau", "Unalaska", "Nome", "Juneau; Gambier Islands"));
      Scheduler.instance.registerEvent(new Celeb11("UTC-09:30", "Nuku Hiva", "Hiva Oa", "Taiohae"));
      Scheduler.instance.registerEvent(new Celeb11("UTC-10:00", "Honolulu", "Rarotonga", "Adak", "Papeete", "Hilo"));
      Scheduler.instance.registerEvent(new Celeb11("UTC-11:00", "Pago Pago", "Niue", "Midway Islands", "Jarvis Isl.", "Kingman Reef", "Palmyra Atoll", "Alofi"));
      Scheduler.instance.registerEvent(new Celeb11("UTC-12:00", "Baker Island", "Howland Island"));
      //@formatter:on
    }
  }

  private final Calendar start;
  private final Calendar end;
  private final List<String> locations;
  private int chatted = 0;

  public Celeb11(String timezone, String... locations) {
    String javatimezone = timezone.replace("UTC", "GMT");
    start = Calendar.getInstance(TimeZone.getTimeZone(javatimezone));
    start.set(2021, 0, 1, 0, 0, 0);
    end = Calendar.getInstance(TimeZone.getTimeZone(javatimezone));
    end.set(2021, 0, 1, 0, 1, 0);
    this.locations = Arrays.asList(locations);
    Collections.shuffle(this.locations);
  }

  @Override
  public boolean isActive(Calendar now) {
    if (start.before(now)) {
      if (end.before(now)) {
        calculate(now);
        return false;
      }
      return true;
    }
    return false;
  }

  @Override
  public long getTimeToStart(Calendar now) {
    long remaining = start.getTimeInMillis() - now.getTimeInMillis();
    return remaining < 0 ? 0 : remaining;
  }

  @Override
  public void calculate(Calendar now) {
    while (end.before(now)) {
      start.add(Calendar.YEAR, 1);
      end.add(Calendar.YEAR, 1);
    }
  }

  @Override
  public void run(Calendar now) {
    if (!PersonalConfig.celebrateNewYear.get() || FMLCommonHandler.instance() == null || FMLCommonHandler.instance().getMinecraftServerInstance() == null) {
      return;
    }
    FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(this);
  }

  private static Random rand = new Random();

  @Override
  public void run() {
    if (FMLCommonHandler.instance() == null || FMLCommonHandler.instance().getMinecraftServerInstance() == null
        || !FMLCommonHandler.instance().getMinecraftServerInstance().isServerRunning() || EnderIO.proxy.isGamePaused()) {
      return;
    }
    for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
      if (player != null) {
        for (int i = rand.nextInt(10) + 1; i > 0; i--) {
          int x = (int) Math.floor(player.posX) - 32 + rand.nextInt(64);
          int z = (int) Math.floor(player.posZ) - 32 + rand.nextInt(64);
          BlockPos pos = player.world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).up(16 + rand.nextInt(32));
          player.world.spawnEntity(EntityUtil.getRandomFirework(player.world, pos));
        }
        if (chatted % 10 == 0 && (chatted / 10) < locations.size()) {
          player.sendMessage(new TextComponentString("Say \"Happy New Year\" to " + locations.get(chatted / 10)));
        }
      }
    }
    chatted++;
  }

  @Override
  public String toString() {
    final int maxLen = 10;
    return "Celeb11 [start=" + start + ", locations=" + (locations != null ? locations.subList(0, Math.min(locations.size(), maxLen)) : null) + "]";
  }

}