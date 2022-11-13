import java.lang.reflect.Array;
import java.util.*;

public class MyBot {
    // if the cell is not ours and it can be conquered, returns the
    // production / strength ratio
    static double heuristic(Location enemy, GameMap gameMap, int owner) {
        int damage;
        if (enemy.getSite().owner != owner && enemy.getSite().strength > 0) {
            return (double) enemy.getSite().production / enemy.getSite().strength;
        }
        return enemy.getSite().production;

    }

    public static Move getMove(Location location, int x, int y, GameMap gameMap) {
        boolean if_border = false;
        double north, south, east, west, max = -1.00;
        Direction direction = null;

        Location north_neigh = gameMap.getLocation(location, Direction.NORTH);
        
        if (north_neigh.getSite().owner != location.getSite().owner) {
            if_border = true;
            north = heuristic(north_neigh, gameMap, location.getSite().owner);
            if (north > max) {
                max = north;
                direction = Direction.NORTH;
            }
        }

        Location south_neigh = gameMap.getLocation(location, Direction.SOUTH);

        if (south_neigh.getSite().owner != location.getSite().owner) {
            if_border = true;
            south = heuristic(south_neigh, gameMap, location.getSite().owner);
            if (south > max) {
                max = south;
                direction = Direction.SOUTH;
            }
        }

        
        Location west_neigh = gameMap.getLocation(location, Direction.WEST);
        if (west_neigh.getSite().owner != location.getSite().owner) {
            if_border = true;
            west = heuristic(west_neigh, gameMap, location.getSite().owner);
            if (west > max) {
                max = west;
                direction = Direction.WEST;
            }
        }

        Location east_neigh = gameMap.getLocation(location, Direction.EAST);
        if (east_neigh.getSite().owner != location.getSite().owner) {
            if_border = true;
            east = heuristic(east_neigh, gameMap, location.getSite().owner);
            if (east > max) {
                max = east;
                direction = Direction.EAST;
            }
        }

        if (if_border) {
            Location loc = gameMap.getLocation(location, direction);
            Direction feeling_attacked = goAttack(location, gameMap);
            if (feeling_attacked != null) {
                return new Move(location, feeling_attacked);
            }

            if (loc.getSite().strength >= location.getSite().strength) {
                return new Move(location, Direction.STILL);
            }
            
            return new Move(location, direction);
        }

        if (location.getSite().strength <= (location.getSite().production * 5) && location.getSite().strength < 255) {
            return new Move(location, Direction.STILL);
        }

        if (!if_border) {
            if (location.getSite().strength < (location.getSite().production * 5) && location.getSite().strength < 255) {
                return new Move(location, Direction.STILL);
            }
            return new Move(location, shortestPath(location, gameMap));
        }

        return new Move(location, Direction.STILL);
    }

    // for a certain neutral neighbor cell relative to our current location,
    // get the total damage which can be done by its neighbors
    public static int detectEnemy(Location location, Location location_neutral, GameMap gameMap) {
        int attack = 0;
        Location north_neigh = gameMap.getLocation(location_neutral, Direction.NORTH);
        if (north_neigh.getSite().owner != 0 && north_neigh.getSite().owner != location.getSite().owner) {
            attack++;
        }

        Location south_neigh = gameMap.getLocation(location_neutral, Direction.SOUTH);
        if (south_neigh.getSite().owner != 0 && south_neigh.getSite().owner != location.getSite().owner) {
            attack++;
        }

        Location east_neigh = gameMap.getLocation(location_neutral, Direction.EAST);
        if (east_neigh.getSite().owner != 0 && east_neigh.getSite().owner != location.getSite().owner) {
            attack++;
        }


        Location west_neigh = gameMap.getLocation(location_neutral, Direction.WEST);
        if (west_neigh.getSite().owner != 0 && west_neigh.getSite().owner != location.getSite().owner) {
            attack++;
        }

        return attack;
    }

