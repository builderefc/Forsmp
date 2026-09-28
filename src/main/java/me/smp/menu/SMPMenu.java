package me.smp.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class SMPMenu extends JavaPlugin implements Listener {

    private static final String MENU_TITLE = "§8Меню SMP";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);

        getLogger().info("SMPMenu включён!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§lМеню SMP");
            compass.setItemMeta(meta);
        }

        player.getInventory().setItemInMainHand(compass);
    }

    @EventHandler
    public void onCompassClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.COMPASS) {
            return;
        }

        event.setCancelled(true);
        openMenu(event.getPlayer());
    }

    private void openMenu(Player player) {
        var inventory = Bukkit.createInventory(null, 27, MENU_TITLE);

        // Красное стекло
        ItemStack glass = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();

        if (glassMeta != null) {
            glassMeta.setDisplayName("§cНельзя");
            glass.setItemMeta(glassMeta);
        }

        int[] border = {
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 17,
                18, 19, 20, 21, 22, 23, 24, 25, 26
        };

        for (int slot : border) {
            inventory.setItem(slot, glass);
        }

        // TNT-вагонетка SMP
        ItemStack smp = new ItemStack(Material.TNT_MINECART);
        ItemMeta smpMeta = smp.getItemMeta();

        if (smpMeta != null) {
            smpMeta.setDisplayName("§c§lSMP");
            smpMeta.setLore(java.util.List.of(
                    "§7Нажми, чтобы подключиться",
                    "§8",
                    "§cНельзя"
            ));
            smp.setItemMeta(smpMeta);
        }

        inventory.setItem(13, smp);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(MENU_TITLE)) {
            return;
        }

        event.setCancelled(true);

        // Нажали на TNT-вагонетку
        if (event.getRawSlot() == 13) {

            if (!(event.getWhoClicked() instanceof Player player)) {
                return;
            }

            String ip = getConfig().getString("server.ip", "play.example.ru");
            int port = getConfig().getInt("server.port", 25565);

            player.closeInventory();

            // Подключение к другому серверу
            player.sendMessage("§7Подключение к §c" + ip + ":" + port + "§7...");

            player.sendPluginMessage(
                    this,
                    "BungeeCord",
                    createConnectMessage(ip)
            );
        }
    }

    @EventHandler
    public void onMenuDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(MENU_TITLE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        if (item.getType() == Material.COMPASS) {
            event.setCancelled(true);
        }
    }

    private byte[] createConnectMessage(String server) {
        try {
            java.io.ByteArrayOutputStream byteStream =
                    new java.io.ByteArrayOutputStream();

            java.io.DataOutputStream out =
                    new java.io.DataOutputStream(byteStream);

            out.writeUTF("Connect");
            out.writeUTF(server);

            return byteStream.toByteArray();

        } catch (Exception e) {
            getLogger().warning("Ошибка подключения: " + e.getMessage());
            return new byte[0];
        }
    }
                  }
