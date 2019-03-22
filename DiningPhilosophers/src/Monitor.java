import java.util.concurrent.locks.Condition;

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	private int numPhil = 0;

	public enum status {
		Hungry,
		Eating,
		WantTalk,
		Talking,
		Sleeping,
		Tired,
		Thinking;
	}

	private status[] state;

	private Condition[] selfEat;
	private Condition[] selfTalk;


	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		numPhil = piNumberOfPhilosophers;
		state = new status[piNumberOfPhilosophers];
		selfEat = new Condition[piNumberOfPhilosophers];
		selfTalk = new Condition[piNumberOfPhilosophers];
		init();
		// TODO: set appropriate number of chopsticks based on the # of philosophers
	}

	private void init(){
		for(int i = 0; i < numPhil; i++){
			state[i] = status.Thinking;
		}
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int pIndex) //TASK 2 only eat if you have both forks
	{
		state[pIndex] = status.Hungry;
		testEat(pIndex);
		if(state[pIndex] != status.Eating){
			try {
				selfEat[pIndex].wait();
			} catch (InterruptedException e){
				System.err.println("Monitor.pickUp():");
				DiningPhilosophers.reportException(e);
				System.exit(1);
			}
		}
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int pIndex) //TASK 2 release your neighbors
	{
		state[pIndex] = status.Thinking;
		testEat((pIndex - 1) % numPhil);
		testEat((pIndex + 1) % numPhil);
	}

	/**
	 * Only one philosopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk(int pIndex) //TASK 2
	{
		state[pIndex] = status.WantTalk;
		testTalk(pIndex);
		if(state[pIndex] != status.Talking)
		{
			try {
				selfTalk[pIndex].wait();
			} catch (InterruptedException e){
				System.err.println("Monitor.putDown():");
				DiningPhilosophers.reportException(e);
				System.exit(1);
			}
		}
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(int pIndex)
	{
		state[pIndex] = status.Thinking;
		for(int i = 0; i < numPhil; i++){
			testTalk(i);
		}
	}

	private void testEat(int pIndex) //TASK 2 to complete pickUpChopsticks()
	{
		System.out.println("in testEat");
		if(state[pIndex] != status.Eating && state[pIndex] != status.Eating &&
				state[pIndex] == status.Hungry)
		{
			state[pIndex] = status.Eating;
			selfEat[pIndex].signal();
		}
	}

	private void testTalk(int pIndex)
	{
		boolean okToTalk = true;
		for (int i = 0; i < numPhil; i++) {
			okToTalk = okToTalk && (state[i] != status.Sleeping && state[i] != status.Talking);
			//todo sleeping ??? ?? ??
		}

		if ( okToTalk && state[pIndex] == status.WantTalk){
			state[pIndex] = status.Talking;
			selfTalk[pIndex].signal();
		}
	}
}

// EOF
