package me.gravitinos.minigame.gamecore.scoreboard;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SBElement {
    private static Random rand = new Random(System.currentTimeMillis());

    private SBTextGetter textGetter;
    private String name;
    public SBElement(@NotNull String text){
        this.name = getRandom12CharName("");
        textGetter = (p) -> text;
    }

    /**
     * Get the non-visible name of this element
     * @return The name of this element
     */
    protected String getName(){
        return this.name;
    }

    /**
     * Set the name of this element
     * @param name The name to set to
     */
    protected void setName(String name){
        this.name = name;
    }

    /**
     * Set the text getter
     * @param getter The text getter
     */
    public SBElement setTextGetter(@NotNull SBTextGetter getter){
        this.textGetter = getter;
        return this;
    }

    /**
     * Set the text getter
     * @param getter The text getter
     */
    public SBElement setTextGetter(@NotNull AltSBTextGetter getter){
        this.textGetter = (p) -> getter.getText();
        return this;
    }

    /**
     * Get the text
     * @return The text returned by the text getter
     */
    public String getText(Player player){
        return this.textGetter.getText(player);
    }


    /**
     * Get a random, non-visible chat-colour consisted name/string
     * @return A name/string
     */
    protected static String getRandom12CharName(String lastColors){
        StringBuilder name = new StringBuilder();
        for(int i = 0; i < 6; i++){
            name.append(ChatColor.values()[rand.nextInt(16)]);
        }
        return name.toString() + lastColors;
    }
}
