package kernitus.plugin.Hotels.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.jnbt.NBTOutputStream;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.SchematicWriter;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.World;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;

/**
 * @author desht
 * Fixed by kernitus
 *
 * A wrapper class for the WorldEdit terrain loading & saving API to make things a little
 * simple for other plugins to use.
 */
@SuppressWarnings("deprecation")
public class TerrainManager {
	private static final String EXTENSION = "schematic";

	private final WorldEdit we;
	private final LocalSession localSession;
	//private final EditSession editSession;
	private final LocalPlayer localPlayer;

	public TerrainManager(WorldEditPlugin wep, Player player) {
		we = wep.getWorldEdit();
		localPlayer = wep.wrapPlayer(player);
		localSession = we.getSessionManager().get(localPlayer);
		//editSession = localSession.createEditSession(localPlayer);
	}

	public TerrainManager(WorldEditPlugin wep, org.bukkit.World world) {
		we = wep.getWorldEdit();
		localPlayer = null;
		localSession = new LocalSession(we.getConfiguration());
		//editSession = we.getEditSessionFactory().getEditSession(new BukkitWorld(world), we.getConfiguration().maxChangeLimit);
	}

	public void saveTerrain(File saveFile, org.bukkit.World world, Location l1, Location l2) throws FilenameException, DataException, IOException{
		saveTerrain(saveFile, world, getMin(l1, l2), getMax(l1, l2));
	}
	
	public void saveTerrain(File saveFile, org.bukkit.World world, Vector min, Vector max) throws FilenameException, DataException, IOException {
		
		File schemDir = HotelsConfigHandler.getFile("Schematics");
		if(!schemDir.exists()) schemDir.mkdir();

		saveFile = we.getSafeSaveFile(localPlayer,
				saveFile.getParentFile(), saveFile.getName(),
				EXTENSION, new String[] { EXTENSION });

		//editSession.enableQueue();
		Clipboard clipboard = new BlockArrayClipboard(new CuboidRegion(min, max));
		//CuboidClipboard cubclipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
		System.out.println("Clip size: " + max.subtract(min).add(new Vector(1, 1, 1)) );
		System.out.println("Max: " + max + " min: " + min);
		
		//clipboard.copy(editSession);
		SchematicWriter writer = new SchematicWriter(new NBTOutputStream(new FileOutputStream(saveFile)));
		writer.write(clipboard, (new BukkitWorld(world)).getWorldData());
		writer.close();
		//SchematicFormat.MCEDIT.save(clipboard, saveFile);
		//editSession.flushQueue();
	}

	public void loadSchematic(File saveFile, Location loc) throws DataException, IOException, WorldEditException {
		System.out.println("Fine");
		World world = new BukkitWorld(loc.getWorld());
		
		saveFile = we.getSafeSaveFile(localPlayer,
				saveFile.getParentFile(), saveFile.getName(),
				EXTENSION, new String[] { EXTENSION });
		System.out.println("We're doing something");

		EditSession editSession = we.getEditSessionFactory().getEditSession(world, 999999999);
		editSession.enableQueue();
		System.out.println("We got here");

		//SchematicFormat schematic = SchematicFormat.getFormat(saveFile);
		//CuboidClipboard clipboard = schematic.load(saveFile);

		//clipboard.paste(editSession, BukkitUtil.toVector(loc), true);
		//editSession.flushQueue();
		
		//SchematicReader reader = new SchematicReader(new NBTInputStream(new FileInputStream(saveFile)));
		//Clipboard clipboard = reader.read(world.getWorldData());
		
		ClipboardHolder holder = localSession.getClipboard();
		Vector vector = new Vector(loc.getX(), loc.getY(), loc.getZ());
		System.out.println("We at the operation");
		System.out.println("Location: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
		Operation operation = holder.createPaste(editSession, world.getWorldData()).to(vector).ignoreAirBlocks(false).build();
		System.out.println("It's about to happen");
		Operations.complete(operation);
		System.out.println("Stuff happaned");
		
	}

	public void loadSchematic(File saveFile) throws DataException, IOException, WorldEditException {
		System.out.println("OK");
		loadSchematic(saveFile, null);
	}

	private Vector getMin(Location l1, Location l2) {
		return new Vector(
				Math.min(l1.getBlockX(), l2.getBlockX()),
				Math.min(l1.getBlockY(), l2.getBlockY()),
				Math.min(l1.getBlockZ(), l2.getBlockZ())
				);
	}

	private Vector getMax(Location l1, Location l2) {
		return new Vector(
				Math.max(l1.getBlockX(), l2.getBlockX()),
				Math.max(l1.getBlockY(), l2.getBlockY()),
				Math.max(l1.getBlockZ(), l2.getBlockZ())
				);
	}
}