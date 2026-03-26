package net.minecraft;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.List;

// MutableComponent (MutableText)
public class class_5250 extends MutableComponent {

    public class_5250(ComponentContents contents, List<Component> siblings, Style style) {
        super(contents, siblings, style);
    }

    public static MutableComponent of(ComponentContents content) {
        return new MutableComponent(content, Lists.newArrayList(), Style.EMPTY);
    }
}
