package org.rsbot.script;

/** This is a Timer based on the Timer at javadocs.rscheata.net */
public class Timer extends java.lang.Object
{
	public long endTime;
	public long startTime;
	public long timeLimit;
	
	public Timer(long timeLimit)
	{
		this.timeLimit = timeLimit;
		this.startTime = System.currentTimeMillis();
		this.endTime = startTime + timeLimit;
	}
	
	public long getTimeElapsed()
	{
		return (System.currentTimeMillis() - startTime);
	}
	
	public long getTimeRemaining()
	{
		if (isNotUp())
			return (endTime - System.currentTimeMillis());
		else
			return (0);
	}
	
	public boolean isNotUp()
	{
		if (System.currentTimeMillis() < endTime) return true;
		else return false;
	}
	
	public boolean isUp()
	{
		if (System.currentTimeMillis() >= endTime) return true;
		else return false;
	}
	
	public boolean isUpThenReset()
	{
		if (isUp())
		{
			reset();
			return true;
		}
		else return false;
	}
	
	public void reset()
	{
		this.endTime = System.currentTimeMillis() + timeLimit;
	}
	
	public long setTimerToEndIn(long ms)
	{
		this.endTime = System.currentTimeMillis() + ms;
		return endTime;
	}
	
	public java.lang.String toString()
	{
		return getClass().getName() + '@' + Integer.toHexString(hashCode());
	}
	
	public static java.lang.String toStringTime(long time)
	{
		final StringBuilder t = new StringBuilder();
		final long TotalSec = time / 1000;
		final long TotalMin = TotalSec / 60;
		final long TotalHour = TotalMin / 60;
		final int second = (int) TotalSec % 60;
		final int minute = (int) TotalMin % 60;
		final int hour = (int) TotalHour % 60;
		if (hour < 10)
			t.append("0");
		t.append(hour);
		t.append(":");
		if (minute < 10)
			t.append("0");
		t.append(minute);
		t.append(":");
		if (second < 10)
			t.append("0");
		t.append(second);
		return(t.toString());
	}
	
	public java.lang.String toStringTimeElapsed()
	{
		return(toStringTime(getTimeElapsed()));
	}
	
	public java.lang.String toStringTimeRemaining()
	{
		return (toStringTime(getTimeRemaining()));
	}
}