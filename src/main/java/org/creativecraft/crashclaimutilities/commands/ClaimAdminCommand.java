package org.creativecraft.crashclaimutilities.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import net.crashcraft.crashclaim.claimobjects.Claim;
import net.crashcraft.crashclaim.fastutil.fastutil.longs.Long2ObjectOpenHashMap;
import net.crashcraft.crashclaim.paperlib.PaperLib;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.creativecraft.crashclaimutilities.CrashClaimUtilities;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashSet;

@CommandAlias("%claimadmin")
@Description("Manage and administer claims.")
public class ClaimAdminCommand extends BaseCommand {
    private final CrashClaimUtilities plugin;

    public ClaimAdminCommand(CrashClaimUtilities plugin) {
        this.plugin = plugin;
    }

    /**
     * Retrieve a list of claims.
     *
     * @param sender The command sender.
     * @param page   The list page.
     * @param value  The world.
     */
    @Subcommand("list")
    @CommandPermission("crashclaim.admin.claimadmin")
    @Syntax("[page] [world]")
    @Description("Retrieve a list of claims.")
    public void onList(CommandSender sender, @Optional Integer page, @Optional String value) {
        HashSet<Integer> claims = new HashSet<>();
        HashSet<Integer> claimPage = new HashSet<>();

        if (!(sender instanceof Player) && value == null) {
            plugin.sendMessage(sender, plugin.localize("messages.list.console"));
            return;
        }

        World world = value != null ? plugin.getServer().getWorld(value) : ((Player) sender).getWorld();

        if (world == null) {
            plugin.sendMessage(sender, plugin.localize("messages.list.unknown-world"));
            return;
        }

        if (page == null || page <= 0) {
            page = 1;
        }

        page = page - 1;

        Long2ObjectOpenHashMap<ArrayList<Integer>> claimChunks = plugin.getCrashClaim().getDataManager().getClaimChunkMap(
            world.getUID()
        );

        if (claimChunks.isEmpty()) {
            plugin.sendMessage(sender, plugin.localize("messages.list.empty"));
            return;
        }

        claimChunks.values().forEach(claim -> {
            if (claim.isEmpty() || claim.get(0) == null) {
                return;
            }

            claims.add(claim.get(0));
        });

        claims.stream().skip(10L * page).limit(10).forEach(claimPage::add);

        if (claimPage.isEmpty()) {
            plugin.sendMessage(sender, plugin.localize("messages.list.empty"));
            return;
        }

        plugin.sendRawMessage(
            sender,
            plugin
                .localize("messages.list.header")
                .replace("{current}", Integer.toString(page + 1))
                .replace("{total}", Integer.toString(claims.toArray().length / 10 + 1))
        );

        claimPage.forEach(id -> {
            Claim claim = plugin.getCrashClaim().getApi().getClaim(id);
            String owner = plugin.getServer().getOfflinePlayer(claim.getOwner()).getName();

            plugin.sendRawMessage(
                sender,
                plugin
                    .localize("messages.list.format")
                    .replace("{id}", Integer.toString(claim.getId()))
                    .replace("{owner}", owner != null ? owner : plugin.localize("messages.generic.unknown"))
            );
        });
    }

    /**
     * Retrieve a list of the specified player's claims.
     *
     * @param sender The command sender.
     * @param target The target player.
     */
    @Subcommand("player|lookup")
    @CommandPermission("crashclaim.admin.claimadmin")
    @Syntax("<player>")
    @CommandCompletion("@players")
    @Description("Retrieve a list of the specified player's claims.")
    public void onPlayer(CommandSender sender, @Optional String target) {
        if (target == null) {
            plugin.sendMessage(sender, plugin.localize("messages.player.no-target"));
            return;
        }

        OfflinePlayer player = plugin.getServer().getOfflinePlayer(target);

        if (player.getName() == null || !player.hasPlayedBefore()) {
            plugin.sendMessage(sender, plugin.localize("messages.player.not-found"));
            return;
        }

        ArrayList<Claim> claims = plugin.getCrashClaim().getDataManager().getOwnedClaims(
            player.getUniqueId()
        );

        if (claims == null || claims.isEmpty()) {
            plugin.sendMessage(sender, plugin.localize("messages.player.empty"));
            return;
        }

        plugin.sendRawMessage(
            sender,
            plugin
                .localize("messages.player.header")
                .replace("{player}", player.getName())
        );

        ArrayList<String> claimList = new ArrayList<>();

        claims.forEach(claim -> claimList.add(
            plugin
                .localize("messages.player.format")
                .replace("{id}", Integer.toString(claim.getId()))
        ));

        plugin.sendRawMessage(
            sender,
            plugin.localize("messages.player.before") +
            StringUtils.join(claimList, plugin.localize("messages.player.delimiter"))
        );
    }

