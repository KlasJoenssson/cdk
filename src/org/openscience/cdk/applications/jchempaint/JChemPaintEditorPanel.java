/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.EventObject;
import javax.swing.JPanel;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.controller.PopupController2D;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.event.CDKChangeListener;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;
import org.openscience.cdk.validate.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.URL;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.openscience.cdk.*;
import org.openscience.cdk.applications.plugin.*;
import org.openscience.cdk.controller.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.validate.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.event.*;
import org.openscience.cdk.applications.jchempaint.*;
import org.openscience.cdk.applications.jchempaint.io.*;
import org.openscience.cdk.applications.jchempaint.action.*;
import org.openscience.cdk.applications.jchempaint.dialogs.*;
import org.openscience.cdk.applications.plugin.*;
import org.openscience.cdk.applications.jchempaint.dnd.JCPTransferHandler;

/**
 *  This class implements an editing JChemPaintPanel.
 *
 *@author        steinbeck
 *@cdk.created       16. Februar 2005
 *@cdk.module    jchempaint
 *@see           JChemPaintViewerPanel
 */
public class JChemPaintEditorPanel extends JChemPaintPanel
		 implements ChangeListener, CDKChangeListener, CDKEditBus
{

	static String JCP_MODEL_CHANGED = "1";
	static String JCP_CLOSING = "2";
	
	String lastEventReason;	
	
	String recentSymbol = "C";

	private static DictionaryDatabase dictdb = null;
	private static ValidatorEngine engine = null;
	private static LoggingTool logger;

	boolean showMenuBar = true;
	boolean showToolBar = true;
	boolean showStatusBar = true;
	
  protected CDKPluginManager pluginManager = null;
	protected EventListenerList changeListeners = null;
	
	/**
	 *  Constructor for the panel
	 *
	 */
	public JChemPaintEditorPanel(){
    this(1, null);
  }

	/**
	 *  Constructor for the panel
	 *
	 *@param  lines  How many lines should the horizontal toolbar have?
	 */
	public JChemPaintEditorPanel(int lines, Dimension panelDimension)
	{
		super();
    customizeView();
		setupPluginManager();
		super.setJChemPaintModel(new JChemPaintModel());
		setShowToolBar(true, lines);
		if (logger == null)
		{
			logger = new LoggingTool(this);
		}
		this.setTransferHandler(new JCPTransferHandler("JCPPanel"));
		logger.debug("JCPPanel set and done...");
		if (panelDimension != null && getIsOpenedByViewer()) {
			super.getJChemPaintModel().getRendererModel().setBackgroundDimension(panelDimension);
			viewerDimension = new Dimension(((int) panelDimension.getWidth()) + 10, ((int) panelDimension.getHeight() + 10));
			super.setPreferredSize(viewerDimension);
		}
	}


	/**
	 *  Gets the pluginManager attribute of the JChemPaint object
	 *
	 *@return    The pluginManager value
	 */
	public CDKPluginManager getPluginManager() {
		return pluginManager;
	}


	/**
	 *  Tells if a menu is shown
	 *
	 *@return    The showMenu value
	 */
	public boolean getShowMenuBar() {
		return showMenuBar;
	}


	/**
	 *  Sets if a menu is shown
	 *
	 *@param  showMenuBar  The new showMenuBar value
	 */
	public void setShowMenuBar(boolean showMenuBar) {
		this.showMenuBar = showMenuBar;
		customizeView();
	}


	/**
	 *  Tells if a status bar is shown
	 *
	 *@return    The showStatusBar value
	 */
	public boolean getShowStatusBar() {
		return showStatusBar;
	}


	/**
	 *  Description of the Method
	 */
	public void customizeView() {
		if (showMenuBar) {
			if (menu == null) {
				menu = new JChemPaintMenuBar(this);
			}
			add(menu, BorderLayout.NORTH);
			revalidate();
		} else {
			try {
				remove(menu);
				revalidate();
			} catch (Exception exc) {

			}
		}
		if (showStatusBar) {
			if (statusBar == null) {
				statusBar = new StatusBar();
			}
			add(statusBar, BorderLayout.SOUTH);
			revalidate();
		} else {
			try {
				remove(statusBar);
				revalidate();
			} catch (Exception exc) {

			}
		}
	}


	/**
	 *  Description of the Method
	 */
	void setupIfModelNotEmpty()
	{
		AtomContainer ac = ChemModelManipulator.getAllInOneContainer(jchemPaintModel.getChemModel());

		Renderer2DModel rendererModel = jchemPaintModel.getRendererModel();
		if (ac.getAtomCount() != 0)
		{
			logger.info("ChemModel is already non-empty! Sizing things to get it visible!");
			// do some magic to get it all in one page
			Dimension oneMoleculeDimension = new Dimension(600, 400);
			// should be based on molecule/reaction size!
			Dimension dimension = makeChemModelFit(
					oneMoleculeDimension, jchemPaintModel.getChemModel()
					);
			rendererModel.setBackgroundDimension(dimension);

			/*
			 *  this code ensures that the molecule ends up somewhere in the model
			 *  of the view screen
			 */
			GeometryTools.translateAllPositive(ac);
			double scaleFactor = GeometryTools.getScaleFactor(ac, rendererModel.getBondLength());
			GeometryTools.scaleMolecule(ac, scaleFactor);
			layoutInTable(oneMoleculeDimension, jchemPaintModel.getChemModel());
			// GeometryTools.center(ac, getPreferredSize());
			// GeometryTools.center(ac, new Dimension(300,200));
		}
	}

	public void registerModel(JChemPaintModel model)
	{
		PopupController2D inputAdapter = new PopupController2D(model.getChemModel(), model.getRendererModel(),model.getControllerModel());
    setupPopupMenus(inputAdapter);
		Renderer2DModel rendererModel = model.getRendererModel();
		model.getControllerModel().setBondPointerLength(rendererModel.getBondLength());
		model.getControllerModel().setRingPointerLength(rendererModel.getBondLength());

		model.getRendererModel().addCDKChangeListener(this);
		inputAdapter.addCDKChangeListener(model);
		//drawingPanel.setJChemPaintModel(model);
		drawingPanel.addMouseListener(inputAdapter);
		drawingPanel.addMouseMotionListener(inputAdapter);
	}
		
	
	/**
	 *  Sets up the plugin manager.
	 */
	private void setupPluginManager() {
		try {
			// set up plugin manager
			JCPPropertyHandler jcph = JCPPropertyHandler.getInstance();
			pluginManager = new CDKPluginManager(jcph.getJChemPaintDir().toString(), this);

			// load the plugins that come with JCP itself
			// pluginManager.loadPlugin("org.openscience.cdkplugin.dirbrowser.DirBrowserPlugin");

			// load the user plugins
			pluginManager.loadPlugins(new File(jcph.getJChemPaintDir(), "plugins").toString());

			// load plugins given with -Dplugin.dir=bla
			if (System.getProperty("plugin.dir") != null) {
				pluginManager.loadPlugins(System.getProperty("plugin.dir"));
			}
		} catch (Exception exc) {
			//logger.error("Could not initialize Plugin-Manager. I might be in a sandbox.");
			//logger.debug(exc);
		}
	}


	/**
	 *  Returns the value of showToolbar.
	 *
	 *@return    The showToolbar value
	 */
	public boolean getShowToolBar()
	{
		return showToolBar;
	}
	
		/**
	 * Returns the value of lastEventReason.
	 */
	public String getLastEventReason()
	{
		return lastEventReason;
	}

	/**
	 * Sets the value of lastEventReason.
	 * @param lastEventReason The value to assign lastEventReason.
	 */
	public void setLastEventReason(String lastEventReason)
	{
		this.lastEventReason = lastEventReason;
	}


	/**
	 *  Sets the value of showToolbar.
	 *
	 *@param  showToolbar  The value to assign showToolbar.
	 */
	public void setShowToolBar(boolean showToolBar)
	{
    setShowToolBar(showToolBar, 1);
  }


	/**
	 *  Sets if statusbar should be shown
	 *
	 *@param  showStatusBar  The value to assign showStatusBar.
	 */
	public void setShowStatusBar(boolean showStatusBar) {
		this.showStatusBar = showStatusBar;
		customizeView();
	}


	/**
	 *  Sets the value of showToolbar.
	 *
	 *@param  showToolbar  The value to assign showToolbar.
	 */
	public void setShowToolBar(boolean showToolBar, int lines)
	{
		this.showToolBar = showToolBar;
		if (showToolBar)
		{
			if (toolBar == null)
			{
    		toolBar = ToolBarMaker.getToolbar(this, lines);
			}
			mainContainer.add(toolBar, BorderLayout.NORTH);
			mainContainer.revalidate();
		}
		else
		{
			try
			{
				mainContainer.remove(toolBar);
				mainContainer.revalidate();
			}
			catch(Exception exc)
			{
				
			}
		}

	}


	/**
	 *  Overwrites the default background color and returns the color taken from
	 *  the Renderer2DModel.
	 *
	 *@return    The background value
	 */
	public Color getBackground()
	{
		if (jchemPaintModel != null)
		{
			return jchemPaintModel.getRendererModel().getBackColor();
		} else
		{
			return Color.WHITE;
		}
	}


	/**
	 *  Gets the toolBar attribute of the JChemPaintPanel object
	 *
	 *@return    The toolBar value
	 */
	public JToolBar getToolBar()
	{
		return toolBar;
	}


	/**
	 *  Creates a new JFrame that owns a new JChemPaintModel and returns it.
	 *
	 *@return    The new JFrame containing the JChemPaintEditorPanel
	 */
	public static JFrame getEmptyFrameWithModel()
	{
		JChemPaintModel model = new JChemPaintModel();
		model.setTitle(getNewFrameName());
		model.setAuthor(JCPPropertyHandler.getInstance().getJCPProperties().getProperty("General.UserName"));
		Package self = Package.getPackage("org.openscience.cdk.applications.jchempaint");
		String version = self.getImplementationVersion();
		model.setSoftware("JChemPaint " + version);
		model.setGendate((Calendar.getInstance()).getTime().toString());
		JFrame jcpf = getNewFrame(model);
		return jcpf;
	}


	/**
	 *  Creates a new JChemPaintEditorPanel and assigns a given Model to it.
	 *
	 *@param  jchemPaintModel  The model to be assigned to the new frame.
	 *@return       The new JChemPaintFrame with its new JChemPaintModel
	 */
	public static JFrame getNewFrame(JChemPaintModel model)
	{
		JFrame frame = new JFrame();
		frame.addWindowListener(new JChemPaintPanel.AppCloser());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JChemPaintEditorPanel jcpep = new JChemPaintEditorPanel();
		frame.getContentPane().add(jcpep);
		jcpep.registerModel(model);
		jcpep.setJChemPaintModel(model);
		frame.setTitle(model.getTitle());
		return frame;
	}


	/**
	 *  Description of the Method
	 */
	public void setupPopupMenus(PopupController2D inputAdapter)
	{
    if (inputAdapter.getPopupMenu(new Atom("H")) == null)
		{
			inputAdapter.setPopupMenu(new Atom("H"), new JChemPaintPopupMenu(this, "atom"));
		}
		if (inputAdapter.getPopupMenu(new PseudoAtom("R")) == null)
		{
			inputAdapter.setPopupMenu(new PseudoAtom("R"), new JChemPaintPopupMenu(this, "pseudo"));
		}
		if (inputAdapter.getPopupMenu(new Bond()) == null)
		{
			inputAdapter.setPopupMenu(new Bond(), new JChemPaintPopupMenu(this, "bond"));
		}
		if (inputAdapter.getPopupMenu(new ChemModel()) == null)
		{
			inputAdapter.setPopupMenu(new ChemModel(), new JChemPaintPopupMenu(this, "chemmodel"));
		}
		if (inputAdapter.getPopupMenu(new Reaction()) == null)
		{
			inputAdapter.setPopupMenu(new Reaction(), new JChemPaintPopupMenu(this, "reaction"));
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public Image takeSnapshot()
	{
		Image snapImage = null;
		try
		{
			logger.info("Making snapshot... ");
			snapImage = createImage(this.getSize().width, this.getSize().height);
			logger.info("created...");

			Graphics snapGraphics = snapImage.getGraphics();
			paint(snapGraphics);

			logger.debug("painting succeeded.");
		} catch (NullPointerException e)
		{
			snapImage = null;
		}
		return snapImage;
	}


	/**
	 *  Method to notify this CDKChangeListener if something has changed in another
	 *  object
	 *
	 *@param  e  The EventObject containing information on the nature and source of
	 *      the event
	 */
	public void stateChanged(EventObject e)
	{
		//FIXME was dependant on ReallyPaintPanel.drawingnow
		drawingPanel.repaint();
	}


	/**
	 *  Enlarges the 'one molecule' dimension to fit a set of reactions or
	 *  molecules.
	 *
	 *@param  baseDim  Description of the Parameter
	 *@param  model    Description of the Parameter
	 *@return          Description of the Return Value
	 */
	private Dimension makeChemModelFit(Dimension baseDim, ChemModel model)
	{
		Dimension newDim = new Dimension(baseDim);
		// a bit ugly, but assume moleculeSet *or* reactionSet
		SetOfMolecules moleculeSet = model.getSetOfMolecules();
		if (moleculeSet != null)
		{
			newDim.height = newDim.height * (moleculeSet.getMoleculeCount());
			return newDim;
		}
		SetOfReactions reactionSet = model.getSetOfReactions();
		if (reactionSet != null)
		{
			newDim.height = newDim.height * (reactionSet.getReactionCount());
		}
		return newDim;
	}


	/**
	 *  Lays out the molecules in a SetOfMolecules, or reaction in a SetOfReactions
	 *  in a one column table.
	 *
	 *@param  baseDim  Description of the Parameter
	 *@param  model    Description of the Parameter
	 */
	private void layoutInTable(Dimension baseDim, ChemModel model)
	{
		// a bit ugly, but assume moleculeSet *or* reactionSet
		SetOfMolecules moleculeSet = model.getSetOfMolecules();
		if (moleculeSet != null)
		{
			Molecule[] mols = moleculeSet.getMolecules();
			for (int i = 0; i < mols.length; i++)
			{
				GeometryTools.center(mols[i], baseDim);
				GeometryTools.translate2D(mols[i], 0, baseDim.height * i);
			}
			return;
		}
		SetOfReactions reactionSet = model.getSetOfReactions();
		if (reactionSet != null)
		{
			Reaction[] reactions = reactionSet.getReactions();
			for (int i = 1; i <= reactions.length; i++)
			{
				AtomContainer ac = ReactionManipulator.getAllInOneContainer(reactions[i - 1]);
				GeometryTools.center(ac, baseDim);
				GeometryTools.translate2D(ac, 0, baseDim.height * (reactions.length - i));
			}
		}
	}


	/**
	 *  Gets the validatorEngine attribute of the JChemPaintEditorPanel class
	 *
	 *@return    The validatorEngine value
	 */
	public static ValidatorEngine getValidatorEngine()
	{
		if (engine == null)
		{
			engine = new ValidatorEngine();
			// default validators
			engine.addValidator(new BasicValidator());
			engine.addValidator(new ValencyValidator());
			engine.addValidator(new CDKValidator());
			engine.addValidator(new DictionaryValidator(dictdb));
			engine.addValidator(new PDBValidator());
		}
		return engine;
	}


	/**
	 *  Gets the dictionaryDatabase attribute of the JChemPaint object
	 *
	 *@return    The dictionaryDatabase value
	 */
	public static DictionaryDatabase getDictionaryDatabase()
	{
		if (dictdb == null)
		{
			dictdb = new DictionaryDatabase();
			try
			{
				File dictdir = new File(JCPPropertyHandler.getInstance().getJChemPaintDir(), "dicts");
				logger.info("User dict dir: ", dictdir);
				logger.debug("       exists: ", dictdir.exists());
				logger.debug("  isDirectory: ", dictdir.isDirectory());
				if (dictdir.exists() && dictdir.isDirectory())
				{
					File[] dicts = dictdir.listFiles();
					for (int i = 0; i < dicts.length; i++)
					{
						// loop over these files and load them
						try
						{
							FileReader reader = new FileReader(dicts[i]);
							String filename = dicts[i].getName();
							dictdb.readDictionary(reader, filename.substring(0, filename.indexOf('.')));
						} catch (IOException exception)
						{
							logger.error("Problem with reading macie dictionary...");
						}
					}
				}
				logger.info("Read these dictionaries: ");
				Enumeration dicts = dictdb.listDictionaries();
				while (dicts.hasMoreElements())
				{
					logger.info(" - ", dicts.nextElement().toString());
				}
			} catch (Exception exc)
			{
				logger.error("Could not handle dictionary initialization. Maybe I'm running in a sandbox.");
			}
		}
		return dictdb;
	}


	/*
	 *  Methods implementing the CDKEditBus interface
	 */
	/**
	 *  Gets the aPIVersion attribute of the JChemPaintEditorPanel object
	 *
	 *@return    The aPIVersion value
	 */
	public String getAPIVersion()
	{
		return "1.8";
	}


	/**
	 *  Description of the Method
	 *
	 *@param  s1  Description of the Parameter
	 *@param  s2  Description of the Parameter
	 */
	public void runScript(String s1, String s2)
	{
		logger.info("runScript method currently not supported");
	}


	/**
    *  Mandatory because JChemPaint is a ChangeListener. Used by other classes to
    *  update the information in one of the three statusbar fields.
    *
    *@param  e  ChangeEvent
    */
   //As long there is nothing it it, it shouldn't overwrite the function of JChemPaintPanel
    public void stateChanged(ChangeEvent e)
   {
       super.stateChanged(e);

        if (jchemPaintModel != null) {
          for (int i = 0; i < 3; i++) {
            String status = jchemPaintModel.getStatus(i);
            statusBar.setStatus(i + 1, status);
          }
        } else {
          if (statusBar != null) {
            statusBar.setStatus(1, "no model");
          }
        }
       // send event to plugins
       if (pluginManager != null)
       {
           pluginManager.stateChanged(new ChemObjectChangeEvent(this));
       }
   }



       /*
    *  Listener notification support methods START here
    */
   /**
    *  Adds a feature to the ChangeListener attribute of the SenecaDataset object
    *
    * @param  x  The feature to be added to the ChangeListener attribute
    */
   public void addChangeListener(ChangeListener x)
   {
       if (changeListeners == null) changeListeners = new EventListenerList();
       changeListeners.add(ChangeListener.class, x);
       // bring it up to date with current state
       x.stateChanged(new ChangeEvent(this));
   }


   /**
    *  Description of the Method
    *
    * @param  x  Description of Parameter
    */
   public void removeChangeListener(ChangeListener x)
   {
       changeListeners.remove(ChangeListener.class, x);
   }


   /**
    *  Description of the Method
    */
   protected void fireChange(String reason)
   {
       lastEventReason = reason;
       // Create the event:
       ChangeEvent c = new ChangeEvent(this);
       // Get the listener list
	   if (changeListeners != null) {
		   Object[] listeners = changeListeners.getListenerList();
		   // Process the listeners last to first
		   // List is in pairs, Class and instance
		   for (int i = listeners.length - 2; i >= 0; i -= 2)
		   {
			   if (listeners[i] == ChangeListener.class)
			   {
				   ChangeListener cl = (ChangeListener) listeners[i + 1];
				   cl.stateChanged(c);
			   }
		   }
	   }
   }

}


