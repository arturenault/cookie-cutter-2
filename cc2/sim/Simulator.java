package cc2.sim;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.tools.*;
import java.awt.Desktop;
import java.util.concurrent.*;

class Simulator {

	private static final String root = "cc2";

	public static void main(String[] args)
	{
		boolean gui = false;
		boolean recompile = false;
		String group_1 = "g0";
		String group_2 = "g0";
		Class <Player> class_1 = null;
		Class <Player> class_2 = null;
		long[] timeout = new long [] {1000, 10000, 1000};
		long gui_refresh = 250;
		try {
			for (int a = 0 ; a != args.length ; ++a)
				if (args[a].equals("-g") || args[a].equals("--groups")) {
					if (a + 2 >= args.length)
						throw new IllegalArgumentException("Missing group names");
					group_1 = args[++a];
					group_2 = args[++a];
				} else if (args[a].equals("--gui-fps")) {
					if (++a == args.length)
						throw new IllegalArgumentException("Missing GUI FPS");
					double gui_fps = Double.parseDouble(args[a]);
					gui_refresh = gui_fps > 0.0 ? (long) Math.round(1000.0 / gui_fps) : -1;
					gui = true;
				} else if (args[a].equals("--cutter-timeout")) {
					if (++a == args.length)
						throw new IllegalArgumentException("Missing cutter timeout");
					timeout[1] = Long.parseLong(args[a]);
				} else if (args[a].equals("--cut-timeout")) {
					if (++a == args.length)
						throw new IllegalArgumentException("Missing cut timeout");
					timeout[2] = Long.parseLong(args[a]);
				} else if (args[a].equals("--gui")) gui = true;
				else throw new IllegalArgumentException("Unknown argument: " + args[a]);
			class_1 = load(group_1);
			class_2 = group_1.equals(group_2) ? class_1 : load(group_2);
		} catch (Exception e) {
			System.err.println("Exception during setup: " + e.getMessage());
			e.printStackTrace();
			System.err.println("Exiting the simulator ...");
			System.exit(1);
		}
		if (!gui)
			System.err.println("GUI: disabled");
		else if (gui_refresh < 0)
			System.err.println("GUI: enabled  (0 FPS)");
		else if (gui_refresh == 0)
			System.err.println("GUI: enabled  (maximum FPS)");
		else {
			double gui_fps = 1000.0 / gui_refresh;
			System.err.println("GUI: enabled  (up to " + gui_fps + " FPS)");
		}
		int[] score = null;
		try {
			score = play(group_1, group_2, class_1, class_2,
			             gui, gui_refresh, timeout, 11, 8, 5);
		} catch (Exception e) {
			System.err.println("Exception during play: " + e.getMessage());
			e.printStackTrace();
			System.err.println("Exiting the simulator ...");
			System.exit(1);
		}
		System.err.println("1st player scored " + score[0]);
		System.err.println("2nd player scored " + score[1]);
		System.exit(0);
	}

