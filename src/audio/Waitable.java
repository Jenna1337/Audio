package audio;

public interface Waitable
{
	/**
	 * This method will only return if either the song is done playing or the
	 * {@link #interrupt()} method is called.
	 * Please note that this method will never return if the loops are set to
	 * {@link #LOOP_CONTINUOUSLY}
	 * 
	 * @throws InterruptedException if this object was interrupted with the
	 *             {@link #interrupt()} method.
	 * @see #interrupt()
	 */
	public void waitUntilDone() throws InterruptedException;
	/**
	 * Interrupts the {@link #waitUntilDone()} method.
	 * 
	 * @see #waitUntilDone()
	 */
	public void interrupt();
}
