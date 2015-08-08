package indigo.impl.javaclass;

import indigo.AbstractSpecification;
import indigo.IndigoAnalyzer;
import indigo.abtract.Clause;
import indigo.abtract.Operation;
import indigo.abtract.PredicateAssignment;
import indigo.annotations.Invariant;
import indigo.effects.AssertionPredicate;
import indigo.effects.AssignPredicate;
import indigo.effects.CounterPredicate;
import indigo.effects.Effect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class JavaClassSpecification implements AbstractSpecification {

	private final Class<?> javaClass;
	private final Set<Operation> operations;
	// TODO: Should reduce to a single invariant?
	private final Set<Clause> invariants;
	private final Map<PredicateAssignment, Set<Clause>> affectedInvariantPerClauses;

	private final static Logger analysisLog = Logger.getLogger(IndigoAnalyzer.class.getName());

	public JavaClassSpecification(Class<?> javaClass) {
		this.javaClass = javaClass;
		this.invariants = readInvariants();
		this.operations = readOperations();
		this.affectedInvariantPerClauses = computeInvariantsForPredicate();
	}

	private Set<Clause> readInvariants() {
		Set<Clause> invariants = Sets.newHashSet();
		for (Invariant i : javaClass.getAnnotationsByType(Invariant.class)) {
			Clause ic = new JavaInvariantClause(i.value());
			invariants.add(ic);
		}
		return ImmutableSet.copyOf(invariants);
	}

	private Set<Operation> readOperations() {
		Set<Operation> operations = new HashSet<>();
		ArrayList<Effect> effectList = new ArrayList<Effect>();
		// Set<PredicateAssignment> result = new HashSet<PredicateAssignment>();
		for (Method m : javaClass.getMethods()) {
			ArrayList<Effect> opEffectList = new ArrayList<Effect>();
			opEffectList.addAll(CounterPredicate.listFor(m));
			opEffectList.addAll(AssertionPredicate.listFor(m));
			opEffectList.addAll(AssignPredicate.listFor(m));
			effectList.addAll(opEffectList);
			Operation operation = new JavaOperation(m.getName(), opEffectList);
			operations.add(operation);
		}
		// effectList.forEach(e -> result.add(new
		// PredicateAssignmentFromEffect(e)));
		return ImmutableSet.copyOf(operations);
	}

	private Map<PredicateAssignment, Set<Clause>> computeInvariantsForPredicate() {
		Collection<PredicateAssignment> predicateAssignment = getAllOperationEffects();
		Map<PredicateAssignment, Set<Clause>> affectedInvariantPerClauses = new HashMap<>();
		analysisLog.fine("Invariants affected by operations in the workload:");
		predicateAssignment.forEach(pa -> {
			Set<Clause> s = Sets.newHashSet();
			for (Invariant i : javaClass.getAnnotationsByType(Invariant.class)) {
				JavaInvariantClause ie = new JavaInvariantClause(i.value());
				if (pa.hasEffectIn(ie)) {
					s.add(ie.copyOf());
					analysisLog.fine("Predicate " + pa + " present in invariant clauses " + s + " for operation " + pa.opName());
				}
			}
			ImmutableSet<Clause> immutable = ImmutableSet.copyOf(s);
			affectedInvariantPerClauses.put(pa, immutable);
		});

		if (affectedInvariantPerClauses.isEmpty()) {
			analysisLog.warning("No invariants are affected by operations in the workload.");
		}
		return ImmutableMap.copyOf(affectedInvariantPerClauses);
	}

	@Override
	public List<PredicateAssignment> getAllOperationEffects() {
		List<PredicateAssignment> predicates = new ArrayList<>();
		for (Operation op : operations) {
			Collection<PredicateAssignment> pred = op.getPredicateAssignments();
			predicates.addAll(pred);
		}
		return predicates;
	}

	@Override
	public Set<Operation> getOperations() {
		return ImmutableSet.copyOf(operations);
	}

	@Override
	public Set<Clause> getInvariantClauses() {
		return invariants;
	}

	@Override
	public String getAppName() {
		return javaClass.getName();
	}

	@Override
	public Map<PredicateAssignment, Set<Clause>> collectInvariantsForPredicate() {
		return affectedInvariantPerClauses;
	}

	@Override
	public Clause newEmptyInvClause() {
		return new JavaInvariantClause();
	}

	@Override
	public Clause newTrueClause() {
		return new JavaInvariantClause("true");
	}

}
