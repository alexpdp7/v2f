package net.pdp7.v2f.core.web;

import schemacrawler.schema.Column;

public class WidgetPolicy {

	protected int textAreaLengthThreshold;

	public WidgetPolicy(int textAreaLengthThreshold) {
		this.textAreaLengthThreshold = textAreaLengthThreshold;
	}

	public String getWidgetName(Column column) {
		if (column.getType().getName().equals("text") && column.getSize() > textAreaLengthThreshold) {
			return "textarea";
		}
		return "text";
	}

}
