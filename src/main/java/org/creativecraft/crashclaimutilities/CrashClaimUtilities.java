package org.creativecraft.crashclaimutilities;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandReplacements;
import co.aikar.commands.MessageType;
import de.themoep.minedown.MineDown;
import net.crashcraft.crashclaim.CrashClaim;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.creativecraft.crashclaimutilities.commands.ClaimAdminCommand;
import org.creativecraft.crashclaimutilities.commands.ClaimTransferCommand;
import org.creativecraft.crashclaimutilities.commands.ClaimTrustCommand;
import org.creativecraft.crashclaimutilities.config.MessagesConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.creativecraft.crashclaimutilities.hook.CrashClaimHook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CrashClaimUtilities extends JavaPlugin {
    public static CrashClaimUtilities plugin;
    private MessagesConfig messagesConfig;
    private CrashClaimHook crashClaimHook;
    private CrashClaim crashClaim;

    @Override
    public void onEnable() {
        plugin = this;
        crashClaim = CrashClaim.getPlugin();
        crashClaimHook = new CrashClaimHook(this);

        registerConfigs();
        registerCommands();

        new MetricsLite(this, 16004);
    }

    @Override
    public void onLoad() {
        //
    }

    @Override
    public void onDisable() {
        //
    }

    /**
     * Register the plugin commands.
     */
    public void registerCommands() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        CommandReplacements replacements = commandManager.getCommandReplacements();

        replacements.addReplacement("claimadmin", getConfig().getString("commands.admin.command", "claimadmin|adminclaim"));
        replacements.addReplacement("claimtrust", getConfig().getString("commands.trust.command", "claimtrust|trust"));
        replacements.addReplacement("claimtransfer", getConfig().getString("commands.transfer.command", "claimtransfer|transferclaim"));

        commandManager.setFormat(MessageType.ERROR, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);
        commandManager.setFormat(MessageType.SYNTAX, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);
        commandManager.setFormat(MessageType.HELP, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);
        commandManager.setFormat(MessageType.INFO, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);

        commandManager.getCommandCompletions().registerCompletion("claims", c -> {
            if (!(c.getSender() instanceof Player)) {
                return null;
            }

            HashSet<String> claims = new HashSet<>();
            HashSet<Integer> claimList = getCrashClaimHook().getClaimIds(c.getPlayer().getWorld());

            if (claimList == null || claimList.isEmpty()) {
                return null;
            }

            claimList.forEach(claim -> claims.add(Integer.toString(claim)));

            return claims;
        });

        commandManager.getCommandCompletions().registerCompletion("listPages", c -> {
            if (!(c.getSender() instanceof Player)) {
                return null;
            }

            HashSet<Integer> claimList = getCrashClaimHook().getClaimIds(c.getPlayer().getWorld());

            if (claimList == null || claimList.isEmpty()) {
                return null;
            }

            HashSet<String> pages = new HashSet<>();
            IntStream
                .range(0, (claimList.size() / 10) + 1)
                .boxed()
                .toList()
                .forEach(page -> pages.add(Integer.toString(page + 1)));

            return pages;
        });

        if (getConfig().getBoolean("commands.admin.enabled")) {
            commandManager.registerCommand(new ClaimAdminCommand(this));
        }

        if (getConfig().getBoolean("commands.trust.enabled")) {
            commandManager.registerCommand(new ClaimTrustCommand(this));
        }

        if (getConfig().getBoolean("commands.transfer.enabled")) {
            commandManager.registerCommand(new ClaimTransferCommand(this));
        }

        commandManager.enableUnstableAPI("help");
    }

    /**
     * Register the plugin configuration.
     */
    public void registerConfigs() {
        getConfig().addDefault("commands.admin.enabled", true);
        getConfig().addDefault("commands.admin.command", "claimadmin|adminclaim");

        getConfig().addDefault("commands.trust.enabled", true);
        getConfig().addDefault("commands.trust.command", "claimtrust|trust");

        getConfig().addDefault("commands.transfer.enabled", true);
        getConfig().addDefault("commands.transfer.command", "claimtransfer|transferclaim");
        getConfig().addDefault("commands.transfer.keep-as-admin", false);

        getConfig().options().copyDefaults(true);

        saveConfig();

        messagesConfig = new MessagesConfig(this);
    }

    /**
     * Retrieve the CrashClaim instance.
     *
     * @return CrashClaim
     */
    public CrashClaim getCrashClaim() {
        return crashClaim;
    }

    /**
     * Retrieve the CrashClaimHook instance.
     *
     * @return CrashClaim
     */
    public CrashClaimHook getCrashClaimHook() {
        return crashClaimHook;
    }

    /**
     * Retrieve a localized message.
     *
     * @param  key The locale key.
     * @return String
     */
    public String localize(String key) {
        String message = messagesConfig.getMessages().getString(key);

        return ChatColor.translateAlternateColorCodes(
            '&',
            message == null ? key + " is missing." : message
        );
    }

    /**
     * Send a message formatted with MineDown.
     *
     * @param sender The command sender.
     * @param value  The message.
     */
    public void sendMessage(CommandSender sender, String value) {
        sender.spigot().sendMessage(
            MineDown.parse(messagesConfig.getMessages().getString("messages.generic.prefix") + value)
        );
    }

    /**
     * Send a raw message formatted with MineDown.
     *
     * @param sender The command sender.
     * @param value  The message.
     */
    public void sendRawMessage(CommandSender sender, String value) {
        sender.spigot().sendMessage(
            MineDown.parse(value)
        );
    }

    /**
     * Reload the plugin configuration.
     */
    public void reload() {
        messagesConfig = new MessagesConfig(this);
        reloadConfig();
    }
}
