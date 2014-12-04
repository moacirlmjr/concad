package concad;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import concad.core.constant.Constant;
import concad.core.log.PluginLogger;


public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "ConCAD";	
	private static Activator plugin;
	
	
	public Activator() {
		PluginLogger.logInfo("Activator / Constructor()");
	}

	public void start(BundleContext context) throws Exception {
		System.out.println("Activitor / Start()");
		PluginLogger.logInfo("Activator / Start()");
		super.start(context);
		plugin = this;		
		setDebugging(Constant.IS_DEBUGGING);
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		PluginLogger.logInfo("Activator / Stop()");
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}
	
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
