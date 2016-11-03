package sat;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PosLiteral;

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
    static Clause IndividualClause=null;
    static Literal a;
    static Literal pos;

    public static Environment solve(Formula formula) {
        Environment env = new Environment();
        ImList <Clause> newClauses=formula.getClauses();
        Environment result=solve(newClauses,env);
        return result;
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

        ImList<Clause> newClauses = clauses;
        Environment result;

        if (newClauses.isEmpty()) {
            return env;
        } else {
            IndividualClause = newClauses.first();
            int minSize = IndividualClause.size();
            for (Clause clause : newClauses) {
                if (clause.size() < minSize) {
                    IndividualClause = clause;
                    minSize = clause.size();
                }
            }
            if (minSize == 0) {
                return null;
            } else {

                a = IndividualClause.chooseLiteral();
                pos = PosLiteral.make(a.getVariable());
                if (IndividualClause.isUnit()) {
                    newClauses = substitute(clauses, a);
                    if (a.negates(pos)) {
                        env = env.putFalse(a.getVariable());
                        result = solve(newClauses, env);
                        return result;
                    } else {
                        env = env.putTrue(a.getVariable());
                        result = solve(newClauses, env);
                        return result;
                    }
                } else {
                    env = env.putTrue(a.getVariable());
                    newClauses = substitute(clauses, pos);
                    result = solve(newClauses, env);
                    if (result == null) {
                        env = env.putFalse(a.getVariable());
                        newClauses = substitute(clauses, pos.getNegation());
                        result = solve(newClauses, env);
                        return result;
                    }
                    return result;
                }

            }
        }
    }





    public static boolean ifEmpty(ImList<Clause> clause){
        for(int i=0;i<clause.size();i++){
            if (clause.first().isEmpty()) {
                return true;
            } else {
                clause = clause.rest();
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
            if (clauses.first().contains(l)) {
                if(clauses.first().reduce(l)!=null){
                    clausesRest = clausesRest.add(clauses.first().reduce(l));}
            }
            else if (clauses.first().contains(l.getNegation())) {
                Clause x = new Clause();
                for (Literal n : clauses.first()) {
                    if (!n.equals(l.getNegation())) {
                        x = x.add(n);
                    }
                }
                clausesRest = clausesRest.add(x);
            } else {
                clausesRest = clausesRest.add(clauses.first());
            }
            clauses=clauses.rest();
        }
        return clausesRest;
    }
}

