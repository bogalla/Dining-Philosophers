import common.BaseThread;
import java.util.concurrent.Semaphore;

/**
 * Class Philosopher.
 * Outlines main subrutines of our virtual philosopher.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Philosopher extends BaseThread
{
	/**
	 * Max time an action can take (in milliseconds)
	 */
	public static final long TIME_TO_WASTE = 1000;

	private Semaphore tb = new Semaphore(0);
	private Semaphore mutex = new Semaphore(0);
	private int sc = 0;

	/**
	 * The act of eating.
	 * - Print the fact that a given phil (their TID) has started eating.
	 * - yield
	 * - Then sleep() for a random interval.
	 * - yield
	 * - The print that they are done eating.
	 */
	public void eat()
	{
		try
		{
			System.out.println("Philosopher " + this.iTID + " has started EATING"); //TASK 1 announcements and yields
			this.randomYield();
			sleep((long)(Math.random() * TIME_TO_WASTE));
			this.randomYield();
			System.out.println("Philosopher " + this.iTID + " has stopped EATING");
		}
		catch(InterruptedException e)
		{
			System.err.println("Philosopher.eat(): ");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}

	/**
	 * The act of thinking.
	 * - Print the fact that a given phil (their TID) has started thinking.
	 * - yield
	 * - Then sleep() for a random interval.
	 * - yield
	 * - The print that they are done thinking.
	 */
	public void think()
	{
		try
		{
			System.out.println("Philosopher " + this.iTID + " has started THINKING"); //TASK 1 announcements and yields
			yield();
			sleep((long)(Math.random() * TIME_TO_WASTE));
			yield();
			System.out.println("Philosopher " + this.iTID + " has stopped THINKING");
		}
		catch(InterruptedException e)
		{
			System.err.println("Philosopher.think(): ");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}

	/**
	 * The act of talking.
	 * - Print the fact that a given phil (their TID) has started talking.
	 * - yield
	 * - Say something brilliant at random
	 * - yield
	 * - The print that they are done talking.
	 */
	public void talk()
	{
		System.out.println("Philosopher " + this.iTID + " has started TALKING"); //TASK 1 announcements and yields
		yield();
		saySomething();
		yield();
		System.out.println("Philosopher " + this.iTID + " has stopped TALKING");
	}

	/**
	 * The act of sleeping.
	 * - Print the fact that a given phil (their TID) has started sleeping.
	 * - yield
	 * - Then sleep() for a random interval.
	 * - yield
	 * - The print that they are done sleeping.
	 */
	public void philSleep() //received but unsure how to implement
	{
		try
		{
			System.out.println("Philosopher " + this.getTID() + " has started sleeping");
			yield();
			sleep((long) (Math.random() * TIME_TO_WASTE));
			yield();
			System.out.println("Philosopher " + this.getTID() + " has finished sleeping");
		}
		catch(InterruptedException e)
		{
			System.err.println("Philosopher.sleep(): ");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}



	/**
	 * No, this is not the act of running, just the overridden Thread.run()
	 */
	public void run()
	{
		for(int i = 0; i < DiningPhilosophers.DINING_STEPS; i++)
		{
			DiningPhilosophers.soMonitor.pickUp(getTID()-1);

			eat();

			DiningPhilosophers.soMonitor.putDown(getTID()-1);

			think();

			/*
			 * TODO:
			 * A decision is made at random whether this particular
			 * philosopher is about to say something terribly useful.
			 */
			if((Math.random() * 100) % 3 == 0)
			{
				// Some monitor ops down here...
				// no one is sleeping ... wait for wake up?
				// no one is talking ... wait for done talking
				DiningPhilosophers.talkingStick.requestTalk(getTID()-1); //TASK 1 new monitor to allow talking

				talk();

				DiningPhilosophers.talkingStick.endTalk(getTID()-1); //TASK 1 new monitor to end talking
			}

			yield();
		}
	} // run()

	/**
	 * Prints out a phrase from the array of phrases at random.
	 * Feel free to add your own phrases.
	 */
	public void saySomething()
	{
		String[] astrPhrases =
		{
			"Eh, it's not easy to be a philosopher: eat, think, talk, eat...",
			"You know, true is false and false is true if you think of it",
			"2 + 2 = 5 for extremely large values of 2...",
			"If thee cannot speak, thee must be silent",
			"My number is " + getTID() + ""
		};

		System.out.println
		(
			"Philosopher " + getTID() + " says: " +
			astrPhrases[(int)(Math.random() * astrPhrases.length)]
		);
	}
}

// EOF
