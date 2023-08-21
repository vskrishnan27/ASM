//$Id$
package zoho.crm.security.agent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;


public class myTranformer implements ClassFileTransformer {

	private static Logger LOGGER = Logger.getLogger(myTranformer.class.getName());
	
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		ClassReader reader = new ClassReader(classfileBuffer);

		ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

		ClassNode CN = new ClassNode(Opcodes.ASM6);
		reader.accept(CN, ClassReader.EXPAND_FRAMES);

		if (className.equals("com/practice/asm/testing")) {

//			writeToFile("before", classfileBuffer);

			LOGGER.log(Level.INFO, "instrumenting..........");
			for (MethodNode mn : CN.methods) {
				if (mn.name.equals("myMethod")) {
					InsnList inList = new InsnList();
					inList.add(new VarInsnNode(Opcodes.ALOAD, 0));
					inList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/practice/asm/testing", "print", "(Ljava/lang/String;)V"));
					mn.instructions.insert(inList);
				}
				if (mn.name.equals("tryTemp")) {
					LOGGER.log(Level.INFO, "try tempinstrumenting DONE..........");
					InsnList in = new InsnList();
					in.add(new VarInsnNode(Opcodes.ALOAD, 0));
					in.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/practice/asm/temp", "print", "()Ljava/lang/String;"));
					// in.add(new InsnNode(Opcodes.POP));
					in.add(new VarInsnNode(Opcodes.ASTORE, 1));
					in.add(new VarInsnNode(Opcodes.ALOAD, 1));
					in.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/practice/asm/testing", "print", "(Ljava/lang/String;)V"));
					mn.instructions.insert(in);
					LOGGER.log(Level.INFO, "try tempinstrumenting DONE.........END.");
				}
			}

			try {
				for (String s : getInterfaces(className)) {
					System.out.println(s + "<==");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//
			LOGGER.log(Level.INFO, "instrumenting DONE..........");
			CN.accept(writer);

//			writeToFile("after", writer.toByteArray());
			return writer.toByteArray();
		}

		return classfileBuffer;
	}
	
	private static void writeToFile(String className, byte[] data) {
		String filePath = "/Users/santhana-16396/CRMBUILD/ASMLearning/tempClass/" + className + ".class";
		try (FileOutputStream stream = new FileOutputStream(filePath)) {
			stream.write(data);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed to write instrumented class", e);
		}
	}
	
	public static List<String> getInterfaces(String className) throws IOException {
		List<String> interfaces = new ArrayList<>();

		ClassReader classReader = new ClassReader(className);
		ClassNode classNode = new ClassNode();
		classReader.accept(classNode, 0);

		for (String iface : classNode.interfaces) {
			interfaces.add(iface.replace('/', '.'));
		}
		if (classNode.superName != null) {
			interfaces.addAll(getInterfaces(classNode.superName.replace('/', '.')));
		}

		return interfaces;
	}
}
