package sat;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
        Environment env=new Environment();
        ImList<Clause> clsList = formula.getClauses();
        Clause IndividualClause=new Clause();
        IndividualClause = clsList.first();
        for(int i=0;i<clsList.size();i++){
            clsList=clsList.rest();
            if(IndividualClause.size()>clsList.first().size()){
                IndividualClause=clsList.first();
            }
        }
        Literal a=IndividualClause.chooseLiteral();
        if(IndividualClause.isUnit()){
            env=env.putTrue(IndividualClause.chooseLiteral().getVariable());
        }else {
            env = env.putTrue(IndividualClause.chooseLiteral().getVariable());
            if (solve(substitute(clsList,a),env) == null) {
                env = env.putFalse(IndividualClause.chooseLiteral().getVariable());
                a=a.getNegation();
            }
        }
        return solve(substitute(clsList,a),env);

        // TODO: implement this.
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     * 
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
        if(clauses==null){
            return env;
        }else if(SATSolver.ifEmpty(clauses)){
            return null;
        }else {
            Clause IndividualClause=new Clause();
            IndividualClause = clauses.first();
            for(int i=0;i<clauses.size();i++){
                clauses=clauses.rest();
                if(IndividualClause.size()>clauses.first().size()){
                    IndividualClause=clauses.first();
                }
            }
            Literal a=IndividualClause.chooseLiteral();
            if(IndividualClause.isUnit()){
                env=env.putTrue(IndividualClause.chooseLiteral().getVariable());
            }else {
                env = env.putTrue(IndividualClause.chooseLiteral().getVariable());
                if (solve(substitute(clauses,a),env) == null) {
                    env = env.putFalse(IndividualClause.chooseLiteral().getVariable());
                    a=a.getNegation();
                }
            }
            return solve(substitute(clauses,a),env);

        }

        // TODO: implement this.

    }

    public static boolean ifEmpty(ImList<Clause> clause){
        for(int i=0;i<clause.size();i++){
            if (clause.first().isEmpty()){
                return true;
            }else{
                clause=clause.rest();
            }
        }return false;
    }



    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     * 
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
            Literal l) {
        ImList<Clause> clausesRest = new EmptyImList<>();
        while (!clauses.isEmpty()) {
            if(clauses.first().contains(l)) {
                clausesRest = clausesRest.add(clauses.first().reduce(l));
            }else if(clauses.first().contains(l.getNegation())){
                Clause x=new Clause();
                for(Literal n:clauses.first()){
                    if(!n.equals(l.getNegation())){
                        x=x.add(n);
                    }
                }
                clausesRest=clausesRest.add(x);
            }else {
                clausesRest=clausesRest.add(clauses.first());
            }
            clauses=clauses.rest();
        }
        return clausesRest;
    }

    // TODO: implement this.

    }

