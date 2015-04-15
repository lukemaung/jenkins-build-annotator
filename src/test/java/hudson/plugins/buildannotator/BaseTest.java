package hudson.plugins.buildannotator;

import hudson.model.AbstractBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.slaves.DumbSlave;

import java.util.Collection;

import org.jvnet.hudson.test.HudsonTestCase;

public abstract class BaseTest extends HudsonTestCase {
    protected AbstractBuild build;
    protected Collection<? extends Job> children;
    private static final String JOB_PREFIX = "testjob_";

    protected DumbSlave createSlaveNode() throws Exception {
        DumbSlave slave = createOnlineSlave();
        return slave;
    }

    protected void setUpAndRunProject(DumbSlave slave) throws Exception {
        FreeStyleProject project = createFreeStyleProject();
        project.renameTo(JOB_PREFIX + System.currentTimeMillis());
        BuildAnnotatorProperty prop = new BuildAnnotatorProperty(true);
        project.setAssignedNode(slave);
        project.addProperty(prop);
        project.save();
        build = project.scheduleBuild2(0).get();
    }
}
