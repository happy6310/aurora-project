package uncertain.ide.eclipse.node.action;

import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.AbstractCMViewer;
import uncertain.ide.help.LocaleMessage;

public class PasteAction extends ActionListener {
	AbstractCMViewer viewer;

	public PasteAction(AbstractCMViewer viewer,int actionStyle) {
		this.viewer = viewer;
		setActionStyle(actionStyle);
	}

	public void run() {
		viewer.pasteElement();
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(LocaleMessage.getString("paste.icon"));
	}

	public String getDefaultText() {
		return LocaleMessage.getString("paste");
	}
}
