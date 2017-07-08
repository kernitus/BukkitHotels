package kernitus.plugin.Hotels.utilities;

import org.bukkit.command.CommandSender;

import java.util.function.BiConsumer;

/**
 * Each subcommand of the /hotels command
 * corresponding to the first argument
 */
public class HTSubCommand {

    private String subSubCommand;
    private boolean needsPlayer;
    private String permission;
    private String[] labels;
    private int minArgs;
    private BiConsumer<CommandSender, String[]> method;

    /**
     * @param permission Permission for subcommand
     * @param minArgs Minimum amount of arguments required, including label
     * @param method Method that takes arguments and executes code corresponding to subcommand
     * @param labels Subcommand label (e.g. list) and any aliases
     */
    public HTSubCommand(String permission, int minArgs, BiConsumer<CommandSender, String[]> method, String... labels) {
        this(null, false, permission, minArgs, method, labels);
    }

    /**
     * @param needsPlayer Whether the command requires a player
     * @param permission Permission for subcommand
     * @param minArgs Minimum amount of arguments required, including label
     * @param method Method that takes arguments and executes code corresponding to subcommand
     * @param labels Subcommand label (e.g. list) and any aliases
     */
    public HTSubCommand(boolean needsPlayer, String permission, int minArgs, BiConsumer<CommandSender, String[]> method, String... labels) {
        this(null, needsPlayer, permission, minArgs, method, labels);
    }

    /**
     * @param subSubCommand Sub-command of this subcommand
     * @param permission Permission for subcommand
     * @param minArgs Minimum amount of arguments required, including label
     * @param method Method that takes arguments and executes code corresponding to subcommand
     * @param labels Subcommand label (e.g. list) and any aliases
     */
    public HTSubCommand(String subSubCommand, String permission, int minArgs, BiConsumer<CommandSender, String[]> method, String... labels) {
        this(subSubCommand, false, permission, minArgs, method, labels);
    }

    /**
     *  @param subSubCommand Sub-command of this subcommand
     * @param needsPlayer Whether the command requires a player
     * @param permission Permission for subcommand
     * @param minArgs Minimum amount of arguments required, including label
     * @param method Method that takes arguments and executes code corresponding to subcommand
     * @param labels Subcommand label (e.g. list) and any aliases
     */
    public HTSubCommand(String subSubCommand, boolean needsPlayer, String permission, int minArgs, BiConsumer<CommandSender, String[]> method, String... labels){
        this.needsPlayer = needsPlayer;
        this.permission = permission;
        this.minArgs = minArgs;
        this.method = method;
        this.labels = labels;

        //Sub-sub-command
        this.subSubCommand = subSubCommand;
    }

    public String[] getLabels() {
        return labels;
    }
    public boolean isAlias(String alias){
        if(alias == null) return false;
        for (String label : labels)
            if(label.equalsIgnoreCase(alias))
                return true;
        return false;
    }

    public boolean hasSubSubCommand(){
        return subSubCommand != null && !subSubCommand.isEmpty();
    }
    public String getSubSubCommand(){
        return subSubCommand;
    }
    public boolean isSubSubCommand(String subSubCommand){
        if(!hasSubSubCommand()) return false;
        return subSubCommand.equalsIgnoreCase(this.subSubCommand);
    }

    public String getPermission() {
        return permission;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public boolean needsPlayer(){
        return needsPlayer;
    }

    public void run(CommandSender sender, String... args){
        method.accept(sender, args);
    }
}
