package org.openscience.cdk.test.pharmacophore;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ConformerContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.iterator.IteratingMDLConformerReader;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.pharmacophore.PharmacophoreMatcher;
import org.openscience.cdk.pharmacophore.PharmacophoreQueryAtom;
import org.openscience.cdk.pharmacophore.PharmacophoreQueryBond;

import java.io.InputStream;

/**
 * @cdk.module test-extra
 */
public class PharmacophoreMatcherTest {

    public static ConformerContainer conformers = null;

    @BeforeClass
    public void loadConformerData() {
        String filename = "data/mdl/pcoretest1.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingMDLConformerReader reader = new IteratingMDLConformerReader(ins, DefaultChemObjectBuilder.getInstance());
        if (reader.hasNext()) conformers = (ConformerContainer) reader.next();
    }


    @Test
    public void testMatcherQuery1() throws CDKException {
        Assert.assertNotNull(conformers);

        // make a query
        QueryAtomContainer query = new QueryAtomContainer();

        PharmacophoreQueryAtom o = new PharmacophoreQueryAtom("D", "[OX1]");
        PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("A", "[N]");
        PharmacophoreQueryAtom n2 = new PharmacophoreQueryAtom("A", "[N]");

        query.addAtom(o);
        query.addAtom(n1);
        query.addAtom(n2);

        PharmacophoreQueryBond b1 = new PharmacophoreQueryBond(o, n1, 4.0, 4.5);
        PharmacophoreQueryBond b2 = new PharmacophoreQueryBond(o, n2, 4.0, 5.0);
        PharmacophoreQueryBond b3 = new PharmacophoreQueryBond(n1, n2, 5.4, 5.8);

        query.addBond(b1);
        query.addBond(b2);
        query.addBond(b3);

        PharmacophoreMatcher matcher = new PharmacophoreMatcher(query);
        boolean[] statuses = matcher.matches(conformers);

        Assert.assertEquals(100, statuses.length);

        int[] hits = new int[18];
        int i = 0;
        for (boolean status : statuses) {
            if (status) hits[i] = i;
            i++;
        }

        int[] expected = {0, 1, 2, 5, 6, 7, 8, 9, 10, 20, 23, 48, 62, 64, 66, 70, 76, 87};

        Assert.assertEquals("There was an error in the expected hits", expected, hits);
    }

    @Test(expected = CDKException.class)
    public void testInvalidQuery() throws CDKException {
        QueryAtomContainer query = new QueryAtomContainer();

        PharmacophoreQueryAtom o = new PharmacophoreQueryAtom("D", "[OX1]");
        PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("A", "[N]");
        PharmacophoreQueryAtom n2 = new PharmacophoreQueryAtom("A", "[NX3]");

        query.addAtom(o);
        query.addAtom(n1);
        query.addAtom(n2);

        PharmacophoreQueryBond b1 = new PharmacophoreQueryBond(o, n1, 4.0, 4.5);
        PharmacophoreQueryBond b2 = new PharmacophoreQueryBond(o, n2, 4.0, 5.0);
        PharmacophoreQueryBond b3 = new PharmacophoreQueryBond(n1, n2, 5.4, 5.8);

        query.addBond(b1);
        query.addBond(b2);
        query.addBond(b3);

        PharmacophoreMatcher matcher = new PharmacophoreMatcher(query);
        matcher.matches(conformers);

    }
}
