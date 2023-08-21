//$Id$
package zoho.crm.security.agent;


import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class StringConstructorTransformer implements ClassFileTransformer {

	
	private static Logger logger = Logger.getLogger(StringConstructorTransformer.class.getName());
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

    	if (className.equals("java/lang/String")) {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            logger.info("e.......................");
            ClassNode cn = new ClassNode(Opcodes.ASM6);
            
            cr.accept(cn, ClassReader.EXPAND_FRAMES);
            for(MethodNode methodNode : cn.methods) {
            	if(methodNode.name.equals("<init>") && methodNode.desc.equals("(Ljava/lang/String;)V")) {
            		try {
            			InsnList inList = new InsnList();
                		inList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                		logger.info("above methodnode.......................");
                		MethodInsnNode min = new MethodInsnNode(Opcodes.INVOKESTATIC, "zoho/crm/security/agent/util", "printer","(Ljava/lang/String;)V",false);
                		logger.info("below methodnode 111");
                		inList.add(min);
                		logger.info("below methodnode");
                		methodNode.instructions.insert(inList);
                		logger.info("STRING CONSTRUCTOE INSTRUMENTED");
                		zoho.crm.security.agent.util.printer("PRINTER CALLED");
            		}catch(Exception e) {
            			e.printStackTrace();
            		}
            	}
            }
            cn.accept(cw);
            logger.info("INSTRUMENTING STRING CLASS===>......");
            
            zoho.crm.security.agent.util.writeToFile("string", cw.toByteArray());
            
            return cw.toByteArray();
        }
        return classfileBuffer;
    }
}

