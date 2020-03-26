package me.gravitinos.bedwars.anticheat.check.checks.world;

import com.google.common.collect.Lists;
import me.gravitinos.bedwars.anticheat.check.AlertType;
import me.gravitinos.bedwars.anticheat.check.Check;
import me.gravitinos.bedwars.anticheat.check.CheckType;
import me.gravitinos.bedwars.anticheat.check.PunishType;
import me.gravitinos.bedwars.anticheat.data.Profile;
import me.gravitinos.bedwars.anticheat.data.Violation;
import me.gravitinos.bedwars.game.SpigotBedwars;
import me.gravitinos.bedwars.gamecore.CoreHandler;
import me.gravitinos.bedwars.gamecore.util.ActionBar;
import me.gravitinos.bedwars.gamecore.util.EventSubscription;
import me.gravitinos.bedwars.gamecore.util.NeuralNetwork.Layers.Dense.DenseLayer;
import me.gravitinos.bedwars.gamecore.util.NeuralNetwork.Layers.Dense.Node;
import me.gravitinos.bedwars.gamecore.util.NeuralNetwork.NeuralNetwork;
import me.gravitinos.bedwars.gamecore.util.NeuralNetwork.Resources.ActivationFunction;
import me.gravitinos.bedwars.gamecore.util.NeuralNetwork.Resources.Matrix;
import me.gravitinos.bedwars.gamecore.util.PacketEvent;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class ScaffoldA extends Check {

    public static final String exFolder = "scaffoldTraining";

    public static NeuralNetwork network = new NeuralNetwork();

    public static ArrayList<TrainingEx> trainingSet = new ArrayList<>();

    public static double percentageTrainingDone = 100;

    public static class TrainingEx {
        public ArrayList<Matrix> input;
        public Matrix answer;

        public TrainingEx(ArrayList<Matrix> input, Matrix answer) {
            this.input = input;
            this.answer = answer;
        }
    }


    static {
        setupNetwork();
        loadTrainingExamples();
        try {
            loadNetwork();
        } catch(Exception e){
            setupNetwork();
        }
    }

    public static void loadNetwork(){
        if(network == null) return;
        File file = new File(SpigotBedwars.instance.getDataFolder() + File.separator + "ac" + File.separator + "scaffold", "nn.yml");
        if(!file.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        int layerIndex = 0;
        for(String layers : config.getKeys(false)){
            DenseLayer layer = (DenseLayer) network.getLayers().get(layerIndex++);
            layer.setDropOut(0);
            int nodeIndex = 0;
            ConfigurationSection section = config.getConfigurationSection(layers);
            for(String nodes : section.getKeys(false)){
                Node node = layer.getNodes().get(nodeIndex++);
                ArrayList<Double> values = Lists.newArrayList(section.getDoubleList(nodes));
                double bias = values.get(values.size()-1);
                values.remove(values.size()-1);
                node.getWeights().setData(values);
                node.setBias(bias);
            }
        }
    }

    public static void loadTrainingExamples(){
        if (trainingSet.size() > 0) {
            return;
        }

        File folder = new File(SpigotBedwars.instance.getDataFolder() + File.separator + exFolder);
        if (!folder.exists()) {
            return;
        }

        ArrayList<Matrix> inputs = new ArrayList<>();

        for (File files : folder.listFiles()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(files);
            ArrayList<Double> ansData = Lists.newArrayList(config.getDoubleList("answer"));
            ArrayList<Double> inputData = Lists.newArrayList(config.getDoubleList("input"));


            while(inputData.size() > 125){
                inputData.remove(inputData.size()-1);
            }

            Matrix ans = new Matrix(ansData.size(), 1);
            Matrix input = new Matrix(inputData.size(), 1);

            ans.setData(ansData);
            input.setData(inputData);

            inputs.add(input);

            trainingSet.add(new TrainingEx(new ArrayList<Matrix>() {{
                add(input);
            }}, ans));
        }
    }

    public static void setupNetwork() {
        network = new NeuralNetwork();
        Random rand = new Random(System.currentTimeMillis());
        network.addLayer(new DenseLayer(30, 125, ActivationFunction.ReLU, true, rand).setDropOut(0.4));
        network.addLayer(new DenseLayer(30, 30, ActivationFunction.ReLU, true, rand).setDropOut(0.4));
        network.addLayer(new DenseLayer(30, 30, ActivationFunction.ReLU, true, rand).setDropOut(0.4));
        network.addLayer(new DenseLayer(30, 30, ActivationFunction.ReLU, true, rand).setDropOut(0.4));
        network.addLayer(new DenseLayer(2, 30, ActivationFunction.Sigmoid, true, rand).setDropOut(0.4));
        network.setLearningRate(0.012d);


    }

    private static final int PACKET_FLY = -1;
    private static final int PACKET_BLOCK_PLACE = 0;

    public ArrayList<Integer> packetTypes = new ArrayList<>();
    public ArrayList<Double> values = new ArrayList<>();
    public ArrayList<Double> blockPlacements = new ArrayList<>();

    public boolean learningFrom = false;
    public boolean isHacking = false;

    private double lastval1 = 0d, lastval2 = 0d;

    public ScaffoldA(Profile profile) {
        super(profile, "Scaffold A", CheckType.PACKET, AlertType.ALL_STAFF, PunishType.EXPERIMENTAL);
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                if(!ScaffoldA.this.isEnabled()){
//                    return;
//                }
//                Player p = getProfile().getPlayer();
//                if (p != null) {
//                    synchronized (ScaffoldA.this) {
//                        String msg = "";
//                        msg += "&bTraining: &e" + (Math.round(percentageTrainingDone * 1000) / 10f) + "% ";
//                        msg += "&6Examples(&3" + (learningFrom ? isHacking : "N/A") + "&6): &e" + trainingSet.size();
//                        msg += " ";
//                        msg += "&aProb. Legit: &e" + Math.round(lastval1 * 100);
//                        msg += " ";
//                        msg += "&cProb. Hacking: &e" + Math.round(lastval2 * 100);
//                        ActionBar.send(p, ChatColor.translateAlternateColorCodes('&', msg));
//                    }
//                }
//            }
//        }.runTaskTimerAsynchronously(CoreHandler.main, 0, 4);
    }

    @EventSubscription
    private void onMove(PlayerMoveEvent event) {
        double yawChange = Math.abs(event.getTo().getYaw() - event.getFrom().getYaw());
        if (yawChange > 360) {
            yawChange = yawChange % 360;
        }
        yawChange /= 360;
        double distanceTraveled = event.getFrom().distance(event.getTo());
        values.add(distanceTraveled);
        values.add(yawChange);
    }

    @EventSubscription
    private synchronized void onFlyingPacket(PacketEvent<PacketPlayInFlying.PacketPlayInPositionLook> event) {

        if (!event.getPlayer().equals(getProfile().getUniqueId())) {
            return;
        }
        if (packetTypes.size() >= 45) {

            int numViolations = this.getViolations().size();

            if (this.check()) {
                packetTypes = Lists.newArrayList(packetTypes.subList(packetTypes.size()-30, packetTypes.size()));
                values = Lists.newArrayList(values.subList(values.size()-26, values.size()));
                if (blockPlacements.size() == 30) {
                    blockPlacements = Lists.newArrayList(blockPlacements.subList(18, 30));
                }
            }
        }
        packetTypes.add(PACKET_FLY);
    }

//    @EventSubscription
//    private synchronized void onArmAnimationPacket(PacketEvent<PacketPlayInArmAnimation> event) {
//        if (!event.getPlayer().equals(getProfile().getUniqueId())) {
//            return;
//        }
//        if (packetTypes.size() >= 45) {
//
//            if (this.check()) {
//                packetTypes = Lists.newArrayList(packetTypes.subList(15, packetTypes.size()));
//                values = Lists.newArrayList(values.subList(25, values.size()));
//            }
//        } else {
//            packetTypes.add(PACKET_ARM_ANIMATION);
//        }
//    }

    @EventSubscription
    private synchronized void onBlockPlacePacket(PacketEvent<PacketPlayInUseItem> event) {
        if (!event.getPlayer().equals(getProfile().getUniqueId())) {
            return;
        }

        if (packetTypes.size() >= 45) {
            int numViolations = this.getViolations().size();
            if (this.check()) {
                packetTypes = Lists.newArrayList(packetTypes.subList(packetTypes.size()-30, packetTypes.size()));
                values = Lists.newArrayList(values.subList(values.size()-26, values.size()));
                if (blockPlacements.size() == 30) {
                    blockPlacements = Lists.newArrayList(blockPlacements.subList(18, 30));
                }

            }
        }

            try {
                Field fieldBlockPos = event.getPacket().getClass().getDeclaredField("a");
                fieldBlockPos.setAccessible(true);
                BlockPosition blockPosition = (BlockPosition) fieldBlockPos.get(event.getPacket());
                fieldBlockPos.setAccessible(false);

                Field fieldEnumHand = event.getPacket().getClass().getDeclaredField("c");
                fieldEnumHand.setAccessible(true);
                EnumHand hand = (EnumHand) fieldEnumHand.get(event.getPacket());
                fieldEnumHand.setAccessible(false);
                ItemStack stack = getProfile().getPlayer().getInventory().getItemInMainHand();
                if(stack == null || stack.getType().equals(org.bukkit.Material.AIR)){
                    return;
                }

                Field fieldInBlockX = event.getPacket().getClass().getDeclaredField("d");
                Field fieldInBlockY = event.getPacket().getClass().getDeclaredField("e");
                Field fieldInBlockZ = event.getPacket().getClass().getDeclaredField("f");

                fieldInBlockX.setAccessible(true);
                fieldInBlockY.setAccessible(true);
                fieldInBlockZ.setAccessible(true);

                float inX = (float) fieldInBlockX.get(event.getPacket());
                float inY = (float) fieldInBlockY.get(event.getPacket());
                float inZ = (float) fieldInBlockZ.get(event.getPacket());

                if(inX > 1.1 || inX < -0.1 || inY > 1.1 || inY < -0.1 || inZ > 1.1 || inZ < -0.1){
                    this.addViolation(new Violation(1));
                }

                fieldInBlockX.setAccessible(false);
                fieldInBlockY.setAccessible(false);
                fieldInBlockZ.setAccessible(false);

                double relX = blockPosition.getX() - getProfile().getPlayer().getLocation().getX();
                double relY = blockPosition.getY() - getProfile().getPlayer().getLocation().getY();
                double relZ = blockPosition.getZ() - getProfile().getPlayer().getLocation().getZ();

                if (blockPlacements.size() < 30) {

                    blockPlacements.add(relX);
                    blockPlacements.add(relY);
                    blockPlacements.add(relZ);

                    blockPlacements.add((double) inX);
                    blockPlacements.add((double) inY);
                    blockPlacements.add((double) inZ);

                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

        packetTypes.add(PACKET_BLOCK_PLACE);
    }

    @Override
    protected boolean isAlertTime() {
        this.clearViolationsPast(5000);
        if (this.getViolations().size() >= 3) {
            Player p = getProfile().getPlayer();
            this.blockPlacements.clear();
            if (p != null) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HARP, 1f, 1f);
                p.sendMessage(ChatColor.RED + "Stop using scaffold!");
            }
            return true;
        }
        return false;
    }

    private synchronized boolean check() {
        if (values.size() < 50 || packetTypes.size() < 45 || blockPlacements.size() < 18) {
            return false;
        }

        Matrix matrix = new Matrix(125, 1);
        int index = 0;
        for (int i = 0; i < 30; i++) {
            if (i < blockPlacements.size()) {
                matrix.set(index++, 0, blockPlacements.get(i));
            } else {
                matrix.set(index++, 0, -2);
            }
        }
        for (int i = 0; i < 45; i++) {
            matrix.set(index++, 0, packetTypes.get(i));
        }
        for (int i = 0; i < 50; i++) {
            matrix.set(index++, 0, values.get(i).floatValue());
        }
        Matrix finalMatrix = matrix;
        ArrayList<Matrix> input = new ArrayList<Matrix>() {{
            add(finalMatrix);
        }};

        if (learningFrom) {
            //Learning from this player
            Matrix ans = new Matrix(2, 1);
            if (this.isHacking) {
                ans.set(1, 0, 1);
            } else {
                ans.set(0, 0, 1);
            }
            File file = new File(SpigotBedwars.instance.getDataFolder() + File.separator + exFolder, UUID.randomUUID().toString() + ".yml");
            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("answer", ans.getData());
            config.set("input", matrix.getData());
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }


            trainingSet.add(new TrainingEx(input, ans));
        } else {
            //Check using network
            synchronized (network) {

                //Run it through network
                ArrayList<Matrix> outW = network.propForward(input);
                Matrix out = outW.get(0);
                this.lastval1 = out.get(0, 0);
                this.lastval2 = out.get(1, 0);
                if (this.lastval2 > 0.935 && Math.abs(this.lastval1 - lastval2) > 0.68) {
                    CoreHandler.doInMainThread(() -> this.addViolation(new Violation((int) Math.round(Math.abs(this.lastval1 - lastval2)) * 10)));
                }
            }
        }

        return true;
    }

    public static double getMean(ArrayList<Matrix> matrices) {
        double mean = 0;
        double div = 0;
        for (Matrix matrix : matrices) {
            mean += matrix.getTotal();
            div += matrix.getData().size();
        }
        return mean / div;
    }

    public static double getStandardDeviation(ArrayList<Matrix> matrices) {
        double mean = getMean(matrices);
        double size = 0;
        double sum = 0;
        for (Matrix matrix : matrices) {
            for (Double datum : matrix.getData()) {
                sum += Math.pow(datum - mean, 2);
            }
            sum += matrix.getData().size();
        }
        return Math.sqrt(sum / size);
    }
}
