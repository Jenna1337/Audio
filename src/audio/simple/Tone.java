package audio.simple;

public class Tone
{
	private Note note;
	private int milli;
	public Tone(Note note, int milli)
	{
		this.setNote(note);
		this.setMilli(milli);
	}
	public Note getNote()
	{
		return note;
	}
	public void setNote(Note note)
	{
		this.note = note;
	}
	public int getMilli()
	{
		return milli;
	}
	public void setMilli(int milli)
	{
		this.milli = milli;
	}
}
