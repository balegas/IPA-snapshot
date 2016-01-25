package indigo.generic;

import java.util.List;

import indigo.AnalysisContext;
import indigo.interfaces.Operation;
import indigo.interfaces.OperationGenerator;

public abstract class AbstractOperationGenerator implements OperationGenerator {

	@Override
	public abstract List<List<Operation>> generate(OperationTest operationTest, AnalysisContext context);

}
