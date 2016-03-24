package net.pdp7.v2f.core.web;

import java.util.Map;
import java.util.UUID;

public interface FormStateStore {

	UUID store(Map<String, String[]> parameterMap);
	Map<String, String[]> retrieve(UUID fromString);

}
