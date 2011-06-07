/**
 * 
 */
package uncertain.ide.eclipse.editor.widgets.core;

import uncertain.composite.CompositeMap;

/**
 * @author linjinxiao
 *
 */
public interface ICellModifierListener {

	public void modify(CompositeMap record, String property, String value);
	
}
