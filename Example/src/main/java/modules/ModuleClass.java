package modules;

import com.github.atomishere.modules.api.CommandModule;
import com.github.atomishere.modules.api.ModuleData;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class ModuleClass extends CommandModule {
    public ModuleClass(ModuleData data) {
        super("moduleexample", "An example for the Modules plugin", "Usage: /moduleexample", new ArrayList<String>(), data);
    }

    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        commandSender.sendMessage("It works!");
        return true;
    }
}
