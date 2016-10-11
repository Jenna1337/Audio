package audio.simple;

import java.util.ArrayList;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.LineUnavailableException;
import audio.Waitable;

public class SimpleToneSequencer implements Waitable
{
	private static LineUnavailableException err = null;
	private static final SimpleTonePlayer player = getSimpleTonePlayer();
	private int loopcount = 1;
	
	private static SimpleTonePlayer getSimpleTonePlayer()
	{
		try
		{
			return new SimpleTonePlayer();
		}
		catch(LineUnavailableException e)
		{
			err = e;
			return null;
		}
	}
	
	private ArrayList<Tone> tones;
	private int length_milli = 0;
	
	public SimpleToneSequencer() throws LineUnavailableException
	{
		if(err != null || player == null)
			throw err;
		tones = new ArrayList<Tone>();
	}
	public SimpleToneSequencer addTone(Note n, int d)
	{
		return this.addTone(new Tone(n, d));
	}
	public SimpleToneSequencer addTone(Tone t)
	{
		tones.add(t);
		length_milli += t.getMilli();
		return this;
	}
	public int getLengthMilli()
	{
		return length_milli;
	}
	/**
	 * Note: it is not recommended to change the loop count or add/remove tones
	 * while it is playing.
	 */
	public void play()
	{
		new Thread()
		{
			public void run()
			{
				if(loopcount!=LOOP_CONTINUOUSLY)
					for(int i = 0; i < loopcount; ++i)
						for(Tone t : tones)
							player.play(t.getNote(), t.getMilli());
				else
					while(!interrupted)
						for(Tone t : tones)
							if(!interrupted)
								player.play(t.getNote(), t.getMilli());
			}
		}.start();
		
	}
	public boolean isPlaying()
	{
		return player.isPlaying();
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
	public SimpleToneSequencer setLoops(int loops)
	{
		loopcount = loops;
		return this;
	}
	
	private volatile boolean interrupted;
	
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
			while(player.isPlaying() && !interrupted)
				Thread.sleep(1);
		}
		catch(InterruptedException ie)
		{
			e = ie;
		}
		player.drain();
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
