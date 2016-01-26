package indigo.interfaces.interactive;

import java.util.Collection;

import indigo.interfaces.operations.Operation;

public interface TestPairFilter {

	boolean toTest(Operation operation, Collection<Operation> successfulOps);

}
