package cc2.g1;

import cc2.sim.Point;
import cc2.sim.Shape;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnemyPredict {
    public Map<Integer, ArrayList<ArrayList<Shape>>> map = new HashMap<>();
    int[] priority = {0, 16, 12, 7, 4, 12, 15, 4};
    int enemy = -1;

    EnemyPredict(String str) {
        String path = "cc2/g1/";
        File fp = new File(path + str);
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(fp));
            String text;

            for (int i = 1; i < 10; i++) {
                map.put(i, new ArrayList<>());
            }

            while ((text = reader.readLine()) != null) {
                String[] parts = text.split("[|]");

                ArrayList<Shape> game = new ArrayList<>(3);
                Pattern ptn = Pattern.compile("(\\d+)([,] )(\\d+)");
                for (int i = 9; i < 12; i++) {
                    Matcher mat = ptn.matcher(parts[i]);
                    ArrayList<Point> pts = new ArrayList<>();
                    while (mat.find()) {
                        pts.add(new Point(Integer.parseInt(mat.group(1)), Integer.parseInt(mat.group(3))));
                    }
                    game.add(new Shape(pts.toArray(new Point[pts.size()])));
                }
                map.get(Integer.parseInt(parts[3])).add(game);

                game = new ArrayList<>(3);
                for (int i = 12; i < 15; i++) {
                    Matcher mat = ptn.matcher(parts[i]);
                    ArrayList<Point> pts = new ArrayList<>();
                    while (mat.find()) {
                        pts.add(new Point(Integer.parseInt(mat.group(1)), Integer.parseInt(mat.group(3))));
                    }
                    game.add(new Shape(pts.toArray(new Point[pts.size()])));
                }
                map.get(Integer.parseInt(parts[4])).add(game);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public int predict(Shape[] shapes) {
        ArrayList<ArrayList<Integer>> rank = new ArrayList<>();
        for (int j = 0; j < 7; j++) {
            rank.add(new ArrayList<>());
        }

        for (int i = 1; i < 10; i++) {
            for (int j = 0; j < map.get(i).size(); j++) {
                int[] matched = {0, 0, 0};
                matched[0] = map.get(i).get(j).get(0).equals(shapes[0]) ? 1 : 0;
                matched[1] = map.get(i).get(j).get(1).equals(shapes[1]) ? 1 : 0;
                matched[2] = map.get(i).get(j).get(2).equals(shapes[2]) ? 1 : 0;

                if ((matched[0] & matched[1] & matched[2]) == 1) {
                    if (rank.get(0).indexOf(i) == -1) {
                        rank.get(0).add(i);
                    }
                } else if ((matched[0] & matched[1]) == 1) {
                    if (rank.get(1).indexOf(i) == -1) {
                        rank.get(1).add(i);
                    }
                } else if ((matched[0] & matched[2]) == 1) {
                    if (rank.get(2).indexOf(i) == -1) {
                        rank.get(2).add(i);
                    }
                } else if ((matched[1] & matched[2]) == 1) {
                    if (rank.get(3).indexOf(i) == -1) {
                        rank.get(3).add(i);
                    }
                } else if ((matched[0]) == 1) {
                    if (rank.get(4).indexOf(i) == -1) {
                        rank.get(4).add(i);
                    }
                } else if ((matched[1]) == 1) {
                    if (rank.get(5).indexOf(i) == -1) {
                        rank.get(5).add(i);
                    }
                } else {
                    if (rank.get(6).indexOf(i) == -1) {
                        rank.get(6).add(i);
                    }

                }
            }
        }

        for (int i = 0; i < 7; i++) {
            if (enemy != -1) {
                break;
            }

            if (rank.get(i).size() == 1) {
                enemy = rank.get(i).get(0);
                break;
            } else if (rank.get(i).size() > 1) {
                int pick = -1;
                int rate = 0;
                for (int j = 0; j < rank.get(i).size(); j++) {
                    if (priority[rank.get(i).get(j)] > rate) {
                        rate = priority[rank.get(i).get(j)];
                        pick = rank.get(i).get(j);
                    }
                }
                enemy = pick;
            }
        }

        return enemy;
    }
}
