package com.mythmc.file;

import com.mythmc.MineBBSTopper;
import com.mythmc.file.statics.ConfigFile;
import com.mythmc.file.statics.GUIFile;
import com.mythmc.file.statics.HologramFile;
import com.mythmc.file.statics.LangFile;
import com.mythmc.tools.Debugger;

public class FileManager {
    private final MineBBSTopper plugin;

    public FileManager(MineBBSTopper plugin) {
        this.plugin = plugin;
    }
    public void load() {
        (new ConfigFile()).load();
        (new GUIFile()).load();
        (new LangFile()).load();
        (new Debugger()).load();
        (new HologramFile()).load();
    }

}
