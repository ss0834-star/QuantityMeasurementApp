public class QuantityMeasurementApp {

    enum LengthUnit {
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
    }

    static class QuantityLength {

        private static final double EPSILON = 0.000001;

        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
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

        public QuantityLength convertTo(LengthUnit targetUnit) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double baseValue = convertToBaseUnit();
            double convertedValue = targetUnit.convertFromBaseUnit(baseValue);

            return new QuantityLength(convertedValue, targetUnit);
        }

        public QuantityLength add(QuantityLength other) {
            return add(other, this.unit);
        }

        public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {
            if (other == null) {
                throw new IllegalArgumentException("Other quantity cannot be null");
            }

            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double sumBase =
                    this.convertToBaseUnit() + other.convertToBaseUnit();

            double result =
                    targetUnit.convertFromBaseUnit(sumBase);

            return new QuantityLength(result, targetUnit);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            QuantityLength other = (QuantityLength) obj;

            return Math.abs(
                    this.convertToBaseUnit() - other.convertToBaseUnit()
            ) < EPSILON;
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    public static void main(String[] args) {
        System.out.println(new QuantityLength(1.0, LengthUnit.FEET)
                .convertTo(LengthUnit.INCHES));

        System.out.println(new QuantityLength(1.0, LengthUnit.FEET)
                .add(new QuantityLength(12.0, LengthUnit.INCHES), LengthUnit.FEET));

        System.out.println(new QuantityLength(36.0, LengthUnit.INCHES)
                .equals(new QuantityLength(1.0, LengthUnit.YARDS)));

        System.out.println(new QuantityLength(1.0, LengthUnit.YARDS)
                .add(new QuantityLength(3.0, LengthUnit.FEET), LengthUnit.YARDS));

        System.out.println(LengthUnit.INCHES.convertToBaseUnit(12.0));
    }
}