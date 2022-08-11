package org.creativecraft.crashclaimutilities.hook;

import net.crashcraft.crashclaim.fastutil.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.creativecraft.crashclaimutilities.CrashClaimUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CrashClaimHook {
    private final CrashClaimUtilities plugin;

    public CrashClaimHook(CrashClaimUtilities plugin) {
        this.plugin = plugin;
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
            Integer id = claim.get(0);

            if (id == null) {
                return;
            }

            claims.add(id);
        });

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