	private static int[] play(String group_1,
	                          String group_2,
	                          Class <Player> class_1,
	                          Class <Player> class_2,
	                          boolean gui,
	                          long gui_refresh,
	                          long[] timeout,
	                          int ... cutter_sizes) throws Exception
	{
		Shape[] cutters_retry = new Shape [5];
		List <Shape> cutters_1 = new ArrayList <Shape> ();
		List <Shape> cutters_2 = new ArrayList <Shape> ();
		List <Move> cuts_1 = gui ? new ArrayList <Move> () : null;
		List <Move> cuts_2 = gui ? new ArrayList <Move> () : null;
		// initialize players
		Player[] players = new Player [2];
		Timer[] timer = new Timer [2];
		for (int p = 0 ; p != 2 ; ++p) {
			timer[p] = new Timer();
			timer[p].start();
			final Class <Player> player_class = p == 0 ? class_1 : class_2;
			players[p] = timer[p].call(new Callable <Player> () {

				public Player call() throws Exception
				{
					return player_class.newInstance();
				}
			}, timeout[0]);
		}
		// initialise GUI
		HTTPServer server = null;
		if (gui) {
			server = new HTTPServer();
			System.err.println("HTTP port: " + server.port());
			// try to open web browser automatically
			if (!Desktop.isDesktopSupported())
				System.err.println("Desktop operations not supported");
			else if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
				System.err.println("Desktop browsing not supported");
			else {
				URI uri = new URI("http://localhost:" + server.port());
				Desktop.getDesktop().browse(uri);
			}
			gui(server, state(group_1, 0, 0, cutters_1, cuts_1,
			                  group_2, 0, 0, cutters_2, cuts_2,
			                  gui_refresh));
		}
		// start cutter selection
		Random gen = new Random();
		for (int c = 0 ; c != cutter_sizes.length ; ++c) {
			System.err.println("Cutter size: " + cutter_sizes[c]);
			for (int r = 0 ;; ++r) {
				if (r == cutters_retry.length) {
					// pick randomly
					System.err.println("Choosing cutter_sizes randomly!");
					int i, j;
					do {
						i = gen.nextInt(cutters_retry.length);
						j = gen.nextInt(cutters_retry.length);
					} while (i == j);
					cutters_1.add(cutters_retry[i]);
					cutters_2.add(cutters_retry[j]);
					break;
				}
				for (int p = 0 ; p != 2 ; ++p) {
					// get next cutter of player
					int size = cutter_sizes[c];
					Shape[] your_cutters = (p == 0 ? cutters_1 : cutters_2).toArray(new Shape [0]);
					Shape[] oppo_cutters = (p == 0 ? cutters_2 : cutters_1).toArray(new Shape [0]);
					Player player = players[p];
					Shape shape = timer[p].call(new Callable <Shape> () {

						public Shape call() throws Exception
						{
							return player.cutter(size, your_cutters, oppo_cutters);
						}
					}, timeout[1]);
					// generate shape and check if repeated
					if (shape.size() != size)
						throw new RuntimeException("Invalid cutter size");
					for (int rr = 0 ; rr != r ; ++rr)
						if (shape.equals(cutters_retry[rr]))
							throw new RuntimeException("Repeated cutter");
					List <Shape> cutters = p == 0 ? cutters_1 : cutters_2;
					cutters.add(shape);
					if (!gui) continue;
					gui(server, state(group_1, 0, timer[0].time(), cutters_1, cuts_1,
			    	                  group_2, 0, timer[1].time(), cutters_2, cuts_2,
			    	                  gui_refresh));
				}
				// check if cutters differ
				int i = cutters_1.size() - 1;
				Shape s1 = cutters_1.get(i);
				Shape s2 = cutters_2.get(i);
				if (!s1.equals(s2)) break;
				System.err.println("Same cutter shape: " + s1);
				cutters_1.remove(i);
				cutters_2.remove(i);
				cutters_retry[r] = s1;
			}
			System.err.println("Player 1 cutter: " + cutters_1.get(c));
			System.err.println("Player 2 cutter: " + cutters_2.get(c));
		}
		// initialize score and termination
		int[] score = new int [2];
		boolean[] no_cuts = new boolean [2];
		// initialize dough
		int dough_side = 50;
		Dough dough = new Dough(dough_side);
		System.err.println("Dough cutting begins ...");
		do {
			for (int p = 0 ; p != 2 ; ++p) {
				// skip if no valid cuts
				if (no_cuts[p]) continue;
				// find if there are more valid cuts
				boolean f = false;
				for (int i = 0 ; i != dough_side ; ++i) {
					for (int j = 0 ; j != dough_side ; ++j) {
						Point q = new Point(i, j);
						List <Shape> cutters = p == 0 ? cutters_1 : cutters_2;
						for (Shape s : cutters) {
							for (Shape r : s.rotations()) {
								f = dough.cuts(r, q);
								if (f) break;
							}
							if (f) break;
						}
						if (f) break;
					}
					if (f) break;
				}
				if (!f) {
					no_cuts[p] = true;
					System.err.println("Player " + (p + 1) + " no cut!");
					continue;
				}
				// get cut from player
				int c = cutter_sizes.length;
				Shape[] your_cutters = (p == 0 ? cutters_1 : cutters_2).toArray(new Shape [0]);
				Shape[] oppo_cutters = (p == 0 ? cutters_2 : cutters_1).toArray(new Shape [0]);
				// call the cut() method of player
				Player player = players[p];;
				Move cut = timer[p].call(new Callable <Move> () {

					public Move call() throws Exception
					{
						return player.cut(dough, your_cutters, oppo_cutters);
					}
				}, timeout[2]);
				// check if shape is valid
				List <Shape> cutters = p == 0 ? cutters_1 : cutters_2;
				if (cut.shape < 0 || cut.shape >= cutters.size())
					throw new RuntimeException("Invalid cutter shape");
				Shape shape = cutters.get(cut.shape);
				Shape[] shape_rotations = shape.rotations();
				// check if rotation is valid
				if (cut.rotation < 0 || cut.rotation >= shape_rotations.length)
					throw new RuntimeException("Invalid cutter rotation");
				shape = shape_rotations[cut.rotation];
				// validate first cut of first player
				if (score[0] + score[1] == 0) {
					int min_cutter_size = Integer.MAX_VALUE;
					for (int cutter_size : cutter_sizes)
						if (min_cutter_size > cutter_size)
							min_cutter_size = cutter_size;
					if (shape.size() != min_cutter_size)
						throw new RuntimeException("Invalid initial cut size: "
						  + shape.size() + " (should be " + min_cutter_size + ")");
				}
				// cut a piece and update score
				if (!dough.cut(shape, cut.point))
					throw new RuntimeException("Invalid cut");
				score[p] += shape.size();
				System.err.println("Player " + (p + 1) + " cut "
				                             + shape.size() + " pieces!");
				if (!gui) continue;
				List <Move> cuts = p == 0 ? cuts_1 : cuts_2;
				cuts.add(cut);
				gui(server, state(group_1, score[0], timer[0].time(), cutters_1, cuts_1,
			                      group_2, score[1], timer[1].time(), cutters_2, cuts_2,
			                      gui_refresh));
			}
		} while (!no_cuts[0] || !no_cuts[1]);
		// final GUI frame
		if (gui) {
			gui_refresh = -1;
			gui(server, state(group_1, score[0], timer[0].time(), cutters_1, cuts_1,
			    	          group_2, score[1], timer[1].time(), cutters_2, cuts_2,
			    	          gui_refresh));
			server.close();
		}
		return score;
	}

