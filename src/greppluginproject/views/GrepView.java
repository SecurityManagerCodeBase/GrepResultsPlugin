package greppluginproject.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.*;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class GrepView extends ViewPart {

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "greppluginproject.views.New4View";

  private TableViewer viewer;
  private Action action1;
  private Action action2;
  private Action doubleClickAction;
  private String currentDir = null;
  private ArrayList<GrepItem> currentInfo = null;
  private File currentFile = null;

  /*
   * The content provider class is responsible for providing objects to the
   * view. It can wrap existing objects in adapters or simply return objects
   * as-is. These objects may be sensitive to the current input of the view, or
   * ignore it and always show the same content (like Task List, for example).
   */

  class ViewContentProvider implements IStructuredContentProvider {
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }

    public void dispose() {
    }

    public Object[] getElements(Object parent) {
      System.out.println("Running getElements");
      if (currentFile != null) {
	currentInfo = parseFile(currentFile);
      }
      else
      {
	currentInfo = null;
      }
      int currentInfoLength = 0;
      if (currentInfo == null) {
	return new Object[] { "Set Main Source Directory", "Load File" };
      } else {
	currentInfoLength = currentInfo.size();
	Object[] resultItems = new Object[2 + currentInfoLength];
	resultItems[0] = "Set Main Source Directory";
	resultItems[1] = "Load File";
	System.out.println("Current info size: " + currentInfoLength);
	for (int i = 0; i < currentInfoLength; i++) {
	  //System.out.println(String.valueOf(i));
	  resultItems[2 + i] =  currentInfo.get(i);

	}
	return resultItems;
      }

    }
  }

  class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
    public String getColumnText(Object obj, int index) {
      return getText(obj);
    }

    public Image getColumnImage(Object obj, int index) {
      if (obj instanceof String) {
	return getImage(obj);
      } else {
	return null;
      }
    }

    public Image getImage(Object obj) {
      return PlatformUI.getWorkbench().getSharedImages()
	  .getImage(ISharedImages.IMG_OBJ_ELEMENT);
    }
  }

  class NameSorter extends ViewerSorter {
  }

  /**
   * The constructor.
   */
  public GrepView() {
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize
   * it.
   */
  public void createPartControl(Composite parent) {
    viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(new ViewContentProvider());
    viewer.setLabelProvider(new ViewLabelProvider());
    // viewer.setSorter(new NameSorter());
    viewer.setInput(getViewSite());

    // Create the help context id for the viewer's control
    PlatformUI.getWorkbench().getHelpSystem()
	.setHelp(viewer.getControl(), "GrepPluginProject.viewer");
    makeActions(parent);
    // hookContextMenu();
    hookDoubleClickAction();
    // contributeToActionBars();
  }

  /*
   * private void hookContextMenu() { MenuManager menuMgr = new
   * MenuManager("#PopupMenu"); menuMgr.setRemoveAllWhenShown(true);
   * menuMgr.addMenuListener(new IMenuListener() { public void
   * menuAboutToShow(IMenuManager manager) {
   * SampleView.this.fillContextMenu(manager); } }); Menu menu =
   * menuMgr.createContextMenu(viewer.getControl());
   * viewer.getControl().setMenu(menu); getSite().registerContextMenu(menuMgr,
   * viewer); }
   * 
   * 
   * private void contributeToActionBars() { IActionBars bars =
   * getViewSite().getActionBars(); fillLocalPullDown(bars.getMenuManager());
   * fillLocalToolBar(bars.getToolBarManager()); }
   * 
   * 
   * private void fillLocalPullDown(IMenuManager manager) {
   * manager.add(action1); manager.add(new Separator()); manager.add(action2); }
   * 
   * private void fillContextMenu(IMenuManager manager) { manager.add(action1);
   * manager.add(action2); // Other plug-ins can contribute there actions here
   * manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS)); }
   * 
   * private void fillLocalToolBar(IToolBarManager manager) {
   * manager.add(action1); manager.add(action2); }
   */

  private void makeActions(final Composite parent) {
    /*
     * action1 = new Action() { public void run() {
     * showMessage("Action 1 executed"); } }; action1.setText("Action 1");
     * action1.setToolTipText("Action 1 tooltip");
     * action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
     * getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
     * 
     * action2 = new Action() { public void run() {
     * showMessage("Action 2 executed"); } }; action2.setText("Action 2");
     * action2.setToolTipText("Action 2 tooltip");
     * action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
     * getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
     */
    doubleClickAction = new Action() {
      public void run() {
	ISelection selection = viewer.getSelection();
	Object obj = ((IStructuredSelection) selection).getFirstElement();
	if (obj == null) {
	  Exception e = new Exception();
	  e.printStackTrace();
	  System.err.println("Null Selection Exception");
	  System.exit(1);
	} else if (obj.toString().equals("Set Main Source Directory")) {
	  JFileChooser dirChooser = new JFileChooser(currentDir);
	  dirChooser
	      .setDialogTitle("Select Directory To Read Source Files From");
	  dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	  dirChooser.setAcceptAllFileFilterUsed(false);
	  if (dirChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    String selectedDir = dirChooser.getSelectedFile().toString();
	    System.out.println("Selected Directory: " + selectedDir);
	    currentDir = selectedDir;
	    currentFile=null;
	    viewer.refresh();
	  }
	} else if (obj.toString().equals("Load File")) {
	  JFileChooser fileChooser = new JFileChooser(currentDir);
	  fileChooser.setDialogTitle("Select Grep Result File");
	  FileNameExtensionFilter filter = new FileNameExtensionFilter(
	      "Grep Output Files", "txt");
	  fileChooser.setFileFilter(filter);
	  if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    File file = fileChooser.getSelectedFile();
	    currentFile = file;
	    viewer.refresh();
	    // createPartControl(parent);
	  }
	}  else if (obj.toString().contains(":")) {
	  //System.out.println("Selected line: "+obj.toString());
	  if (obj instanceof GrepItem) {
	    //System.out.println("type of grep item");
	    GrepItem grepItem = (GrepItem) obj;
	    IWorkbenchPage page = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage();
	    if (page == null)
	    {
	      System.out.println("page is null");
	      return;
	    }
	    File fileToOpen = grepItem.getPath().toFile();

	    if (fileToOpen.exists() && fileToOpen.isFile()) {
	      IFileStore fileStore = EFS.getLocalFileSystem().getStore(
		  fileToOpen.toURI());

	      try {
		IDE.openEditorOnFileStore(page, fileStore);
		goToLine(page.getActiveEditor(), grepItem.getLineNumber());
	      } catch (PartInitException e) {
		System.out.println("file open exception");
	      }
	      // match to grep result - adjust editor
	    }
	    else
	    {
	      int startOfRelativePath = grepItem.getPath().toString().indexOf(currentDir)+currentDir.length();
	      String errorMessage = "file doesn't exist: "+grepItem.getPath().toString()+
		  "\nMain Source Directory: "+currentDir+"\nRelative File Path: "+
		  grepItem.getPath().toString().substring(startOfRelativePath);
	      System.out.println("start of relative path position: "+startOfRelativePath);
	      JOptionPane.showMessageDialog(null, errorMessage,
		      "Error", JOptionPane.ERROR_MESSAGE);
	      System.out.println(errorMessage);
	    }
	  }
	  else
	  {
	    System.out.println("not of type grepitem");
	  }
	}
	else
	{
	  //System.out.println("not a line: "+obj.toString());
	}
      }
    };
  }

  private static void goToLine(IEditorPart editorPart, int lineNumber) {
    if (!(editorPart instanceof ITextEditor) || lineNumber <= 0) {
      return;
    }
    ITextEditor editor = (ITextEditor) editorPart;
    IDocument document = editor.getDocumentProvider().getDocument(
      editor.getEditorInput());
    if (document != null) {
      IRegion lineInfo = null;
      try {
        // line count internaly starts with 0, and not with 1 like in
        // GUI
        lineInfo = document.getLineInformation(lineNumber - 1);
      } catch (BadLocationException e) {
        // ignored because line number may not really exist in document,
        // we guess this...
      }
      if (lineInfo != null) {
        editor.selectAndReveal(lineInfo.getOffset(), lineInfo.getLength());
      }
    }
  }
  
  private void hookDoubleClickAction() {
    viewer.addDoubleClickListener(new IDoubleClickListener() {
      public void doubleClick(DoubleClickEvent event) {
	doubleClickAction.run();
      }
    });
  }

  private void showMessage(String message) {
    MessageDialog.openInformation(viewer.getControl().getShell(),
	"Grep SecurityManager View", message);
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  public void setFocus() {
    viewer.getControl().setFocus();
  }

  private ArrayList<GrepItem> parseFile(File file) {
    Scanner fileScanner;
    ArrayList<GrepItem> grepResults = new ArrayList<GrepItem>();
    Path currentPath = null;
    try {
      fileScanner = new Scanner(file);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
    // currentFile = file;
    while (fileScanner.hasNextLine()) {
      String line = fileScanner.nextLine();
      line = line.trim();
      if (line.equals("")) {
	//do nothing in this case - so don't display the line separators
	//grepResults.add(new GrepItem(currentPath, -99, line));
	
      } else if (line.startsWith("./", 0)) {
	if (currentDir == null) {
	  JOptionPane.showMessageDialog(null, "Haven't set source directory",
	      "Error", JOptionPane.ERROR_MESSAGE);
	  fileScanner.close();
	  return null;
	}
	currentPath = Paths.get(currentDir, line.substring(1));
	grepResults.add(new GrepItem(currentPath, -99, line));
      } else if (line.contains(":")) {
	if (currentPath == null) {
	  Exception e = new Exception();
	  e.printStackTrace();
	  System.err.println("Unset Grep Path Exception");
	  System.exit(1);
	}
	String lineNumberString = line.substring(0, line.indexOf(":"));
	grepResults.add(new GrepItem(currentPath,
	    (new Integer(lineNumberString)).intValue(), line));
      } else {
	Exception e = new Exception();
	e.printStackTrace();
	System.err.println("Unhandled Line Exception");
	System.exit(1);
      }
    }
    fileScanner.close();
    return grepResults;
  }

}