package kernitus.plugin.Hotels.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;

/**
 * @author desht
 * Updated by kernitus
 *
 * A wrapper class for the WorldEdit terrain loading & saving API to make things a little
 * simple for other plugins to use.
 */
@SuppressWarnings("deprecation")
public class TerrainManager {
	private static final String EXTENSION = "schematic";

	private final WorldEdit we;
	//private final LocalSession localSession;
	//private final EditSession editSession;
	private final LocalPlayer localPlayer;

	public TerrainManager(WorldEditPlugin wep, Player player) {
		we = wep.getWorldEdit();
		localPlayer = wep.wrapPlayer(player);
		//localSession = we.getSessionManager().get(localPlayer);
		//editSession = localSession.createEditSession(localPlayer);
	}

	public TerrainManager(WorldEditPlugin wep, org.bukkit.World world) {
		we = wep.getWorldEdit();
		localPlayer = null;
		//slocalSession = new LocalSession(we.getConfiguration());
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

		WorldData worldData = new BukkitWorld(world).getWorldData();
		Clipboard clipboard = new BlockArrayClipboard(new CuboidRegion(min, max));
		System.out.println("Clipboard dimensions: " + clipboard.getDimensions());
		
		OutputStream out = new FileOutputStream(saveFile);
		ClipboardWriter clipWriter = ClipboardFormat.SCHEMATIC.getWriter(out);
		clipWriter.write(clipboard, worldData);
		clipWriter.close();
		
		//editSession.enableQueue();

		//CuboidClipboard cubclipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
		System.out.println("Clip size: " + max.subtract(min).add(new Vector(1, 1, 1)) );
		System.out.println("Max: " + max + " min: " + min);

		/*SchematicWriter writer = new SchematicWriter(new NBTOutputStream(new FileOutputStream(saveFile)));
		writer.write(clipboard, worldData);
		writer.close();*/
		
		//clipboard.copy(editSession);
		//SchematicFormat.MCEDIT.save(clipboard, saveFile);
		//editSession.flushQueue();
	}

	public void loadSchematic(File saveFile, Location loc) throws DataException, IOException, WorldEditException {
		System.out.println("World name is: " + loc.getWorld().getName());
		World world = new BukkitWorld(loc.getWorld());

		saveFile = we.getSafeSaveFile(localPlayer,
				saveFile.getParentFile(), saveFile.getName(),
				EXTENSION, new String[] { EXTENSION });
		System.out.println("We're doing something");

		//EditSession editSession = we.getEditSessionFactory().getEditSession(world, 999999999);
		//editSession.enableQueue();
		//System.out.println("We got here");

		//SchematicFormat schematic = SchematicFormat.getFormat(saveFile);
		//CuboidClipboard clipboard = schematic.load(saveFile);

		//clipboard.paste(editSession, BukkitUtil.toVector(loc), true);
		//editSession.flushQueue();

		//SchematicReader reader = new SchematicReader(new NBTInputStream(new FileInputStream(saveFile)));
		//Clipboard clipboard = reader.read(world.getWorldData());

		/*Vector vector = new Vector(loc.getX(), loc.getY(), loc.getZ());
		System.out.println("We at the operation");
		System.out.println("Location: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
		Clipboard board = new BlockArrayClipboard();
		ClipboardHolder holda = new ClipboardHolder(board, world.getWorldData());
		localSession.setClipboard(holda);
		ClipboardHolder holder = localSession.getClipboard();
		Operation operation = holder.createPaste(editSession, world.getWorldData()).to(vector).ignoreAirBlocks(false).build();
		System.out.println("It's about to happen");
		Operations.complete(operation);
		System.out.println("Stuff happaned");*/

		InputStream in = new FileInputStream(saveFile);
		System.out.println("SAVE FILE NAME: " + saveFile.getName() + " Type: " + ClipboardFormat.findByFile(saveFile));
		ClipboardReader reader = ClipboardFormat.SCHEMATIC.getReader(in);
		WorldData worldData = world.getWorldData();
		Clipboard clipboard = reader.read(worldData);
		ClipboardHolder holder = new ClipboardHolder(clipboard, worldData);
		EditSession editSession = we.getEditSessionFactory().getEditSession(world, 999999999);
		editSession.enableQueue();
		editSession.setFastMode(true);
		Vector vector = new Vector(loc.getX(), loc.getY(), loc.getZ());
		Operation operation = holder.createPaste(editSession, worldData).to(vector).ignoreAirBlocks(false).build();
		Operations.complete(operation);
		editSession.flushQueue();
		editSession.commit();
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