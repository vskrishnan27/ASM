//$Id$
package zoho.crm.security.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ifExtractorTransformer implements ClassFileTransformer {

	private static Logger LOGGER = Logger.getLogger(ifExtractorTransformer.class.getName());

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		
		if (!className.startsWith("vskrishnan/test/asm")) {
			return classfileBuffer;
		}

		ClassReader classReader = new ClassReader(classfileBuffer);
		ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		ClassNode classNode = new ClassNode(Opcodes.ASM9);

		classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

		try {
			for (MethodNode methodNode : classNode.methods) {
				parseMethodAndGetIfBlock(methodNode);

			}
		} catch (Exception e) {

			LOGGER.log(Level.INFO, "EXCEPTION :::", e);
			return classfileBuffer;
		}

		classNode.accept(classWriter);

		util.writeToFile("test", classWriter.toByteArray());

		return classWriter.toByteArray();
	}

	private void parseMethodAndGetIfBlock(MethodNode methodNode) {
		LOGGER.log(Level.INFO, "ifCOndition transformer");
		InsnList insnList = methodNode.instructions;

		for (AbstractInsnNode node : insnList) {
			// SINGLE INVOKES
			if (node.getOpcode() >= Opcodes.IFEQ && node.getOpcode() <= Opcodes.IFLE) {
				AbstractInsnNode prevInsnNode = node.getPrevious();
				int prevOpcode = prevInsnNode.getOpcode();
				switch (prevOpcode) {
				case Opcodes.INVOKEVIRTUAL:
				case Opcodes.INVOKESPECIAL:
				case Opcodes.INVOKESTATIC:
				case Opcodes.INVOKEINTERFACE:
					if (prevInsnNode instanceof MethodInsnNode) {
						MethodInsnNode methodInsn = (MethodInsnNode) prevInsnNode;
						InsnList newInsnList = new InsnList();
						
						/*
						AbstractInsnNode prev1 = prevInsnNode.getPrevious();
						AbstractInsnNode prev2 = prevInsnNode.getPrevious().getPrevious();

						if (prev1 instanceof VarInsnNode && prev2 instanceof VarInsnNode) {
							int one = ((VarInsnNode) prev1).var;
							int two = ((VarInsnNode) prev2).var;
							newInsnList.add(new VarInsnNode(Opcodes.ALOAD, two));
							newInsnList.add(new VarInsnNode(Opcodes.ALOAD, one));
						} else {
							duplicateOperands(newInsnList, prev1, prev2);
						}
						
						*/
						
						
						//newInsnList.add(new InsnNode(Opcodes.DUP2));
						newInsnList.add(new LdcInsnNode(methodInsn.name));
						newInsnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zoho/crm/security/agent/util", "printer", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false));
						insnList.insertBefore(node, newInsnList);
						LOGGER.log(Level.INFO, "IF CONDITION METHOD INSTRUMENTED");
					}
					break;
				default:
					InsnList newInsnList = new InsnList();
					newInsnList.add(new InsnNode(Opcodes.DUP));
					LOGGER.log(Level.INFO, "IF CONDITION INSTRUMENTED");
					newInsnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zoho/crm/security/agent/util", "printer", "(Z)V", false));
					LOGGER.log(Level.INFO, "IF CONDITION INSTRUMENTED");
					insnList.insertBefore(node, newInsnList);
				}

			}

		}

	}

	private void duplicateOperands(InsnList newInsnList, AbstractInsnNode prev1, AbstractInsnNode prev2) {
		if (prev1 instanceof VarInsnNode) {
			int var1 = ((VarInsnNode) prev1).var;
			newInsnList.add(new VarInsnNode(Opcodes.ALOAD, var1));
		} else if (prev1 instanceof LdcInsnNode) {
			Object cst = ((LdcInsnNode) prev1).cst;
			newInsnList.add(new LdcInsnNode(cst));
		}

		if (prev2 instanceof VarInsnNode) {
			int var2 = ((VarInsnNode) prev2).var;
			newInsnList.add(new VarInsnNode(Opcodes.ALOAD, var2));
		} else if (prev2 instanceof LdcInsnNode) {
			Object cst = ((LdcInsnNode) prev2).cst;
			newInsnList.add(new LdcInsnNode(cst));
		}
	}
}
/*
 * IFEQ (153): Jump if int comparison with zero succeeds (if value == 0) IFNE (154): Jump if int comparison with zero doesn't succeed (if value != 0) IFLT (155): Jump if int comparison with zero
 * succeeds (if value < 0) IFGE (156): Jump if int comparison with zero doesn't succeed (if value >= 0) IFGT (157): Jump if int comparison with zero succeeds (if value > 0) IFLE (158): Jump if int
 * comparison with zero doesn't succeed (if value <= 0)
 * 
 * IF_ICMPEQ (159): Jump if int comparison succeeds (if value1 == value2) IF_ICMPNE (160): Jump if int comparison doesn't succeed (if value1 != value2) IF_ICMPLT (161): Jump if int comparison succeeds
 * (if value1 < value2) IF_ICMPGE (162): Jump if int comparison doesn't succeed (if value1 >= value2) IF_ICMPGT (163): Jump if int comparison succeeds (if value1 > value2) IF_ICMPLE (164): Jump if int
 * comparison doesn't succeed (if value1 <= value2) IF_ACMPEQ (165): Jump if reference comparison succeeds (if object1 == object2) IF_ACMPNE (166): Jump if reference comparison doesn't succeed (if
 * object1 != object2) IFNULL (198): Jump if reference is null IFNONNULL (199): Jump if reference not null
 */