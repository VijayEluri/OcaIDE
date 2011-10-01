package ocaml.build.ocamlbuild;

import java.util.ArrayList;

import ocaml.OcamlPlugin;
import ocaml.properties.OcamlProjectPropertiesSerialization;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.dialogs.IDialogSettings;

public class OcamlbuildFlags {
	
	public static final String SECTION = "OcamlbuildSettings";
	public static final String TARGETS = "ocamlbuildProjectTargets";
	public static final String LIBS = "ocamlbuildProjectLibs";
	public static final String CFLAGS = "ocamlbuildProjectCompilerFlags";
	public static final String LFLAGS = "ocamlbuildProjectLinkerFlags";
	public static final String OTHER_FLAGS = "ocamlbuildProjectOtherFlags";
	public static final String GENERATE_TYPE_INFO = "ocamlbuildGenerateTypeInfo";

	private ArrayList<String> targets;
	private ArrayList<String> libs;
	private ArrayList<String> cflags;
	private ArrayList<String> lflags;
	private ArrayList<String> otherFlags;
	private boolean generateTypeInfo;

	private final IProject project;

	public OcamlbuildFlags(IProject project) {
		this.project = project;
	}

	public void load() {
		
		OcamlProjectPropertiesSerialization propertiesSerialization = new OcamlProjectPropertiesSerialization(project);
		IDialogSettings dialogSettings = propertiesSerialization.load(SECTION);

		targets = new ArrayList<String>();
		libs = new ArrayList<String>();
		cflags = new ArrayList<String>();
		lflags = new ArrayList<String>();
		otherFlags = new ArrayList<String>();
		
		try {
			if (project.getPersistentProperty(new QualifiedName(OcamlPlugin.QUALIFIER, TARGETS)) != null
					&& dialogSettings.get(TARGETS) == null) {
				compatibilityLoad();
			} else {
				// load targets
				String strTargets = dialogSettings.get(TARGETS);
				if (strTargets == null)
					strTargets = "";
				setTargets(strTargets);

				// load libs
				String strLibs = dialogSettings.get(LIBS);
				if (strLibs == null)
					strLibs = "";
				setLibs(strLibs);

				// load cflags
				String strCFlags = dialogSettings.get(CFLAGS);
				if (strCFlags == null)
					strCFlags = "";
				setCFlags(strCFlags);

				// load lflags
				String strLFlags = dialogSettings.get(LFLAGS);
				if (strLFlags == null)
					strLFlags = "";
				setLFlags(strLFlags);

				// load otherFlags
				String strOtherFlags = dialogSettings.get(OTHER_FLAGS);
				if (strOtherFlags == null)
					strOtherFlags = "";
				setOtherFlags(strOtherFlags);

				// generate type info?
				String strGenerateTypeInfo = dialogSettings.get(GENERATE_TYPE_INFO);
				if (strGenerateTypeInfo == null)
					strGenerateTypeInfo = "true";
				setGenerateTypeInfo("true".equalsIgnoreCase(strGenerateTypeInfo));
			}
		} catch (Throwable e) {
			OcamlPlugin.logError("problem loading ocamlbuild project properties", e);
		}

	}

	/** Loads the properties from the project persistent properties to preserve backwards compatibility */
	private void compatibilityLoad() {
		try {
			// load targets
			String strTargets = project.getPersistentProperty(new QualifiedName(
					OcamlPlugin.QUALIFIER, TARGETS));
			if (strTargets == null)
				strTargets = "";
			setTargets(strTargets);

			// load libs
			String strLibs = project.getPersistentProperty(new QualifiedName(OcamlPlugin.QUALIFIER,
					LIBS));
			if (strLibs == null)
				strLibs = "";
			setLibs(strLibs);

			// load cflags
			String strCFlags = project.getPersistentProperty(new QualifiedName(
					OcamlPlugin.QUALIFIER, CFLAGS));
			if (strCFlags == null)
				strCFlags = "";
			setCFlags(strCFlags);

			// load lflags
			String strLFlags = project.getPersistentProperty(new QualifiedName(
					OcamlPlugin.QUALIFIER, LFLAGS));
			if (strLFlags == null)
				strLFlags = "";
			setLFlags(strLFlags);

			// load otherFlags
			String strOtherFlags = project.getPersistentProperty(new QualifiedName(
					OcamlPlugin.QUALIFIER, OTHER_FLAGS));
			if (strOtherFlags == null)
				strOtherFlags = "";
			setOtherFlags(strOtherFlags);

			// generate type info?
			String strGenerateTypeInfo = project.getPersistentProperty(new QualifiedName(
					OcamlPlugin.QUALIFIER, GENERATE_TYPE_INFO));
			if (strGenerateTypeInfo == null)
				strGenerateTypeInfo = "true";
			setGenerateTypeInfo("true".equalsIgnoreCase(strGenerateTypeInfo));
			
			
			// clear the old persistent properties, so that only the new format is used in the future
			project.setPersistentProperty(new QualifiedName(OcamlPlugin.QUALIFIER, TARGETS), null);
			project.setPersistentProperty(new QualifiedName(OcamlPlugin.QUALIFIER, LIBS), null);
			project.setPersistentProperty(new QualifiedName(OcamlPlugin.QUALIFIER, CFLAGS), null);
			project.setPersistentProperty(new QualifiedName(OcamlPlugin.QUALIFIER, LFLAGS), null);
			project.setPersistentProperty(new QualifiedName(OcamlPlugin.QUALIFIER, OTHER_FLAGS), null);
			project.setPersistentProperty(new QualifiedName(OcamlPlugin.QUALIFIER, GENERATE_TYPE_INFO), null);
			
		} catch (Throwable e) {
			OcamlPlugin.logError("problem loading ocamlbuild project properties", e);
		}
	}

