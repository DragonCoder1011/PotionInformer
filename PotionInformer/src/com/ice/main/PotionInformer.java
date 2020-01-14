package com.ice.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

public class PotionInformer extends JavaPlugin implements Listener, CommandExecutor {


    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("potions").setExecutor(this);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void onDisable() {

    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();
        if (!getConfig().getBoolean(name + ".PotionsToggle")) {
            getConfig().set(name + ".PotionsToggle", true);
            saveConfig();
        } else {
            if (getConfig().getBoolean(name + ".PotionsToggle")) {
                return;
            }
        }
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();

        if (!getConfig().getBoolean(player.getName() + "." + "PotionsToggle")) {
            return;
        }

        if (e.getItem().getType() == Material.POTION && player.hasPermission("potions.announce") && getConfig().getBoolean(player.getName() + ".PotionsToggle")) {
            Potion potion = Potion.fromItemStack(e.getItem());
            for (PotionEffect effect : potion.getEffects()) {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (getConfig().getBoolean(all.getName() + ".PotionsToggle"))
                        all.sendMessage(format("&c&lINFO &8» &e" + name + " &fconsumed &a"
                                + potion.getType().getEffectType().getName() + " " + potion.getLevel() + " (" + effect.getDuration() / 20 + " Seconds)"));
                }
            }

        } else {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (!getConfig().getBoolean(all.getName() + ".PotionsToggle")) {
                    return;
                }
            }
        }

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getType() == Material.AIR || e.getItem().getType() != Material.GLASS_BOTTLE) {
            return;
        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR && e.getItem().getType() == Material.GLASS_BOTTLE) {
            e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
        }
    }

    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player)) {
            System.out.println("Only players can use this command!");
            return true;
        }

        Player player = (Player) s;
        if (cmd.getName().equalsIgnoreCase("potions")) {

            if (!player.hasPermission("potions.announce")) {
                player.sendMessage(format("&c&lINFO &8» &cYou don't have enough perms to use this command!"));
                return true;
            }


            if (getConfig().getBoolean(player.getName() + "." + "PotionsToggle")) {
                getConfig().set(player.getName() + "." + "PotionsToggle", false);
                saveConfig();
                player.sendMessage(format("&c&lINFO &8» &fYou have &cdisabled &fPotion Notifications!"));
                return true;
            }

            if (!getConfig().getBoolean(player.getName() + ".PotionsToggle")) {
                getConfig().set(player.getName() + "." + "PotionsToggle", true);
                saveConfig();
                player.sendMessage(format("&c&lINFO &8» &fYou have &aEnabled &fPotion Notifications!"));
                return true;
            }
        }

        return true;
    }

    private String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}