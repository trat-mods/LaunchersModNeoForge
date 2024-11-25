package net.launchers.mod.initializer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.launchers.mod.block.abstraction.AbstractLauncherBlock;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class LCommands {
    private static final String LAUNCHER_ID = "l";
    private static final String P_LAUNCHER_ID = "p";
    private static final String E_LAUNCHER_ID = "e";

    public static void onCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        ArrayList<String> launchersList = new ArrayList<>();
        dispatcher.register(Commands.literal("lcalc").then(Commands.argument("first", word()).suggests(suggestedStrings()).executes(ctx -> {
            launchersList.add(getString(ctx, "first"));
            return checkIdsAndPrintForce(launchersList, ctx.getSource());
        }).then(Commands.argument("second", word()).suggests(suggestedStrings()).executes(ctx -> {
            launchersList.add(getString(ctx, "first"));
            launchersList.add(getString(ctx, "second"));
            return checkIdsAndPrintForce(launchersList, ctx.getSource());
        }).then(Commands.argument("third", word()).suggests(suggestedStrings()).executes(ctx -> {
            launchersList.add(getString(ctx, "first"));
            launchersList.add(getString(ctx, "second"));
            launchersList.add(getString(ctx, "third"));
            return checkIdsAndPrintForce(launchersList, ctx.getSource());
        }).then(Commands.argument("fourth", word()).suggests(suggestedStrings()).executes(ctx -> {
            launchersList.add(getString(ctx, "first"));
            launchersList.add(getString(ctx, "second"));
            launchersList.add(getString(ctx, "third"));
            launchersList.add(getString(ctx, "fourth"));
            return checkIdsAndPrintForce(launchersList, ctx.getSource());
        }))))).executes(ctx -> checkIdsAndPrintForce(launchersList, ctx.getSource())));
    }

    public static void register() {

        NeoForge.EVENT_BUS.addListener(LCommands::onCommands);
    }

    private static boolean areLauncherIdValid(ArrayList<String> ids) {
        for (String current : ids) {
            if (!current.equals("l") && !current.equals("p") && !current.equals("e")) {
                return false;
            }
        }
        return !ids.isEmpty();
    }

    private static float getStackForceByLauncherId(String id) {
        return switch (id) {
            case LAUNCHER_ID -> ((AbstractLauncherBlock) LBlocks.LAUNCHER_BLOCK.get()).stackMultiplier;
            case P_LAUNCHER_ID -> ((AbstractLauncherBlock) LBlocks.POWERED_LAUNCHER_BLOCK.get()).stackMultiplier;
            case E_LAUNCHER_ID -> ((AbstractLauncherBlock) LBlocks.EXTREME_LAUNCHER_BLOCK.get()).stackMultiplier;
            default -> 0F;
        };
    }

    private static float getBaseForceByLauncherId(String id) {
        return switch (id) {
            case LAUNCHER_ID -> ((AbstractLauncherBlock) LBlocks.LAUNCHER_BLOCK.get()).baseMultiplier;
            case P_LAUNCHER_ID -> ((AbstractLauncherBlock) LBlocks.POWERED_LAUNCHER_BLOCK.get()).baseMultiplier;
            case E_LAUNCHER_ID -> ((AbstractLauncherBlock) LBlocks.EXTREME_LAUNCHER_BLOCK.get()).baseMultiplier;
            default -> 0F;
        };
    }

    private static float getForce(ArrayList<String> ids) {
        float base = getBaseForceByLauncherId(ids.get(0));
        float multiplier = 1F;
        for (int i = 1; i < ids.size(); i++) {
            multiplier += getStackForceByLauncherId(ids.get(i));
        }
        return base * multiplier;
    }

    private static int checkIdsAndPrintForce(ArrayList<String> launchersList, CommandSourceStack context) {
        if (!areLauncherIdValid(launchersList)) {
            context.sendSystemMessage(Component.literal("""
                                                                One or more parameters are not correct.
                                                                Usage:
                                                                l: Launcher
                                                                p: Powered Launcher
                                                                e: Extreme Launcher
                                                                """));
            launchersList.clear();
            return 0;
        }
        StringBuilder printString = new StringBuilder("\nStack: \n");
        for (String s : launchersList) {
            printString.append(s).append("\n");
        }
        var force = getForce(launchersList);
        var height = heightFromForce(force);
        printString.append("Force: ").append(force).append("\n").append("Expected height ±5 blocks: ").append(Math.floor(height));
        context.sendSystemMessage(Component.literal(printString.toString()));
        launchersList.clear();
        return 1;
    }

    private static float heightFromForce(float force) {
        return (float) (0.005 * Math.pow(force, 4) - 0.225 * Math.pow(force, 3) + 3.956 * Math.pow(force, 2) + 2.882 * force + 1.62);
    }

    public static SuggestionProvider<CommandSourceStack> suggestedStrings() {
        ArrayList<String> suggestions = new ArrayList<>();
        suggestions.add(LAUNCHER_ID);
        suggestions.add(P_LAUNCHER_ID);
        suggestions.add(E_LAUNCHER_ID);
        return (ctx, builder) -> getSuggestionsBuilder(builder, suggestions);
    }

    private static CompletableFuture<Suggestions> getSuggestionsBuilder(SuggestionsBuilder builder, List<String> list) {
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

        if (list.isEmpty()) { // If the list is empty then return no suggestions
            return Suggestions.empty(); // No suggestions
        }

        for (String str : list) { // Iterate through the supplied list
            if (str.toLowerCase(Locale.ROOT).startsWith(remaining)) {
                builder.suggest(str); // Add every single entry to suggestions list.
            }
        }
        return builder.buildFuture(); // Create the CompletableFuture containing all the suggestions
    }
}

