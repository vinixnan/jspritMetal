package br.usp.lti.vrptwga.optimization;

import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity.JobActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

/**
 *
 * @author viniciusdecarvalho
 */
public class VRPTWSolution extends VehicleRoutingProblemSolution implements PermutationSolution {

    protected Map<String, Object> attributes;
    protected VehicleRoutingProblem problem;
    protected List<String> variables;

    public VRPTWSolution(VehicleRoutingProblem problem) {
        super(new ArrayList<>(), -1);
        this.problem = problem;
        this.attributes = new HashMap<>();
        this.variables = new ArrayList<>();
    }

    public VRPTWSolution(Collection<VehicleRoute> routes, double cost) {
        super(routes, cost);
        this.attributes = new HashMap<>();
        this.variables = new ArrayList<>();
    }

    @Override
    public VRPTWSolution copy() {
        return new VRPTWSolution(getRoutes(), getCost());
    }

    @Override
    public int getLength() {
        return variables().size();
    }

    public void routeToCromossome(Collection<VehicleRoute> routes) {
        variables.clear();
        for (VehicleRoute route : routes) {
            variables.add("R" + route.getVehicle().getId());
            for (TourActivity ta : route.getActivities()) {
                if (ta instanceof JobActivity) {
                    variables.add(((JobActivity) ta).getJob().getId());
                }
            }
        }
    }

    public void cromossomeToRoute(List<String> cromossome) {
        this.getRoutes().clear();

        Map<String, VehicleImpl> vehicleMap = problem.getVehicles().stream()
                .collect(Collectors.toMap(v -> v.getId(), v -> (VehicleImpl) v));

        Map<String, Service> serviceMap = problem.getJobs().values().stream()
                .filter(j -> j instanceof Service)
                .collect(Collectors.toMap(j -> j.getId(), j -> (Service) j));

        List<String> currentRoute = new LinkedList<>();
        VehicleImpl currentVehicle = null;

        for (String gene : cromossome) {
            if (gene.startsWith("R")) {
                if (!currentRoute.isEmpty() && currentVehicle != null) {
                    VehicleRoute route = buildRoute(currentRoute, currentVehicle, serviceMap);
                    this.getRoutes().add(route);
                }
                String vehicleId = gene.substring(1);
                currentVehicle = vehicleMap.get(vehicleId);
                currentRoute.clear();
            } else {
                currentRoute.add(gene);
            }
        }

        if (!currentRoute.isEmpty() && currentVehicle != null) {
            VehicleRoute route = buildRoute(currentRoute, currentVehicle, serviceMap);
            this.getRoutes().add(route);
        }

    }

    private VehicleRoute buildRoute(List<String> clientIds, VehicleImpl vehicle, Map<String, Service> serviceMap) {
        VehicleRoute.Builder builder = VehicleRoute.Builder.newInstance(vehicle);
        for (String clientId : clientIds) {
            Service service = serviceMap.get(clientId);
            if (service != null) {
                builder.addService(service);
            }
        }
        return builder.build();
    }

    @Override
    public List<String> variables() {
        return variables;
    }

    @Override
    public double[] objectives() {
        double[] obj = new double[2];
        obj[0] = this.getCost();
        obj[1] = this.getCost();
        return obj;
    }

    @Override
    public double[] constraints() {
        return new double[0]; // Pode ser ajustado se restrições forem avaliadas
    }

    @Override
    public Map<String, Object> attributes() {
        return attributes;
    }
}
