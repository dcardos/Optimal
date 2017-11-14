package model.math;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Danilo on 10/09/2016.
 */
public class CoefficientTest {

    private Coefficient bigA = new Coefficient('A');

    @Before
    public void setUp() throws Exception {
        bigA.addIndex('n');
        bigA.addIndex('j');
        bigA.addIndex('2');
    }

    @Test
    public void getLetter() throws Exception {
        assertEquals(bigA.getLetter(), 'A');
    }

    @Test
    public void setLetter() throws Exception {
        bigA.setLetter('B');
        assertEquals(bigA.getLetter(), 'B');
    }

    @Test
    public void getIndexes() throws Exception {

    }

    @Test
    public void setIndexes() throws Exception {

    }

    @Test
    public void addIndex() throws Exception {

    }

    @Test
    public void removeIndex() throws Exception {

    }

    @Test
    public void getLatexExpression() throws Exception {
        assertEquals(bigA.getLatexExpression(), "A_{nj2}");
    }

}