package concad.core.constant;

import concad.Activator;

/**
 * This class contains constants used by the application.
 * 
 * @author Luciano Sampaio
 */
public abstract class Constant {

	public static final boolean IS_DEBUGGING = true;
	public static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";
	public static final String NATURE_ID = Activator.PLUGIN_ID + ".CONCAD_NATURE";
	public static final String BUILDER_ID = Activator.PLUGIN_ID + ".CONCAD_BUILDER";
	public static final String VIEW_ID = Activator.PLUGIN_ID + ".CONCAD_VIEW";
	// public static final String MARKER_ID = Activator.PLUGIN_ID +
	// ".TCM_ESVD_MARKER";
	// public static final String MARKER_ID_INVISIBLE = Activator.PLUGIN_ID +
	// ".TCM_ESVD_MARKER_INVISIBLE";

	public static final String SEPARATOR_RESOURCES_TYPE = ";";
	public static final String SEPARATOR_FULL_PATH = " - ";
	public static final String RESOURCE_TYPE_TO_PERFORM_DETECTION = "java";

	public static final int VERIFIER_ID_COMMAND_INJECTION = 1;
	public static final int VERIFIER_ID_COOKIE_POISONING = 2;
	public static final int VERIFIER_ID_CROSS_SITE_SCRIPTING = 3;
	public static final int VERIFIER_ID_HTTP_RESPONSE_SPLITTING = 4;
	public static final int VERIFIER_ID_LOG_FORGING = 5;
	public static final int VERIFIER_ID_PATH_TRAVERSAL = 6;
	public static final int VERIFIER_ID_REFLECTION_INJECTION = 7;
	public static final int VERIFIER_ID_SECURITY_MISCONFIGURATION = 8;
	public static final int VERIFIER_ID_SQL_INJECTION = 9;

	public static final int RESOLUTION_ID_IGNORE_WARNING = 10;

	public static final String OBJECT = "java.lang.Object";
	public static final int LITERAL = 1;

	public abstract class Package {
		public static final String UI = "concad.ui";
		public static final String L10N_MESSAGES = UI + ".l10n.message";
	}

	public abstract class PrefPageSettings {
		public static final String ID_PAGE = Package.UI + ".PREFERENCE.PAGE.SETTINGS";
	}

	public abstract class PrefPageSecurityVulnerability {
		public static final String FIELD_MONITORED_PROJECTS = Activator.PLUGIN_ID + ".MonitoredProjects";
	}

}
