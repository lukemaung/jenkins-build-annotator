package hudson.plugins.buildannotator;

import hudson.Extension;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixRun;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.tasks.BuildStepMonitor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.logging.Logger;

/**
 * <p>Injects a JobProperty that is used to add AnnotationAction (which points 
 * to a collection of key-value pairs representing annotations) to all builds.
 * There are no configuration options.</p>
 * 
 * @author lmaung
 *
 */
public class BuildAnnotatorProperty extends JobProperty<AbstractProject<?, ?>> {
    private static final Logger LOGGER = Logger
            .getLogger(BuildAnnotatorProperty.class.getName());
    private boolean enable;

    @DataBoundConstructor
    public BuildAnnotatorProperty(boolean enable) {
        this.enable = enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean getEnable() {
        return enable;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return super.getRequiredMonitorService();
    }

    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        if (enable) {
            try {
                if (build != null) {
                    build.addAction(new AnnotationAction(build));
                }
            } catch (Throwable t) {
                LOGGER.severe("Exception adding AnnotationAction: "
                        + t.getMessage());
            }
        }
        return true;
    }

    // this marker indicates Hudson that this is an implementation of an extension point.
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {

        @Override
        public JobProperty<?> newInstance(StaplerRequest req,
                JSONObject formData) throws FormException {
            boolean enable = formData.containsKey("enable");
            return new BuildAnnotatorProperty(enable);
        }

        @Override
        public String getDisplayName() {
            return "Build Annotations";
        }
    }

    /**
     * Catch the beginning of a matrix run (the child builds) and add annotation instances.
     */
    @Extension
    public static final RunListener<Run> annotatorRunListener = new RunListener<Run>() {
        @Override
        public void onStarted(Run run, TaskListener listener) {
            if (run != null && run instanceof MatrixRun) {
                MatrixRun mr = (MatrixRun) run;
                MatrixBuild mb = mr.getParentBuild();
                if (mb != null && mb.getAction(AnnotationAction.class) != null) {
                    // only allow if matrix build (the master) has the annotation plugin enabled
                    mr.addAction(new AnnotationAction(mr));
                }
            }
            super.onStarted(run, listener);
        }
    };
}
