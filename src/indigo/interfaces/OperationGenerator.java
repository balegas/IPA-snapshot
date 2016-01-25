package indigo.interfaces;

import java.util.List;

import indigo.AnalysisContext;
import indigo.generic.OperationTest;

public interface OperationGenerator {

	public List<List<Operation>> generate(OperationTest operationTest, AnalysisContext context);

}
