package fesa.needyfesa;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.Map;

public class ChestFinder {
    public static void chestRequest(int range) {
        assert MinecraftClient.getInstance().player != null;
        ArrayList<BlockPos> chests = getChests(NeedyFesa.staticLobbyChestLocations, range);
        if (chests.isEmpty()) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("\u00A73No chests have been found :("));
        } else {
            StringBuilder s = new StringBuilder();
            for (BlockPos chest : chests) {
                s.append("\u00A73Found a chest @\u00A72 ").append(chest.getX()).append(", ").append(chest.getY()).append(", ").append(chest.getZ()).append("\u00A76!\n");
            }
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(String.valueOf(s)));
            SoundEvent sound = Registry.SOUND_EVENT.get(new Identifier("entity.experience_orb.pickup"));
            assert MinecraftClient.getInstance().world != null;
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().world.playSound(MinecraftClient.getInstance().player.getX(), MinecraftClient.getInstance().player.getY(),
                    MinecraftClient.getInstance().player.getZ(), sound, SoundCategory.MASTER, 1f, 1f, true);

            BlockPos partyChest = chests.get(0);
            if (NeedyFesa.partyStatus && (NeedyFesa.currentChestCoords != partyChest || NeedyFesa.chestPartyAnnounce < 3)) {
                String msg = "@&3Found a chest @&2 " + partyChest.getX() + ", " + partyChest.getY() + ", " + partyChest.getZ() + "&6!";
                MinecraftClient.getInstance().player.sendChatMessage(msg, Text.of(msg));
                if (NeedyFesa.currentChestCoords != partyChest) {
                    NeedyFesa.chestPartyAnnounce = 0;
                } else {
                    NeedyFesa.chestPartyAnnounce++;
                }
            }
            NeedyFesa.currentChestCoords = partyChest;
        }
    }

    private static ArrayList<BlockPos> getChests(JsonArray lobbyChests, int range) {
        ArrayList<BlockPos> out = new ArrayList<BlockPos>();
        assert MinecraftClient.getInstance().player != null;
        assert MinecraftClient.getInstance().world != null;
        ChunkPos playerPos = MinecraftClient.getInstance().player.getChunkPos();

        for (int xCord = -range; xCord <= range; xCord++) {
            for (int zCord = -range; zCord <= range; zCord++) {
                WorldChunk currentChunk = MinecraftClient.getInstance().world.getChunk(playerPos.x + xCord, playerPos.z + zCord);
                for (Map.Entry<BlockPos, BlockEntity> entry: currentChunk.getBlockEntities().entrySet()) {
                    if (entry.getValue().getType().equals(BlockEntityType.CHEST)) {
                        boolean addToOut = true;
                        for (int i = 0; i < lobbyChests.size(); i++) {
                            JsonObject chestLocation = lobbyChests.get(i).getAsJsonObject();
                            if (chestLocation.get("x").getAsInt() == entry.getKey().getX()
                                    && chestLocation.get("y").getAsInt() == entry.getKey().getY()
                                    && chestLocation.get("z").getAsInt() == entry.getKey().getZ()) {
                                addToOut = false;
                                break;
                            }
                        }
                        if (addToOut) {
                            out.add(entry.getKey());
                        }

                    }
                }
            }
        }
        return out;
    }
}
