package kernitus.plugin.Hotels.managers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.EditSession;
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
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;

/**
 * @author desht
 * Heavily modified by kernitus
 * to use only WE 6+ API
 *
 * A wrapper class for the WorldEdit terrain loading & saving API to make things a little
 * simple for other plugins to use.
 */
public class TerrainManager {
	private static final String EXTENSION = "schematic";

	private final WorldEdit we;
	private final com.sk89q.worldedit.entity.Player localPlayer;

	public TerrainManager(WorldEditPlugin wep, Player player) {
		we = wep.getWorldEdit();
		localPlayer = wep.wrapPlayer(player);
	}

	public TerrainManager(WorldEditPlugin wep, org.bukkit.World world) {
		we = wep.getWorldEdit();
		localPlayer = null;
	}

	public void saveTerrain(File saveFile, org.bukkit.World world, Location l1, Location l2) throws DataException, IOException, WorldEditException{
		saveTerrain(saveFile, world, getMin(l1, l2), getMax(l1, l2));
	}

	public void saveTerrain(File saveFile, org.bukkit.World world, Vector min, Vector max) throws DataException, IOException, WorldEditException {

		File schemDir = HotelsConfigHandler.getFile("Schematics");
		if(!schemDir.exists()) schemDir.mkdir();

		saveFile = we.getSafeSaveFile(localPlayer,
				saveFile.getParentFile(), saveFile.getName(),
				EXTENSION, new String[] { EXTENSION });

		World bworld = new BukkitWorld(world);
		WorldData worldData = bworld.getWorldData();

		EditSession editSession = we.getEditSessionFactory().getEditSession(bworld, 999999999);
		CuboidRegion selection = new CuboidRegion(bworld, min, max);
		BlockArrayClipboard clipboard = new BlockArrayClipboard(selection);
		clipboard.setOrigin(min);
		ForwardExtentCopy copy = new ForwardExtentCopy(editSession, new CuboidRegion(bworld, min, max), clipboard, min);
		Operations.complete(copy);
		Closer closer = Closer.create();
		FileOutputStream fos = closer.register(new FileOutputStream(saveFile));
		BufferedOutputStream bos = closer.register(new BufferedOutputStream(fos));
		ClipboardWriter writer = closer.register(ClipboardFormat.SCHEMATIC.getWriter(bos));
		writer.write(clipboard, worldData);
		closer.close();
	}

	public void loadSchematic(File saveFile, Location loc) throws DataException, IOException, WorldEditException {
		System.out.println("World name is: " + loc.getWorld().getName());
		World world = new BukkitWorld(loc.getWorld());

		saveFile = we.getSafeSaveFile(localPlayer,
				saveFile.getParentFile(), saveFile.getName(),
				EXTENSION, new String[] { EXTENSION });

		InputStream in = new FileInputStream(saveFile);
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