package aurora.plugin.source.gen.builders;

import java.util.Map;


public class CheckboxBuilder extends DefaultSourceBuilder {
	protected Map<String, String> getAttributeMapping() {
		Map<String, String> attributeMapping = super.getAttributeMapping();
		attributeMapping.put("bindTarget", "bindTarget");
		attributeMapping.put("prompt", "prompt");
		attributeMapping.put("width", "width");
		attributeMapping.put("text", "text");
		return attributeMapping;
	}
}
