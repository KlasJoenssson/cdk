/*
 *  $RCSfile$
 *  $Author: miguelrojasch $
 *  $Date: 2008-05-01 12:46:02 +0200 (Thu, 01 May 2008) $
 *  $Revision: 10745 $
 *
 *  Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.charges.StabilizationCharge;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  The stabilization of the positive charge 
 *  (e.g.) obtained in the polar breaking of a bond is calculated from the sigma- and 
 *  lone pair-electronegativity values of the atoms that are in conjugation to the atoms 
 *  obtaining the charges. The method is based following H. Saller, Dissertation, TU München, 1985.
 *  The value is calculated looking for resonance structures which can stabilize the charge.
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>maxIterations</td>
 *     <td>0</td>
 *     <td>Number of maximum iterations</td>
 *   </tr>
 * </table>
 *
 * @author         Miguel Rojas Cherto
 * @cdk.created    2008-104-31
 * @cdk.module     qsaratomic
 * @cdk.set        qsar-descriptors
 * @see StabilizationCharge
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.StabilizationPlusChargeDescriptorTest")
public class StabilizationPlusChargeDescriptor implements IAtomicDescriptor {
	
    String[] descriptorNames = {"stabilPlusC"};
    
	private StabilizationCharge stabil;

    /**
     *  Constructor for the StabilizationPlusChargeDescriptor object
     */
    public StabilizationPlusChargeDescriptor() {
    	stabil = new StabilizationCharge();
  }


    /**
     *  Gets the specification attribute of the StabilizationPlusChargeDescriptor
     *  object
     *
     *@return    The specification value
     */
    @TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#stabilizationPlusCharge",
            this.getClass().getName(),
            "$Id: StabilizationPlusChargeDescriptor.java 10745 2008-05-01 10:46:02Z miguelrojasch $",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the StabilizationPlusChargeDescriptor
     *  object
     *
     *@param  params            1: max iterations (optional, defaults to 20)
     *@exception  CDKException  Description of the Exception
     */
    @TestMethod(value="testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        
    }


    /**
     *  Gets the parameters attribute of the StabilizationPlusChargeDescriptor
     *  object
     *
     *@return    The parameters value
     */
    @TestMethod(value="testGetParameters")
    public Object[] getParameters() {
        return null;
    }


    /**
     *  The method calculates the stabilization of charge of a given atom
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  container         AtomContainer
     *@return                   return the stabilization value
     *@exception  CDKException  Possible Exceptions
     */
    @TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer container) throws CDKException {
    	
    	AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);

  		double result = stabil.calculatePositive(container, atom);
	    
	    return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(result),descriptorNames);
    }


    /**
     *  Gets the parameterNames attribute of the StabilizationPlusChargeDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        return null;
    }


    /**
     *  Gets the parameterType attribute of the StabilizationPlusChargeDescriptor
     *  object
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        return null; 
    }
}
