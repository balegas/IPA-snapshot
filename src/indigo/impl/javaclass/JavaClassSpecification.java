package indigo.impl.javaclass;

import indigo.IndigoAnalyzer;
import indigo.ProgramSpecification;
import indigo.impl.javaclass.effects.AssertionPredicate;
import indigo.impl.javaclass.effects.AssignPredicate;
import indigo.impl.javaclass.effects.CounterPredicate;
import indigo.impl.javaclass.effects.JavaEffect;
import indigo.interfaces.Invariant;
import indigo.interfaces.Operation;
import indigo.interfaces.PredicateAssignment;

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

public class JavaClassSpecification implements ProgramSpecification {

	private final Class<?> javaClass;
	private final Set<Operation> operations;
	// TODO: Should reduce to a single invariant?
	private final Set<Invariant> invariants;
	private final Map<PredicateAssignment, Set<Invariant>> affectedInvariantPerClauses;

	private final static Logger analysisLog = Logger.getLogger(IndigoAnalyzer.class.getName());

	public JavaClassSpecification(Class<?> javaClass) {
		this.javaClass = javaClass;
		this.invariants = readInvariants();
		this.operations = readOperations();
		this.affectedInvariantPerClauses = computeInvariantsForPredicate();
	}

	private Set<Invariant> readInvariants() {
		Set<Invariant> invariants = Sets.newHashSet();
		for (indigo.annotations.Invariant i : javaClass.getAnnotationsByType(indigo.annotations.Invariant.class)) {
			Invariant ic = new JavaInvariantClause(i.value());
			invariants.add(ic);
		}
		return ImmutableSet.copyOf(invariants);
	}

	private Set<Operation> readOperations() {
		Set<Operation> operations = new HashSet<>();
		for (Method m : javaClass.getMethods()) {
			ArrayList<JavaEffect> opEffectList = new ArrayList<JavaEffect>();
			opEffectList.addAll(CounterPredicate.listFor(m));
			opEffectList.addAll(AssertionPredicate.listFor(m));
			opEffectList.addAll(AssignPredicate.listFor(m));
			Operation operation = new JavaOperation(m.getName(), opEffectList);
			operations.add(operation);
		}
		return ImmutableSet.copyOf(operations);
	}

	private Map<PredicateAssignment, Set<Invariant>> computeInvariantsForPredicate() {
		Collection<PredicateAssignment> predicateAssignment = getAllOperationEffects();
		Map<PredicateAssignment, Set<Invariant>> affectedInvariantPerClauses = new HashMap<>();
		analysisLog.fine("Invariants affected by operations in the workload:");
		predicateAssignment.forEach(pa -> {
			Set<Invariant> s = Sets.newHashSet();
			for (indigo.annotations.Invariant i : javaClass.getAnnotationsByType(indigo.annotations.Invariant.class)) {
				JavaInvariantClause ie = new JavaInvariantClause(i.value());
				if (pa.affects(ie)) {
					s.add(ie.copyOf());
					analysisLog.fine("Predicate " + pa + " present in invariant clauses " + s + " for operation " + pa.getOperationName());
				}
			}
			ImmutableSet<Invariant> immutable = ImmutableSet.copyOf(s);
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
			Collection<PredicateAssignment> pred = op.getEffects();
			predicates.addAll(pred);
		}
		return predicates;
	}

	@Override
	public Set<Operation> getOperations() {
		return ImmutableSet.copyOf(operations);
	}

	@Override
	public Set<Invariant> getInvariantClauses() {
		return ImmutableSet.copyOf(invariants);
	}

	@Override
	public String getAppName() {
		return javaClass.getName();
	}

	@Override
	public Map<PredicateAssignment, Set<Invariant>> invariantsAffectedPerPredicateAssignemnt() {
		return ImmutableMap.copyOf(affectedInvariantPerClauses);
	}

	@Override
	public Invariant newEmptyInv() {
		return new JavaInvariantClause("true");
	}

}
