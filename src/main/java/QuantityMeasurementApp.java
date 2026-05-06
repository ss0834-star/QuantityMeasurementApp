public class QuantityMeasurementApp {

    interface IMeasurable {
        double getConversionFactor();
        double convertToBaseUnit(double value);
        double convertFromBaseUnit(double baseValue);
        String getUnitName();
    }

    enum LengthUnit implements IMeasurable {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.0328084);

        private final double conversionFactor;

        LengthUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        public double getConversionFactor() {
            return conversionFactor;
        }

        public double convertToBaseUnit(double value) {
            return value * conversionFactor;
        }

        public double convertFromBaseUnit(double baseValue) {
            return baseValue / conversionFactor;
        }

        public String getUnitName() {
            return name();
        }
    }

    enum WeightUnit implements IMeasurable {
        KILOGRAM(1.0),
        GRAM(0.001),
        POUND(0.453592);

        private final double conversionFactor;

        WeightUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        public double getConversionFactor() {
            return conversionFactor;
        }

        public double convertToBaseUnit(double value) {
            return value * conversionFactor;
        }

        public double convertFromBaseUnit(double baseValue) {
            return baseValue / conversionFactor;
        }

        public String getUnitName() {
            return name();
        }
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

        public Quantity<U> convertTo(U targetUnit) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double baseValue = convertToBaseUnit();
            double convertedValue = targetUnit.convertFromBaseUnit(baseValue);

            return new Quantity<>(convertedValue, targetUnit);
        }

        public Quantity<U> add(Quantity<U> other) {
            return add(other, this.unit);
        }

        public Quantity<U> add(Quantity<U> other, U targetUnit) {
            validateOtherAndTarget(other, targetUnit);

            double sumBase =
                    this.convertToBaseUnit() + other.convertToBaseUnit();

            double result =
                    targetUnit.convertFromBaseUnit(sumBase);

            return new Quantity<>(result, targetUnit);
        }

        private void validateOtherAndTarget(Quantity<U> other, U targetUnit) {
            if (other == null) {
                throw new IllegalArgumentException("Other quantity cannot be null");
            }

            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            if (this.unit.getClass() != other.unit.getClass()) {
                throw new IllegalArgumentException("Cannot operate on different measurement categories");
            }
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
                .equals(new Quantity<>(12.0, LengthUnit.INCHES)));

        System.out.println(new Quantity<>(1.0, LengthUnit.FEET)
                .convertTo(LengthUnit.INCHES));

        System.out.println(new Quantity<>(1.0, LengthUnit.FEET)
                .add(new Quantity<>(12.0, LengthUnit.INCHES), LengthUnit.FEET));

        System.out.println(new Quantity<>(1.0, WeightUnit.KILOGRAM)
                .equals(new Quantity<>(1000.0, WeightUnit.GRAM)));

        System.out.println(new Quantity<>(1.0, WeightUnit.KILOGRAM)
                .convertTo(WeightUnit.GRAM));

        System.out.println(new Quantity<>(1.0, WeightUnit.KILOGRAM)
                .add(new Quantity<>(1000.0, WeightUnit.GRAM), WeightUnit.KILOGRAM));

        System.out.println(new Quantity<>(1.0, LengthUnit.FEET)
                .equals(new Quantity<>(1.0, WeightUnit.KILOGRAM)));
    }
}