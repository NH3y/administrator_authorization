package net.mcreator.administratorauthorization.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.*;

public class DataViewerOverlay extends Screen {

    private static final Logger log = LoggerFactory.getLogger(DataViewerOverlay.class);
    private Button confirmButton;
    private Button superButton;
    private Button extendButton;

    private EditBox valueEditBox;
    private boolean isEditing = false;
    private Field editingField;

    private enum ViewMode { FIELDS, ELEMENTS }
    private ViewMode currentMode = ViewMode.FIELDS;

    // ... history stack and standard fields ...
    private final List<ExplorerEntry> cachedEntries = new ArrayList<>();

    // History stack for the "Previous Page" feature
    private final Stack<Object> history = new Stack<>();
    private Object currentTarget;
    private List<Set<Field>> fieldCache = null;
    private int classIndex = 0;

    // UI State
    private double scrollOffset = 0;
    private final int entryHeight = 15;

    public DataViewerOverlay(Object initialTarget) {
        super(Component.literal("Runtime Object Explorer"));
        this.currentTarget = initialTarget;
        refreshEntries();
    }

    private void refreshData() {
        cachedEntries.clear();
        if (currentTarget == null) return;

        // Determine if we should show fields or elements
        Class<?> clazz = currentTarget.getClass();
        if (clazz.isArray() || currentTarget instanceof Collection || currentTarget instanceof Map) {
            currentMode = ViewMode.ELEMENTS;
            populateElements();
        } else {
            currentMode = ViewMode.FIELDS;
            populateFields(clazz);
        }
    }

    private void populateFields(Class<?> clazz) {
        refreshEntries();
    }

