package editor.textpage.hyperlinks;

import ide.AuroraPlugin;
import helpers.ApplicationException;
import helpers.DialogUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import bm.BMUtil;


public class BMFileHyperlink implements IHyperlink {
	private IRegion region;
	private ITextViewer viewer;

	public BMFileHyperlink(IRegion region, ITextViewer viewer) {
		this.region = region;
		this.viewer = viewer;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getHyperlinkText() {
		return null;
	}

	public String getTypeLabel() {
		return null;
	}

	public void open() {
		IDocument doc = viewer.getDocument();
		try {
			String classPath = doc.get(region.getOffset(), region.getLength());
			IResource file = BMUtil.getBMResourceFromClassPath(classPath);
			if (!(file instanceof IFile)) {
				DialogUtil.showErrorMessageBox("资源" + file + "不是一个文件类型");
				return;
			}
			IDE.openEditor(AuroraPlugin.getActivePage(), (IFile) file);
		} catch (PartInitException e) {
			DialogUtil.showExceptionMessageBox(e);
		} catch (BadLocationException e) {
			DialogUtil.showExceptionMessageBox(e);
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);

		}
	}
}
