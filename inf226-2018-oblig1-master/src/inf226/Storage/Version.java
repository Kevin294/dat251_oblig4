package inf226.Storage;

/**
 * Keeping track of versions in a Storage.
 * @author INF226
 *
 */
public final class Version {

	public final Id id; 

	private static Integer serial_counter = 0;
    private final Integer serial;
    
    public Version(Id id) {
    	synchronized(serial_counter) {
    		this.serial = serial_counter++;
    	}
    	this.id = id;
    }
	public boolean equals(Version rhs) {
		if(! this.id.equals(rhs.id)) {
			throw new RuntimeException("Tried to compare versions with different ids: "
		                               + this.id.toString() + "≠" + rhs.toString() );
		}
		return (this.serial.equals(rhs.serial));
	}

	/**
	 * 
	 * @return A newer version-number.
	 */
	public Version increment() {
		return new Version(id);
	}
}
