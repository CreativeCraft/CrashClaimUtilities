package org.creativecraft.crashclaimutilities.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.creativecraft.crashclaimutilities.CrashClaimUtilities;

import java.io.File;

public class MessagesConfig {
    private final CrashClaimUtilities plugin;
    private FileConfiguration messages;
    private File messagesFile;

    public MessagesConfig(CrashClaimUtilities plugin) {
        this.plugin = plugin;
        this.register();
    }

    /**
     * Register the messages configuration.
     */
    public void register() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            plugin.saveResource("messages.yml", false);
        }

        messages = new YamlConfiguration();

        try {
            messages.load(messagesFile);
            setDefaults();
            messages.save(messagesFile);
        } catch (Exception e) {
            //
        }
    }

    /**
     * Register the messages defaults.
     */
    public void setDefaults() {
        messages.addDefault("messages.generic.prefix", "&a&lCrash&fClaim &8> &f");
        messages.addDefault("messages.generic.unknown", "Unknown");

        messages.addDefault("messages.list.header", "&a&m+&8&m             &a Crash&fClaim List &7- &fPage &a{current}&f of &a{total} &8&m             &a&m+");
        messages.addDefault("messages.list.format", "[&8➝ &fID: &a{id}&7 - &fOwner: &a{owner}](run_command=/claimadmin tp {id} hover=&aClick here&7 to teleport to claim &a{id}&7.)");
        messages.addDefault("messages.list.unknown-world", "The specified &aworld&f could not be found.");
        messages.addDefault("messages.list.console", "Console must specify a &aworld&f.");
        messages.addDefault("messages.list.empty", "This claims list page is &aempty&f.");
        messages.addDefault("messages.list.description", "List the known claims.");

        messages.addDefault("messages.player.header", "&a&m+&8&m             &a {player}'s &fClaims &8&m             &a&m+");
        messages.addDefault("messages.player.before", "&8➝ &f");
        messages.addDefault("messages.player.format", "[&7[&a{id}&7]](run_command=/claimadmin tp {id} hover=&aClick here&7 to teleport to claim &a{id}&7.)");
        messages.addDefault("messages.player.delimiter", " ");
        messages.addDefault("messages.player.no-target", "You must specify a &aplayer&f name.");
        messages.addDefault("messages.player.not-found", "The player &aspecified&f could not be found.");
        messages.addDefault("messages.player.empty", "That &aplayer&f does not have any &aclaims&f.");
        messages.addDefault("messages.player.description", "Retrieve a list of the specified player's claims.");

        messages.addDefault("messages.teleport.success", "You have teleported to claim &a{id}&f owned by &a{owner}&f.");
        messages.addDefault("messages.teleport.not-found", "The claim &a{id}&f could not be found.");
        messages.addDefault("messages.teleport.unknown-world", "The &aworld&f that contains this &aclaim&f could not be found.");
        messages.addDefault("messages.teleport.invalid-claim", "You must specify a valid &aclaim id&f.");
        messages.addDefault("messages.teleport.console", "You must be &ain-game&f to teleport to a claim.");
        messages.addDefault("messages.teleport.description", "Teleport to the specified claim.");

        messages.addDefault("messages.delete.success", "The claim &a{id}&f has been deleted.");
        messages.addDefault("messages.delete.not-found", "The claim &a{id}&f could not be found.");
        messages.addDefault("messages.delete.invalid-claim", "You must specify a valid &aclaim id&f.");
        messages.addDefault("messages.delete.description", "Delete the specified claim.");

        messages.addDefault("messages.trust.not-claim", "You are not &astanding&f in a claim.");
        messages.addDefault("messages.trust.self", "You can not &atrust&f yourself.");
        messages.addDefault("messages.trust.empty", "You must specify a &aplayer&f to trust.");
        messages.addDefault("messages.trust.not-found", "The &aplayer&f specified could not be found.");
        messages.addDefault("messages.trust.owner", "You can not &amodify&f the claim owner's &atrust&f.");
        messages.addDefault("messages.trust.no-permission", "You do not have permission to &atrust&f on this claim.");
        messages.addDefault("messages.trust.console", "You must be &ain-game&f to trust players to a claim.");
        messages.addDefault("messages.trust.description", "Open the claim permission menu for the specified player.");

        messages.addDefault("messages.reload.success", "Plugin has been &asuccessfully&f reloaded.");
        messages.addDefault("messages.reload.failed", "Plugin &cfailed&f to reload. Check console for details.");
        messages.addDefault("messages.reload.description", "Reload the plugin configuration.");

        messages.addDefault("messages.help.header", "&a&m+&8&m                         &a Crash&fClaim Utilities &8&m                         &a&m+");
        messages.addDefault("messages.help.format", "&8➝ &a/{command} &7{parameters} &f- {description}");
        messages.addDefault("messages.help.footer", "&a&m+&8&m                                                                          &a&m+");
        messages.addDefault("messages.help.description", "View the plugin help.");

        messages.options().copyDefaults(true);
    }

    /**
     * Retrieve the messages configuration.
     *
     * @return FileConfiguration
     */
    public FileConfiguration getMessages() {
        return messages;
    }

    /**
     * Retrieve the messages file.
     *
     * @return File
     */
    public File getMessagesFile() {
        return messagesFile;
    }
}
