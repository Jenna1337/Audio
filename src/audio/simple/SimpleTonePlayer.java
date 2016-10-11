package audio.simple;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SimpleTonePlayer implements java.io.Closeable
{
	private static final AudioFormat af = new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, true);
	private static LineUnavailableException err = null;
	private static final SourceDataLine line = getSourceDataLine();
	private static SourceDataLine getSourceDataLine()
	{
		try
		{
			return AudioSystem.getSourceDataLine(af);
		}
		catch(LineUnavailableException e)
		{
			err=e;
			return null;
		}
	}
	public SimpleTonePlayer() throws LineUnavailableException
	{
		if(err!=null || line==null)
			throw err;
		line.open(af, Note.SAMPLE_RATE);
		line.start();
	}
	public SimpleTonePlayer drain()
	{
		line.drain();
		return this;
	}
	public void close()
	{
		line.drain();
		line.close();
	}
	public SimpleTonePlayer play(Note note, int ms)
	{
		ms = Math.min(ms, Note.SECONDS * 1000);
		int length = Note.SAMPLE_RATE * ms / 1000;
		@SuppressWarnings("unused")
		int count = line.write(note.data(), 0, length);
		return this;
	}
	public boolean isPlaying()
	{
		return line.isRunning();
	}
}
