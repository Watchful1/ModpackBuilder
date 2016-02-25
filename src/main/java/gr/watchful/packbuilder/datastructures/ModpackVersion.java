package gr.watchful.packbuilder.datastructures;

import java.util.ArrayList;

/**
 * A specific working instance of a modpack
 * Contains mods, configs and all the other necessary files inside the minecraft folder for a modpack to work
 */
public class ModpackVersion {
    private ArrayList<Mod> mods;
    private Modpack parentModpack;
        // not entirely sure this is the right way to implement this, but I really don't want to
        // pass it around everywhere

}
