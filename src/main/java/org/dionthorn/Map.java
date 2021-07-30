package org.dionthorn;

import java.net.URI;

public class Map {

    private final int[][] map;

    public Map(String filename) {
        String[] flatData = FileOpUtils.getFileLines(URI.create(getClass().getResource("/Maps/" + filename).toString()));
        int size = flatData.length; // maps are square
        map = new int[size][size];
        int x = 0;
        int y = 0;
        for(String line: flatData) {
            String[] lineSplit = line.split(",");
            for(String num: lineSplit) {
                map[x][y] = Integer.parseInt(num);
                x++;
            }
            x = 0;
            y++;
        }
    }

    public int[][] getMap() {
        return map;
    }

}