	public static String state(String group_1, int score_1, long cpu_1, List <Shape> cutters_1, List <Move> cuts_1,
	                           String group_2, int score_2, long cpu_2, List <Shape> cutters_2, List <Move> cuts_2,
	                           long gui_refresh)
	{
		StringBuffer buf = new StringBuffer();
		buf.append(group_1 + ", " + score_1 + ", " + human_no_power(cpu_1 / 1.0e9, 2)
		                   + ", " + cutters_1.size() + ", " + cuts_1.size() + "\n");
		buf.append(group_2 + ", " + score_2 + ", " + human_no_power(cpu_2 / 1.0e9, 2)
		                   + ", " + cutters_2.size() + ", " + cuts_2.size() + "\n");
		// send cutters
		for (Shape s : cutters_1) {
			buf.append(s.toString(new Point(0, 0), false));
			buf.append("\n");
		}
		for (Shape s : cutters_2) {
			buf.append(s.toString(new Point(0, 0), false));
			buf.append("\n");
		}
		// send cuts
		for (Move m : cuts_1) {
			Shape s = cutters_1.get(m.shape).rotations()[m.rotation];
			buf.append(s.toString(m.point, false));
			buf.append("\n");
		}
		for (Move m : cuts_2) {
			Shape s = cutters_2.get(m.shape).rotations()[m.rotation];
			buf.append(s.toString(m.point, false));
			buf.append("\n");
		}
		buf.append(gui_refresh);
		return buf.toString();
	}

