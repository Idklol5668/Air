package net.minecraft.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public enum Direction implements IStringSerializable {
    DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
    UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
    WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

    /**
     * Ordering index for D-U-N-S-W-E
     */
    private final int index;

    /**
     * Index of the opposite Facing in the VALUES array
     */
    private final int opposite;

    /**
     * Ordering index for the HORIZONTALS field (S-W-N-E)
     */
    private final int horizontalIndex;
    private final String name;
    private final Direction.Axis axis;
    private final Direction.AxisDirection axisDirection;

    /**
     * Normalized Vector that points in the direction of this Facing
     */
    private final Vec3i directionVec;

    /**
     * All facings in D-U-N-S-W-E order
     */
    public static final Direction[] VALUES = new Direction[6];

    /**
     * All Facings with horizontal axis in order S-W-N-E
     */
    private static final Direction[] HORIZONTALS = new Direction[4];
    private static final Map<String, Direction> NAME_LOOKUP = Maps.newHashMap();

    Direction(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, Direction.AxisDirection axisDirectionIn, Direction.Axis axisIn, Vec3i directionVecIn) {
        this.index = indexIn;
        this.horizontalIndex = horizontalIndexIn;
        this.opposite = oppositeIn;
        this.name = nameIn;
        this.axis = axisIn;
        this.axisDirection = axisDirectionIn;
        this.directionVec = directionVecIn;
    }

    /**
     * Get the Index of this Facing (0-5). The order is D-U-N-S-W-E
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Get the index of this horizontal facing (0-3). The order is S-W-N-E
     */
    public int getHorizontalIndex() {
        return this.horizontalIndex;
    }

    /**
     * Get the AxisDirection of this Facing.
     */
    public Direction.AxisDirection getAxisDirection() {
        return this.axisDirection;
    }

    /**
     * Get the opposite Facing (e.g. DOWN => UP)
     */
    public Direction getOpposite() {
        return VALUES[this.opposite];
    }

    /**
     * Rotate this Facing around the given axis clockwise. If this facing cannot be rotated around the given axis,
     * returns this facing without rotating.
     */
    public Direction rotateAround(Direction.Axis axis) {
        switch (axis) {
            case X:
                if (this != WEST && this != EAST) {
                    return this.rotateX();
                }

                return this;

            case Y:
                if (this != UP && this != DOWN) {
                    return this.rotateY();
                }

                return this;

            case Z:
                if (this != NORTH && this != SOUTH) {
                    return this.rotateZ();
                }

                return this;

            default:
                throw new IllegalStateException("Unable to get CW facing for axis " + axis);
        }
    }

    /**
     * Rotate this Facing around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
     */
    public Direction rotateY() {
        switch (this) {
            case NORTH:
                return EAST;

            case EAST:
                return SOUTH;

            case SOUTH:
                return WEST;

            case WEST:
                return NORTH;

            default:
                throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        }
    }

    /**
     * Rotate this Facing around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
     */
    private Direction rotateX() {
        switch (this) {
            case NORTH:
                return DOWN;

            case EAST:
            case WEST:
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of " + this);

            case SOUTH:
                return UP;

            case UP:
                return NORTH;

            case DOWN:
                return SOUTH;
        }
    }

    /**
     * Rotate this Facing around the Z axis (EAST => DOWN => WEST => UP => EAST)
     */
    private Direction rotateZ() {
        switch (this) {
            case EAST:
                return DOWN;

            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);

            case WEST:
                return UP;

            case UP:
                return EAST;

            case DOWN:
                return WEST;
        }
    }

    /**
     * Rotate this Facing around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
     */
    public Direction rotateYCCW() {
        switch (this) {
            case NORTH:
                return WEST;

            case EAST:
                return NORTH;

            case SOUTH:
                return EAST;

            case WEST:
                return SOUTH;

            default:
                throw new IllegalStateException("Unable to get CCW facing of " + this);
        }
    }

    /**
     * Returns a offset that addresses the block in front of this facing.
     */
    public int getFrontOffsetX() {
        return this.axis == Direction.Axis.X ? this.axisDirection.getOffset() : 0;
    }

    public int getFrontOffsetY() {
        return this.axis == Direction.Axis.Y ? this.axisDirection.getOffset() : 0;
    }

    /**
     * Returns a offset that addresses the block in front of this facing.
     */
    public int getFrontOffsetZ() {
        return this.axis == Direction.Axis.Z ? this.axisDirection.getOffset() : 0;
    }

    /**
     * Same as getName, but does not override the method from Enum.
     */
    public String getName2() {
        return this.name;
    }

    public Direction.Axis getAxis() {
        return this.axis;
    }

    /**
     * Get the facing specified by the given name
     */
    public static Direction byName(String name) {
        return name == null ? null : NAME_LOOKUP.get(name.toLowerCase());
    }

    /**
     * Get a Facing by it's index (0-5). The order is D-U-N-S-W-E. Named getFront for legacy reasons.
     */
    public static Direction getFront(int index) {
        return VALUES[MathHelper.abs_int(index % VALUES.length)];
    }

    /**
     * Get a Facing by it's horizontal index (0-3). The order is S-W-N-E.
     */
    public static Direction getHorizontal(int p_176731_0_) {
        return HORIZONTALS[MathHelper.abs_int(p_176731_0_ % HORIZONTALS.length)];
    }

    /**
     * Get the Facing corresponding to the given angle (0-360). An angle of 0 is SOUTH, an angle of 90 would be WEST.
     */
    public static Direction fromAngle(double angle) {
        return getHorizontal(MathHelper.floor_double(angle / 90.0D + 0.5D) & 3);
    }

    /**
     * Choose a random Facing using the given Random
     */
    public static Direction random(Random rand) {
        return values()[rand.nextInt(values().length)];
    }

    public static Direction getFacingFromVector(float p_176737_0_, float p_176737_1_, float p_176737_2_) {
        Direction enumfacing = NORTH;
        float f = Float.MIN_VALUE;

        for (Direction enumfacing1 : values()) {
            float f1 = p_176737_0_ * (float) enumfacing1.directionVec.getX() + p_176737_1_ * (float) enumfacing1.directionVec.getY() + p_176737_2_ * (float) enumfacing1.directionVec.getZ();

            if (f1 > f) {
                f = f1;
                enumfacing = enumfacing1;
            }
        }

        return enumfacing;
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public static Direction getFacingFromAxis(Direction.AxisDirection p_181076_0_, Direction.Axis p_181076_1_) {
        for (Direction enumfacing : values()) {
            if (enumfacing.getAxisDirection() == p_181076_0_ && enumfacing.getAxis() == p_181076_1_) {
                return enumfacing;
            }
        }

        throw new IllegalArgumentException("No such direction: " + p_181076_0_ + " " + p_181076_1_);
    }

    /**
     * Get a normalized Vector that points in the direction of this Facing.
     */
    public Vec3i getDirectionVec() {
        return this.directionVec;
    }

    static {
        for (Direction enumfacing : values()) {
            VALUES[enumfacing.index] = enumfacing;

            if (enumfacing.getAxis().isHorizontal()) {
                HORIZONTALS[enumfacing.horizontalIndex] = enumfacing;
            }

            NAME_LOOKUP.put(enumfacing.getName2().toLowerCase(), enumfacing);
        }
    }

    public enum Axis implements Predicate<Direction>, IStringSerializable {
        X("x", Direction.Plane.HORIZONTAL),
        Y("y", Direction.Plane.VERTICAL),
        Z("z", Direction.Plane.HORIZONTAL);

        private static final Map<String, Direction.Axis> NAME_LOOKUP = Maps.newHashMap();
        private final String name;
        private final Direction.Plane plane;

        Axis(String name, Direction.Plane plane) {
            this.name = name;
            this.plane = plane;
        }

        public static Direction.Axis byName(String name) {
            return name == null ? null : NAME_LOOKUP.get(name.toLowerCase());
        }

        public String getName2() {
            return this.name;
        }

        public boolean isVertical() {
            return this.plane == Direction.Plane.VERTICAL;
        }

        public boolean isHorizontal() {
            return this.plane == Direction.Plane.HORIZONTAL;
        }

        public String toString() {
            return this.name;
        }

        public boolean apply(Direction p_apply_1_) {
            return p_apply_1_ != null && p_apply_1_.getAxis() == this;
        }

        public Direction.Plane getPlane() {
            return this.plane;
        }

        public String getName() {
            return this.name;
        }

        static {
            for (Direction.Axis enumfacing$axis : values()) {
                NAME_LOOKUP.put(enumfacing$axis.getName2().toLowerCase(), enumfacing$axis);
            }
        }
    }

    public enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int offset;
        private final String description;

        AxisDirection(int offset, String description) {
            this.offset = offset;
            this.description = description;
        }

        public int getOffset() {
            return this.offset;
        }

        public String toString() {
            return this.description;
        }
    }

    public enum Plane implements Predicate<Direction>, Iterable<Direction> {
        HORIZONTAL,
        VERTICAL;

        public Direction[] facings() {
            switch (this) {
                case HORIZONTAL:
                    return new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
                case VERTICAL:
                    return new Direction[]{Direction.UP, Direction.DOWN};
                default:
                    throw new Error("Someone's been tampering with the universe!");
            }
        }

        public Direction random(Random rand) {
            Direction[] aenumfacing = this.facings();
            return aenumfacing[rand.nextInt(aenumfacing.length)];
        }

        public boolean apply(Direction p_apply_1_) {
            return p_apply_1_ != null && p_apply_1_.getAxis().getPlane() == this;
        }

        public Iterator<Direction> iterator() {
            return Iterators.forArray(this.facings());
        }
    }
}
