package modules;

import com.github.atomishere.modules.api.CommandModule;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class ModuleClass extends CommandModule {
    protected ModuleClass() {
        super("moduleexample", "An example for the Modules plugin", "Usage: /moduleexample", new ArrayList<String>());
    }

    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        commandSender.sendMessage("It works!");
        return true;
    }
}
