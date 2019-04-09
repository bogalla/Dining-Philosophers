import common.BaseThread;

/**
 * Class Philosopher.
 * Outlines main subrutines of our virtual philosopher.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Philosopher extends BaseThread
{
	int priority;
	/**
	 * Max time an action can take (in milliseconds)
	 */
	public static final long TIME_TO_WASTE = 1000;

	public Philosopher(int numPhil){
		super();
		this.priority = calculatePriority(getTID()-1, numPhil);
	}

	private int calculatePriority(int id, int numPhil){
		if((id % 2) == 0){
			return (int)((numPhil/2)*Math.random());
		}
		return (int)(((numPhil/2)*Math.random()) + numPhil/2);
	}

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
			System.out.println("Philosopher " + (this.getTID()-1) + " has started EATING"); //TASK 1 announcements and yields
			this.randomYield();
			sleep((long)(Math.random() * TIME_TO_WASTE));
			this.randomYield();
			System.out.println("Philosopher " + (this.getTID()-1) + " has stopped EATING");
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
			//System.out.println("Philosopher " + (this.getTID()-1) + " has started THINKING"); //TASK 1 announcements and yields
			yield();
			sleep((long)(Math.random() * TIME_TO_WASTE));
			yield();
			//System.out.println("Philosopher " + (this.getTID()-1) + " has stopped THINKING");
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
<<<<<<< HEAD
		//System.out.println("Philosopher " + (this.getTID()-1) + " has started TALKING"); //TASK 1 announcements and yields
=======
		try
		{
		System.out.println("Philosopher " + (this.getTID()-1) + " has started TALKING"); //TASK 1 announcements and yields
>>>>>>> 1c7af581a64f631af9dbcc9c2652fe8181c098f7
		yield();
		saySomething();
		sleep((long)(Math.random() * TIME_TO_WASTE));
		yield();
<<<<<<< HEAD
		//System.out.println("Philosopher " + (this.getTID()-1) + " has stopped TALKING");
=======
		System.out.println("Philosopher " + (this.getTID()-1) + " has stopped TALKING");
		}
			catch(InterruptedException e)
		{
			System.err.println("Philosopher.think(): ");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
>>>>>>> 1c7af581a64f631af9dbcc9c2652fe8181c098f7
	}

	/**
	 * The act of sleeping.
	 * - Print the fact that a given phil (their TID) has started sleeping.
	 * - yield
	 * - Then sleep() for a random interval.
	 * - yield
	 * - The print that they are done sleeping.
	 */
	public void philSleep()
	{
		try
		{
<<<<<<< HEAD
			//System.out.println("Philosopher " + (this.getTID()-1) + " has started sleeping");
			yield();
			sleep((long) (Math.random() * TIME_TO_WASTE));
			yield();
			//System.out.println("Philosopher " + (this.getTID()-1) + " has finished sleeping");
=======
			System.out.println("Philosopher " + (this.getTID()-1) + " has started SLEEPING");
			yield();
			sleep((long) (Math.random() * TIME_TO_WASTE));
			yield();
			System.out.println("Philosopher " + (this.getTID()-1) + " has finished SLEEPING");
>>>>>>> 1c7af581a64f631af9dbcc9c2652fe8181c098f7
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
		try {
			for (int i = 0; i < DiningPhilosophers.DINING_STEPS; i++) {
				DiningPhilosophers.soMonitor.pickUp(getTID() - 1);

				eat();

				DiningPhilosophers.soMonitor.putDown(getTID() - 1);

<<<<<<< HEAD
=======
				System.out.println("Philosopher " + (getTID()-1) + " has done eat, will sleep");

				DiningPhilosophers.soMonitor.requestSleep(getTID()-1);

				philSleep();

				DiningPhilosophers.soMonitor.endSleep(getTID()-1);

>>>>>>> 1c7af581a64f631af9dbcc9c2652fe8181c098f7
				think();

				/*
				 * TODO:
				 * A decision is made at random whether this particular
				 * philosopher is about to say something terribly useful.
				 */
				/*(int)((Math.random() * 100) % 2) == 0*/
				if (true) {
					// Some monitor ops down here...
					// no one is sleeping ... wait for wake up?
					// no one is talking ... wait for done talking

<<<<<<< HEAD
					DiningPhilosophers.talkingStick.requestTalk(getTID() - 1); //TASK 1 new monitor to allow talking

					talk();

					DiningPhilosophers.talkingStick.endTalk(getTID() - 1); //TASK 1 new monitor to end talking
=======
					DiningPhilosophers.soMonitor.requestTalk(getTID() - 1); //TASK 1 new monitor to allow talking

					talk();

					DiningPhilosophers.soMonitor.endTalk(getTID() - 1); //TASK 1 new monitor to end talking
>>>>>>> 1c7af581a64f631af9dbcc9c2652fe8181c098f7
				}

				yield();
			}
		}
		catch(InterruptedException e)
		{
			System.out.println(e.getMessage());
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
			"My number is " + (getTID()-1) + ""
		};

		System.out.println
		(
			"Philosopher " + (getTID()-1) + " says: " +
			astrPhrases[(int)(Math.random() * astrPhrases.length)]
		);
	}
}

// EOF