    /**
     * Teleport to the specified claim.
     *
     * @param sender The command sender.
     * @param value  The claim ID.
     */
    @Subcommand("teleport|tp")
    @CommandPermission("crashclaim.admin.claimadmin")
    @Syntax("<id>")
    @Description("Teleport to the specified claim.")
    public void onTeleport(CommandSender sender, @Optional String value) {
        if (!(sender instanceof Player player)) {
            plugin.sendMessage(sender, plugin.localize("messages.teleport.console"));
            return;
        }

        if (!StringUtils.isNumeric(value)) {
            plugin.sendMessage(sender, plugin.localize("messages.teleport.invalid-claim"));
            return;
        }

        Claim claim;

        try {
            claim = plugin.getCrashClaim().getApi().getClaim(
                Integer.parseInt(value)
            );
        } catch (Exception e) {
            plugin.sendMessage(
                player,
                plugin
                    .localize("messages.teleport.not-found")
                    .replace("{id}", value)
            );

            return;
        }

        String owner = plugin.getServer().getOfflinePlayer(claim.getOwner()).getName();
        World world = plugin.getServer().getWorld(claim.getWorld());

        if (world == null) {
            plugin.sendMessage(player, plugin.localize("messages.teleport.unknown-world"));
            return;
        }

        int x = (int) Math.round(claim.getMaxX() - (double) (Math.abs(claim.getMaxX() - claim.getMinX()) / 2));
        int z = (int) Math.round(claim.getMaxZ() - (double) (Math.abs(claim.getMaxZ() - claim.getMinZ()) / 2));

        PaperLib.teleportAsync(
            player,
            new Location(
                world,
                x,
                world.getHighestBlockYAt(x, z) + 1,
                z
            )
        );

        plugin.sendMessage(
            player,
            plugin
                .localize("messages.teleport.success")
                .replace("{id}", Integer.toString(claim.getId()))
                .replace("{owner}", owner != null ? owner : plugin.localize("messages.generic.unknown"))
        );
    }

    /**
     * Delete the specified claim.
     *
     * @param sender The command sender.
     * @param value  The claim ID.
     */
    @Subcommand("delete")
    @CommandPermission("crashclaim.admin.claimadmin")
    @Syntax("<id>")
    @Description("Delete the specified claim.")
    public void onDelete(CommandSender sender, @Optional String value) {
        if (!StringUtils.isNumeric(value)) {
            plugin.sendMessage(sender, plugin.localize("messages.delete.invalid-claim"));
            return;
        }

        Claim claim;

        try {
            claim = plugin.getCrashClaim().getApi().getClaim(
                Integer.parseInt(value)
            );
        } catch (Exception e) {
            plugin.sendMessage(
                sender,
                plugin
                    .localize("messages.delete.not-found")
                    .replace("{id}", value)
            );

            return;
        }

        plugin.getCrashClaim().getDataManager().deleteClaim(claim);

        plugin.sendMessage(
            sender,
            plugin
                .localize("messages.delete.success")
                .replace("{id}", Integer.toString(claim.getId()))
        );
    }

    /**
     * Retrieve the plugin help.
     *
     * @param sender The command sender.
     * @param help   The CommandHelp instance.
     */
    @HelpCommand
    @Syntax("[page]")
    @CommandPermission("crashclaim.admin.claimadmin")
    @Description("View the plugin help.")
    public void onHelp(CommandSender sender, CommandHelp help) {
        plugin.sendRawMessage(sender, plugin.localize("messages.help.header"));

        for (HelpEntry entry : help.getHelpEntries()) {
            plugin.sendRawMessage(
                sender,
                plugin.localize("messages.help.format")
                    .replace("{command}", entry.getCommand())
                    .replace("{parameters}", entry.getParameterSyntax())
                    .replace("{description}", plugin.localize("messages." + entry.getCommand().split("\\s+")[1] + ".description"))
            );
        }

        plugin.sendRawMessage(sender, plugin.localize("messages.help.footer"));
    }

    /**
     * Reload the plugin confirmation.
     *
     * @param sender The command sender.
     */
    @Subcommand("reload")
    @CommandPermission("crashclaim.admin.claimadmin")
    @Description("Reload the plugin configuration.")
    public void onReload(CommandSender sender) {
        try {
            plugin.reload();
            plugin.sendMessage(sender, plugin.localize("messages.reload.success"));
        } catch (Exception e) {
            plugin.sendMessage(sender, plugin.localize("messages.reload.failed"));
        }
    }
}
