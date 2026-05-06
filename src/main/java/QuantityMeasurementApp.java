public class QuantityMeasurementApp {

    interface IMeasurable {
        double getConversionFactor();
        double convertToBaseUnit(double value);
        double convertFromBaseUnit(double baseValue);
        String getUnitName();
    }

    enum LengthUnit implements IMeasurable {
        FEET(1.0), INCHES(1.0 / 12.0), YARDS(3.0), CENTIMETERS(0.0328084);

        private final double conversionFactor;

        LengthUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        public double getConversionFactor() { return conversionFactor; }
        public double convertToBaseUnit(double value) { return value * conversionFactor; }
        public double convertFromBaseUnit(double baseValue) { return baseValue / conversionFactor; }
        public String getUnitName() { return name(); }
    }

    enum WeightUnit implements IMeasurable {
        KILOGRAM(1.0), GRAM(0.001), POUND(0.453592);

        private final double conversionFactor;

        WeightUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        public double getConversionFactor() { return conversionFactor; }
        public double convertToBaseUnit(double value) { return value * conversionFactor; }
        public double convertFromBaseUnit(double baseValue) { return baseValue / conversionFactor; }
        public String getUnitName() { return name(); }
    }

    enum VolumeUnit implements IMeasurable {
        LITRE(1.0), MILLILITRE(0.001), GALLON(3.78541);

        private final double conversionFactor;

        VolumeUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        public double getConversionFactor() { return conversionFactor; }
        public double convertToBaseUnit(double value) { return value * conversionFactor; }
        public double convertFromBaseUnit(double baseValue) { return baseValue / conversionFactor; }
        public String getUnitName() { return name(); }
    }

    enum ArithmeticOperation {
        ADD {
            public double compute(double first, double second) {
                return first + second;
            }
        },
        SUBTRACT {
            public double compute(double first, double second) {
                return first - second;
            }
        },
        DIVIDE {
            public double compute(double first, double second) {
                if (Math.abs(second) < Quantity.EPSILON) {
                    throw new ArithmeticException("Division by zero is not allowed");
                }
                return first / second;
            }
        };

        public abstract double compute(double first, double second);
    }

    static class Quantity<U extends IMeasurable> {
        private static final double EPSILON = 0.000001;

        private final double value;
        private final U unit;

        public Quantity(double value, U unit) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid numeric value");
            }
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            this.value = value;
            this.unit = unit;
        }

        private double convertToBaseUnit() {
            return unit.convertToBaseUnit(value);
        }

        private void validateArithmeticOperands(Quantity<U> other, U targetUnit, boolean targetUnitRequired) {
            if (other == null) {
                throw new IllegalArgumentException("Other quantity cannot be null");
            }
            if (targetUnitRequired && targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }
            if (this.unit.getClass() != other.unit.getClass()) {
                throw new IllegalArgumentException("Cannot operate on different measurement categories");
            }
        }

        private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation operation) {
            validateArithmeticOperands(other, this.unit, false);

            double firstBaseValue = this.convertToBaseUnit();
            double secondBaseValue = other.convertToBaseUnit();

            return operation.compute(firstBaseValue, secondBaseValue);
        }

        public Quantity<U> convertTo(U targetUnit) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            return new Quantity<>(
                    targetUnit.convertFromBaseUnit(convertToBaseUnit()),
                    targetUnit
            );
        }

        public Quantity<U> add(Quantity<U> other) {
            return add(other, this.unit);
        }

        public Quantity<U> add(Quantity<U> other, U targetUnit) {
            validateArithmeticOperands(other, targetUnit, true);
            double resultBase = performBaseArithmetic(other, ArithmeticOperation.ADD);
            return new Quantity<>(targetUnit.convertFromBaseUnit(resultBase), targetUnit);
        }

        public Quantity<U> subtract(Quantity<U> other) {
            return subtract(other, this.unit);
        }

        public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
            validateArithmeticOperands(other, targetUnit, true);
            double resultBase = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);
            return new Quantity<>(targetUnit.convertFromBaseUnit(resultBase), targetUnit);
        }

        public double divide(Quantity<U> other) {
            return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            Quantity<?> other = (Quantity<?>) obj;

            if (this.unit.getClass() != other.unit.getClass()) {
                return false;
            }

            return Math.abs(
                    this.convertToBaseUnit() - other.unit.convertToBaseUnit(other.value)
            ) < EPSILON;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(Math.round(convertToBaseUnit() / EPSILON) * EPSILON);
        }

        @Override
        public String toString() {
            return value + " " + unit.getUnitName();
        }
    }

    public static void main(String[] args) {
        System.out.println(new Quantity<>(1.0, LengthUnit.FEET)
                .add(new Quantity<>(12.0, LengthUnit.INCHES)));

        System.out.println(new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(6.0, LengthUnit.INCHES)));

        System.out.println(new Quantity<>(10.0, LengthUnit.FEET)
                .divide(new Quantity<>(2.0, LengthUnit.FEET)));

        System.out.println(new Quantity<>(10.0, WeightUnit.KILOGRAM)
                .add(new Quantity<>(5000.0, WeightUnit.GRAM), WeightUnit.GRAM));

        System.out.println(new Quantity<>(5.0, VolumeUnit.LITRE)
                .subtract(new Quantity<>(2.0, VolumeUnit.LITRE), VolumeUnit.MILLILITRE));
    }
}