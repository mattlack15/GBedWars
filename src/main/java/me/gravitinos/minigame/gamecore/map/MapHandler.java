package me.gravitinos.minigame.gamecore.map;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.FaweQueue;
import com.boydti.fawe.object.RunnableVal2;
import com.boydti.fawe.object.visitor.FastChunkIterator;
import com.boydti.fawe.util.EditSessionBuilder;
import com.google.common.collect.Lists;
import com.sk89q.jnbt.StringTag;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.SignBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.util.SyncProgressReport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapHandler {
    private boolean mapPrePlaced = true;
    private File mapFile = null;
    private ArrayList<MapKeyPoint> keyPoints = new ArrayList<>();

    public MapHandler() {
    }

    public MapHandler(File mapSchematic) {
        this.mapFile = mapSchematic;
    }

    public void addKeyPoint(MapKeyPoint point) {
        this.keyPoints.add(point);
    }

    public void removeKeyPoint(MapKeyPoint point) {
        this.keyPoints.remove(point);
    }

    public ArrayList<MapKeyPoint> getKeyPoints() {
        return Lists.newArrayList(this.keyPoints);
    }

    public void clearKeyPoints() {
        this.keyPoints.clear();
    }

    public SyncProgressReport<Boolean> findKeyPoints(CuboidRegion region, boolean destroyPoints) {
        SyncProgressReport<Boolean> progressReport = new SyncProgressReport<>("Map Key Point Search");

        CoreHandler.instance.getAsyncExecutor().execute(() -> {
            World world = region.getWorld();
            if (world == null) {
                progressReport.getFuture().complete(false);
                return;
            }

            EditSession session = new EditSessionBuilder(world).fastmode(true).allowedRegionsEverywhere().build();

            FaweQueue queue = FaweAPI.createQueue(world, false);

            int area = region.getArea();
            if(area == 0){
                area = -1;
            }
            int[] progress = {0};

            for (com.sk89q.worldedit.Vector2D chunkPos : new FastChunkIterator(region.getChunks(), session)) {

                progressReport.setPercentProgress(progress[0] / (double) area);

                queue.forEachBlockInChunk(chunkPos.getBlockX(), chunkPos.getBlockZ(), new RunnableVal2<com.sk89q.worldedit.Vector, BaseBlock>() {
                    @Override
                    public void run(com.sk89q.worldedit.Vector blockPos, BaseBlock block) {
                        try {

                            if (!region.contains(blockPos)) {
                                return;
                            }

                            progress[0]++;

                            Map<com.sk89q.worldedit.Vector, BaseBlock> blockCache = new HashMap<>();

                            blockCache.put(blockPos, block);

                            for (MapKeyPoint points : keyPoints) {

                                MapBlockIdentity origin = points.getOriginIdentifyingPoint();
                                if (block.getId() != origin.getId() || (origin.getData() != -1 && block.getData() != block.getData())) {
                                    continue;
                                }

                                Map<Vector, MapBlockIdentity> identifyingPoints = points.getIdentifyingPoints();
                                boolean matched = true;
                                for (Vector relPos : identifyingPoints.keySet()) {
                                    MapBlockIdentity identity = identifyingPoints.get(relPos);

                                    com.sk89q.worldedit.Vector relBlockPos = blockPos.add(new com.sk89q.worldedit.Vector(relPos.getX(), relPos.getY(), relPos.getZ()));
                                    BaseBlock rel;
                                    if (blockCache.containsKey(relBlockPos)) {
                                        rel = blockCache.get(relBlockPos);
                                    } else {
                                        rel = session.getBlock(relBlockPos);
                                        blockCache.put(relBlockPos, rel);
                                    }

                                    //Check if they match
                                    if (rel.getId() != identity.getId() || (identity.getData() != -1 && rel.getData() != identity.getData())) {
                                        matched = false;
                                        break;
                                    }

                                    //If it is a sign block
                                    if (rel.getId() == Material.SIGN_POST.getId()) {
                                        if (rel.getNbtData() == null) {
                                            matched = false;
                                            break;
                                        }
                                        rel.getNbtData().getValue().put("id", new StringTag("Sign"));
                                        SignBlock signBlock = new SignBlock(rel.getId(), rel.getData());
                                        signBlock.setNbtData(rel.getNbtData());
                                        String[] text = signBlock.getText(); //Get sign text
                                        Object[] data = identity.getExtraData(); //Get the text data from identity
                                        if (data.length > 0) { //If the data's length is 0 then skip, because it means it doesn't matter what the text is
                                            if (data[0] instanceof String[]) { //If data is in form of String[] in data[0]
                                                String[] idText = (String[]) data;
                                                for (int i = 0; i < idText.length; i++) { //Loop through and check each one
                                                    if (i >= text.length || !text[i].equals(idText[i])) { //If all have been the same up to now, and text for sign ends, but there is more in identity text
                                                        //then there is no match, or if the text does not match, then there is no match
                                                        matched = false;
                                                        break;
                                                    }
                                                }
                                            } else if (data[0] instanceof String) {
                                                for (int i = 0; i < data.length; i++) {
                                                    if (!(data[i] instanceof String)) {
                                                        break;
                                                    }

                                                    String line = (String) data[i];
                                                    if (i >= text.length || !text[i].contains(line)) { //Basically same thing
                                                        matched = false;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (matched) {
                                    Location l = new Location(Bukkit.getWorld(Objects.requireNonNull(region.getWorld()).getName()), blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                    points.getLocationConsumer().accept(l);

                                    if (destroyPoints) {
                                        points.buildWith(session, new ArrayList<Location>() {{
                                            add(l);
                                        }}, new BaseBlock(Material.AIR.getId(), 0));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressReport.getFuture().complete(false);
                        }
                    }
                });
            }
            if (destroyPoints && session != null) {
                session.flushQueue();
            }
            progressReport.getFuture().complete(true);
        });

        return progressReport;
    }
}
