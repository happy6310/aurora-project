package uncertain.ide.eclipse.editor.textpage.scanners;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

import uncertain.ide.eclipse.editor.textpage.ColorManager;
import uncertain.ide.eclipse.editor.textpage.IXMLColorConstants;
import uncertain.ide.eclipse.editor.textpage.XMLWhitespaceDetector;
import uncertain.ide.eclipse.editor.textpage.rules.AttributeRule;
import uncertain.ide.eclipse.editor.textpage.rules.XMLTagNameRule;




public class XMLTagScanner extends RuleBasedScanner {

	public XMLTagScanner(ColorManager manager) {
		IToken string =
			new Token(
				new TextAttribute(manager.getColor(IXMLColorConstants.STRING)));
		IToken tagName =
			new Token(
				new TextAttribute(manager.getColor(IXMLColorConstants.TAG_NAME)));
		
		IToken attribute =
			new Token(
				new TextAttribute(manager.getColor(IXMLColorConstants.ATTRIBUTE)));

//		IRule[] rules = new IRule[3];
//
////		rules[0] = new XMLTagNameRule(tagName, true);
//		// Add rule for double quotes
//		rules[0] = new SingleLineRule("\"", "\"", string, '\\');
//		// Add a rule for single quotes
//		rules[1] = new SingleLineRule("'", "'", string, '\\');
//		// Add generic whitespace rule.
//		rules[2] = new WhitespaceRule(new XMLWhitespaceDetector());

//		IRule[] rules = new IRule[4];
//
//		rules[0] = new XMLTagNameRule(tagName, true);
//		// Add rule for double quotes
//		rules[1] = new SingleLineRule("\"", "\"", string, '\\');
//		// Add a rule for single quotes
//		rules[2] = new SingleLineRule("'", "'", string, '\\');
//		// Add generic whitespace rule.
//		rules[3] = new WhitespaceRule(new XMLWhitespaceDetector());
//		
		
		
		IRule[] rules = new IRule[5];

		rules[0] = new XMLTagNameRule(tagName, true);
		// Add rule for double quotes
		rules[1] = new SingleLineRule("\"", "\"", string, '\\');
		// Add a rule for single quotes
		rules[2] = new SingleLineRule("'", "'", string, '\\');
		// Add generic whitespace rule.
		rules[3] = new WhitespaceRule(new XMLWhitespaceDetector());
		
		rules[4] = new AttributeRule(attribute,true);
		
		setRules(rules);
	}
}
