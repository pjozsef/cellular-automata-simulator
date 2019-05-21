package hu.elte.inf.people.pojsaai.cellularautomaton.neighborhood;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This enum class provides the implementation of the neighborhood relations defined in the grammar of the cellular automaton.
 * @author József Pollák
 */
public enum Neighborhood {

    DIAMOND {
                @Override
                public String toString() {
                    return "Diamond";
                }

                @Override
                public List<Point> getNeighborhood(Point point, int range) {

                    List<Point> points = new ArrayList<>(neighborCount(range));

                    int x = point.x - range;
                    int y = point.y;
                    for (int i = 0; i < range + 1; ++i) {
                        int dx = 0;
                        int dy = 0;
                        int steps = 0;
                        while (steps < range + 1) {
                            if (!(dx + x == point.x && dy + y == point.y)) {
                                points.add(new Point(x + dx, y + dy));
                            }
                            if (steps < range && i < range) {
                                if (!(dx + x + 1 == point.x && dy + y == point.y)) {
                                    points.add(new Point(x + dx + 1, y + dy));
                                }
                            }
                            dx++;
                            dy++;
                            steps++;
                        }
                        x++;
                        y--;
                    }
                    return points;
                }

                private int neighborCount(int range) {
                    return (range + 1) * (range + 1) + range * range - 1;
                }
            },
    SQUARE {
                @Override
                public String toString() {
                    return "Square";
                }

                @Override
                public List<Point> getNeighborhood(Point point, int range) {
                    int x = point.x;
                    int y = point.y;

                    List<Point> points = new ArrayList<>(neighborCount(range));
                    for (int i = x - range; i <= x + range; i++) {
                        for (int j = y - range; j <= y + range; j++) {
                            if (!(i == x && j == y)) {
                                Point p = new Point(i, j);
                                points.add(p);
                            }
                        }
                    }
                    return points;
                }

                private int neighborCount(int range) {
                    return (int) Math.pow(2 * range + 1, 2) - 1;
                }

            },
    CROSS {
                @Override
                public List<Point> getNeighborhood(Point point, int range) {
                    List<Point> points = new ArrayList<>(neighborCount(range));
                    //up, right, down, left
                    raycastCollect(range, point, new Point(-1, 0), points);
                    raycastCollect(range, point, new Point(0, 1), points);
                    raycastCollect(range, point, new Point(1, 0), points);
                    raycastCollect(range, point, new Point(0, -1), points);
                    return points;
                }

                @Override
                public String toString() {
                    return "Cross";
                }

                private int neighborCount(int range) {
                    return 4 * range;
                }
            },
    X {
                @Override
                public List<Point> getNeighborhood(Point point, int range) {
                    List<Point> points = new ArrayList<>(neighborCount(range));
                    //upper-right, bottom-right, bottom-left, upper-left
                    raycastCollect(range, point, new Point(-1, 1), points);
                    raycastCollect(range, point, new Point(1, 1), points);
                    raycastCollect(range, point, new Point(1, -1), points);
                    raycastCollect(range, point, new Point(-1, -1), points);
                    return points;
                }

                @Override
                public String toString() {
                    return "X";
                }

                private int neighborCount(int range) {
                    return 4 * range;
                }
            },
    STAR {
                @Override
                public List<Point> getNeighborhood(Point point, int range) {
                    List<Point> points = new ArrayList<>(neighborCount(range));
                    //up, right, down, left
                    points.addAll(CROSS.getNeighborhood(point, range));
                    //upper-right, bottom-right, bottom-left, upper-left
                    points.addAll(X.getNeighborhood(point, range / 2));
                    return points;
                }

                @Override
                public String toString() {
                    return "Star";
                }

                private int neighborCount(int range) {
                    return 4 * range + 4 * (range / 2);

                }
            };

    /**
     * The method responsible for returning the given neighbors of the active cell.
     * @param point the coordinates of the active cell
     * @param range the radius determining the size of the neighborhood
     * @return the list of neighbors
     */
    public abstract List<Point> getNeighborhood(Point point, int range);

    public static final String NEIGHBORHOOD_CODES_REGEX = "(D|SQ|C|X|ST)";
    private static final List<String> neighborhoodCodes = Arrays.asList("D", "SQ", "C", "X", "ST");
    private static final Map<String, Neighborhood> neighborhoodMap;

