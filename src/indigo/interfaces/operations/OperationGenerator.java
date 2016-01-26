package indigo.interfaces.operations;

import java.util.List;

import indigo.conflicts.test.OperationTest;
import indigo.runtime.AnalysisContext;

public interface OperationGenerator {

	public List<List<Operation>> generate(OperationTest operationTest, AnalysisContext context);

}
