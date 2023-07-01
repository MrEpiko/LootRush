package me.mrepiko.lootrush;

import lombok.Getter;
import lombok.Setter;
import me.mrepiko.lootrush.commands.*;
import me.mrepiko.lootrush.listeners.*;
import me.mrepiko.lootrush.manager.GeneratorManager;
import me.mrepiko.lootrush.manager.lootdrop.LootDrop;
import me.mrepiko.lootrush.manager.lootdrop.LootDropItem;
import me.mrepiko.lootrush.manager.RoundManager;
import me.mrepiko.lootrush.config.ConfigItem;
import me.mrepiko.lootrush.config.ConfigGenerator;
import me.mrepiko.lootrush.config.ConfigTeam;
import me.mrepiko.lootrush.papi.LootRushExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LootRush extends JavaPlugin {

    @Getter private static LootRush INSTANCE;

    @Getter private static String world;

    @Getter private static final List<ConfigTeam> teamConfigs = new ArrayList<>();
    @Getter private static final List<ConfigTeam> playerConfigs = new ArrayList<>();
    @Getter private static final List<ItemStack> shopItems = new ArrayList<>();
    @Getter private static final HashMap<EventType, List<ItemStack>> startItems = new HashMap<>();

    @Getter private static final List<ConfigGenerator> generators = new ArrayList<>();
    @Getter private static final HashMap<String, String> messages = new HashMap<>();
    @Getter private static final HashMap<ItemStack, Integer> generatorItems = new HashMap<>();

    @Getter private static Location spectatorRoom;
    @Getter private static Location respawnRoom;

    @Getter private static int pointsNeededTeam;
    @Getter private static int pointsNeededSolo;
    @Getter private static int maximumPlayers;
    @Getter private static int respawnInternal;
    @Getter private static int coinsPerKill;
    @Getter private static int postEndWaitingInterval;
    @Getter private static int shopSize;

    @Getter private static Boolean syncArmorWithTeamColor;
    @Getter private static Boolean friendlyFire;
    @Getter private static Boolean closeShopUponFailure;
    @Getter private static Boolean autoEquipArmor;
    @Getter private static Boolean disableInventoryActions;
    @Getter private static Boolean unbreakableItems;

    @Getter private static String shopTitle;
    @Getter private static String permissionsMessage;
    @Getter private static String trackerIdentifier;

    @Getter private static LootDrop lootDrop = null;

    @Getter @Setter private static EventStatus eventStatus;
    @Getter @Setter private static EventType eventType;

    @Getter private static RoundManager roundManager = new RoundManager();
    @Getter private static GeneratorManager generatorManager = new GeneratorManager();

    @Override
    public void onEnable() {

        setupConfigurations();

        INSTANCE = this;

        getServer().getPluginCommand("lootrushreload").setExecutor(new ReloadCmd());
        getServer().getPluginCommand("lootrushallowreg").setExecutor(new AllowRegCmd());
        getServer().getPluginCommand("lootrushregister").setExecutor(new RegisterCmd());
        getServer().getPluginCommand("lootrushinfo").setExecutor(new InfoCmd());
        getServer().getPluginCommand("lootrushreboot").setExecutor(new RebootCmd());
        getServer().getPluginCommand("lootrushstart").setExecutor(new StartCmd());
        getServer().getPluginCommand("lootrushremove").setExecutor(new RemoveCmd());
        getServer().getPluginCommand("lootrushshop").setExecutor(new ShopCmd());
        getServer().getPluginCommand("lootrushmessage").setExecutor(new MessageCmd());

        getServer().getPluginManager().registerEvents(new ItemDeliveryListener(), this);
        getServer().getPluginManager().registerEvents(new ShopListener(), this);
        getServer().getPluginManager().registerEvents(new DisconnectListener(), this);
        getServer().getPluginManager().registerEvents(new KillListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new LootDropListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInventoryListener(), this);

        new LootRushExpansion().register();

        eventStatus = EventStatus.INACTIVE;
        eventType = EventType.NONE;

        Bukkit.getConsoleSender().sendMessage("[LootRush] The plugin has been started.");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[LootRush] The plugin has been stopped.");
    }

    public static void setupConfigurations() {

        File file;
        FileConfiguration config = new YamlConfiguration();

        file = new File(Bukkit.getPluginManager().getPlugin("LootRush").getDataFolder(), "config.yml");
        if (!file.exists()) {
            Bukkit.getPluginManager().getPlugin("LootRush").saveResource("config.yml", false);
        }

        try {

            config.load(file);

            playerConfigs.clear();
            teamConfigs.clear();
            shopItems.clear();
            generators.clear();
            messages.clear();
            generatorItems.clear();
            startItems.clear();

            world = config.getString("world");
            pointsNeededTeam = config.getInt("points-needed.team");
            pointsNeededSolo = config.getInt("points-needed.solo");
            permissionsMessage = ChatColor.translateAlternateColorCodes('&', config.getString("permissions-message"));
            maximumPlayers = config.getInt("maximum-players");
            respawnInternal = config.getInt("respawn-internal");
            coinsPerKill = config.getInt("coins-per-kill");
            syncArmorWithTeamColor = config.getBoolean("start-items.sync-armor-with-color");
            friendlyFire = config.getBoolean("friendly-fire");
            shopTitle = config.getString("shop.gui-title");
            shopSize = config.getInt("shop.gui-size");
            closeShopUponFailure = config.getBoolean("shop.close-shop-upon-failure");
            postEndWaitingInterval = config.getInt("post-end-waiting-interval");
            autoEquipArmor = config.getBoolean("shop.auto-equip-armor");
            disableInventoryActions = config.getBoolean("disable-inventory-actions");
            unbreakableItems = config.getBoolean("unbreakable-items");
            trackerIdentifier = ChatColor.translateAlternateColorCodes('&', config.getString("tracker-identifier"));

            for (String n: config.getConfigurationSection("positions.team").getKeys(false)) {

                teamConfigs.add(
                        new ConfigTeam(
                                new Location(
                                        Bukkit.getWorld(world),
                                        config.getDouble("positions.team." + n + ".x"),
                                        config.getDouble("positions.team." + n + ".y"),
                                        config.getDouble("positions.team." + n + ".z"),
                                        config.getInt("positions.team." + n + ".yaw"),
                                        config.getInt("positions.team." + n + ".pitch")
                                ),
                                config.getString("positions.team." + n + ".color"),
                                config.getString("positions.team." + n + ".color-name"),
                                config.getString("positions.team." + n + ".color-code")
                        )
                );

            }

            for (String n: config.getConfigurationSection("positions.solo").getKeys(false)) {

                playerConfigs.add(
                        new ConfigTeam(
                                new Location(
                                        Bukkit.getWorld(world),
                                        config.getDouble("positions.solo." + n + ".x"),
                                        config.getDouble("positions.solo." + n + ".y"),
                                        config.getDouble("positions.solo." + n + ".z"),
                                        config.getInt("positions.solo." + n + ".yaw"),
                                        config.getInt("positions.solo." + n + ".pitch")
                                ),
                                null,
                                null,
                                null
                        )
                );

            }

            ConfigItem configItem;
            ItemStack item;

            for (String n: config.getConfigurationSection("positions.generators").getKeys(false)) {

                configItem = new ConfigItem(config.getConfigurationSection("positions.generators." + n), "item");

                generators.add(new ConfigGenerator(
                                new Location(
                                        Bukkit.getWorld(world),
                                        config.getDouble("positions.generators." + n + ".x"),
                                        config.getDouble("positions.generators." + n + ".y"),
                                        config.getDouble("positions.generators." + n + ".z")
                                ),
                                configItem.getItemStack(),
                                config.getInt("positions.generators." + n + ".points"),
                                config.getInt("positions.generators." + n + ".interval"),
                                config.getStringList("positions.generators." + n + ".hologram-messages")
                        )
                );

                if (!generatorItems.containsKey(configItem.getItemStack())) {
                    item = configItem.getItemStack().clone();
                    item.setAmount(1);
                    generatorItems.put(item, config.getInt("positions.generators." + n + ".points"));
                }

            }

            for (String n: config.getConfigurationSection("messages").getKeys(false)) {

                messages.put(n, ChatColor.translateAlternateColorCodes('&', config.getString("messages." + n)));

            }

            ItemStack it;
            for (String n: config.getConfigurationSection("shop.shop-items").getKeys(false)) {

                it = new ConfigItem(config.getConfigurationSection("shop.shop-items"), n).getItemStack();

                boolean hasPrice = false;

                for (String w: it.getItemMeta().getLore().get(it.getItemMeta().getLore().size() - 1).split(" ")) {
                    try {
                        Integer.parseInt(w);
                        hasPrice = true;
                    } catch (NumberFormatException ex) {
                        //
                    }
                }

                if (hasPrice) shopItems.add(it);

            }

            List<ItemStack> soloItems = new ArrayList<>();
            List<ItemStack> teamItems = new ArrayList<>();

            for (String n: config.getConfigurationSection("start-items.solo").getKeys(false)) {

                soloItems.add(new ConfigItem(config.getConfigurationSection("start-items.solo"), n).getItemStack());

            }

            startItems.put(EventType.SOLO, soloItems);

            for (String n: config.getConfigurationSection("start-items.team").getKeys(false)) {

                teamItems.add(new ConfigItem(config.getConfigurationSection("start-items.team"), n).getItemStack());

            }

            startItems.put(EventType.TEAM, teamItems);

            spectatorRoom = new Location(
                    Bukkit.getWorld(world),
                    config.getDouble("positions.spectator-room.x"),
                    config.getDouble("positions.spectator-room.y"),
                    config.getDouble("positions.spectator-room.z"),
                    config.getInt("positions.spectator-room.yaw"),
                    config.getInt("positions.spectator-room.pitch")
            );

            respawnRoom = new Location(
                    Bukkit.getWorld(world),
                    config.getDouble("positions.respawn-room.x"),
                    config.getDouble("positions.respawn-room.y"),
                    config.getDouble("positions.respawn-room.z"),
                    config.getInt("positions.respawn-room.yaw"),
                    config.getInt("positions.respawn-room.pitch")
            );

            if (config.getBoolean("loot-drop.enabled")) {

                List<LootDropItem> lootDropItems = new ArrayList<>();
                int spotMin, spotMax, amountMin, amountMax = 0;

                for (String n : config.getConfigurationSection("loot-drop.items").getKeys(false)) {

                    spotMin = Integer.parseInt(config.getString("loot-drop.items." + n + ".spots").split(",")[0]);
                    spotMax = Integer.parseInt(config.getString("loot-drop.items." + n + ".spots").split(",")[1]);

                    amountMin = Integer.parseInt(config.getString("loot-drop.items." + n + ".amount").split(",")[0]);
                    amountMax = Integer.parseInt(config.getString("loot-drop.items." + n + ".amount").split(",")[1]);

                    lootDropItems.add(new LootDropItem(new ConfigItem(config.getConfigurationSection("loot-drop.items"), n + ".item-configuration").getItemStack(), spotMin, spotMax, amountMin, amountMax));

                }

                Location firstCorner = new Location(
                        Bukkit.getWorld(world),
                        config.getDouble("loot-drop.first-corner.x"),
                        config.getDouble("loot-drop.first-corner.y"),
                        config.getDouble("loot-drop.first-corner.z")
                );

                Location secondCorner = new Location(
                        Bukkit.getWorld(world),
                        config.getDouble("loot-drop.second-corner.x"),
                        config.getDouble("loot-drop.second-corner.y"),
                        config.getDouble("loot-drop.second-corner.z")
                );

                lootDrop = new LootDrop(Material.getMaterial(config.getString("loot-drop.material")), config.getInt("loot-drop.interval"), lootDropItems, config.getInt("loot-drop.gui-size"), config.getString("loot-drop.gui-title"), config.getStringList("loot-drop.hologram-messages"), firstCorner, secondCorner);

            }

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

}