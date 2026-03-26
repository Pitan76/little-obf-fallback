package net.minecraft;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// Component (Text)
public interface class_2561 extends Component {
    default Style method_10866() {
        return this.getStyle();
    }

    default ComponentContents method_10851() {
        return this.getContents(); // getContent()
    }

    default String method_10858(int length) {
        return this.getString(length);
    }

    default List<Component> method_10855() {
        return this.getSiblings();
    }

    default boolean method_44745(Component text) {
        return this.contains(text);
    }

    static Component method_30163(@Nullable String string) {
        return Component.nullToEmpty(string);
    }

    static MutableComponent method_43470(String string) {
        return Component.literal(string);
    }

    static MutableComponent method_43471(String string) {
        return Component.translatable(string);
    }

    static MutableComponent method_43469(String string, Object... args) {
        return Component.translatable(string, args);
    }

    static MutableComponent method_43473() {
        return Component.empty();
    }
}
