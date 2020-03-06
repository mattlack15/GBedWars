package me.gravitinos.bedwars.gamecore.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    @NotNull
    @Contract("_ -> new")
    public static <T> List<T> clone(List<T> in) {
        return new ArrayList<>(in);
    }
}