    // the next move will be in the direction from where the enemy could attack with
    // the maximum damage (so we get the neighbors of each neighbor cell and calculate
    // the the number of cells that are owned by the enemy)
    public static Direction goAttack(Location location, GameMap gameMap) {
        Location north_neutral_neigh = gameMap.getLocation(location, Direction.NORTH);
        int attack_north = detectEnemy(location, north_neutral_neigh, gameMap);

        Location south_neutral_neigh = gameMap.getLocation(location, Direction.SOUTH);
        int attack_south = detectEnemy(location, south_neutral_neigh, gameMap);

        Location east_neutral_neigh = gameMap.getLocation(location, Direction.EAST);
        int attack_east = detectEnemy(location, east_neutral_neigh, gameMap);

        Location west_neutral_neigh = gameMap.getLocation(location, Direction.WEST);
        int attack_west = detectEnemy(location, west_neutral_neigh, gameMap);

        int res = Math.max(Math.max(Math.max(attack_north, attack_south), attack_east), attack_west);
        if (res == 0) {
            return null;
        }
        if (res == attack_south) {
            return Direction.SOUTH;
        } else if (res == attack_north) {
            return Direction.NORTH;
        } else if (res == attack_west) {
            return Direction.WEST;
        } else if (res == attack_east) {
            return Direction.EAST;
        }
        return null;
    }

    // for an inside cell, calculate the distance to which the border is
    // reached quicker
    public static Direction shortestPath(Location location, GameMap gameMap) {
        Location aux = location;
        Location aux2 = location;
        Location north_neigh = gameMap.getLocation(location, Direction.NORTH);
        int dist_north = 0;
        while (true) {
            if (north_neigh == aux2) {
                break;
            }
            if (north_neigh.getSite().owner == aux.getSite().owner) {
                dist_north++;
                aux = north_neigh;
                north_neigh = gameMap.getLocation(aux, Direction.NORTH);
            } else {
                break;
            }
        }

        aux = location;
        aux2 = location;
        Location south_neigh = gameMap.getLocation(location, Direction.SOUTH);
        int dist_south = 0;
        while (true) {
            if (south_neigh == aux2) {
                break;
            }
            if (south_neigh.getSite().owner == aux.getSite().owner) {
                dist_south++;
                aux = south_neigh;
                south_neigh = gameMap.getLocation(aux, Direction.SOUTH);
            } else {
                break;
            }
        }

        aux = location;
        aux2 = location;
        Location east_neigh = gameMap.getLocation(location, Direction.EAST);
        int dist_east = 0;
        while (true) {
            if (east_neigh == aux2) {
                break;
            }
            if (east_neigh.getSite().owner == aux.getSite().owner) {
                dist_east++;
                aux = east_neigh;
                east_neigh = gameMap.getLocation(aux, Direction.EAST);
            } else {
                break;
            }
        }

        aux = location;
        aux2 = location;
        Location west_neigh = gameMap.getLocation(location, Direction.WEST);
        int dist_west = 0;
        while (true) {
            if (west_neigh == aux2) {
                break;
            }
            if (west_neigh.getSite().owner == aux.getSite().owner) {
                dist_west++;
                aux = west_neigh;
                west_neigh = gameMap.getLocation(aux, Direction.WEST);
            } else {
                break;
            }
        }

        int min = Math.min(Math.min(Math.min(dist_east, dist_west), dist_south), dist_north);
        if (min == dist_north) {
            return Direction.NORTH;
        } else if (min == dist_south) {
            return Direction.SOUTH;
        } else if (min == dist_east) {
            return Direction.EAST;
        } else if (min == dist_west) {
            return Direction.WEST;
        }

        return Direction.STILL;
    }

    public static void main(String[] args) throws java.io.IOException {
        final InitPackage iPackage = Networking.getInit();
        final int myID = iPackage.myID;
        final GameMap gameMap = iPackage.map;

        Networking.sendInit("MyJavaBot");

        while(true) {
            List<Move> moves = new ArrayList<Move>();

            Networking.updateFrame(gameMap);
            ArrayList<Location> neighbors = new ArrayList<>();

            // for each cell of the map, make a certain move
            for (int y = 0; y < gameMap.height; y++) {
                for (int x = 0; x < gameMap.width; x++) {
                    final Location location = gameMap.getLocation(x, y);
                    final Site site = location.getSite();
                    if (site.owner == myID) {
                        moves.add(getMove(location, x, y, gameMap));
                    }
                }
            }
            Networking.sendFrame(moves);
        }
    }
}
