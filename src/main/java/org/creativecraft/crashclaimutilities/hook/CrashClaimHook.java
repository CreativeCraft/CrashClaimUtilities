package org.creativecraft.crashclaimutilities.hook;

import net.crashcraft.crashclaim.fastutil.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.creativecraft.crashclaimutilities.CrashClaimUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CrashClaimHook {
    private final CrashClaimUtilities plugin;
    private HashMap<UUID, HashSet<Integer>> claimIds;
    private HashMap<UUID, Long> claimIdsExpiry;

    public CrashClaimHook(CrashClaimUtilities plugin) {
        this.plugin = plugin;
        this.claimIds = new HashMap<>();
        this.claimIdsExpiry = new HashMap<>();
    }

    /**
     * Retrieve the claims in the specified world.
     *
     * @param  world The world.
     * @return HashSet
     */
    public HashSet<Integer> getClaims(World world) {
        HashSet<Integer> claims = new HashSet<>();

        Long2ObjectOpenHashMap<ArrayList<Integer>> claimChunks = plugin.getCrashClaim().getDataManager().getClaimChunkMap(
            world.getUID()
        );

        if (claimChunks.isEmpty()) {
            return null;
        }

        claimChunks.values().forEach(claim -> {
            if (claim.isEmpty() || claim.get(0) == null) {
                return;
            }

            claims.add(claim.get(0));
        });

        return claims;
    }

    /**
     * Retrieve cached claim ID's.
     *
     * @param  world The world.
     * @return HashSet
     */
    public HashSet<Integer> getClaimIds(World world) {
        UUID uuid = world.getUID();

        if (
            claimIds.containsKey(uuid) &&
            claimIdsExpiry.containsKey(uuid) &&
            claimIdsExpiry.get(uuid) > System.currentTimeMillis()
        ) {
            return claimIds.get(uuid);
        }

        HashSet<Integer> claims = getClaims(world);

        if (claims == null || claims.isEmpty()) {
            return null;
        }

        if (claimIds.containsKey(uuid)) {
            claimIds.get(uuid).clear();
        }

        claimIds.put(uuid, claims);
        claimIdsExpiry.put(uuid, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15));

        return claims;
    }

    /**
     * Retrieve the container permission set.
     *
     * @param  value The permission value.
     * @return HashMap
     */
    public HashMap<Material, Integer> getContainers(Integer value) {
        HashMap<Material, Integer> containers = new HashMap<>();

        for (Material item : plugin.getCrashClaim().getDataManager().getPermissionSetup().getTrackedContainers()) {
            containers.put(item, value);
        }

        return containers;
    }
}
