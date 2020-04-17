package me.gravitinos.minigame.bedwars.game.command.admin;

import com.google.common.collect.Lists;
import me.gravitinos.minigame.SpigotMinigames;
import me.gravitinos.minigame.bedwars.anticheat.check.Check;
import me.gravitinos.minigame.bedwars.anticheat.check.checks.world.ScaffoldA;
import me.gravitinos.minigame.bedwars.anticheat.data.Profile;
import me.gravitinos.minigame.bedwars.game.SpigotBedwars;
import me.gravitinos.minigame.bedwars.game.command.GravCommandPermissionable;
import me.gravitinos.minigame.bedwars.game.command.GravSubCommand;
import me.gravitinos.minigame.gamecore.CoreHandler;
import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Layers.Dense.DenseLayer;
import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Layers.Dense.Node;
import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Layers.Layer;
import me.gravitinos.minigame.gamecore.util.NeuralNetwork.NeuralNetwork;
import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Resources.Matrix;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class CommandAC extends GravSubCommand {
    public CommandAC(GravCommandPermissionable parentCommand, String cmdPath) {
        super(parentCommand, cmdPath);
    }

    @Override
    public String getPermission() {
        return "bw.ac";
    }

    @Override
    public String getDescription() {
        return "none";
    }

    @Override
    public String getAlias() {
        return "ac";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, Object... passedArgs) {

        if (!(sender instanceof Player)) {
            return this.sendErrorMessage(sender, "You must be a player to use this command!");
        }

        Player p = (Player) sender;

        if (args.length < 1) {
            return this.sendErrorMessage(sender, "Need more arguments");
        }

        String arg = args[0];

        if (arg.equalsIgnoreCase("learnFrom")) {
            Profile profile = Check.getProfile(p.getUniqueId());
            assert profile != null;
            profile.getCheck(ScaffoldA.class).learningFrom = !profile.getCheck(ScaffoldA.class).learningFrom;
            return this.sendErrorMessage(sender, "Learning From You set to " + profile.getCheck(ScaffoldA.class).learningFrom);
        } else if (arg.equalsIgnoreCase("toggleHacking")) {
            Profile profile = Check.getProfile(p.getUniqueId());
            assert profile != null;
            profile.getCheck(ScaffoldA.class).isHacking = !profile.getCheck(ScaffoldA.class).isHacking;
            profile.getCheck(ScaffoldA.class).blockPlacements.clear();
            profile.getCheck(ScaffoldA.class).values.clear();
            profile.getCheck(ScaffoldA.class).packetTypes.clear();

            return this.sendErrorMessage(sender, "isHacking From You set to " + profile.getCheck(ScaffoldA.class).isHacking);
        } else if (arg.equalsIgnoreCase("reset")) {
            ScaffoldA.setupNetwork();
            return this.sendErrorMessage(sender, "Reset completed!");
        } else if (arg.equalsIgnoreCase("startTraining")) {
            CoreHandler.instance.getAsyncExecutor().execute(() -> {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    Profile profile = Check.getProfile(players.getUniqueId());
                    assert profile != null;
                    profile.getCheck(ScaffoldA.class).disable();
                }

                Collections.shuffle(ScaffoldA.trainingSet);

                ArrayList<ArrayList<Matrix>> testInputs = new ArrayList<>();
                ArrayList<Matrix> testAnswers = new ArrayList<>();

                int testThreshold = (int) Math.floor((double) ScaffoldA.trainingSet.size() * 0.22d);

                ArrayList<ArrayList<Matrix>> inputs = new ArrayList<>();
                ArrayList<Matrix> answers = new ArrayList<>();

                int i1 = 0;
                for (ScaffoldA.TrainingEx te : ScaffoldA.trainingSet) {
                   // ArrayList<Matrix> in = new ArrayList<>();
                   // in.add(NeuralNetwork.normalize(te.input.get(0), ScaffoldA.dataMean, ScaffoldA.dataStd));
                    if(i1++ < testThreshold){
                        testInputs.add(te.input);
                        testAnswers.add(te.answer);
                    } else {
                        inputs.add(te.input);
                        answers.add(te.answer);
                    }
                }

                sendErrorMessage(sender, "Starting training..");
                NeuralNetwork network = ScaffoldA.network;

                network.getLayers().forEach(l -> {
                    if(l instanceof DenseLayer){
                        ((DenseLayer) l).setDropOut(0.33d);
                    }
                });

                double a = 0;

                int iterations = 2000;
                int batchSize = 36;

                for (int i = 0; i < iterations; i++) {
                    if (i % 5 == 0) ScaffoldA.percentageTrainingDone = i / (double) iterations;
                    if(i == 1000){
                        network.setLearningRate(0.006);
                    }
                    double loss = network.train(inputs, answers, batchSize);
                    a += loss;
                }
                double avgLoss = a / (double) iterations;

                network.getLayers().forEach(l -> {
                    if(l instanceof DenseLayer){
                        ((DenseLayer) l).setDropOut(0d);
                    }
                });

                double accuracy = getAccuracy(network, testInputs, testAnswers);
                accuracy = Math.round(accuracy * 1000) / 10d;

                sendErrorMessage(sender, "&a&lTraining Finished!");

                sendErrorMessage(sender, "&6&lTraining Stats");
                sendErrorMessage(sender, "&eAverage Loss: &f" + avgLoss);
                sendErrorMessage(sender, "&eTest Accuracy: &f" + accuracy + "%");
                sendErrorMessage(sender, "&eTraining Iterations: &f" + iterations * inputs.size());
                sendErrorMessage(sender, "&eBatch Size: &f" + batchSize);


                for (Player players : Bukkit.getOnlinePlayers()) {
                    Profile profile = Check.getProfile(players.getUniqueId());
                    assert profile != null;
                    profile.getCheck(ScaffoldA.class).enable();
                    profile.getCheck(ScaffoldA.class).learningFrom = false;
                }
            });
        } else if (arg.equalsIgnoreCase("savescaffoldnn")) {
            CoreHandler.instance.getAsyncExecutor().execute(() -> {
                NeuralNetwork network = ScaffoldA.network;
                File file = new File(SpigotMinigames.instance.getDataFolder() + File.separator + "ac" + File.separator + "scaffold", "nn.yml");
                file.getParentFile().mkdirs();
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                int layer = 0;
                for (Layer layers : network.getLayers()) {
                    if (!(layers instanceof DenseLayer)) continue;
                    ConfigurationSection section = config.createSection(layer++ + "");
                    int node = 0;
                    for (Node nodes : ((DenseLayer) layers).getNodes()) {
                        ArrayList<Double> data = Lists.newArrayList(nodes.getWeights().getData());
                        data.add(nodes.getBias());
                        section.set(node++ + "", data);
                    }
                }
                try {
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendErrorMessage(sender, "&aNetwork saved to /ac/scaffold/nn.yml");
            });
        }


        return true;
    }

    public static double getAccuracy(NeuralNetwork network, ArrayList<ArrayList<Matrix>> testInputs, ArrayList<Matrix> testAnswers){
        double accuracy = 0;
        for(int i = 0; i < testInputs.size(); i++){
            Matrix out = network.propForward(testInputs.get(i)).get(0);
            out.subtract(testAnswers.get(i).getCopy());
            out.updateAll(Math::abs);
            accuracy += out.getTotal() / 2;
        }

        accuracy /= (double) testInputs.size();
        return 1 - accuracy;
    }
}
