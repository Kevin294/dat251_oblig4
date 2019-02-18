package inf226.Storage;

/**
 * Class of identifiers to identify stored objects.
 * 
 * @author INF226
 *
 */
public final class Id implements Comparable<Id> {
	private final Generator generator;
    private final Integer number;
    private Id(Generator generator, Integer number) {
    	this.generator = generator;
    	this.number = number;
    }

    /**
     * Compare two Ids.
     * @param rhs right-hand-side
     * @return True if Ids are the same and from the same generator.
     */
	public boolean equals(Id rhs) {
        if (!this.generator.equals(rhs.generator)) {
        	throw new RuntimeException
        	   ("Identifiers from different serials compared: " 
        	    + this.generator.toString() + " ≠" + rhs.generator.toString() );
        }
		return this.number.equals(rhs.number);
	}
	
	public static class Generator {
		private static Integer serial_counter = 0;
		private final Integer serial;
		private Integer number_counter = 0;
		public Generator(){
			serial = serial_counter++;
			number_counter = 0;
		}
		
		public Id fresh() {
			Integer number = number_counter++;
			return new Id(this,number);
		}
		
		public boolean equals(Generator g) {
			return this.serial.equals(g.serial);
		}
		
		public String toString() {
			return "Id generator";
		}
	}

	/**
	 * Unsafe, only used by TreeMap.
	 */
	@Override
	public int compareTo(Id arg) {
		return number - arg.number;
	}
}
