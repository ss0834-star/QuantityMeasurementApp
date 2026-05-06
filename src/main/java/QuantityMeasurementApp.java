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
    }

    static class QuantityLength {

        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Value must be numeric");
            }

            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }

            this.value = value;
            this.unit = unit;
        }

        private double convertToFeet() {
            return value * unit.getConversionFactor();
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

            return Math.abs(this.convertToFeet() - other.convertToFeet()) < 0.000001;
        }
    }

    public static void main(String[] args) {
        System.out.println(new QuantityLength(1.0, LengthUnit.YARDS)
                .equals(new QuantityLength(3.0, LengthUnit.FEET)));

        System.out.println(new QuantityLength(1.0, LengthUnit.YARDS)
                .equals(new QuantityLength(36.0, LengthUnit.INCHES)));

        System.out.println(new QuantityLength(2.0, LengthUnit.CENTIMETERS)
                .equals(new QuantityLength(2.0, LengthUnit.CENTIMETERS)));

        System.out.println(new QuantityLength(1.0, LengthUnit.CENTIMETERS)
                .equals(new QuantityLength(0.393701, LengthUnit.INCHES)));
    }
}