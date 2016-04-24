
package com.quexten.ravtech.animation;

public interface VariableAccessor {

	void setVariable (int variableID, Object value);

	int getVariableId (String variableName);

	Object getVariable (int variableID);

	String[] getVariableNames ();

	Object[] getValiables ();
}