    static {
        neighborhoodMap = new HashMap<>();
        neighborhoodMap.put("D", DIAMOND);
        neighborhoodMap.put("SQ", SQUARE);
        neighborhoodMap.put("C", CROSS);
        neighborhoodMap.put("X", X);
        neighborhoodMap.put("ST", STAR);
    }

    /**
     * Replaces the neighborhood mappings with their number value in the given String input.
     * @param inputExpression the expression containing the number mappings
     * @param cells the corresponding cellular automaton state
     * @param currentCell the active cell
     * @return the new expression which is now acceptable by the Evaluator module
     * @throws InvalidNeighborhoodCodeFoundException if an invalid neighborhood code is encountered
     */
    public static String replaceWithNeighborCount(final String inputExpression, final int[][] cells, final Point currentCell) throws InvalidNeighborhoodCodeFoundException {
        final String noWhitespaceInputExpression = inputExpression.replaceAll(" ", "");

        Map<String, Integer> neighborhoodExpressions = findNeighborhoodExpressions(noWhitespaceInputExpression);
        evaluateNeighborhoodExpressions(neighborhoodExpressions, currentCell, cells);
        return replaceNeighborhoodExpressions(noWhitespaceInputExpression, neighborhoodExpressions);
    }

    private static Map<String, Integer> findNeighborhoodExpressions(final String inputExpression) {
        //parse SQ(2):1+4<D(1):2*3 => 1+4<2*3
        Map<String, Integer> neighborhoodExpressions = new HashMap<>();
        Pattern pattern = Pattern.compile("[A-Z]{1,2}\\(\\d+\\):\\d+");//pattern that matches SQ(4):1
        Matcher matcher = pattern.matcher(inputExpression);
        while (matcher.find()) {
            neighborhoodExpressions.put(matcher.group(), 0);
        }
        return neighborhoodExpressions;
    }

    private static void evaluateNeighborhoodExpressions(Map<String, Integer> neighborhoodExpressions, final Point currentCell, final int[][] cells) throws NumberFormatException, InvalidNeighborhoodCodeFoundException {
        for (String s : neighborhoodExpressions.keySet()) {//SQ(2):1
            int depth, cellType;
            String neighborCode;

            String[] colonSplit = s.split(":"); //"SQ(2)", "1"
            cellType = Integer.parseInt(colonSplit[1]);

            colonSplit[0] = colonSplit[0].substring(0, colonSplit[0].length() - 1);//SQ(2
            String[] functionSplit = colonSplit[0].split("\\("); //"SQ", "2"

            depth = Integer.parseInt(functionSplit[1]);
            neighborCode = functionSplit[0];
            if (!neighborhoodCodes.contains(neighborCode)) {
                throw new InvalidNeighborhoodCodeFoundException(functionSplit[0] + " is not a valid neighborhood code!");
            }

            List<Point> neighbors = neighborhoodMap.get(neighborCode).getNeighborhood(currentCell, depth);

            {
                int width = cells.length;
                int height = cells[0].length;
                normalizeNeighbors(neighbors, width, height);
            }

            long count = (int) neighbors.stream().filter(e -> {
                return cells[e.x][e.y] == cellType;
            }).count();
            neighborhoodExpressions.put(s, (int) count);
        }
    }

    private static String replaceNeighborhoodExpressions(final String inputExpression, Map<String, Integer> neighborhoodExpressions) {
        String resultString = inputExpression;
        for (String s : neighborhoodExpressions.keySet()) {
            resultString = resultString.replaceAll(Pattern.quote(s), neighborhoodExpressions.get(s) + "");
        }
        return resultString;
    }

    private static void normalizeNeighbors(List<Point> neighbors, int width, int height) {
        neighbors.forEach(e -> {
            e.x = e.x % width;
            if (e.x < 0) {
                e.x += width;
            }
            e.y = e.y % height;
            if (e.y < 0) {
                e.y += height;
            }
        });
    }

    private static void raycastCollect(int length, Point current, Point direction, final List<Point> points) {
        if (length > 0) {
            Point nextPoint = new Point(current);
            nextPoint.translate(direction.x, direction.y);
            points.add(nextPoint);
            length--;
            raycastCollect(length, nextPoint, direction, points);
        }
    }
}
