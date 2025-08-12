package br.usp.lti.vrptwga.optimization;

import com.graphhopper.jsprit.core.analysis.SolutionAnalyser;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.uma.jmetal.problem.Problem;

/**
 * Problem definition for VRPTW using jMetal and jsprit.
 */
public class VRPTWProblem implements Problem<VRPTWSolution> {

    protected final VehicleRoutingProblem jspritProblem;
    protected final int numberOfObjectives = 2;
    protected final int numberOfConstraints = 0;
    protected final Random random = new Random();

    public VRPTWProblem(VehicleRoutingProblem problem) {
        this.jspritProblem = problem;
    }

    @Override
    public int numberOfVariables() {
        return jspritProblem.getJobs().size() + jspritProblem.getVehicles().size();
    }

    @Override
    public int numberOfObjectives() {
        return numberOfObjectives;
    }

    @Override
    public int numberOfConstraints() {
        return numberOfConstraints;
    }

    @Override
    public String name() {
        return "VRPTW";
    }

    @Override
    public VRPTWSolution evaluate(VRPTWSolution solution) {
        solution.cromossomeToRoute(solution.variables());

        SolutionAnalyser analyser = new SolutionAnalyser(jspritProblem, solution, jspritProblem.getTransportCosts());
        double cost = analyser.getTotalCosts();

        solution.setCost(cost);
        solution.objectives()[0] = cost;
        solution.objectives()[1] = cost;

        return solution;
    }

    @Override
    public VRPTWSolution createSolution() {
        List<Service> services = jspritProblem.getJobs().values().stream()
                .filter(j -> j instanceof Service)
                .map(j -> (Service) j)
                .collect(Collectors.toList());

        List<VehicleImpl> vehicles = jspritProblem.getVehicles().stream()
                .map(v -> (VehicleImpl) v)
                .collect(Collectors.toList());

        Collections.shuffle(services, random);

        List<String> cromossome = new ArrayList<>();
        int vIndex = 0;

        for (Service service : services) {
            // Com probabilidade de 20% ou se cromossomo estiver vazio, inicia nova rota
            if (cromossome.isEmpty() || random.nextDouble() < 0.2) {
                VehicleImpl vehicle = vehicles.get(vIndex % vehicles.size());
                cromossome.add("R" + vehicle.getId());
                vIndex++;
            }
            cromossome.add(service.getId());
        }

        // Cria solução diretamente com cromossomo de strings
        VRPTWSolution solution = new VRPTWSolution(jspritProblem);
        solution.variables().clear();              // limpa lista de genes
        solution.variables().addAll(cromossome);

        // Avaliação via Jsprit
        return evaluate(solution);
    }

    public Class<?> getSolutionType() {
        return VRPTWSolution.class;
    }

}
