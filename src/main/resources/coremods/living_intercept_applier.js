function initializeCoreMod() {
    return {
        'hierarchy_injector': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.world.entity.player.Player'
            },
            'transformer': function(classNode) {

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
                var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');

                print('Injecting custom class into Player hierarchy');

                // Change Player's superclass from LivingEntity to our interceptor
                var originalSuper = classNode.superName;
                print('Original superclass: ' + originalSuper);

                var newSuper = 'net/mcreator/administratorauthorization/classes/LivingEntityInterceptor';

                classNode.superName = newSuper
                print('New superclass: ' + classNode.superName);

                for (var i = 0; i < classNode.methods.size(); i++) {
                    var method = classNode.methods.get(i);
                    var methodName = method.name;

                    if (methodName.equals('<init>') || methodName.equals('remove') || methodName.equals('die')) {
                        var instructions = method.instructions;

                        // Fix super constructor calls
                        for (var j = 0; j < instructions.size(); j++) {
                            var insn = instructions.get(j);

                             // Fix invokespecial calls to super constructor
                            if (insn.getOpcode() === Opcodes.INVOKESPECIAL) {
                                var methodInsn = insn;
                                if (methodInsn.owner.equals(originalSuper) &&
                                    methodInsn.name.equals('<init>')) {
                                    print('Redirecting super() call from ' + originalSuper + ' to ' + newSuper);
                                    methodInsn.owner = newSuper;
                                    break;
                                }
                            }
                        }
                    }
                }
                return classNode;
            }
        }
    };
}