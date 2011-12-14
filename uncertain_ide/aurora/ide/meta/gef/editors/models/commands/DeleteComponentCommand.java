package aurora.ide.meta.gef.editors.models.commands;

import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;

public class DeleteComponentCommand extends Command {
	protected Container container;
	protected AuroraComponent child;
	private int oriIndex = -1;

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public AuroraComponent getChild() {
		return child;
	}

	public void setChild(AuroraComponent child) {
		this.child = child;
	}

	// ------------------------------------------------------------------------
	// Overridden from Command

	public void execute() {
		oriIndex = container.getChildren().indexOf(child);
		container.removeChild(oriIndex);
	}

	public String getLabel() {
		return "Delete Component";
	}

	public void redo() {
		execute();
	}

	public void undo() {
		container.addChild(child, oriIndex);
	}
}