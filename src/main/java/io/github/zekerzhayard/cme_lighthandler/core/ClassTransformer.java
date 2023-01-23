package io.github.zekerzhayard.cme_lighthandler.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class ClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String className, String transformedName, byte[] basicClass) {
        if ("net.minecraft.world.World".equals(transformedName)) {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, ClassReader.EXPAND_FRAMES);
            for (MethodNode mn : cn.methods) {
                if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "<init>") && RemapUtils.checkMethodDesc(mn.desc, "(Lnet/minecraft/world/storage/ISaveHandler;Lnet/minecraft/world/storage/WorldInfo;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/profiler/Profiler;Z)V")) {
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain.getOpcode() == Opcodes.PUTFIELD) {
                            FieldInsnNode fin = (FieldInsnNode) ain;
                            if (RemapUtils.checkClassName(fin.owner, "net/minecraft/world/World") && RemapUtils.checkFieldName(fin.owner, fin.name, fin.desc, "field_147482_g") && RemapUtils.checkFieldDesc(fin.desc, "Ljava/util/List;")) {
                                mn.instructions.insertBefore(fin, new InsnNode(Opcodes.POP));
                                mn.instructions.insertBefore(fin, new TypeInsnNode(Opcodes.NEW, "io/github/zekerzhayard/cme_lighthandler/CopyOnWriteArrayListWithMutableIterator"));
                                mn.instructions.insertBefore(fin, new InsnNode(Opcodes.DUP));
                                mn.instructions.insertBefore(fin, new MethodInsnNode(Opcodes.INVOKESPECIAL, "io/github/zekerzhayard/cme_lighthandler/CopyOnWriteArrayListWithMutableIterator", "<init>", "()V", false));
                            }
                        }
                    }
                }
            }
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            basicClass = cw.toByteArray();
        }
        return basicClass;
    }
}
