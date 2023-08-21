//$Id$
package zoho.crm.security.agent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.security.AllPermission;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;




public class InjectionAgent {

	public static Logger LOGGER = Logger.getLogger(InjectionAgent.class.getName());

	public static void premain(String agentArgs, Instrumentation inst) throws UnmodifiableClassException, ClassNotFoundException {
		LOGGER.log(Level.INFO, "In Injection Agent ::: Premain.......");
		
		

		Class.forName("zoho.crm.security.agent.util");
		
		zoho.crm.security.agent.util.printer("PRINTER CALLED");
		
		inst.addTransformer(new myTranformer(), true);
		inst.addTransformer(new StringConstructorTransformer(), true);
		inst.addTransformer(new ifExtractorTransformer(), true);
		
		
		Class<?>[] loadedClasses = inst.getAllLoadedClasses();
		for (Class<?> loadedClass : loadedClasses) {
			if (loadedClass.getName().equals("java.lang.String")) {
					inst.retransformClasses(loadedClass);
			}
		}

	}


}



