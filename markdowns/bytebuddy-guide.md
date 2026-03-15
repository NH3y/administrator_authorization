# ByteBuddy: Comprehensive Guide to Dynamic Class Creation

---

## Table of Contents

1. [What Is ByteBuddy?](#1-what-is-bytebuddy)
2. [Setup & Dependencies](#2-setup--dependencies)
3. [Core Concepts](#3-core-concepts)
4. [Creating Classes from Scratch](#4-creating-classes-from-scratch)
5. [Subclassing & Method Overriding](#5-subclassing--method-overriding)
6. [Implementing Interfaces](#6-implementing-interfaces)
7. [Method Interception (The Real Power)](#7-method-interception-the-real-power)
8. [Field Manipulation](#8-field-manipulation)
9. [Annotations](#9-annotations)
10. [Loading & Instantiating Dynamic Classes](#10-loading--instantiating-dynamic-classes)
11. [Java Agents & Runtime Redefinition](#11-java-agents--runtime-redefinition)
12. [Advanced: Advice API](#12-advanced-advice-api)
13. [Advanced: TypePool & Rebase vs. Redefine vs. Subclass](#13-advanced-typepool--rebase-vs-redefine-vs-subclass)
14. [Advanced: Custom ElementMatcher Chains](#14-advanced-custom-elementmatcher-chains)
15. [Performance Considerations](#15-performance-considerations)
16. [Real-World Patterns](#16-real-world-patterns)
17. [Common Pitfalls](#17-common-pitfalls)

---

## 1. What Is ByteBuddy?

ByteBuddy is a **runtime code generation and manipulation library** for the JVM. Unlike compile-time annotation processors or reflection, ByteBuddy generates and transforms actual bytecode at runtime. It sits in the same space as ASM, Javassist, and CGLIB — but with a fluent, type-safe DSL that makes it dramatically more ergonomic.

**When you need ByteBuddy:**
- Building frameworks (dependency injection, ORM, mocking)
- Implementing AOP (aspect-oriented programming) without a full framework
- Creating proxies and decorators dynamically
- Writing Java agents that instrument arbitrary third-party code
- Generating specialized implementations of interfaces at runtime

**ByteBuddy vs. alternatives:**

| Library     | Abstraction Level | Type Safety | Agent Support | Ease of Use |
|-------------|-------------------|-------------|---------------|-------------|
| ASM         | Raw bytecode      | None        | Yes           | Low         |
| Javassist   | Source strings    | Partial     | Yes           | Medium      |
| CGLIB       | High              | Partial     | No            | Medium      |
| **ByteBuddy** | **Fluent DSL**  | **High**    | **Yes**       | **High**    |

---

## 2. Setup & Dependencies

### Maven
```xml
<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy</artifactId>
    <version>1.14.18</version>
</dependency>

<!-- For Java agent support -->
<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy-agent</artifactId>
    <version>1.14.18</version>
</dependency>
```

### Gradle
```groovy
implementation 'net.bytebuddy:byte-buddy:1.14.18'
implementation 'net.bytebuddy:byte-buddy-agent:1.14.18' // optional
```

### Java Version Notes
- ByteBuddy 1.14.x supports Java 8–21+
- For Java 16+ with strong encapsulation, you may need `--add-opens` JVM flags or the agent
- Module-info considerations: ByteBuddy can operate on both named and unnamed modules

---

## 3. Core Concepts

### The ByteBuddy Entry Point

Everything starts with a `new ByteBuddy()` instance. This holds global configuration like:
- The naming strategy for generated classes
- The class file version to target
- The method graph compiler

```java
ByteBuddy byteBuddy = new ByteBuddy();

// With custom config
ByteBuddy byteBuddy = new ByteBuddy()
    .with(ClassFileVersion.JAVA_V17)
    .with(NamingStrategy.SuffixingRandom.BYTE_BUDDY); // default
```

### The Three Starting Modes

```java
byteBuddy.subclass(Foo.class)    // Extend an existing class
byteBuddy.redefine(Foo.class)    // Replace a class's implementation in-place
byteBuddy.rebase(Foo.class)      // Rename original methods, overlay new ones
```

We'll explore each in detail. The most common starting point for dynamic creation is `subclass`.

### DynamicType.Builder Pipeline

Every ByteBuddy operation is a builder chain that produces a `DynamicType.Unloaded<T>`:

```
new ByteBuddy()
  .subclass(...)         → DynamicType.Builder<T>
  .method(...)           → DynamicType.Builder<T>.MethodDefinition.ReceiverTypeDefinition<T>
  .intercept(...)        → DynamicType.Builder<T>
  .make()                → DynamicType.Unloaded<T>
  .load(classLoader)     → DynamicType.Loaded<T>
  .getLoaded()           → Class<T>
```

---

## 4. Creating Classes from Scratch

You can create a completely new class with no parent (beyond `Object`):

```java
Class<?> dynamicClass = new ByteBuddy()
    .subclass(Object.class)
    .name("com.example.GeneratedClass")
    .make()
    .load(ClassLoader.getSystemClassLoader())
    .getLoaded();

Object instance = dynamicClass.getDeclaredConstructor().newInstance();
System.out.println(instance.getClass().getName()); // com.example.GeneratedClass
```

### Naming Strategies

```java
// Explicit name
.name("com.example.MyDynamicClass")

// Suffix-based (default): appends "$ByteBuddy$<random>" to superclass name
new ByteBuddy().with(new NamingStrategy.SuffixingRandom("Generated"))

// Prefix-based
new ByteBuddy().with(new NamingStrategy.PrefixingRandom("Dynamic"))

// Custom strategy
new ByteBuddy().with((superClass, interfaces, classLoader, injectionStrategy) ->
    "com.generated." + superClass.getSimpleName() + "_" + System.nanoTime()
)
```

---

## 5. Subclassing & Method Overriding

### Basic Subclass

```java
public class Base {
    public String greet(String name) {
        return "Hello, " + name;
    }
}

Class<? extends Base> generated = new ByteBuddy()
    .subclass(Base.class)
    .method(ElementMatchers.named("greet"))
    .intercept(FixedValue.value("Intercepted!"))
    .make()
    .load(Base.class.getClassLoader())
    .getLoaded();

Base instance = generated.getDeclaredConstructor().newInstance();
System.out.println(instance.greet("World")); // "Intercepted!"
```

### ElementMatchers — Selecting What to Intercept

`ElementMatchers` is your filter system. Methods are selected by composable predicates:

```java
import static net.bytebuddy.matcher.ElementMatchers.*;

// By name
named("toString")

// By name pattern
nameStartsWith("get")
nameEndsWith("Service")
nameContains("update")

// By return type
returns(String.class)
returns(named("java.util.List"))  // by type name string

// By parameter types
takesArguments(String.class, int.class)
takesArgument(0, String.class)    // first parameter is String
takesNoArguments()

// By modifiers
isPublic()
isStatic()
isFinal()
isAbstract()
isConstructor()

// By annotation
isAnnotatedWith(Override.class)

// Logical composition
named("greet").and(isPublic())
named("foo").or(named("bar"))
not(isStatic())
any()   // matches everything

// Inheritance
isDeclaredBy(Base.class)          // only declared on Base, not inherited
isOverriddenFrom(Base.class)      // overrides a method from Base
```

---

## 6. Implementing Interfaces

### Fully Implementing an Interface

```java
public interface Greeter {
    String greet(String name);
    default String farewell(String name) { return "Bye, " + name; }
}

Class<? extends Greeter> impl = new ByteBuddy()
    .subclass(Greeter.class)  // ByteBuddy detects it's an interface
    .method(named("greet"))
    .intercept(FixedValue.value("Hi from dynamic class!"))
    .make()
    .load(Greeter.class.getClassLoader())
    .getLoaded();

Greeter g = impl.getDeclaredConstructor().newInstance();
System.out.println(g.greet("World"));   // "Hi from dynamic class!"
System.out.println(g.farewell("World")); // "Bye, World" (default method preserved)
```

### Adding an Interface to an Existing Class

```java
Class<?> enriched = new ByteBuddy()
    .subclass(HashMap.class)
    .implement(Serializable.class, Cloneable.class)
    .method(isToString())
    .intercept(FixedValue.value("MySpecialMap"))
    .make()
    .load(HashMap.class.getClassLoader())
    .getLoaded();
```

---

## 7. Method Interception (The Real Power)

### FixedValue

Returns a hardcoded value. Useful for stubs.

```java
.intercept(FixedValue.value("constant string"))
.intercept(FixedValue.value(42))
.intercept(FixedValue.nullValue())   // returns null
.intercept(FixedValue.self())        // returns `this`
.intercept(FixedValue.originType())  // returns the Class object
```

### MethodDelegation — Delegate to Another Object/Class

This is the most flexible and commonly used implementation strategy.

#### Delegate to a static class

```java
public class Interceptor {
    public static String greet(String name) {
        return "Delegated hello, " + name + "!";
    }
}

Class<? extends Base> cls = new ByteBuddy()
    .subclass(Base.class)
    .method(named("greet"))
    .intercept(MethodDelegation.to(Interceptor.class))
    .make()
    .load(Base.class.getClassLoader())
    .getLoaded();
```

ByteBuddy matches methods by **parameter compatibility** — it finds the best matching static method in `Interceptor` by looking at parameter types.

#### Delegate to an instance

```java
Interceptor interceptorInstance = new Interceptor();

Class<? extends Base> cls = new ByteBuddy()
    .subclass(Base.class)
    .method(named("greet"))
    .intercept(MethodDelegation.to(interceptorInstance))
    .make()
    .load(Base.class.getClassLoader())
    .getLoaded();
```

### MethodDelegation Special Annotations

These annotations on the delegate's parameters let ByteBuddy inject contextual data:

```java
import net.bytebuddy.implementation.bind.annotation.*;

public class AdvancedInterceptor {

    public static Object intercept(
        @This Object self,                  // the proxy instance
        @Origin Method method,              // the intercepted method
        @AllArguments Object[] args,        // all arguments as array
        @Argument(0) String firstArg,       // specific argument by index
        @SuperCall Callable<?> zuper,       // calls the original super method
        @Super Base superInstance           // proxy to invoke super methods on
    ) throws Exception {
        System.out.println("Before: " + method.getName());
        Object result = zuper.call();       // invoke original
        System.out.println("After: " + method.getName());
        return result;
    }
}
```

**`@SuperCall`** is especially powerful — it gives you a `Callable` or `Runnable` that calls the original superclass implementation. This is how you implement "around advice" without a full AOP framework.

#### Full around-advice pattern:

```java
public class TimingInterceptor {
    public static Object time(
        @Origin Method method,
        @SuperCall Callable<Object> zuper
    ) throws Exception {
        long start = System.nanoTime();
        try {
            return zuper.call();
        } finally {
            long elapsed = System.nanoTime() - start;
            System.out.printf("%s took %dns%n", method.getName(), elapsed);
        }
    }
}

Class<? extends MyService> timed = new ByteBuddy()
    .subclass(MyService.class)
    .method(isPublic().and(not(isStatic())))
    .intercept(MethodDelegation.to(TimingInterceptor.class))
    .make()
    .load(MyService.class.getClassLoader())
    .getLoaded();
```

### InvocationHandlerAdapter

If you already have a `java.lang.reflect.InvocationHandler` (like from `Proxy`), you can adapt it:

```java
InvocationHandler handler = (proxy, method, args) -> {
    System.out.println("Intercepted: " + method.getName());
    return null;
};

Class<?> cls = new ByteBuddy()
    .subclass(Object.class)
    .method(isPublic())
    .intercept(InvocationHandlerAdapter.of(handler))
    .make()
    .load(ClassLoader.getSystemClassLoader())
    .getLoaded();
```

### Chaining Implementations with MethodDelegation.withDefaultConfiguration()

For disambiguation when multiple delegate methods could match:

```java
MethodDelegation.withDefaultConfiguration()
    .withBinders(TargetMethodAnnotationDrivenBinder.ParameterBinder.DEFAULTS)
    .filter(not(named("toString")))
    .to(MyInterceptor.class)
```

---

## 8. Field Manipulation

### Defining New Fields

```java
Class<?> cls = new ByteBuddy()
    .subclass(Object.class)
    .defineField("count", int.class, Visibility.PUBLIC)
    .defineField("name", String.class, Modifier.PUBLIC | Modifier.STATIC)
    .make()
    .load(ClassLoader.getSystemClassLoader())
    .getLoaded();

// Access via reflection
Field f = cls.getField("count");
Object instance = cls.getDeclaredConstructor().newInstance();
f.set(instance, 42);
System.out.println(f.get(instance)); // 42
```

### FieldAccessor — Auto-generating Getters/Setters

This is ByteBuddy's killer feature for generating property-style classes:

```java
public interface Nameable {
    String getName();
    void setName(String name);
}

Class<? extends Nameable> bean = new ByteBuddy()
    .subclass(Nameable.class)
    // Define the backing field
    .defineField("name", String.class, Visibility.PRIVATE)
    // Wire getter
    .method(named("getName"))
    .intercept(FieldAccessor.ofField("name"))
    // Wire setter
    .method(named("setName"))
    .intercept(FieldAccessor.ofField("name"))
    .make()
    .load(Nameable.class.getClassLoader())
    .getLoaded();

Nameable n = bean.getDeclaredConstructor().newInstance();
n.setName("Alice");
System.out.println(n.getName()); // "Alice"
```

### FieldAccessor with convention-based naming

```java
// ByteBuddy can infer field name from getter/setter name automatically
.method(nameStartsWith("get").or(nameStartsWith("set")))
.intercept(FieldAccessor.ofBeanProperty())
```

---

## 9. Annotations

### Adding Annotations to a Class

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Generated {
    String value();
}

Class<?> annotated = new ByteBuddy()
    .subclass(Object.class)
    .annotateType(AnnotationDescription.Builder.ofType(Generated.class)
        .define("value", "my-generator")
        .build())
    .make()
    .load(ClassLoader.getSystemClassLoader())
    .getLoaded();

Generated ann = annotated.getAnnotation(Generated.class);
System.out.println(ann.value()); // "my-generator"
```

### Adding Annotations to Methods

```java
new ByteBuddy()
    .subclass(Object.class)
    .defineMethod("myMethod", void.class, Visibility.PUBLIC)
    .intercept(FixedValue.originType())
    .annotateMethod(AnnotationDescription.Builder.ofType(Override.class).build())
    .make()
```

### Preserving Existing Annotations

When subclassing or rebasing, existing annotations on the parent are inherited naturally via Java's annotation inheritance mechanism (for `@Inherited` annotations). For other cases, you can copy them explicitly using `TypeDescription` introspection.

---

## 10. Loading & Instantiating Dynamic Classes

### ClassLoadingStrategy

How ByteBuddy injects the generated class into a ClassLoader is controlled by the strategy:

```java
DynamicType.Unloaded<Foo> unloaded = new ByteBuddy()
    .subclass(Foo.class)
    .make();

// INJECTION: Uses reflection/Unsafe to inject into existing classloader (default for most use)
unloaded.load(Foo.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)

// WRAPPER: Creates a new child ClassLoader (safest, most isolated)
unloaded.load(Foo.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)

// CHILD_FIRST: Child classloader that prefers the generated class
unloaded.load(Foo.class.getClassLoader(), ClassLoadingStrategy.Default.CHILD_FIRST)
```

**Recommendation:** Use `WRAPPER` for safety and isolation. Use `INJECTION` when the generated class needs to be seen by the parent classloader (e.g., for `instanceof` checks with the parent's types).

### Java 9+ Module System

For named modules, `INJECTION` via `Unsafe` may require explicit opens. Use the agent-based strategy:

```java
// Attach agent at startup: -javaagent:byte-buddy-agent.jar
ByteBuddyAgent.install();

unloaded.load(
    Foo.class.getClassLoader(),
    ClassLoadingStrategy.Default.INJECTION.allowExistingTypes()
)
```

Or use `UsingLookup` for explicit module control:

```java
unloaded.load(
    MethodHandles.lookup(),
    ClassLoadingStrategy.UsingLookup.of(MethodHandles.privateLookupIn(Foo.class, MethodHandles.lookup()))
)
```

### Instantiation

After loading, it's a normal `Class<T>`, so use reflection or constructor references:

```java
Class<? extends MyInterface> cls = ...;

// No-arg constructor
MyInterface inst = cls.getDeclaredConstructor().newInstance();

// With arguments
MyInterface inst = cls.getDeclaredConstructor(String.class, int.class)
                      .newInstance("hello", 42);

// Bypass constructor entirely with Objenesis (if needed)
// (ByteBuddy doesn't include Objenesis, but it's a common pairing)
```

### Saving to Disk

Useful for debugging or precompiling:

```java
DynamicType.Unloaded<?> unloaded = new ByteBuddy()
    .subclass(Object.class)
    .make();

// Save .class file for inspection with javap
unloaded.saveIn(new File("/tmp/generated-classes/"));

// Or inject into a jar
unloaded.inject(new File("output.jar"));
```

---

## 11. Java Agents & Runtime Redefinition

ByteBuddy's `AgentBuilder` is its most powerful feature — it lets you transform any class as it is loaded by the JVM, without modifying source code.

### Creating a Java Agent

```java
// In your premain / agentmain:
public class MyAgent {

    public static void premain(String args, Instrumentation instrumentation) {
        new AgentBuilder.Default()
            // Which classes to intercept
            .type(nameStartsWith("com.myapp.service"))
            // What to do with them
            .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                builder
                    .method(isPublic().and(not(isStatic())))
                    .intercept(MethodDelegation.to(TracingInterceptor.class))
            )
            .installOn(instrumentation);
    }
}
```

### Manifest for Agent JAR

```
Manifest-Version: 1.0
Premain-Class: com.example.MyAgent
Agent-Class: com.example.MyAgent
Can-Redefine-Classes: true
Can-Retransform-Classes: true
```

### Listener for Debugging

```java
new AgentBuilder.Default()
    .with(AgentBuilder.Listener.StreamWriting.toSystemOut()) // log all transforms
    .type(...)
    .transform(...)
    .installOn(instrumentation);
```

### Redefine vs. Retransform

```java
// Redefine: completely replaces the class definition
new AgentBuilder.Default(new ByteBuddy().with(TypeValidation.DISABLED))
    .disableClassFormatChanges()
    .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
    .type(is(MyClass.class))
    .transform(...)
    .installOn(instrumentation);
```

`RETRANSFORMATION` is needed for already-loaded classes. `REDEFINITION` works for classes not yet loaded. In practice, use `RETRANSFORMATION` for maximum coverage.

---

## 12. Advanced: Advice API

`Advice` is the recommended approach for Java agent transformations (over `MethodDelegation`) because it **inlines** bytecode directly into the target method rather than dispatching a delegate call. This is more efficient and avoids classloader complications.

```java
public class LoggingAdvice {

    @Advice.OnMethodEnter
    static long enter(@Advice.Origin Method method) {
        System.out.println(">>> " + method.getName());
        return System.nanoTime();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void exit(
        @Advice.Origin Method method,
        @Advice.Enter long startTime,
        @Advice.Return(readOnly = false) Object returnValue,
        @Advice.Thrown Throwable thrown
    ) {
        long elapsed = System.nanoTime() - startTime;
        if (thrown != null) {
            System.out.println("<<< " + method.getName() + " THREW after " + elapsed + "ns");
        } else {
            System.out.println("<<< " + method.getName() + " returned in " + elapsed + "ns");
        }
    }
}

// In agent transform:
builder.visit(Advice.to(LoggingAdvice.class).on(isPublic().and(not(isConstructor()))))
```

### Advice Annotations Reference

| Annotation | Location | Description |
|---|---|---|
| `@Advice.OnMethodEnter` | Method | Runs before the intercepted method |
| `@Advice.OnMethodExit` | Method | Runs after (optionally on throw) |
| `@Advice.This` | Parameter | The receiver object |
| `@Advice.Origin` | Parameter | Method/Constructor/String descriptor |
| `@Advice.Argument(n)` | Parameter | The nth method argument |
| `@Advice.AllArguments` | Parameter | All arguments as Object[] |
| `@Advice.Return` | Parameter (exit only) | Return value (writable if `readOnly=false`) |
| `@Advice.Thrown` | Parameter (exit only) | Exception thrown (null if none) |
| `@Advice.Enter` | Parameter (exit only) | Value returned by enter advice |
| `@Advice.FieldValue("x")` | Parameter | Value of a field by name |
| `@Advice.Local("x")` | Parameter | A local variable defined in advice |

### Mutating return values

```java
@Advice.OnMethodExit
static void exit(@Advice.Return(readOnly = false) String returnValue) {
    returnValue = returnValue.toUpperCase(); // modifies what the caller receives
}
```

---

## 13. Advanced: TypePool & Rebase vs. Redefine vs. Subclass

### TypePool — Working Without Class Objects

When you don't have access to the actual `Class<?>` (e.g., in an agent processing classes before they're loaded), use `TypePool`:

```java
TypePool typePool = TypePool.Default.ofSystemLoader();

TypeDescription typeDescription = typePool.describe("com.example.MyClass").resolve();

new ByteBuddy()
    .redefine(typeDescription, ClassFileLocator.ForClassLoader.ofSystemLoader())
    .method(named("compute"))
    .intercept(FixedValue.value(0))
    .make()
    .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION);
```

### Subclass vs. Redefine vs. Rebase

| Strategy | How it works | Calls to super? | Original code preserved? | Use case |
|---|---|---|---|---|
| **subclass** | Creates a new class extending the target | Yes, via `@SuperCall` | Yes (in parent) | Proxying, decoration |
| **redefine** | Replaces the original class entirely | No | No (overwritten) | Full replacement |
| **rebase** | Renames original methods to `$original`, installs new ones | Yes (via renamed method) | Yes (renamed) | Patching while preserving original |

#### Rebase example (most powerful for patching):

```java
new ByteBuddy()
    .rebase(MyClass.class)
    .method(named("calculate"))
    .intercept(MethodDelegation.to(PatchedImpl.class))
    .make()
    .load(MyClass.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION);

// The original `calculate` still exists but is renamed `calculate$original$xyz`
// PatchedImpl can invoke it if desired
```

---

## 14. Advanced: Custom ElementMatcher Chains

For complex matching logic, implement `ElementMatcher` directly:

```java
ElementMatcher<MethodDescription> matcher = new ElementMatcher<MethodDescription>() {
    @Override
    public boolean matches(MethodDescription target) {
        // Custom logic: match only methods that have exactly one String parameter
        // and return a primitive
        return target.getParameters().size() == 1
            && target.getParameters().get(0).getType().asErasure().represents(String.class)
            && target.getReturnType().isPrimitive();
    }
};

new ByteBuddy()
    .subclass(MyClass.class)
    .method(matcher)
    .intercept(FixedValue.value(0))
    .make();
```

### Junction composition

All `ElementMatcher` instances are also `Junction`, which supports `.and()`, `.or()`, `.negate()`:

```java
ElementMatcher.Junction<MethodDescription> publicNonStatic =
    isPublic().and(not(isStatic())).and(not(isConstructor()));

ElementMatcher.Junction<TypeDescription> appClasses =
    nameStartsWith("com.myapp").and(not(nameStartsWith("com.myapp.generated")));
```

---

## 15. Performance Considerations

### Class Generation Cost

Generating a class is expensive (bytecode writing, classloading). **Cache your generated classes:**

```java
// Bad: generates a new class per call
public Proxy createProxy() {
    return new ByteBuddy().subclass(Target.class)...make().load(...).getLoaded().newInstance();
}

// Good: generate once, instantiate many times
private static final Class<? extends Target> PROXY_CLASS = new ByteBuddy()
    .subclass(Target.class)
    ...
    .make()
    .load(Target.class.getClassLoader())
    .getLoaded();

public Target createProxy() {
    return PROXY_CLASS.getDeclaredConstructor().newInstance();
}
```

### Advice vs. MethodDelegation

- **Advice** inlines bytecode → zero dispatch overhead, ideal for agents
- **MethodDelegation** introduces a method dispatch call → small overhead, but easier to reason about

### TypeCache

ByteBuddy provides a `TypeCache` utility for generated class caching:

```java
TypeCache<TypeDescription> cache = new TypeCache.WithInlineExpunction<>(TypeCache.Sort.WEAK);

Class<?> generated = cache.findOrInsert(
    classLoader,
    typeDescription,
    () -> new ByteBuddy().subclass(typeDescription)...make().load(classLoader).getLoaded()
);
```

---

## 16. Real-World Patterns

### Pattern 1: AOP-style Transaction Wrapper

```java
public class TransactionAdvice {
    @Advice.OnMethodEnter
    static void begin(@Advice.Origin String methodName) {
        TransactionManager.begin();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void commit(@Advice.Thrown Throwable thrown) {
        if (thrown == null) TransactionManager.commit();
        else TransactionManager.rollback();
    }
}

// Applied via AgentBuilder to all @Transactional methods
.type(declaresMethod(isAnnotatedWith(Transactional.class)))
.transform((builder, ...) ->
    builder.visit(Advice.to(TransactionAdvice.class)
        .on(isAnnotatedWith(Transactional.class)))
)
```

### Pattern 2: Dynamic DTO / Value Object

```java
public static <T> Class<? extends T> generateBean(Class<T> iface) {
    DynamicType.Builder<T> builder = new ByteBuddy().subclass(iface);
    
    for (Method method : iface.getMethods()) {
        String name = method.getName();
        Class<?> type;
        if (name.startsWith("get") && method.getParameterCount() == 0) {
            type = method.getReturnType();
            String field = Character.toLowerCase(name.charAt(3)) + name.substring(4);
            builder = builder
                .defineField(field, type, Visibility.PRIVATE)
                .method(named(name)).intercept(FieldAccessor.ofField(field));
        }
    }
    
    return builder.make()
        .load(iface.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
        .getLoaded();
}
```

### Pattern 3: Mock-like Stub Generator

```java
public static <T> T stub(Class<T> type, Map<String, Object> returnValues) {
    DynamicType.Builder<T> builder = new ByteBuddy().subclass(type);
    
    for (var entry : returnValues.entrySet()) {
        builder = builder
            .method(named(entry.getKey()))
            .intercept(FixedValue.value(entry.getValue()));
    }
    
    return builder.make()
        .load(type.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
        .getLoaded()
        .getDeclaredConstructor()
        .newInstance();
}

// Usage:
MyService stub = stub(MyService.class, Map.of("fetchUser", new User("Alice")));
```

### Pattern 4: Decorator with Fallback to Super

```java
public class CachingInterceptor {
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    @RuntimeType
    public Object intercept(
        @Origin Method method,
        @AllArguments Object[] args,
        @SuperCall Callable<Object> zuper
    ) throws Exception {
        String key = method.getName() + Arrays.toString(args);
        return cache.computeIfAbsent(key, k -> {
            try { return zuper.call(); }
            catch (Exception e) { throw new RuntimeException(e); }
        });
    }
}
```

---

## 17. Common Pitfalls

### 1. ClassLoader Visibility

If your generated class references types from a different classloader than the one you load into, you'll get `NoClassDefFoundError`. Always load into a classloader that has visibility into all referenced types.

### 2. Final Classes and Methods

ByteBuddy's `subclass` cannot override `final` methods or extend `final` classes. For those, you must use `redefine` or `rebase` with an agent (which bypass this restriction at the bytecode level). You'll see a `CannotSubclassIllegalTypeException` if you try.

### 3. @SuperCall on Interfaces

If the superclass method is abstract, `@SuperCall` will fail at binding time. Guard with:

```java
// Only intercept non-abstract methods when using @SuperCall
.method(isPublic().and(not(isAbstract())))
```

### 4. Type Erasure on Generics

ByteBuddy works with erased types. If you need to generate methods with specific generic signatures (for reflection use), you must set them manually with `withGenericSignature`.

### 5. java.lang.instrument.IllegalClassFormatException

In agents, if you get this, ByteBuddy failed to transform the bytecode. Common causes: targeting a class with frames that need recomputation. Fix with:

```java
new AgentBuilder.Default(new ByteBuddy().with(TypeValidation.DISABLED))
```

### 6. Verify Your Output

Always inspect generated classes with `javap` during development:

```bash
javap -c -p /tmp/generated-classes/com/example/GeneratedClass.class
```

Or save to file and decompile with IntelliJ / CFR.

---

## Quick Reference Card

```java
// === Entry point ===
new ByteBuddy()

// === Starting modes ===
.subclass(Foo.class)           // extend
.redefine(Foo.class)           // replace
.rebase(Foo.class)             // patch with original preserved

// === Common matchers ===
named("methodName")
isPublic().and(not(isStatic()))
nameStartsWith("get")
returns(String.class)
isAnnotatedWith(MyAnnotation.class)
any()

// === Implementations ===
FixedValue.value(x)
FieldAccessor.ofField("name")
FieldAccessor.ofBeanProperty()
MethodDelegation.to(MyInterceptor.class)
MethodDelegation.to(instanceRef)
InvocationHandlerAdapter.of(handler)
Advice.to(MyAdvice.class)

// === Delegate parameter annotations ===
@This, @Origin, @AllArguments, @Argument(n), @SuperCall, @Super

// === Advice method annotations ===
@Advice.OnMethodEnter / @Advice.OnMethodExit
@Advice.This, @Advice.Origin, @Advice.Return, @Advice.Thrown, @Advice.Enter

// === Loading strategies ===
ClassLoadingStrategy.Default.WRAPPER      // safest
ClassLoadingStrategy.Default.INJECTION    // into existing CL
ClassLoadingStrategy.Default.CHILD_FIRST  // child-first

// === Pipeline end ===
.make()                                   // → DynamicType.Unloaded<T>
.load(classLoader, strategy)             // → DynamicType.Loaded<T>
.getLoaded()                              // → Class<T>
```
