package dev.thatsmybaby.shared.command;

import cn.nukkit.command.CommandSender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor @Getter
public abstract class Argument {

    private final String name;
    private final String description;
    private final String permission;
    private String[] aliases;

    public Argument(String name, String description, String permission, String... aliases) {
        this.name = name;

        this.description = description;

        this.permission = permission;

        this.aliases = aliases;
    }

    public boolean equals(String argumentName) {
        return this.name.equalsIgnoreCase(argumentName) || Arrays.stream(this.aliases).anyMatch(alias -> alias.equalsIgnoreCase(argumentName));
    }

    public abstract void execute(CommandSender sender, String commandLabel, String argumentLabel, String[] args);
}