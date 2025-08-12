
package br.usp.lti.vrptwga;

import br.usp.lti.vrptwga.optimization.VRPTWProblem;
import br.usp.lti.vrptwga.optimization.VRPTWSolution;
import br.usp.lti.vrptwga.util.ProblemData;
import br.usp.lti.vrptwga.util.SolomonReader;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author viniciusdecarvalho
 */
public class Vrptwga {

    public static void main(String[] args) {
        InputStream is = null;
        try {
            Path path = Paths.get("TestInstance.txt");
            ProblemData data = new ProblemData(path);
            is = data.getInputStream();
            VehicleRoutingProblem.Builder builder = VehicleRoutingProblem.Builder.newInstance();
            SolomonReader reader = new SolomonReader(builder);
            reader.read(is);
            VehicleRoutingProblem problem = builder.build();
            List<String> cromossomo = List.of("R1", "2", "3", "R2", "4");
            VRPTWProblem optproblem = new VRPTWProblem(problem);
            VRPTWSolution sol = new VRPTWSolution(problem);
            sol.variables().addAll(cromossomo);
            optproblem.evaluate(sol);
            System.out.println(Arrays.toString(sol.objectives()));
            sol = optproblem.createSolution();
            System.out.println(Arrays.toString(sol.objectives()));
            
        } catch (IOException ex) {
            System.getLogger(Vrptwga.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                System.getLogger(Vrptwga.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }

    }
}
