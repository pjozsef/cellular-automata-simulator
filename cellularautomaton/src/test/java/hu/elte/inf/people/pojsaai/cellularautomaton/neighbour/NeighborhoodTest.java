package hu.elte.inf.people.pojsaai.cellularautomaton.neighbour;

import hu.elte.inf.people.pojsaai.cellularautomaton.neighborhood.InvalidNeighborhoodCodeFoundException;
import java.awt.Point;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static hu.elte.inf.people.pojsaai.cellularautomaton.neighborhood.Neighborhood.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 *
 * @author József Pollák
 */
public class NeighborhoodTest {

    private List<Point> expectedList;

    @Before
    public void setUp() {
        expectedList = new ArrayList<>();
    }

    //////////////////////////////////////////
    /////NEIGHBORHOOD TESTS ARE SENSITIVE/////
    /////FOR POINT ORDER IN EXPECTEDLIST//////
    //////////////////////////////////////////
    //////////////DIAMOND TESTS///////////////
    //////////////////////////////////////////
    @Test
    public void DIAMOND_getNeighborhood_zero_range() {
        List<Point> returnList = DIAMOND.getNeighborhood(new Point(2, 1), 0);
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void DIAMOND_getNeighborhood_one_range() {
        List<Point> returnList = DIAMOND.getNeighborhood(new Point(2, 2), 1);
        expectedList.add(new Point(1, 2));
        expectedList.add(new Point(2, 3));
        expectedList.add(new Point(2, 1));
        expectedList.add(new Point(3, 2));

        assertThat(returnList.size()).isEqualTo(expectedList.size());
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void DIAMOND_getNeighborhood_two_range() {
        List<Point> returnList = DIAMOND.getNeighborhood(new Point(5, 3), 2);
        expectedList.add(new Point(3, 3));
        expectedList.add(new Point(4, 3));
        expectedList.add(new Point(4, 4));
        expectedList.add(new Point(5, 4));
        expectedList.add(new Point(5, 5));

        expectedList.add(new Point(4, 2));
        expectedList.add(new Point(5, 2));
        expectedList.add(new Point(6, 3));
        expectedList.add(new Point(6, 4));

        expectedList.add(new Point(5, 1));
        expectedList.add(new Point(6, 2));
        expectedList.add(new Point(7, 3));

        assertThat(returnList.size()).isEqualTo(expectedList.size());
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void DIAMOND_getNeighborhood_n_range() {
        for (int depth = 1; depth <= 10; ++depth) {
            List<Point> returnList = DIAMOND.getNeighborhood(new Point(5, 3), depth);
            assertThat((depth + 1) * (depth + 1) + depth * depth - 1)
                    .isEqualTo(returnList.size());
        }
    }

    /////////////////////////////////////////
    //////////////SQUARE TESTS///////////////
    /////////////////////////////////////////
    @Test
    public void SQUARE_getNeighborhood_zero_range() {
        List<Point> returnList = SQUARE.getNeighborhood(new Point(2, 1), 0);
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void SQUARE_getNeighborhood_one_range() {
        List<Point> returnList = SQUARE.getNeighborhood(new Point(5, 3), 1);
        expectedList.add(new Point(4, 2));
        expectedList.add(new Point(4, 3));
        expectedList.add(new Point(4, 4));
        expectedList.add(new Point(5, 2));
        expectedList.add(new Point(5, 4));
        expectedList.add(new Point(6, 2));
        expectedList.add(new Point(6, 3));
        expectedList.add(new Point(6, 4));

        assertThat(returnList.size()).isEqualTo(expectedList.size());
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void SQUARE_getNeighborhood_two_range() {
        List<Point> returnList = SQUARE.getNeighborhood(new Point(5, 3), 2);
        expectedList.add(new Point(3, 1));
        expectedList.add(new Point(3, 2));
        expectedList.add(new Point(3, 3));
        expectedList.add(new Point(3, 4));
        expectedList.add(new Point(3, 5));

        expectedList.add(new Point(4, 1));
        expectedList.add(new Point(4, 2));
        expectedList.add(new Point(4, 3));
        expectedList.add(new Point(4, 4));
        expectedList.add(new Point(4, 5));

        expectedList.add(new Point(5, 1));
        expectedList.add(new Point(5, 2));
        expectedList.add(new Point(5, 4));
        expectedList.add(new Point(5, 5));

        expectedList.add(new Point(6, 1));
        expectedList.add(new Point(6, 2));
        expectedList.add(new Point(6, 3));
        expectedList.add(new Point(6, 4));
        expectedList.add(new Point(6, 5));

        expectedList.add(new Point(7, 1));
        expectedList.add(new Point(7, 2));
        expectedList.add(new Point(7, 3));
        expectedList.add(new Point(7, 4));
        expectedList.add(new Point(7, 5));

        assertThat(returnList.size()).isEqualTo(expectedList.size());
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void SQUARE_getNeighborhood_n_range() {
        for (int depth = 1; depth <= 10; ++depth) {
            List<Point> returnList = SQUARE.getNeighborhood(new Point(5, 3), depth);
            assertThat((int) Math.pow(2 * depth + 1, 2) - 1)
                    .isEqualTo(returnList.size());
        }
    }

    /////////////////////////////////////////
    ///////////////CROSS TESTS///////////////
    /////////////////////////////////////////
    @Test
    public void CROSS_getNeighborhood_zero_range() {
        List<Point> returnList = CROSS.getNeighborhood(new Point(2, 1), 0);
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void CROSS_getNeighborhood_one_range() {
        List<Point> returnList = CROSS.getNeighborhood(new Point(3, 2), 1);
        expectedList.add(new Point(2, 2));
        expectedList.add(new Point(3, 3));
        expectedList.add(new Point(4, 2));
        expectedList.add(new Point(3, 1));

        assertThat(returnList.size()).isEqualTo(expectedList.size());
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void CROSS_getNeighborhood_two_range() {
        List<Point> returnList = CROSS.getNeighborhood(new Point(3, 2), 2);
        expectedList.add(new Point(2, 2));
        expectedList.add(new Point(1, 2));
        expectedList.add(new Point(3, 3));
        expectedList.add(new Point(3, 4));
        expectedList.add(new Point(4, 2));
        expectedList.add(new Point(5, 2));
        expectedList.add(new Point(3, 1));
        expectedList.add(new Point(3, 0));

        assertThat(returnList.size()).isEqualTo(expectedList.size());
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void CROSS_getNeighborhood_n_range() {
        for (int depth = 1; depth <= 10; ++depth) {
            List<Point> returnList = CROSS.getNeighborhood(new Point(5, 3), depth);
            assertThat(4 * depth)
                    .isEqualTo(returnList.size());
        }
    }

    /////////////////////////////////////////
    /////////////////X TESTS/////////////////
    /////////////////////////////////////////
    @Test
    public void X_getNeighborhood_zero_range() {
        List<Point> returnList = X.getNeighborhood(new Point(2, 1), 0);
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void X_getNeighborhood_one_range() {
        List<Point> returnList = X.getNeighborhood(new Point(3, 2), 1);
        expectedList.add(new Point(2, 3));
        expectedList.add(new Point(4, 3));
        expectedList.add(new Point(4, 1));
        expectedList.add(new Point(2, 1));

        assertThat(returnList.size()).isEqualTo(expectedList.size());
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void X_getNeighborhood_two_range() {
        List<Point> returnList = X.getNeighborhood(new Point(3, 2), 2);
        expectedList.add(new Point(2, 3));
        expectedList.add(new Point(1, 4));
        expectedList.add(new Point(4, 3));
        expectedList.add(new Point(5, 4));
        expectedList.add(new Point(4, 1));
        expectedList.add(new Point(5, 0));
        expectedList.add(new Point(2, 1));
        expectedList.add(new Point(1, 0));

        assertThat(returnList.size()).isEqualTo(expectedList.size());
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void X_getNeighborhood_n_range() {
        for (int depth = 1; depth <= 10; ++depth) {
            List<Point> returnList = X.getNeighborhood(new Point(5, 3), depth);
            assertThat(4 * depth)
                    .isEqualTo(returnList.size());
        }
    }

    /////////////////////////////////////////
    ///////////////STAR TESTS////////////////
    /////////////////////////////////////////
    @Test
    public void STAR_getNeighborhood_zero_range() {
        List<Point> returnList = STAR.getNeighborhood(new Point(2, 1), 0);
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void STAR_getNeighborhood_one_range() {
        List<Point> returnList = STAR.getNeighborhood(new Point(3, 2), 1);
        expectedList.add(new Point(2, 2));
        expectedList.add(new Point(3, 3));
        expectedList.add(new Point(4, 2));
        expectedList.add(new Point(3, 1));

        assertThat(returnList.size()).isEqualTo(expectedList.size());
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void STAR_getNeighborhood_two_range() {
        List<Point> returnList = STAR.getNeighborhood(new Point(3, 2), 2);
        //CROSS with depth=2
        expectedList.add(new Point(2, 2));
        expectedList.add(new Point(1, 2));
        expectedList.add(new Point(3, 3));
        expectedList.add(new Point(3, 4));
        expectedList.add(new Point(4, 2));
        expectedList.add(new Point(5, 2));
        expectedList.add(new Point(3, 1));
        expectedList.add(new Point(3, 0));

        //X with depth=1
        expectedList.add(new Point(2, 3));
        expectedList.add(new Point(4, 3));
        expectedList.add(new Point(4, 1));
        expectedList.add(new Point(2, 1));

        assertThat(returnList.size()).isEqualTo(expectedList.size());
        assertThat(returnList).containsExactlyElementsOf(expectedList);
    }

    @Test
    public void STAR_getNeighborhood_n_range() {
        for (int depth = 1; depth <= 10; ++depth) {
            List<Point> returnList = STAR.getNeighborhood(new Point(5, 3), depth);
            assertThat(4 * depth + 4 * (depth / 2))
                    .isEqualTo(returnList.size());
        }
    }

    /////////////////////////////////////////
    ////////////REPLACEMENT TESTS////////////
    /////////////////////////////////////////    
    @Test
    public void test_replaceWithNeighborCount1() throws InvalidNeighborhoodCodeFoundException {
        int[][] cells = {
            {1, 1, 0, 1, 0},
            {0, 1, 0, 0, 0},
            {1, 1, 0, 1, 0},
            {0, 1, 0, 1, 0},
            {1, 1, 0, 0, 1}
        };

        Point cell = new Point(2, 2);

        String[] input = {
            "SQ(2):1+4<X(1):0*3",
            "SQ(1):0-D(2):1=9",
            "ST(1):0-C(1):0+D(2):1=9",
            "SQ(2):0-SQ(1):0+SQ(1):1=SQ(2):1",
            "D(2):0-D(1):0+D(1):1=D(2):1",
            "C(2):0-C(1):0+C(1):1=C(2):1",
            "X(2):0-X(1):0+X(1):1=X(2):1",
            "ST(2):0-ST(1):0+ST(1):1=ST(2):1"
        };

        String[] expected = {
            "12+4<1*3",
            "3-6=9",
            "2-2+6=9",
            "12-3+5=12",
            "6-2+2=6",
            "5-2+2=3",
            "2-1+3=6",
            "6-2+2=6"
        };

        IntStream.range(0, input.length)
                .forEach((i) -> {
                    try {
                        assertThat(replaceWithNeighborCount(input[i], cells, cell)).isEqualTo(expected[i]);
                    } catch (InvalidNeighborhoodCodeFoundException incfe) {
                        org.junit.Assert.fail("InvalidNeighborhoodCodeFoundException encountered: " + incfe.getMessage());
                    }
                });
    }

    @Test
    public void test_replaceWithSpaces() throws InvalidNeighborhoodCodeFoundException {
        int[][] cells = {
            {1, 1, 0, 1, 0},
            {0, 1, 0, 0, 0},
            {1, 1, 0, 1, 0},
            {0, 1, 0, 1, 0},
            {1, 1, 0, 0, 1}
        };

        Point cell = new Point(2, 2);

        String[] input = {
            "SQ   ( 2 ) :1+    4<X (1):0 *  3 ",
            "SQ(1  ):0- D( 2):     1=9",
            "  ST(1):      0-C(1):0    +D ( 2 ) : 1 = 9"
        };

        String[] expected = {
            "12+4<1*3",
            "3-6=9",
            "2-2+6=9"
        };

        IntStream.range(0, input.length)
                .forEach((i) -> {
                    try {
                        assertThat(replaceWithNeighborCount(input[i], cells, cell)).isEqualTo(expected[i]);
                    }catch(InvalidNeighborhoodCodeFoundException incfe){
                        org.junit.Assert.fail("InvalidNeighborhoodCodeFoundException encountered: "+incfe.getMessage());
                    }
                });
    }

    @Test
    public void test_replaceWithNeighborCount2() throws InvalidNeighborhoodCodeFoundException {
        final int[][] cells = {
            {1, 1, 0, 1, 0},
            {0, 1, 0, 0, 0},
            {1, 1, 0, 1, 0},
            {0, 1, 0, 1, 0},
            {1, 1, 0, 0, 1}
        };

        String expression0a = "D(2):1+3<6";
        String expression0b = "SQ(1):0+D(2):1>2";
        String expression1 = "SQ(2):1>8";

        
        assertEquals("11>8", replaceWithNeighborCount(expression1, cells, new Point(0, 0)));
        assertEquals("11>8", replaceWithNeighborCount(expression1, cells, new Point(0, 1)));
        assertEquals("5+3<6", replaceWithNeighborCount(expression0a, cells, new Point(0, 2)));
        assertEquals("4+5>2", replaceWithNeighborCount(expression0b, cells, new Point(0, 2)));
        assertEquals("11>8", replaceWithNeighborCount(expression1, cells, new Point(0, 3)));
        assertEquals("5+3<6", replaceWithNeighborCount(expression0a, cells, new Point(0, 4)));
        assertEquals("4+5>2", replaceWithNeighborCount(expression0b, cells, new Point(0, 4)));

        assertEquals("6+3<6", replaceWithNeighborCount(expression0a, cells, new Point(1, 0)));
        assertEquals("3+6>2", replaceWithNeighborCount(expression0b, cells, new Point(1, 0)));
        assertEquals("11>8", replaceWithNeighborCount(expression1, cells, new Point(1, 1)));
        assertEquals("5+3<6", replaceWithNeighborCount(expression0a, cells, new Point(1, 2)));
        assertEquals("3+5>2", replaceWithNeighborCount(expression0b, cells, new Point(1, 2)));
        assertEquals("4+3<6", replaceWithNeighborCount(expression0a, cells, new Point(1, 3)));
        assertEquals("6+4>2", replaceWithNeighborCount(expression0b, cells, new Point(1, 3)));
        assertEquals("6+3<6", replaceWithNeighborCount(expression0a, cells, new Point(1, 4)));
        assertEquals("4+6>2", replaceWithNeighborCount(expression0b, cells, new Point(1, 4)));
    }

    @Test(expected = InvalidNeighborhoodCodeFoundException.class)
    public void test_replaceWithNeighborCount_Exception() throws InvalidNeighborhoodCodeFoundException {
        replaceWithNeighborCount("TR(3):1+3=1", null, null);
        fail();
    }
}
