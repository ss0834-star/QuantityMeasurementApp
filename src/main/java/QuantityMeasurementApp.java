public class QuantityMeasurementApp {

    static class Feet {
        private final double value;

        public Feet(double value) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Feet value must be numeric");
            }
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            Feet feet = (Feet) obj;
            return Double.compare(feet.value, value) == 0;
        }
    }

    static class Inches {
        private final double value;

        public Inches(double value) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Inches value must be numeric");
            }
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            Inches inches = (Inches) obj;
            return Double.compare(inches.value, value) == 0;
        }
    }

    static boolean compareFeetEquality(double firstValue, double secondValue) {
        Feet firstFeet = new Feet(firstValue);
        Feet secondFeet = new Feet(secondValue);
        return firstFeet.equals(secondFeet);
    }

    static boolean compareInchesEquality(double firstValue, double secondValue) {
        Inches firstInches = new Inches(firstValue);
        Inches secondInches = new Inches(secondValue);
        return firstInches.equals(secondInches);
    }

    public static void main(String[] args) {
        System.out.println("Input: 1.0 ft and 1.0 ft");
        System.out.println("Output: Equal (" + compareFeetEquality(1.0, 1.0) + ")");

        System.out.println("Input: 1.0 inch and 1.0 inch");
        System.out.println("Output: Equal (" + compareInchesEquality(1.0, 1.0) + ")");

        System.out.println("Input: 1.0 ft and 2.0 ft");
        System.out.println("Output: Equal (" + compareFeetEquality(1.0, 2.0) + ")");

        System.out.println("Input: 1.0 inch and 2.0 inch");
        System.out.println("Output: Equal (" + compareInchesEquality(1.0, 2.0) + ")");
    }
}