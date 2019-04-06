import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	private enum status {
		THINKING,
		HUNGRY,
		EATING,
		EUREKA,
		TALKING
	}
	private Lock lock = new ReentrantLock();
	private status[] state;
	private Condition[] self;
	private int nbPhil;

	/**
	 * Constructor
	 */
	public Monitor(int nbPhil)
	{
		this.nbPhil = nbPhil;
		state = new status[nbPhil];
		self = new Condition[nbPhil];
		init();
		// TODO: set appropriate number of chopsticks based on the # of philosophers
	}

	private void init(){
		for(int i = 0; i < state.length; i++){
			state[i] = status.THINKING;
			self[i] = lock.newCondition();
		}
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	private int getRight(int id){
		return (id < nbPhil -1) ? id + 1: 0;
	}

	private int getLeft(int id){
		return (id > 0) ? id - 1 : nbPhil - 1;
	}

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public void pickUp(final int id) throws InterruptedException//TASK 2 only eat if you have both forks
	{
		System.out.println("Philosopher " + id + " will try to pick up");
		lock.lock();
		state[id] = status.HUNGRY;
		testEat(id);
		if(state[id] != status.EATING){
			System.out.println("Philosopher " + id + " has is waiting to eat");
			self[id].await();
		}
		lock.unlock();
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public void putDown(final int id) //TASK 2 release your neighbors
	{
		lock.lock();
		state[id] = status.THINKING;
		testEat(getLeft(id));
		testEat(getRight(id));
		lock.unlock();
	}

	/**
	 * Only one philosopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public void requestTalk(int id) throws InterruptedException//TASK 2
	{
		lock.lock();
		state[id] = status.EUREKA;
		testTalk(id);
		if(state[id] != status.TALKING){
			self[id].await();
		}
		lock.unlock();
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(int id) //Task 2
	{
		lock.lock();
		state[id] = status.THINKING;
		testTalk(getLeft(id));
		testTalk(getRight(id));
		lock.unlock();
	}

	public void testEat(int id){  //Task 2
		lock.lock();
		if (bothChopsticksFree(id) && state[id] == status.HUNGRY)
		{
			System.out.println("Philosopher " + id + " has passes test eat");
			state[id] = status.EATING;
			self[id].signal();
		}
		lock.unlock();
	}

	private boolean bothChopsticksFree(int id){ //Task 2
		return state[getLeft(id)] != status.EATING && state[getRight(id)] != status.EATING;
	}

	public void testTalk(int id){ //Task 2
		lock.lock();
		if(noOneIsTalking() && state[id] == status.EUREKA){
			state[id] = status.TALKING;
			self[id].signal();
		}
		lock.unlock();
	}

	private boolean noOneIsTalking(){ //Task 2
		for (int i = 0; i < state.length; i++){
			if(state[i] == status.TALKING){
				return false;
			}
		}
		return true;
	}
}

// EOF
