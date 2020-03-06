package me.gravitinos.bedwars.gamecore.util;

import com.google.common.collect.Lists;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class ChestUtils {
    private static Random rand = new Random(System.currentTimeMillis());

    public static ItemStack[] getRandomChestContents(int invSize, int maxItems, int minItems, Map<ItemStack, Float> itemsAndChances){
        ItemStack items[] = new ItemStack[invSize];

        ArrayList<ItemStack> possibleItems = Lists.newArrayList(itemsAndChances.keySet());

        ArrayList<Integer> usedSpaces = new ArrayList<>();

        for(int i = 0; i < maxItems; i++){
            possibleItems = Lists.newArrayList(itemsAndChances.keySet());
            int size = possibleItems.size();
            boolean itemFound = false;
            while(possibleItems.size() > 0){
                int item = rand.nextInt(possibleItems.size());
                if(rand.nextFloat() < itemsAndChances.get(possibleItems.get(item))){
                    int randSpace = rand.nextInt(invSize);
                    int tries = 0;
                    while(usedSpaces.contains(randSpace) && tries < invSize){
                        randSpace = rand.nextInt(invSize);
                    }
                    usedSpaces.add(randSpace);
                    items[randSpace] = possibleItems.get(item);
                    itemFound = true;
                    possibleItems.remove(item);
                    break;
                }
                possibleItems.remove(item);
            }
            if(!itemFound && i < minItems){
                if(possibleItems.size() == 0){
                    possibleItems = Lists.newArrayList(itemsAndChances.keySet());
                }
                i--;
            }
        }
        return items;

    }

}
