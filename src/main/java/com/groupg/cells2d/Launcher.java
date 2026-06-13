package com.groupg.cells2d;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.util.Objects;

import com.groupg.cells2d.view.Login;


public class Launcher {
    @Parameter(names = {"--console", "-c"}, description = "Launch in console mode")
    private boolean console = false;

    @Parameter(names = {"--help", "-h"}, description = "Show help")
    private boolean help = false;

    @Parameter(names = {"--save", "-s"}, description = "File to save simulation (in console mode)")
    private String savePath = "console-save.dat";

    @Parameter(names = {"--load", "-l"}, description = "File to load simulation from (in console mode)")
    private String loadPath = "";

    @Parameter(names = {"--skipsave", "-S"}, description = "Skip saving at end of simulation (in console mode)")
    private boolean skipSave = false;

    public static void main(String[] args) {
        Launcher launcher = new Launcher();
        JCommander jc = JCommander.newBuilder()
                .addObject(launcher)
                .build();

        try {
            jc.parse(args);

            if (launcher.help) {
                System.out.println("Cells2D PGL");
                jc.usage();
                System.out.println("Veuillez vous référer au README.md pour les instructions.");
            } else if (launcher.console) {
                ConsoleLauncher console = new ConsoleLauncher();
                if(Objects.equals(launcher.loadPath, "")) {
                    console.populate();
                } else {
                    console.load(launcher.loadPath);
                }
                console.run();
                if(!launcher.skipSave) {
                    console.save(launcher.savePath);
                }
            } else {
                // Pass all original args to Login
                Login.main(args);
            }

        } catch (ParameterException e) {
            System.err.println("Error: " + e.getMessage());
            jc.usage();
        }
    }
}