	public void save() {
		OcamlProjectPropertiesSerialization serialization = new OcamlProjectPropertiesSerialization(project);
		IDialogSettings dialogSettings = serialization.load(SECTION);
		
		try {
			// save targets
			dialogSettings.put(TARGETS, getTargets());

			// save libs
			dialogSettings.put(LIBS, getLibs());

			// save cflags
			dialogSettings.put(CFLAGS, getCFlags());

			// save lflags
			dialogSettings.put(LFLAGS, getLFlags());

			// save otherFlags
			dialogSettings.put(OTHER_FLAGS, getOtherFlagsAsString());

			// save generateTypeInfo
			dialogSettings.put(GENERATE_TYPE_INFO, Boolean.toString(isGenerateTypeInfo()));
		} catch (Throwable e) {
			OcamlPlugin.logError("problem saving ocamlbuild project properties", e);
		}
		
		serialization.save();
	}
	

	public void setTargets(String strTargets) {
		targets = new ArrayList<String>();
		String targets[] = strTargets.split(",");

		for (String target : targets)
			if (!"".equals(target.trim()))
				this.targets.add(target.trim());
	}

	public void setLibs(String strLibs) {
		libs = new ArrayList<String>();
		String libs[] = strLibs.split(",");

		for (String lib : libs)
			if (!"".equals(lib.trim()))
				this.libs.add(lib.trim());
	}

	public void setCFlags(String strCFlags) {
		cflags = new ArrayList<String>();
		String cflags[] = strCFlags.split(",");

		for (String cflag : cflags)
			if (!"".equals(cflag.trim()))
				this.cflags.add(cflag.trim());
	}

	public void setLFlags(String strLFlags) {
		lflags = new ArrayList<String>();
		String lflags[] = strLFlags.split(",");

		for (String lflag : lflags)
			if (!"".equals(lflag.trim()))
				this.lflags.add(lflag.trim());
	}

	public void setOtherFlags(String strOtherFlags) {
		String[] flags = DebugPlugin.parseArguments(strOtherFlags);
		otherFlags = new ArrayList<String>();

		for (String flag : flags)
			if (flag.trim().length() > 0)
				this.otherFlags.add(flag.trim());
	}
	
	public void setGenerateTypeInfo(boolean value) {
		this.generateTypeInfo = value;
	}
	
	public String getTargets() {
		boolean bFirst = true;

		StringBuilder strTargets = new StringBuilder();
		bFirst = true;
		for (String target : this.targets) {
			if (bFirst)
				bFirst = false;
			else
				strTargets.append(",");
			strTargets.append(target.trim());
		}
		
		return strTargets.toString();
	}

	public String[] getTargetsAsList() {
		return this.targets.toArray(new String[this.targets.size()]);
	}

	public String getLibs() {
		StringBuilder strLibs = new StringBuilder();
		boolean bFirst = true;
		for (String lib : this.libs) {
			if (bFirst)
				bFirst = false;
			else
				strLibs.append(",");
			strLibs.append(lib.trim());
		}
		
		return strLibs.toString();
	}

	public String getCFlags() {
		StringBuilder strcflags = new StringBuilder();
		boolean bFirst = true;
		for (String cflag : this.cflags) {
			if (bFirst)
				bFirst = false;
			else
				strcflags.append(",");
			strcflags.append(cflag.trim());
		}
		
		return strcflags.toString();
	}

	public String getLFlags() {
		StringBuilder strlflags = new StringBuilder();
		boolean bFirst = true;
		for (String lflag : this.lflags) {
			if (bFirst)
				bFirst = false;
			else
				strlflags.append(",");
			strlflags.append(lflag.trim());
		}
		
		return strlflags.toString();
	}
	
	public String[] getOtherFlags() {
		return otherFlags.toArray(new String[otherFlags.size()]);
	}
	
	public String getOtherFlagsAsString() {
		StringBuilder strOtherFlags = new StringBuilder();
		boolean bFirst = true;
		for (String flag : this.otherFlags) {
			if (bFirst)
				bFirst = false;
			else
				strOtherFlags.append(" ");
			
			String trimmed = flag.trim();
			if(trimmed.contains(" "))
				trimmed = "\"" + trimmed + "\"";
			strOtherFlags.append(trimmed);
		}
		
		return strOtherFlags.toString();
	}

	public boolean isGenerateTypeInfo() {
		return generateTypeInfo;
	}
}
