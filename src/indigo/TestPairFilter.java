package indigo;

import java.util.Collection;

import indigo.interfaces.Operation;

public interface TestPairFilter {

	boolean toTest(Operation operation, Collection<Operation> successfulOps);

}
