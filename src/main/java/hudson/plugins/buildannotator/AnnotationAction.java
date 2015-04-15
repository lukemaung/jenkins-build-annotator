package hudson.plugins.buildannotator;

import hudson.model.AbstractBuild;
import hudson.model.Api;
import hudson.model.Hudson;
import hudson.model.Run;
import hudson.model.RunAction;

import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * <p>Build Annotation Plugin provides programmatic access to
 * annotate builds with arbitrary text. The annotations are
 * saved at the build level.</p>
 * 
 * <p>Provides CRUD for user-defined annotations for builds.</p>
 * <ul>
 *   <li>create/update - $BUILD_URL/annotations/set?key=$KEY&value=$VALUE</li>
 *   <li>retrieve - $BUILD_URL/annotations/api/xml (built-in Hudson api)</li>
 *   <li>delete - $BUILD_URL/annotations/delete?key=$KEY</li>
 * </ul>
 * 
 * @author lmaung
 *
 */
@ExportedBean
public class AnnotationAction implements RunAction {
    /**
     * points to the build for which the annotations will be attached to
     */
    private AbstractBuild<?, ?> build;

    /**
     * Sorted, concurrent map containing all build annotations
     */
    private ConcurrentSkipListMap<String, String> properties;

    public AnnotationAction(AbstractBuild<?, ?> build) {
        this.build = build;
        this.properties = new ConcurrentSkipListMap<String, String>();
    }

    @Exported
    public ConcurrentSkipListMap<String, String> getProperties() {
        return properties;
    }

    /**
     * Wrap class with api helper
     * 
     * @return
     */
    public Api getApi() {
        return new Api(this);
    }

    public String getIconFileName() {
        return "notepad.gif";
    }

    public String getDisplayName() {
        return "Build Annotations";
    }

    public String getUrlName() {
        return "annotations";
    }

    public void onLoad() {
    }

    public void onAttached(Run r) {
    }

    public void onBuildComplete() {
    }

    /**
     * <p>
     * Add/Update annotation (if key-value pair exists, the old one is replaced).
     * 
     * The URL endpoint is $BUILD_URL/annotations/set?key=$KEY&value=$VALUE
     * </p>
     * 
     * @param req
     * @param rsp
     * @param key
     * @param value
     * @throws IOException 
     * @throws ServletException 
     */
    public void doSet(StaplerRequest req, StaplerResponse rsp,
            @QueryParameter String key, @QueryParameter String value)
            throws IOException {
        if (properties == null) {
            // backward compatibility. old builds may not have this property
            properties = new ConcurrentSkipListMap<String, String>();
        }
        if (key != null && value != null) {
            properties.put(key, value);
            build.save();
        }
        if (rsp != null) {
            rsp.sendRedirect2(Hudson.getInstance().getRootUrl()
                    + build.getUrl());
        }
    }

    /**
     * <p>
     * Deletes annotation.
     * </p>
     * 
     * @param req
     * @param rsp
     * @param key
     * @throws IOException
     */
    public void doDelete(StaplerRequest req, StaplerResponse rsp,
            @QueryParameter String key) throws IOException {
        if (key != null && properties != null && properties.containsKey(key)) {
            properties.remove(key);
            build.save();
        }
        if (rsp != null) {
            rsp.sendRedirect2(Hudson.getInstance().getRootUrl()
                    + build.getUrl());
        }
    }
}
