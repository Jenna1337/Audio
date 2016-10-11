package audio;

import javax.sound.midi.*;

public class MIDI implements Waitable
{
	private Sequencer sequencer;
	private Sequence sequence;
	private long ticks = 0;
	
	public MIDI()
	{
		try
		{
			sequencer = MidiSystem.getSequencer();
			sequence = new Sequence(Sequence.PPQ, 30);
			sequencer.setSequence(sequence);
		}
		catch(Exception e)
		{
			throw new Error(e);
		}
	}
	public MIDI addNote(int d1, int d2, long duration)
	{
		try
		{
			sequence.getTracks()[0]
					.add(new MidiEvent(new javax.sound.midi.ShortMessage(
							ShortMessage.NOTE_ON, d1, d2), ticks));
			sequence.getTracks()[0]
					.add(new MidiEvent(
							new javax.sound.midi.ShortMessage(
									ShortMessage.NOTE_OFF, d1, d2),
							ticks += duration));
		}
		catch(Exception e)
		{
			throw new Error(e);
		}
		return this;
	}
	
	/**
	 * A value indicating that looping should continue indefinitely rather than
	 * complete after a specific number of loops.
	 * 
	 * @see #setLoops(int)
	 */
	public int LOOP_CONTINUOUSLY = Sequencer.LOOP_CONTINUOUSLY;
	
	/**
	 * 
	 * @param loops - the number of times playback should loop back, or
	 *            LOOP_CONTINUOUSLY to indicate that looping should continue
	 *            until interrupted
	 * @throws IllegalArgumentException if count is negative and not equal to
	 *             LOOP_CONTINUOUSLY
	 * @return <code>this</code>
	 * @see #play()
	 */
	public MIDI setLoops(int loops)
	{
		sequencer.setLoopCount(loops);
		return this;
	}
	/**
	 * 
	 * @return <code>this</code>
	 */
	public MIDI play()
	{
		sequencer.start();
		return this;
	}
	
	private boolean interrupted;
	
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
	public void waitUntilDone() throws InterruptedException
	{
		interrupted = false;
		InterruptedException e = null;
		try
		{
			while(sequencer.isRunning() && !interrupted)
				Thread.sleep(1);
		}
		catch(InterruptedException ie)
		{
			e = ie;
		}
		sequencer.stop();
		if(interrupted)
			throw e != null ? e : new InterruptedException();
	}
	/**
	 * Interrupts the {@link #waitUntilDone()} method.
	 * 
	 * @see #waitUntilDone()
	 */
	public void interrupt()
	{
		interrupted = true;
	}
}
