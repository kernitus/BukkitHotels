package kernitus.plugin.Hotels.managers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector2D;
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
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

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

	public TerrainManager(Player player) {
		WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		we = wep.getWorldEdit();
		localPlayer = wep.wrapPlayer(player);
	}

	public TerrainManager(org.bukkit.World world) {
		we = ((WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit")).getWorldEdit();
		localPlayer = null;
	}
	public void saveTerrain(File saveFile, org.bukkit.World world, Location l1, Location l2) throws DataException, IOException, WorldEditException {
		World bworld = new BukkitWorld(world);
		Vector origin = getMin(l1, l2);
		CuboidRegion selection = new CuboidRegion(bworld, origin, getMax(l1, l2));
		saveTerrain(saveFile, bworld, selection, origin);
	}
	public void saveTerrain(File saveFile, org.bukkit.World world, List<BlockVector2D> points, int minY, int maxY) throws DataException, IOException, WorldEditException {
		World bworld = new BukkitWorld(world);
		Polygonal2DRegion selection = new Polygonal2DRegion(bworld, points, minY, maxY);
		
		Mes.debug("Printing out points:");
		for(BlockVector2D point : points){
			System.out.println(point);
		}
		
		Vector origin = points.get(0).toVector();
		saveTerrain(saveFile, bworld, selection, origin);
	}
	public void saveTerrain(File saveFile, org.bukkit.World world, Region selection) throws DataException, IOException, WorldEditException {
		World bworld = new BukkitWorld(world);
		saveTerrain(saveFile, bworld, selection, getOriginFromRegion(selection));
	}
	public void saveTerrain(File saveFile, org.bukkit.World world, ProtectedRegion selection) throws DataException, IOException, WorldEditException {
		World bworld = new BukkitWorld(world);
		Region region = getRegionFromProtectedRegion(world, selection);
		Vector origin = getOriginFromRegion(region);
		saveTerrain(saveFile, bworld, region, origin);
	}
	public void saveTerrain(File saveFile, World world, Region selection, Vector origin) throws DataException, IOException, WorldEditException {
		File schemDir = HotelsConfigHandler.getFile("Schematics");
		if(!schemDir.exists()) schemDir.mkdirs();

		saveFile = we.getSafeSaveFile(localPlayer, saveFile.getParentFile(), saveFile.getName(), EXTENSION, new String[] { EXTENSION });

		WorldData worldData = world.getWorldData();

		EditSession editSession = we.getEditSessionFactory().getEditSession(world, 999999999);
		BlockArrayClipboard clipboard = new BlockArrayClipboard(selection);
		clipboard.setOrigin(origin);
		Mes.debug("Clipboard DIMENSIONS: " + clipboard.getDimensions());
		Mes.debug("Region area: " + clipboard.getRegion().getArea());
		
		ForwardExtentCopy copy = new ForwardExtentCopy(editSession, selection, clipboard, origin);
		Operations.complete(copy);
		Closer closer = Closer.create();
		FileOutputStream fos = closer.register(new FileOutputStream(saveFile));
		BufferedOutputStream bos = closer.register(new BufferedOutputStream(fos));
		ClipboardWriter writer = closer.register(ClipboardFormat.SCHEMATIC.getWriter(bos));
		writer.write(clipboard, worldData);
		closer.close();
	}

	public void loadSchematic(File saveFile, Location loc) throws DataException, IOException, WorldEditException {
		World world = new BukkitWorld(loc.getWorld());

		saveFile = we.getSafeSaveFile(localPlayer, saveFile.getParentFile(), saveFile.getName(), EXTENSION, new String[] { EXTENSION });

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
	public Region getRegionFromProtectedRegion(org.bukkit.World world, ProtectedRegion pregion) throws RegionOperationException {
		World bworld = new BukkitWorld(world);
		Region region = null;
		switch(pregion.getType()){

		case CUBOID:
			region = new CuboidRegion(bworld, pregion.getMinimumPoint(), pregion.getMaximumPoint());
			break;

		case POLYGON:
			region = new Polygonal2DRegion(bworld, pregion.getPoints(), pregion.getMinimumPoint().getBlockY(), pregion.getMaximumPoint().getBlockY());
			break;

		default: throw new RegionOperationException("Region is neither Cuboid or Polygonal");
		}
		Mes.debug("Region min is: " + region.getMinimumPoint() + " max is: " + pregion.getMaximumPoint());
		return region;
	}
	public Vector getOriginFromRegion(Region region) throws RegionOperationException{
		Vector origin = null;
		if(region instanceof CuboidRegion)
			origin = region.getMinimumPoint();
		else if(region instanceof Polygonal2DRegion)
			origin = ((Polygonal2DRegion) region).getPoints().get(0).toVector();
		else throw new RegionOperationException("Region is neither Cuboid or Polygonal");
		Mes.debug("Origin IS: " + origin.getBlockX() +" " + origin.getBlockY() + " " + origin.getBlockZ());
		return origin;
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