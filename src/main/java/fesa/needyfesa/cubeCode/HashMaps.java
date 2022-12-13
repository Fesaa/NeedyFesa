package fesa.needyfesa.cubeCode;

import java.util.HashMap;
import java.util.Map;

public class HashMaps {

    public HashMap<String, Integer> EggWarsItems = new HashMap<>();
    public HashMap<Integer, String> EggWarsItemsInv = new HashMap<>();
    public HashMap<String, Integer> EggWarsHealth = new HashMap<>();
    public HashMap<Integer, String> EggWarsHealthInv = new HashMap<>();

    public HashMap<String, Integer> SkyWarsChests = new HashMap<>();
    public HashMap<Integer, String> SkyWarsChestsInv = new HashMap<>();
    public HashMap<String, Integer> SkyWarsProjectiles = new HashMap<>();
    public HashMap<Integer, String> SkyWarsProjectilesInv = new HashMap<>();

    public HashMap<String, Integer> LuckyIslandsBlocks = new HashMap<>();
    public HashMap<Integer, String> LuckyIslandsBlocksInv = new HashMap<>();

    public HashMap<String, Integer> Time = new HashMap<>();
    public HashMap<Integer, String> TimeInv = new HashMap<>();

    public HashMaps() {
        EggWarsItems.put("Hardcore", 10);
        EggWarsItems.put("Normal", 13);
        EggWarsItems.put("Overpowered", 16);

        EggWarsHealth.put("Half", 10);
        EggWarsHealth.put("Normal", 13);
        EggWarsHealth.put("Double", 16);

        SkyWarsChests.put("Basic", 10);
        SkyWarsChests.put("Normal", 13);
        SkyWarsChests.put("Overpowered", 16);

        SkyWarsProjectiles.put("No Projectiles", 10);
        SkyWarsProjectiles.put("Normal Projectiles", 13);
        SkyWarsProjectiles.put("Soft Blocks", 16);

        LuckyIslandsBlocks.put("Blessed", 10);
        LuckyIslandsBlocks.put("Normal", 12);
        LuckyIslandsBlocks.put("Overpowered", 14);
        LuckyIslandsBlocks.put("Crazy", 16);

        Time.put("Day Time", 10);
        Time.put("Night Time", 13);
        Time.put("Sunset", 16);

        for(Map.Entry<String, Integer> entry : EggWarsItems.entrySet()){
            EggWarsItemsInv.put(entry.getValue(), entry.getKey());
        }

        for(Map.Entry<String, Integer> entry : EggWarsHealth.entrySet()){
            EggWarsHealthInv.put(entry.getValue(), entry.getKey());
        }

        for(Map.Entry<String, Integer> entry : SkyWarsChests.entrySet()){
            SkyWarsChestsInv.put(entry.getValue(), entry.getKey());
        }

        for(Map.Entry<String, Integer> entry : SkyWarsProjectiles.entrySet()){
            SkyWarsProjectilesInv.put(entry.getValue(), entry.getKey());
        }

        for(Map.Entry<String, Integer> entry : LuckyIslandsBlocks.entrySet()){
            LuckyIslandsBlocksInv.put(entry.getValue(), entry.getKey());
        }

        for(Map.Entry<String, Integer> entry : Time.entrySet()){
            TimeInv.put(entry.getValue(), entry.getKey());
        }
    }



}
