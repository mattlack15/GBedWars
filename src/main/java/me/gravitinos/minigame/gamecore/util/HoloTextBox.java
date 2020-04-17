package me.gravitinos.minigame.gamecore.util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;

public class HoloTextBox {

    private static final double AS_HEIGHT = 0.25;

    private ArrayList<EntityStore<ArmorStand>> lines = new ArrayList<>();

    private double lineSpacing;
    private Location location;
    private boolean moveUpwards;

    public HoloTextBox(Location location, double lineSpacing, boolean moveUpwards){
        this.location = location;
        this.lineSpacing = lineSpacing;
        this.moveUpwards = moveUpwards;
    }

    /**
     * Set the text of a line
     */
    public void setLine(int line, String text){
        if(this.lines.size() <= line){
            return;
        }

        text = ChatColor.translateAlternateColorCodes('&', text);

        ArmorStand stand = this.lines.get(line).getEntity();
        if(stand == null) {
            this.lines.set(line, new EntityStore<>(ArmorStandFactory.createText(this.getLocationOfLine(line), text)));
            stand = this.lines.get(line).getEntity();
        }
        stand.setVisible(false);
        stand.setCustomName(text);
    }

    /**
     * Removes a line
     */
    public void removeLine(int line){
        if(this.lines.size() <= line){
            return;
        }
        ArmorStand stand = this.lines.get(line).getEntity();
        if(stand != null) stand.remove();
        this.lines.remove(line);
    }

    /**
     * Get the text of all lines
     */
    public ArrayList<String> getLines(){
        ArrayList<String> out = new ArrayList<>();
        Iterator<EntityStore<ArmorStand>> it = this.lines.iterator();
        while (it.hasNext()) {
            EntityStore<ArmorStand> line = it.next();
            ArmorStand stand = line.getEntity();
            stand.setVisible(false);
            if(stand != null){
                out.add(stand.getCustomName());
            } else {
                it.remove();
            }
        }
        return out;
    }

    /**
     * Adds a line
     */
    public void addLine(String text){
        text = ChatColor.translateAlternateColorCodes('&', text);
        ArmorStand stand = ArmorStandFactory.createText(this.getLocationOfLine(this.lines.size()), text);
        stand.setVisible(false);
        EntityStore<ArmorStand> line = new EntityStore<>(stand);
        this.lines.add(line);
    }

    /**
     * Gets the location of the text on a certain line
     * Lines start at 0
     */
    public Location getLocationOfLine(int line){
        Location baseLoc = location.clone().subtract(new Vector(0, AS_HEIGHT, 0));
        return baseLoc.add(new Vector(0, lineSpacing * line * (moveUpwards ? 1 : -1), 0));
    }

    /**
     * Removes all lines
     */
    public void clear(){
        for (EntityStore<ArmorStand> line : this.lines) {
            ArmorStand stand = line.getEntity();
            if(stand != null) stand.remove();
        }
    }

}
