/*
 *  Author:  Chris Seguin
 *
 *  This software has been developed under the copyleft
 *  rules of the GNU General Public License.  Please
 *  consult the GNU General Public License for more
 *  details about use and distribution of this software.
 */
package org.acm.seguin.ide.command;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.acm.seguin.ide.common.PackageSelectorArea;
import org.acm.seguin.io.Saveable;
import org.acm.seguin.summary.PackageSummary;
import org.acm.seguin.summary.SummaryTraversal;
import org.acm.seguin.uml.UMLPackage;
import org.acm.seguin.uml.loader.Reloader;
import org.acm.seguin.uml.loader.ReloaderSingleton;

/**
 *  Creates a panel for the selection of packages to view.
 *
 *@author     Chris Seguin
 *@created    August 10, 1999
 */
public class PackageSelectorPanel extends PackageSelectorArea
		 implements ActionListener, Saveable, Reloader {
	/**
	 *  The root directory
	 */
	protected String rootDir = null;

	//  Instance Variables
	private HashMap viewList;
	private ButtonPanel buttons;

	//  Class Variables
	private static PackageSelectorPanel mainPanel;


	/**
	 *  Constructor for the PackageSelectorPanel object
	 *
	 *@param  root  The root directory
	 */
	protected PackageSelectorPanel(String root)
	{
		super();

		//  Setup the instance variables
		setRootDirectory(root);

		ReloaderSingleton.register(this);
		ReloaderSingleton.reload();

		buttons = new ButtonPanel(this);
		createFrame();
	}


	/**
	 *  Handle the button press events
	 *
	 *@param  evt  the event
	 */
	public void actionPerformed(ActionEvent evt)
	{
		String command = evt.getActionCommand();
		if (command.equals("Show")) {
			Object[] selection = listbox.getSelectedValues();
			for (int ndx = 0; ndx < selection.length; ndx++) {
				PackageSummary next = (PackageSummary) selection[ndx];
				showSummary(next);
			}
		}
		else if (command.equals("Hide")) {
			Object[] selection = listbox.getSelectedValues();
			for (int ndx = 0; ndx < selection.length; ndx++) {
				PackageSummary next = (PackageSummary) selection[ndx];
				hideSummary(next);
			}
		}
		else if (command.equals("Reload")) {
			ReloaderSingleton.reload();
		}
	}


	/**
	 *  Reloads the package information
	 */
	public void reload()
	{
		loadPackages();
	}


	/**
	 *  Saves the diagrams
	 *
	 *@exception  IOException  Description of Exception
	 */
	public void save() throws IOException
	{
		Iterator iter = viewList.keySet().iterator();
		while (iter.hasNext()) {
			PackageSummary packageSummary = (PackageSummary) iter.next();
			UMLPackage view = getPackage(packageSummary).getUmlPackage();
			view.save();
		}
	}


	/**
	 *  Loads the packages into the listbox and refreshes the UML diagrams
	 */
	public void loadPackages()
	{
		loadSummaries();

		super.loadPackages();

		//  Reloads the screens
		UMLPackage view = null;
		PackageSummary packageSummary = null;

		if (viewList == null) {
			viewList = new HashMap();
			return;
		}

		Iterator iter = viewList.keySet().iterator();
		while (iter.hasNext()) {
			packageSummary = (PackageSummary) iter.next();
			view = getPackage(packageSummary).getUmlPackage();
			view.reload();
		}
	}


	/**
	 *  Load the summaries
	 */
	public void loadSummaries()
	{
		//  Load the summaries
		(new SummaryTraversal(rootDir)).go();
	}


	/**
	 *  Set the root directory
	 *
	 *@param  root  the new root directory
	 */
	protected void setRootDirectory(String root)
	{
		if (root == null) {
			rootDir = System.getProperty("user.dir");
		}
		else {
			rootDir = root;
		}
	}


	/**
	 *  Get the package from the central store
	 *
	 *@param  summary  The package summary that we are looking for
	 *@return          The UML package
	 */
	protected UMLFrame getPackage(PackageSummary summary)
	{
		return (UMLFrame) viewList.get(summary);
	}


	/**
	 *  Add package to central store
	 *
	 *@param  summary  the summary we are adding
	 *@param  view     the associated view
	 */
	protected void addPackage(PackageSummary summary, UMLFrame view)
	{
		viewList.put(summary, view);
	}


	/**
	 *  Shows the summary
	 *
	 *@param  packageSummary  the summary to show
	 */
	private void showSummary(PackageSummary packageSummary)
	{
		UMLFrame view = getPackage(packageSummary);
		if ((view == null) && (packageSummary.getFileSummaries() != null)) {
			createNewView(packageSummary);
		}
		else if (packageSummary.getFileSummaries() == null) {
			//  Nothing to view
		}
		else {
			view.getUmlPackage().reload();
			view.setVisible(true);
		}
	}


	/**
	 *  Hide the summary
	 *
	 *@param  packageSummary  the summary to hide
	 */
	private void hideSummary(PackageSummary packageSummary)
	{
		UMLFrame view = getPackage(packageSummary);
		view.setVisible(false);
	}


	/**
	 *  Creates a new view
	 *
	 *@param  packageSummary  The packages summary
	 */
	private void createNewView(PackageSummary packageSummary)
	{
		UMLFrame frame = new UMLFrame(packageSummary);
		addPackage(packageSummary, frame);
	}


	/**
	 *  Creates the frame
	 */
	private void createFrame()
	{
		JFrame frame = new JFrame("Package Selector");

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JScrollPane scrollPane = getScrollPane();
		scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(buttons, BorderLayout.EAST);
		frame.getContentPane().add(panel);

		CommandLineMenu clm = new CommandLineMenu();
		frame.setJMenuBar(clm.getMenuBar(this));
		frame.addWindowListener(new ExitMenuSelection());
		frame.setSize(350, 350);
		frame.setVisible(true);
	}


	/**
	 *  Get the main panel
	 *
	 *@param  directory  Description of Parameter
	 *@return            The MainPanel value
	 */
	public static PackageSelectorPanel getMainPanel(String directory)
	{
		if (mainPanel == null) {
			if (directory == null) {
				return null;
			}

			mainPanel = new PackageSelectorPanel(directory);
		}

		mainPanel.setVisible(true);
		return mainPanel;
	}


	/**
	 *  Main program for testing purposes
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		if (args.length != 1) {
			System.out.println("Syntax:  java org.acm.seguin.uml.PackageSelectorPanel <dir>");
			return;
		}

		PackageSelectorPanel panel = PackageSelectorPanel.getMainPanel(args[0]);
		ReloaderSingleton.register(panel);
	}


	/**
	 *  Description of the Class
	 *
	 *@author    Chris Seguin
	 */
	private class ButtonPanel extends JPanel {
		private ActionListener listener;


		/**
		 *  Constructor for the ButtonPanel object
		 *
		 *@param  listener  Description of Parameter
		 */
		public ButtonPanel(ActionListener listener)
		{
			this.listener = listener;
			init();
			this.setSize(getPreferredSize());
		}


		/**
		 *  Gets the PreferredSize attribute of the ButtonPanel object
		 *
		 *@return    The PreferredSize value
		 */
		public Dimension getPreferredSize()
		{
			return new Dimension(110, 170);
		}


		/**
		 *  Gets the MaximumSize attribute of the ButtonPanel object
		 *
		 *@return    The MaximumSize value
		 */
		public Dimension getMaximumSize()
		{
			return getPreferredSize();
		}


		/**
		 *  Description of the Method
		 */
		private void init()
		{
			this.setLayout(null);

			//  Add the buttons
			JButton showButton = new JButton("Show");
			showButton.setBounds(0, 10, 100, 25);
			add(showButton);
			showButton.addActionListener(listener);

			JButton hideButton = new JButton("Hide");
			hideButton.setBounds(0, 50, 100, 25);
			add(hideButton);
			hideButton.addActionListener(listener);

			JButton reloadButton = new JButton("Reload");
			reloadButton.setBounds(0, 90, 100, 25);
			add(reloadButton);
			reloadButton.addActionListener(listener);

			JButton reloadAllButton = new JButton("Reload All");
			reloadAllButton.setBounds(0, 130, 100, 25);
			reloadAllButton.setEnabled(false);
			add(reloadAllButton);
		}
	}
}
