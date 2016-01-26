package indigo.interactive.generator;

import java.util.List;

import indigo.conflicts.test.OperationTest;
import indigo.interfaces.operations.Operation;
import indigo.interfaces.operations.OperationGenerator;
import indigo.runtime.AnalysisContext;

public abstract class AbstractOperationGenerator implements OperationGenerator {

	@Override
	public abstract List<List<Operation>> generate(OperationTest operationTest, AnalysisContext context);

}
