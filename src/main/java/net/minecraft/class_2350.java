package net.minecraft;

import net.minecraft.core.Direction;

// Direction
public interface class_2350 {

//    class_2350 field_11043 = (class_2350) (Object) Direction.NORTH;
//    class_2350 field_11035 = (class_2350) (Object) Direction.SOUTH;
//    class_2350 field_11034 = (class_2350) (Object) Direction.EAST;
//    class_2350 field_11039 = (class_2350) (Object) Direction.WEST;
//    class_2350 field_11036 = (class_2350) (Object) Direction.UP;
//    class_2350 field_11033 = (class_2350) (Object) Direction.DOWN;

    class Holder {
        static class_2350 north() { return (class_2350) (Object) Direction.NORTH; }
        static class_2350 south() { return (class_2350) (Object) Direction.SOUTH; }
        static class_2350 east() { return (class_2350) (Object) Direction.EAST; }
        static class_2350 west() { return (class_2350) (Object) Direction.WEST; }
        static class_2350 up() { return (class_2350) (Object) Direction.UP; }
        static class_2350 down() { return (class_2350) (Object) Direction.DOWN; }
    }

    class_2350 field_11043 = Holder.north();
    class_2350 field_11035 = Holder.south();
    class_2350 field_11034 = Holder.east();
    class_2350 field_11039 = Holder.west();
    class_2350 field_11036 = Holder.up();
    class_2350 field_11033 = Holder.down();

    default Direction asDirection() {
        return (Direction) (Object) this;
    }

    default Direction method_10153() {
        return asDirection().getOpposite();
    }

    static class_2350[] values() {
        return (class_2350[]) (Object[]) Direction.values();
    }
}