	public static void gui(HTTPServer server, String content)
	                       throws UnknownServiceException
	{
		String path = null;
		for (;;) {
			// get request
			for (;;)
				try {
					path = server.request();
					break;
				} catch (IOException e) {
					System.err.println("HTTP request error: " + e.getMessage());
				}
			// dynamic content
			if (path.equals("data.txt")) {
				// send dynamic content
				try {
					server.reply(content);
					return;
				} catch (IOException e) {
					System.err.println("HTTP dynamic reply error: " + e.getMessage());
					continue;
				}
			}
			// static content
			if (path.equals("")) path = "webpage.html";
			else if (!path.equals("favicon.ico") &&
			         !path.equals("apple-touch-icon.png") &&
			         !path.equals("script.js")) break;
			// send file
			File file = new File(root + File.separator + "sim"
			                          + File.separator + path);
			try {
				server.reply(file);
			} catch (IOException e) {
				System.err.println("HTTP static reply error: " + e.getMessage());
			}
		}
		if (path == null)
			throw new UnknownServiceException("Unknown HTTP request (null path)");
		else
			throw new UnknownServiceException("Unknown HTTP request: \"" + path + "\"");
	}

	// scan directory (and subdirectories) for files with given extension
	private static Set <File> directory(String path, String extension)
	{
		Set <File> files = new HashSet <File> ();
		Set <File> prev_dirs = new HashSet <File> ();
		prev_dirs.add(new File(path));
		do {
			Set <File> next_dirs = new HashSet <File> ();
			for (File dir : prev_dirs)
				for (File file : dir.listFiles())
					if (!file.canRead()) ;
					else if (file.isDirectory())
						next_dirs.add(file);
					else if (file.getPath().endsWith(extension))
						files.add(file);
			prev_dirs = next_dirs;
		} while (!prev_dirs.isEmpty());
		return files;
	}

	// last modified
	private static long last_modified(Iterable <File> files)
	{
		long last_date = 0;
		for (File file : files) {
			long date = file.lastModified();
			if (last_date < date)
				last_date = date;
		}
		return last_date;
	}

	// compile and load
	private static Class <Player> load(String group) throws IOException,
	                                       ReflectiveOperationException
	{
		String sep = File.separator;
		Set <File> player_files = directory(root + sep + group, ".java");
		File class_file = new File(root + sep + group + sep + "Player.class");
		long class_modified = class_file.exists() ? class_file.lastModified() : -1;
		if (class_modified < 0 || class_modified < last_modified(player_files) ||
		    class_modified < last_modified(directory(root + sep + "sim", ".java"))) {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			if (compiler == null)
				throw new IOException("Cannot find Java compiler");
			StandardJavaFileManager manager = compiler.
			                        getStandardFileManager(null, null, null);
			long files = player_files.size();
			System.err.print("Compiling " + files + " .java files ... ");
			if (!compiler.getTask(null, manager, null, null, null,
			     manager.getJavaFileObjectsFromFiles(player_files)).call())
				throw new IOException("Compilation failed");
			System.err.println("done!");
			class_file = new File(root + sep + group + sep + "Player.class");
			if (!class_file.exists())
				throw new FileNotFoundException("Missing class file");
		}
		ClassLoader loader = ToolProvider.getSystemToolClassLoader();
		if (loader == null)
			throw new IOException("Cannot find Java class loader");
		@SuppressWarnings("rawtypes")
		Class raw_class = loader.loadClass(root + "." + group + ".Player");
		@SuppressWarnings("unchecked")
		Class <Player> player_class = raw_class;
		return player_class;
	}

	// parse a real number and cut the number of decimals
	private static String human_no_power(double x, int d)
	{
		if (x == 0.0) return "0";
		if (d < 0) throw new IllegalArgumentException();
		int e = 1;
		double b = 10.0;
		while (b <= x) {
			b *= 10.0;
			e++;
		}
		StringBuffer buf = new StringBuffer();
		do {
			b *= 0.1;
			int i = (int) (x / b);
			x -= b * i;
			if (e == 0) buf.append(".");
			buf.append(i);
		} while (--e != -d);
		return buf.toString();
	}
}