    private void refreshEntries() {
        cachedEntries.clear();
        if (currentTarget == null) return;

        Class<?> clazz = currentTarget instanceof Class ? (Class<?>) currentTarget : currentTarget.getClass();
        if (fieldCache == null) {
            getAllField(clazz);
        }

        // 1. Separate Static and Non-Static fields
        List<Field> staticFields = new ArrayList<>();
        List<Field> instanceFields = new ArrayList<>();

        Set<Field> fields = fieldCache.get(this.classIndex);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                staticFields.add(field);
            } else {
                instanceFields.add(field);
            }
        }

        // Populate cache (you would do the same for methods)
        cachedEntries.add(new ExplorerEntry("--- Static Fields ---", null, null, true));
        staticFields.forEach(f -> cachedEntries.add(new ExplorerEntry(f.getName(), null, f, false)));

        cachedEntries.add(new ExplorerEntry("--- Instance Fields ---", null, null, true));
        instanceFields.forEach(f -> cachedEntries.add(new ExplorerEntry(f.getName(), null, f, false)));
    }

    private void getAllField(Class<?> clazz) {
        List<Class<?>> classes = new ArrayList<>();
        if (clazz.getSuperclass() == null) {
            classes.add(clazz);
        } else {
            Class<?> classHolder = clazz;
            do {
                classes.add(classHolder);
            } while ((classHolder = classHolder.getSuperclass()) != null);
        }

        List<Set<Field>> fields = new ArrayList<>();
        for (Class<?> aClass : classes) {
            fields.add(Set.of(aClass.getDeclaredFields()));
        }

        this.fieldCache = fields;
    }

    private void populateElements() {
        cachedEntries.add(new ExplorerEntry("--- Collection Elements ---", null, null, true));

        if (currentTarget.getClass().isArray()) {
            int length = Array.getLength(currentTarget);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(currentTarget, i);
                cachedEntries.add(new ExplorerEntry("[" + i + "] " + getPreview(element), element, null, false));
            }
        }
        else if (currentTarget instanceof Collection<?> coll) {
            int i = 0;
            for (Object element : coll) {
                cachedEntries.add(new ExplorerEntry("[" + i + "] " + getPreview(element), element, null, false));
                i++;
            }
        }
        else if (currentTarget instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String keyStr = getPreview(entry.getKey());
                cachedEntries.add(new ExplorerEntry("[" + keyStr + "] " + getPreview(entry.getValue()), entry.getValue(), null, false));
            }
        }
    }

    // Helper to generate a short string representation to prevent massive text overflow
    private String getPreview(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String || obj instanceof Number || obj instanceof Boolean) return obj.toString();
        return obj.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(obj));
    }

    @Override
    protected void init() {
        super.init(); // Always call super.init() to clear old widgets on resize

        int boxWidth = 150;
        int boxHeight = 20;
        int xPos = (this.width - boxWidth) / 2;
        int yPos = (this.height - boxHeight) / 2;

        // 1. Instantiate the EditBox
        // Parameters: Font, x, y, width, height, Accessibility/Narration message
        this.valueEditBox = new EditBox(this.font, xPos, yPos, boxWidth, boxHeight, Component.literal("Edit Value"));

        // 2. Set maximum string length (optional, defaults to 32, which is often too short for reflection)
        this.valueEditBox.setMaxLength(256);

        // 3. Make it invisible by default until the user left-clicks a field
        this.valueEditBox.visible = false;

        // 4. THE CRITICAL STEP: Register it to the screen
        // This ensures mouseClicked, keyPressed, and render are automatically handled!
        this.addRenderableWidget(this.valueEditBox);

        // Optional: Add a confirm button for the overlay
        Button confirmButton = Button
                .builder(Component.literal("Apply"), button -> applyValueChange(this.valueEditBox.getValue()))
                .bounds(xPos, yPos + 25, boxWidth, 20).build();

        confirmButton.visible = false;
        // Keep a reference to it if you need to toggle visibility later
        this.confirmButton = this.addRenderableWidget(confirmButton);

        Button goSuper = Button.builder(Component.literal("To Super"), button -> {
            if (classIndex < fieldCache.size() - 1) {
                classIndex++;
                refreshEntries();
            }
        }).bounds(xPos + 250, yPos + 150, boxWidth / 2, 20).build();

        this.superButton = this.addRenderableWidget(goSuper);

        Button goExtend = Button.builder(Component.literal("To Sub"), button -> {
            if (classIndex > 0) {
                classIndex--;
                refreshEntries();
            }
        }).bounds(xPos + 150, yPos + 150, boxWidth / 2, 20).build();

        this.extendButton = this.addRenderableWidget(goExtend);
    }

    @SuppressWarnings({"unchecked", "rawtypes", "CallToPrintStackTrace"})
    private void applyValueChange(String value) {
        log.info("change field {} value to: {}", editingField.getName(), value);

        Field f = editingField;
        if (f == null) {
            closeEditing();
            return;
        }
        if (!Modifier.isStatic(f.getModifiers())) {
            Optional<?> primitive = parsePrimitive(value, f);
            if (primitive.isPresent()) {
                try {
                    f.set(
                            currentTarget,
                            primitive.get()
                    );
                } catch (IllegalAccessException e) {
                    log.info("illegal access exception when applying value: {}", value);
                } catch (IllegalArgumentException ignored) {

                }
            } else if (value.equalsIgnoreCase("null")) {
                try {
                    f.set(
                           currentTarget,
                           null
                    );
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (f.getType().isEnum()) {
                try {
                    Class enumClass = f.getType();
                    f.set(
                            currentTarget,
                            Enum.valueOf(enumClass, value)
                    );
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        closeEditing();
    }

    private void closeEditing() {
        valueEditBox.setVisible(false);
        isEditing = false;
        this.setFocused(null);
        editingField = null;
        this.confirmButton.visible = false;

        this.extendButton.visible = true;
        this.superButton.visible = true;
    }

    private Optional<?> parsePrimitive(String value, Field field) {
        Object result;
        try {
            result = field.get(currentTarget);
        } catch (IllegalAccessException e) {
            return Optional.empty();
        }
        return Optional.ofNullable(
                switch (result) {
                    case Integer ignored -> Integer.parseInt(value);
                    case Float ignored -> Float.parseFloat(value);
                    case Boolean ignored -> Boolean.parseBoolean(value);
                    case String ignored -> value;
                    case Double ignored -> Double.parseDouble(value);
                    case Byte ignored -> Byte.parseByte(value);
                    case Short ignored -> Short.parseShort(value);
                    case Character ignored -> value.charAt(0);
                    case Long ignored -> Long.parseLong(value);
                    default -> null;
                }
        );
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        if (!isEditing) {
            // Render "Back" instruction
            guiGraphics.drawString(this.font, "Press ESC or BACKSPACE to go back", 10, 10, 0xFFFFFF);

            int yPos = 30 - (int) scrollOffset;

            for (ExplorerEntry entry : cachedEntries) {
                if (yPos > 20 && yPos < this.height) {
                    int color = entry.isHeader ? 0xFFAA00 : (isHovering(mouseX, mouseY, yPos) ? 0x00FF00 : 0xFFFFFF);
                    guiGraphics.drawString(this.font, entry.displayName, 20, yPos, color);
                }
                yPos += entryHeight;
            }

            yPos = 30;

            int color = 0xFFAA00;
            guiGraphics.drawString(this.font, currentTarget.getClass().getSimpleName() + "(" + getSuper() + ")", this.width - 200, yPos, color);
            yPos += entryHeight;
            for (Object o : history.reversed()) {
                if (yPos > 20 && yPos < this.height) {
                    guiGraphics.drawString(this.font, o.getClass().getSimpleName(), this.width - 200, yPos, 0xFFFFFF);
                }
                yPos += entryHeight;
            }

            this.superButton.render(guiGraphics, mouseX, mouseY, partialTick);
            this.extendButton.render(guiGraphics, mouseX, mouseY, partialTick);
        } else {
            guiGraphics.fill(0, 0, this.width, this.height, 0xAA000000);
            guiGraphics.drawCenteredString(this.font, "Enter new value:", this.width / 2, (this.height / 2) - 30, 0xFFFFFF);
            this.valueEditBox.render(guiGraphics, mouseX, mouseY, partialTick);
            this.confirmButton.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    private String getSuper() {
        Class<?> holder = currentTarget.getClass();
        for (int i = 0; i < classIndex; i++) {
            holder = holder.getSuperclass();
        }
        return holder.getSimpleName();
    }

    // 2. Mouse Input Handling
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int yPos = 30 - (int) scrollOffset;

        for (ExplorerEntry entry : cachedEntries) {
            if (!entry.isHeader && isHovering(mouseX, mouseY, yPos)) {

                if (button == 0 && currentMode == ViewMode.FIELDS && entry.field != null) {
                    // Left Click: Edit field value (Collections usually shouldn't be replaced wholesale)
                    openValueEditOverlay(entry.field);
                    return true;
                }
                else if (button == 1) {
                    // Right Click: Dive deeper
                    dive(entry);
                    return true;
                }
            }
            yPos += entryHeight;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void dive(ExplorerEntry entry) {
        Object nextTarget = null;

        if (currentMode == ViewMode.FIELDS && entry.field != null) {
            try {
                entry.field.setAccessible(true);
                // Use VarHandles or ASM accessors here for performance if implemented
                nextTarget = entry.field.get(Modifier.isStatic(entry.field.getModifiers()) ? null : currentTarget);
            } catch (IllegalAccessException | InaccessibleObjectException e) { return; }
        } else if (currentMode == ViewMode.ELEMENTS) {
            // We already have the direct object from the array/collection
            nextTarget = entry.elementInstance;
        }

        if (nextTarget != null) {
            history.push(currentTarget);
            currentTarget = nextTarget;
            classIndex = 0;
            fieldCache = null;
            scrollOffset = 0;
            refreshData(); // Will automatically switch to ViewMode.FIELDS for the new element
        }
    }

    // 3. Mouse Scrolling
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // scrollY is positive for scrolling up, negative for down
        scrollOffset -= scrollY * 15.0;
        scrollOffset = Math.max(0, scrollOffset); // Prevent scrolling past top
        return true;
    }

    // Keyboard handling for "Previous Page"
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isEditing) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                closeEditing();
                return true;
            }
        } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_ESCAPE) {
              if (!history.isEmpty()) {
                this.currentTarget = history.pop();
                classIndex = 0;
                fieldCache = null;
                this.scrollOffset = 0;
                refreshData();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void openValueEditOverlay(Field field) {
        this.isEditing = true;
        this.valueEditBox.visible = true;

        try {
            field.setAccessible(true);
            Object currentValue = field.get(Modifier.isStatic(field.getModifiers()) ? null : currentTarget);

            // Set the text box to the current value
            this.valueEditBox.setValue(currentValue == null ? "null" : currentValue.toString().trim());

            editingField = field;
        } catch (IllegalAccessException e) {
            this.valueEditBox.setValue("Access Denied");
        }

        // Force focus so typing works immediately without having to click the box
        this.setFocused(this.valueEditBox);
        this.valueEditBox.setFocused(true);
        this.confirmButton.visible = true;

        this.extendButton.visible = false;
        this.superButton.visible = false;
    }

    private boolean isHovering(double mouseX, double mouseY, int yPos) {
        return mouseX >= 20 && mouseX <= 200 && mouseY >= yPos && mouseY < yPos + font.lineHeight;
    }

    private static class ExplorerEntry {
        String displayName;
        Field field;             // Populated if ViewMode.FIELDS
        Object elementInstance;  // Populated if ViewMode.ELEMENTS
        boolean isHeader;

        public ExplorerEntry(String displayName, Object instance, Field field, boolean isHeader) {
            this.displayName = displayName;
            this.elementInstance = instance;
            this.field = field;
            this.isHeader = isHeader;
        }
    }
}