package aurora.ide.meta.gef.editors.source.gen.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.ResourceDescription;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.ide.meta.exception.GeneratorException;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.exception.TemplateNotBindedException;
import aurora.ide.meta.gef.FileFinder;
import aurora.ide.meta.gef.editors.models.ILink;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.io.ModelIOManager;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.search.core.Message;
import aurora.ide.search.core.Util;
import aurora.ide.search.ui.MessageFormater;

public class ProjectGenerator {
	private IProject project;
	private boolean isOverlap;
	private int fNumberOfFilesToScan;
	private IFile fCurrentFile;
	private int fNumberOfScannedFiles;
	private Shell shell;
	private IProject auroraProject;
	private IFolder screenFolder;
	private IContainer auroraWebFolder;
	private String errorMessage;
	private String header;

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public ProjectGenerator(IProject project, boolean isOverlap, Shell shell) {
		super();
		this.project = project;
		this.isOverlap = isOverlap;
		this.shell = shell;
	}

	public boolean isOverlap() {
		return isOverlap;
	}

	public void setOverlap(boolean isOverlap) {
		this.isOverlap = isOverlap;
	}

	public void go(final IProgressMonitor monitor)
			throws InvocationTargetException {

		boolean validate = validate();
		if (validate == false)
			throw new InvocationTargetException(new GeneratorException());

		FileFinder fileFinder = new FileFinder();
		try {
			project.accept(fileFinder);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

		header = createHeader();

		List<IResource> files = fileFinder.getResult();
		fNumberOfFilesToScan = files.size();
		Job monitorUpdateJob = new Job("source generator") { //$NON-NLS-1$
			private int fLastNumberOfScannedFiles = 0;

			public IStatus run(final IProgressMonitor inner) {
				while (!inner.isCanceled()) {
					final IFile file = fCurrentFile;
					if (file != null) {
						updateMonitor(monitor, file);
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						return Status.OK_STATUS;
					}
				}
				return Status.OK_STATUS;
			}

			private void updateMonitor(final IProgressMonitor monitor,
					final IFile file) {
				String fileName = file.getName();
				final Object[] args = { fileName,
						new Integer(fNumberOfScannedFiles),
						new Integer(fNumberOfFilesToScan) };
				monitor.subTask(MessageFormater.format(Message._scanning, args));
				int steps = fNumberOfScannedFiles - fLastNumberOfScannedFiles;
				monitor.worked(steps);
				fLastNumberOfScannedFiles += steps;
			}
		};

		monitor.beginTask(Messages.ProjectGenerator_Gen_source, files.size());
		monitorUpdateJob.setSystem(true);
		monitorUpdateJob.schedule();
		try {
			if (files != null) {
				for (int i = 0; i < files.size(); i++) {
					if (monitor.isCanceled())
						return;
					fCurrentFile = (IFile) files.get(i);
					fNumberOfScannedFiles++;
					processFile(fCurrentFile, monitor);
				}
			}
		} finally {
			monitorUpdateJob.cancel();
			monitor.done();
		}

	}

	private String createHeader() {
		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"; //$NON-NLS-1$
		String date = DateFormat.getDateInstance().format(new java.util.Date());
		String user = System.getProperty("user.name"); //$NON-NLS-1$
		String comment = "<!-- \n  $Author: " + user + " \n  $Date: " + date //$NON-NLS-1$ //$NON-NLS-2$
				+ " \n" //$NON-NLS-1$
				+ "  $Revision: 1.0 \n  $add by aurora_ide team.\n-->\n\r "; //$NON-NLS-1$

		return s + comment;
	}

	public boolean validate() {
		auroraProject = this.getAuroraProject();
		screenFolder = this.getScreenFolder();
		auroraWebFolder = this.getAuroraWebFolder();
		if (auroraProject == null) {
			errorMessage = Messages.ProjectGenerator_Project_error;
			return false;
		}
		if (auroraWebFolder == null) {
			errorMessage = Messages.ProjectGenerator_web_error;
			return false;
		}
		if (screenFolder == null) {
			errorMessage = Messages.ProjectGenerator__folder_erroe;
			return false;
		}
		return true;
	}

	private ViewDiagram loadFile(IFile file) {
		ViewDiagram diagram = null;
		InputStream is = null;
		try {
			is = file.getContents(false);

			CompositeLoader parser = new CompositeLoader();
			CompositeMap rootMap = parser.loadFromStream(is);
			ModelIOManager mim = ModelIOManager.getNewInstance();
			diagram = mim.fromCompositeMap(rootMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return diagram;
	}
	
	private void genNewFile(IFile newFile,String content) throws InvocationTargetException{
		InputStream is = new ByteArrayInputStream(content.getBytes());
		if (newFile.exists() && isOverlap) {
			try {
				// newFile.delete(true, monitor);
				newFile.setContents(is, true, false, null);
				return;
			} catch (CoreException e) {
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

			}
		}
		CreateFileOperation cfo = new CreateFileOperation(newFile, null,
				is, "create file.") { //$NON-NLS-1$
			@Override
			protected void setResourceDescriptions(
					ResourceDescription[] descriptions) {
				super.setResourceDescriptions(descriptions);
			}

			public IStatus computeExecutionStatus(IProgressMonitor monitor) {
				IStatus status = super.computeExecutionStatus(monitor);
				if (status.isOK()) {
					// Overwrite is not allowed when we are creating a new
					// file
					status = computeCreateStatus(false);
				}
				return status;
			}
		};

		try {
			cfo.execute(null, WorkspaceUndoUtil.getUIInfoAdapter(shell));
		} catch (ExecutionException e) {
			throw new InvocationTargetException(e);
		}
	}

	private void processFile(IFile fCurrentFile, IProgressMonitor monitor)
			throws InvocationTargetException {
		ScreenGenerator sg = new ScreenGenerator(project);
		try {
			IFile newFile = getNewFile(fCurrentFile);
			if (newFile.exists() && !isOverlap) {
				return;
			}
			ViewDiagram loadFile = this.loadFile(fCurrentFile);

			String genFile = sg.genFile(header, loadFile);
			
			genNewFile(newFile,genFile);
			
			genRelationFile(sg);
			

		} catch (TemplateNotBindedException e) {
		}
	}

	private void genRelationFile(ScreenGenerator sg) throws InvocationTargetException {
		Map<Object, String> linkIDs = sg.getScriptGenerator().getLinkIDs();
		Set<Object> keySet = linkIDs.keySet();	
		for (Object link : keySet) {
			if(link instanceof ILink){
				String openPath = ((ILink) link).getOpenPath();
				IPath p = new Path(openPath);
				if("uip".equalsIgnoreCase(p.getFileExtension())){
					ScreenGenerator dsg = new DisplayScreenGenerator(project,(ILink)link);
					try {
						IFile fCurrentFile = this.screenFolder.getFile(p);
						IFile newFile = this.getNewFile(p);
						if (newFile.exists() && !isOverlap) { 
							return;
						}
						ViewDiagram loadFile = this.loadFile(fCurrentFile);
						if(loadFile == null)
							continue;
						String genFile = dsg.genFile(header, loadFile);
						genNewFile(newFile,genFile);
					} catch (TemplateNotBindedException e) {
					}
				}
			}
		}
	}

	private IProject getAuroraProject() {
		AuroraMetaProject amp = new AuroraMetaProject(project);
		try {
			return amp.getAuroraProject();
		} catch (ResourceNotFoundException e) {
		}
		return null;
	}

	private IFolder getScreenFolder() {
		AuroraMetaProject amp = new AuroraMetaProject(project);
		try {
			return amp.getScreenFolder();
		} catch (ResourceNotFoundException e) {
		}
		return null;
	}

	private IContainer getAuroraWebFolder() {
		IContainer findWebInf = Util.findWebInf(auroraProject);
		return (IContainer) (findWebInf == null ? null : findWebInf.getParent());
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	private IFile getNewFile(IFile file) {
		IPath makeRelativeTo = file.getProjectRelativePath().makeRelativeTo(
				screenFolder.getProjectRelativePath());
		return getNewFile(makeRelativeTo);
	}
	private IFile getNewFile(IPath makeRelativeTo) {
		makeRelativeTo = makeRelativeTo.removeFileExtension();
		makeRelativeTo = makeRelativeTo.addFileExtension("screen"); //$NON-NLS-1$
		return auroraWebFolder.getFile(makeRelativeTo);
	}
}